/**
 * Copyright 2009 The Apache Software Foundation Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package org.apache.hadoop.hbase.regionserver.transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.KeyValueScanner;
import org.apache.hadoop.hbase.regionserver.ScanQueryMatcher;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;

/**
 * Holds the state of a transaction. This includes a buffer of all writes, a
 * record of all reads / scans, and information about which other transactions
 * we need to check against.
 */
class TransactionState {

	private static final Log LOG = LogFactory.getLog(TransactionState.class);

	/** Current status. */
	public enum Status {
		/** Initial status, still performing operations. */
		PENDING,
		/**
		 * Checked if we can commit, and said yes. Still need to determine the
		 * global decision.
		 */
		COMMIT_PENDING,
		/** Committed. */
		COMMITED,
		/** Aborted. */
		ABORTED
	}

	/**
	 * Simple container of the range of the scanners we've opened. Used to check
	 * for conflicting writes.
	 */
	private static class ScanRange {

		protected byte[] startRow;
		protected byte[] endRow;

		public ScanRange(final byte[] startRow, final byte[] endRow) {
			this.startRow = startRow == HConstants.EMPTY_START_ROW ? null
					: startRow;
			this.endRow = endRow == HConstants.EMPTY_END_ROW ? null : endRow;
		}

		/**
		 * Check if this scan range contains the given key.
		 * 
		 * @param rowKey
		 * @return boolean
		 */
		public boolean contains(final byte[] rowKey) {
			if (startRow != null && Bytes.compareTo(rowKey, startRow) < 0) {
				return false;
			}
			if (endRow != null && Bytes.compareTo(endRow, rowKey) < 0) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "startRow: "
					+ (startRow == null ? "null" : Bytes.toString(startRow))
					+ ", endRow: "
					+ (endRow == null ? "null" : Bytes.toString(endRow));
		}
	}

	private final HRegionInfo regionInfo;
	private final long hLogStartSequenceId;
	private final long transactionId;
	private Status status;
	private List<ScanRange> scans = new LinkedList<ScanRange>();
	private List<Delete> deletes = new LinkedList<Delete>();
	private List<WriteAction> writeOrdering = new LinkedList<WriteAction>();
	private Set<TransactionState> transactionsToCheck = new HashSet<TransactionState>();
	private int startSequenceNumber;
	private Integer sequenceNumber;
	private int commitPendingWaits = 0;

	TransactionState(final long transactionId, final long rLogStartSequenceId,
			final HRegionInfo regionInfo) {
		this.transactionId = transactionId;
		this.hLogStartSequenceId = rLogStartSequenceId;
		this.regionInfo = regionInfo;
		this.status = Status.PENDING;
	}

	void addRead(final byte[] rowKey) {
		scans.add(new ScanRange(rowKey, rowKey));
	}

	void addWrite(final Put write) {
		updateLatestTimestamp(write.getFamilyMap().values(),
				EnvironmentEdgeManager.currentTimeMillis());
		writeOrdering.add(new WriteAction(write));
	}

	static void updateLatestTimestamp(
			final Collection<List<KeyValue>> kvsCollection, final long time) {
		byte[] timeBytes = Bytes.toBytes(time);
		// HAVE to manually set the KV timestamps
		for (List<KeyValue> kvs : kvsCollection) {
			for (KeyValue kv : kvs) {
				if (kv.isLatestTimestamp()) {
					kv.updateLatestStamp(timeBytes);
				}
			}
		}
	}

	boolean hasWrite() {
		return writeOrdering.size() > 0;
	}

	void addDelete(final Delete delete) {
		long now = EnvironmentEdgeManager.currentTimeMillis();
		updateLatestTimestamp(delete.getFamilyMap().values(), now);
		if (delete.getTimeStamp() == HConstants.LATEST_TIMESTAMP) {
			delete.setTimestamp(now);
		}
		deletes.add(delete);
		writeOrdering.add(new WriteAction(delete));
	}

	void applyDeletes(final List<KeyValue> input, final long minTime,
			final long maxTime) {
		if (deletes.isEmpty()) {
			return;
		}
		for (Iterator<KeyValue> itr = input.iterator(); itr.hasNext();) {
			KeyValue included = applyDeletes(itr.next(), minTime, maxTime);
			if (null == included) {
				itr.remove();
			}
		}
	}

	KeyValue applyDeletes(final KeyValue kv, final long minTime,
			final long maxTime) {
		if (deletes.isEmpty()) {
			return kv;
		}

		for (Delete delete : deletes) {
			// Skip if delete should not apply
			if (!Bytes.equals(kv.getRow(), delete.getRow())
					|| kv.getTimestamp() > delete.getTimeStamp()
					|| delete.getTimeStamp() > maxTime
					|| delete.getTimeStamp() < minTime) {
				continue;
			}

			// Whole-row delete
			if (delete.isEmpty()) {
				return null;
			}

			for (Entry<byte[], List<KeyValue>> deleteEntry : delete
					.getFamilyMap().entrySet()) {
				byte[] family = deleteEntry.getKey();
				if (!Bytes.equals(kv.getFamily(), family)) {
					continue;
				}
				List<KeyValue> familyDeletes = deleteEntry.getValue();
				if (familyDeletes == null) {
					return null;
				}
				for (KeyValue keyDeletes : familyDeletes) {
					byte[] deleteQualifier = keyDeletes.getQualifier();
					byte[] kvQualifier = kv.getQualifier();
					if (keyDeletes.getTimestamp() > kv.getTimestamp()
							&& Bytes.equals(deleteQualifier, kvQualifier)) {
						return null;
					}
				}
			}
		}

		return kv;
	}

	void addTransactionToCheck(final TransactionState transaction) {
		transactionsToCheck.add(transaction);
	}

	boolean hasConflict() {
		for (TransactionState transactionState : transactionsToCheck) {
			if (hasConflict(transactionState)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasConflict(final TransactionState checkAgainst) {
		if (checkAgainst.getStatus().equals(TransactionState.Status.ABORTED)) {
			return false; // Cannot conflict with aborted transactions
		}

		for (WriteAction otherUpdate : checkAgainst.writeOrdering) {
			byte[] row = otherUpdate.getRow();
			for (ScanRange scanRange : this.scans) {
				if (scanRange.contains(row)) {
					LOG.debug("Transaction [" + this.toString()
							+ "] has scan which conflicts with ["
							+ checkAgainst.toString() + "]: region ["
							+ regionInfo.getRegionNameAsString()
							+ "], scanRange[" + scanRange.toString()
							+ "] ,row[" + Bytes.toString(row) + "]");
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the status.
	 * 
	 * @return Return the status.
	 */
	Status getStatus() {
		return status;
	}

	/**
	 * Set the status.
	 * 
	 * @param status
	 *            The status to set.
	 */
	void setStatus(final Status status) {
		this.status = status;
	}

	/**
	 * Get the startSequenceNumber.
	 * 
	 * @return Return the startSequenceNumber.
	 */
	int getStartSequenceNumber() {
		return startSequenceNumber;
	}

	/**
	 * Set the startSequenceNumber.
	 * 
	 * @param startSequenceNumber
	 */
	void setStartSequenceNumber(final int startSequenceNumber) {
		this.startSequenceNumber = startSequenceNumber;
	}

	/**
	 * Get the sequenceNumber.
	 * 
	 * @return Return the sequenceNumber.
	 */
	Integer getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * Set the sequenceNumber.
	 * 
	 * @param sequenceNumber
	 *            The sequenceNumber to set.
	 */
	void setSequenceNumber(final Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[transactionId: ");
		result.append(transactionId);
		result.append(" status: ");
		result.append(status.name());
		result.append(" scan Size: ");
		result.append(scans.size());
		result.append(" write Size: ");
		result.append(getWriteOrdering().size());
		result.append(" startSQ: ");
		result.append(startSequenceNumber);
		if (sequenceNumber != null) {
			result.append(" commitedSQ:");
			result.append(sequenceNumber);
		}
		result.append("]");

		return result.toString();
	}

	/**
	 * Get the transactionId.
	 * 
	 * @return Return the transactionId.
	 */
	long getTransactionId() {
		return transactionId;
	}

	/**
	 * Get the startSequenceId.
	 * 
	 * @return Return the startSequenceId.
	 */
	long getHLogStartSequenceId() {
		return hLogStartSequenceId;
	}

	void addScan(final Scan scan) {
		ScanRange scanRange = new ScanRange(scan.getStartRow(),
				scan.getStopRow());
		LOG.trace(String.format(
				"Adding scan for transcaction [%s], from [%s] to [%s]",
				transactionId,
				scanRange.startRow == null ? "null" : Bytes
						.toString(scanRange.startRow),
				scanRange.endRow == null ? "null" : Bytes
						.toString(scanRange.endRow)));
		scans.add(scanRange);
	}

	int getCommitPendingWaits() {
		return commitPendingWaits;
	}

	void incrementCommitPendingWaits() {
		this.commitPendingWaits++;
	}

	/**
	 * Get deletes.
	 * 
	 * @return deletes
	 */
	List<Delete> getDeletes() {
		return deletes;
	}

	/**
	 * Get a scanner to go through the puts and deletes from this transaction.
	 * Used to weave together the local trx puts with the global state.
	 * 
	 * @return scanner
	 */
	KeyValueScanner getScanner(final Scan scan) {
		return new TransactionScanner(scan);
	}

	private KeyValue[] getAllKVs(final Scan scan) {
		List<KeyValue> kvList = new ArrayList<KeyValue>();

		for (WriteAction action : writeOrdering) {
			byte[] row = action.getRow();
			List<KeyValue> kvs = action.getKeyValues();

			if (scan.getStartRow() != null
					&& !Bytes.equals(scan.getStartRow(),
							HConstants.EMPTY_START_ROW)
					&& Bytes.compareTo(row, scan.getStartRow()) < 0) {
				continue;
			}
			if (scan.getStopRow() != null
					&& !Bytes.equals(scan.getStopRow(),
							HConstants.EMPTY_END_ROW)
					&& Bytes.compareTo(row, scan.getStopRow()) > 0) {
				continue;
			}

			kvList.addAll(kvs);
		}

		return kvList.toArray(new KeyValue[kvList.size()]);
	}

	private int getTransactionSequenceIndex(final KeyValue kv) {
		for (int i = 0; i < writeOrdering.size(); i++) {
			WriteAction action = writeOrdering.get(i);
			if (isKvInPut(kv, action.getPut())) {
				return i;
			}
			if (isKvInDelete(kv, action.getDelete())) {
				return i;
			}
		}
		throw new IllegalStateException("Can not find kv in transaction writes");
	}

	private boolean isKvInPut(final KeyValue kv, final Put put) {
		if (null != put) {
			for (List<KeyValue> putKVs : put.getFamilyMap().values()) {
				for (KeyValue putKV : putKVs) {
					if (putKV == kv) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isKvInDelete(final KeyValue kv, final Delete delete) {
		if (null != delete) {
			for (List<KeyValue> putKVs : delete.getFamilyMap().values()) {
				for (KeyValue deleteKv : putKVs) {
					if (deleteKv == kv) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Scanner of the puts and deletes that occur during this transaction.
	 * 
	 * @author clint.morgan
	 */
	private class TransactionScanner extends KeyValueListScanner implements
			InternalScanner {

		private ScanQueryMatcher matcher;

		TransactionScanner(final Scan scan) {
			super(new KeyValue.KVComparator() {

				@Override
				public int compare(final KeyValue left, final KeyValue right) {
					int result = super.compare(left, right);
					if (result != 0) {
						return result;
					}
					if (left == right) {
						return 0;
					}
					int put1Number = getTransactionSequenceIndex(left);
					int put2Number = getTransactionSequenceIndex(right);
					return put2Number - put1Number;
				}
			}, getAllKVs(scan));

			// We want transaction scanner to always take priority over store
			// scanners.
			setSequenceID(Long.MAX_VALUE);

			// matcher = new ScanQueryMatcher(scan, null, null,
			// HConstants.FOREVER, KeyValue.KEY_COMPARATOR,
			// scan.getMaxVersions());
			matcher = new ScanQueryMatcher(scan, null, null,
					null, Long.MAX_VALUE, HConstants.LATEST_TIMESTAMP,
                    EnvironmentEdgeManager.currentTimeMillis());
		}

		/**
		 * Get the next row of values from this transaction.
		 * 
		 * @param outResult
		 * @param limit
		 * @return true if there are more rows, false if scanner is done
		 */
		@Override
		public synchronized boolean next(final List<KeyValue> outResult,
				final int limit) throws IOException {
			KeyValue peeked = this.peek();
			if (peeked == null) {
				close();
				return false;
			}
			matcher.setRow(peeked.getRow());
			KeyValue kv;
			List<KeyValue> results = new ArrayList<KeyValue>();
			LOOP: while ((kv = this.peek()) != null) {
				ScanQueryMatcher.MatchCode qcode = matcher.match(kv);
				switch (qcode) {
				case INCLUDE:
					KeyValue next = this.next();
					results.add(next);
					if (limit > 0 && results.size() == limit) {
						break LOOP;
					}
					continue;

				case DONE:
					// copy jazz
					outResult.addAll(results);
					return true;

				case DONE_SCAN:
					close();

					// copy jazz
					outResult.addAll(results);

					return false;

				case SEEK_NEXT_ROW:
					this.next();
					break;

				case SEEK_NEXT_COL:
					this.next();
					break;

				case SKIP:
					this.next();
					break;

				default:
					throw new RuntimeException("UNEXPECTED");
				}
			}

			if (!results.isEmpty()) {
				// copy jazz
				outResult.addAll(results);
				return true;
			}

			// No more keys
			close();
			return false;
		}

		@Override
		public boolean next(final List<KeyValue> results) throws IOException {
			return next(results, -1);
		}

	}

	/**
	 * Simple wrapper for Put and Delete since they don't have a common enough
	 * interface.
	 */
	class WriteAction {

		private Put put;
		private Delete delete;

		public WriteAction(final Put put) {
			if (null == put) {
				throw new IllegalArgumentException(
						"WriteAction requires a Put or a Delete.");
			}
			this.put = put;
		}

		public WriteAction(final Delete delete) {
			if (null == delete) {
				throw new IllegalArgumentException(
						"WriteAction requires a Put or a Delete.");
			}
			this.delete = delete;
		}

		public Put getPut() {
			return put;
		}

		public Delete getDelete() {
			return delete;
		}

		public byte[] getRow() {
			if (put != null) {
				return put.getRow();
			} else if (delete != null) {
				return delete.getRow();
			}
			throw new IllegalStateException("WriteAction is invalid");
		}

		@SuppressWarnings("deprecation")
		List<KeyValue> getKeyValues() {
			List<KeyValue> edits = new ArrayList<KeyValue>();
			Collection<List<KeyValue>> kvsList;

			if (put != null) {
				kvsList = put.getFamilyMap().values();
			} else if (delete != null) {
				if (delete.getFamilyMap().isEmpty()) {
					// If whole-row delete then we need to expand for each
					// family
					kvsList = new ArrayList<List<KeyValue>>(1);
					for (byte[] family : regionInfo.getTableDesc()
							.getFamiliesKeys()) {
						KeyValue familyDelete = new KeyValue(delete.getRow(),
								family, null, delete.getTimeStamp(),
								KeyValue.Type.DeleteFamily);
						kvsList.add(Collections.singletonList(familyDelete));
					}
				} else {
					kvsList = delete.getFamilyMap().values();
				}
			} else {
				throw new IllegalStateException("WriteAction is invalid");
			}

			for (List<KeyValue> kvs : kvsList) {
				for (KeyValue kv : kvs) {
					edits.add(kv);
				}
			}
			return edits;
		}
	}

	/**
	 * Get the puts and deletes in transaction order.
	 * 
	 * @return Return the writeOrdering.
	 */
	List<WriteAction> getWriteOrdering() {
		return writeOrdering;
	}
}
