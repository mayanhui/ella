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
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.regionserver.transactional.TransactionState.WriteAction;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.SequenceFileLogReader;
import org.apache.hadoop.hbase.regionserver.wal.SequenceFileLogWriter;
import org.apache.hadoop.hbase.regionserver.wal.WALActionsListener;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;

/**
 * Add support for transactional operations to the regionserver's
 * write-ahead-log.
 */
public class THLog extends HLog {

	static final String THLOG_DATFILE = "thlog.dat.";

	/** Name of old log file for reconstruction */
	static final String HREGION_OLD_THLOGFILE_NAME = "oldthlogfile.log";

	public THLog(final FileSystem fs, final Path dir, final Path oldLogDir,
			final Configuration conf, final List<WALActionsListener> listeners)
			throws IOException {
		super(fs, dir, oldLogDir, conf, listeners, false, null);
	}

	/**
	 * Get a writer for the WAL.
	 * 
	 * @param path
	 * @param conf
	 * @return A WAL writer. Close when done with it.
	 * @throws IOException
	 */
	public static Writer createWriter(final FileSystem fs, final Path path,
			final Configuration conf) throws IOException {
		try {
			HLog.Writer writer = new SequenceFileLogWriter(THLogKey.class);
			writer.init(fs, path, conf);
			return writer;
		} catch (Exception e) {
			IOException ie = new IOException("cannot get log writer");
			ie.initCause(e);
			throw ie;
		}
	}

	@Override
	protected Writer createWriterInstance(final FileSystem fs, final Path path,
			final Configuration conf) throws IOException {
		return createWriter(fs, path, conf);
	}

	/**
	 * This is a convenience method that computes a new filename with a given
	 * file-number.
	 * 
	 * @param fn
	 * @return Path
	 */
	@Override
	protected Path computeFilename() {
		// REVIEW : Use prefix ?
		return new Path(getDir(), THLOG_DATFILE + getFilenum());
	}

	/**
	 * Get a reader for the WAL.
	 * 
	 * @param fs
	 * @param path
	 * @param conf
	 * @return A WAL reader. Close when done with it.
	 * @throws IOException
	 */
	public static Reader getReader(final FileSystem fs, final Path path,
			final Configuration conf) throws IOException {
		try {
			HLog.Reader reader = new SequenceFileLogReader(THLogKey.class);
			reader.init(fs, path, conf);
			return reader;
		} catch (Exception e) {
			IOException ie = new IOException("cannot get log reader");
			ie.initCause(e);
			throw ie;
		}
	}

	protected THLogKey makeKey(final byte[] regionName, final byte[] tableName,
			final long seqnum, final long now) {
		return new THLogKey(regionName, tableName, seqnum, now, null, -1);
	}

	/**
	 * Write a transactional state to the log after we have decide that it can
	 * be committed. At this time we are still waiting for the final vote (from
	 * other regions), so the commit may not be processed.
	 */
	public void writeCommitResuestToLog(final HRegionInfo regionInfo,
			final TransactionState transactionState) throws IOException {
		this.appendCommitRequest(regionInfo,
				EnvironmentEdgeManager.currentTimeMillis(), transactionState);
	}

	/**
	 * @param regionInfo
	 * @param transactionId
	 * @throws IOException
	 */
	public void writeCommitToLog(final HRegionInfo regionInfo,
			final long transactionId) throws IOException {
		this.append(regionInfo, EnvironmentEdgeManager.currentTimeMillis(),
				THLogKey.TrxOp.COMMIT, transactionId);
	}

	/**
	 * @param regionInfo
	 * @param transactionId
	 * @throws IOException
	 */
	public void writeAbortToLog(final HRegionInfo regionInfo,
			final long transactionId) throws IOException {
		this.append(regionInfo, EnvironmentEdgeManager.currentTimeMillis(),
				THLogKey.TrxOp.ABORT, transactionId);
	}

	/**
	 * Write a general transaction op to the log. This covers: start, commit,
	 * and abort.
	 * 
	 * @param regionInfo
	 * @param now
	 * @param txOp
	 * @param transactionId
	 * @throws IOException
	 */
	private void append(final HRegionInfo regionInfo, final long now,
			final THLogKey.TrxOp txOp, final long transactionId)
			throws IOException {
		@SuppressWarnings("deprecation")
		THLogKey key = new THLogKey(regionInfo.getRegionName(), regionInfo
				.getTableDesc().getName(), -1, now, txOp, transactionId);
		WALEdit e = new WALEdit();
		e.add(new KeyValue(new byte[0], 0, 0)); // Empty KeyValue
		super.append(regionInfo, key, e, null, false);
	}

	/**
	 * Write a transactional state to the log for a commit request.
	 * 
	 * @param regionInfo
	 * @param update
	 * @param transactionId
	 * @throws IOException
	 */
	private void appendCommitRequest(final HRegionInfo regionInfo,
			final long now, final TransactionState transactionState)
			throws IOException {

		@SuppressWarnings("deprecation")
		THLogKey key = new THLogKey(regionInfo.getRegionName(), regionInfo
				.getTableDesc().getName(), -1, now,
				THLogKey.TrxOp.COMMIT_REQUEST,
				transactionState.getTransactionId());

		WALEdit e = new WALEdit();

		for (WriteAction write : transactionState.getWriteOrdering()) {
			for (KeyValue value : write.getKeyValues()) {
				e.add(value);
			}
		}

		super.append(regionInfo, key, e, null, false);

	}
}
