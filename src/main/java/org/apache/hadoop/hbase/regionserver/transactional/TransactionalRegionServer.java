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
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.RemoteExceptionHandler;
import org.apache.hadoop.hbase.catalog.CatalogTracker;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.ipc.HBaseRPCProtocolVersion;
import org.apache.hadoop.hbase.ipc.TransactionalRegionInterface;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.Leases;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLogKey;
import org.apache.hadoop.hbase.regionserver.wal.HLogSplitter;
import org.apache.hadoop.hbase.regionserver.wal.WALActionsListener;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.HasThread;
import org.apache.hadoop.io.MapWritable;
import org.apache.zookeeper.KeeperException;

/**
 * RegionServer with support for transactions. Transactional logic is at the
 * region level, so we mostly just delegate to the appropriate
 * TransactionalRegion.
 */
public class TransactionalRegionServer extends HRegionServer implements
		TransactionalRegionInterface {

	private static final String LEASE_TIME = "hbase.transaction.leasetime";
	private static final int DEFAULT_LEASE_TIME = 60 * 1000;
	private static final int LEASE_CHECK_FREQUENCY = 1000;

	static final Log LOG = LogFactory.getLog(TransactionalRegionServer.class);
	private final Leases transactionLeases;
	private final CleanOldTransactionsChore cleanOldTransactionsThread;

	private THLog trxHLog;

	/**
	 * @param conf
	 * @throws IOException
	 */
	public TransactionalRegionServer(final Configuration conf)
			throws IOException, InterruptedException {
		super(conf);
		this.getRpcMetrics().createMetrics(
				new Class<?>[] { TransactionalRegionInterface.class });
		cleanOldTransactionsThread = new CleanOldTransactionsChore(this);
		transactionLeases = new Leases(conf.getInt(LEASE_TIME,
				DEFAULT_LEASE_TIME), LEASE_CHECK_FREQUENCY);
		LOG.info("Transaction lease time: "
				+ conf.getInt(LEASE_TIME, DEFAULT_LEASE_TIME));
	}

	protected THLog getTransactionLog() {
		return trxHLog;
	}

	/**
	 * Make sure we add a listener for closing the transactional log when the
	 * regular WAL closes.
	 */
	@Override
	protected List<WALActionsListener> getWALActionListeners() {
		List<WALActionsListener> listeners = super.getWALActionListeners();
		// super.getWALActionListeners()
		listeners.add(new WALActionsListener() {

			@Override
			public void logCloseRequested() {
				closeTransactionWAL();
			}

			@Override
			public void logRollRequested() {
				// don't care
			}

			@Override
			public void visitLogEntryBeforeWrite(final HRegionInfo info,
					final HLogKey logKey, final WALEdit logEdit) {
				// don't care
			}

			@Override
			public void preLogRoll(Path oldPath, Path newPath)
					throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void postLogRoll(Path oldPath, Path newPath)
					throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void preLogArchive(Path oldPath, Path newPath)
					throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void postLogArchive(Path oldPath, Path newPath)
					throws IOException {
				// TODO Auto-generated method stub

			}

			@Override
			public void visitLogEntryBeforeWrite(HTableDescriptor htd,
					HLogKey logKey, WALEdit logEdit) {
				// TODO Auto-generated method stub

			}
		});
		return listeners;
	}

	@Override
	public long getProtocolVersion(final String protocol,
			final long clientVersion) throws IOException {
		if (protocol.equals(TransactionalRegionInterface.class.getName())) {
			return HBaseRPCProtocolVersion.versionID;
		}
		return super.getProtocolVersion(protocol, clientVersion);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleReportForDutyResponse(final MapWritable c)
			throws IOException {
		super.handleReportForDutyResponse(c);
		initializeTHLog();
		String n = Thread.currentThread().getName();
		UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {

			public void uncaughtException(final Thread t, final Throwable e) {
				abort("Set stop flag in " + t.getName(), e);
				LOG.fatal("Set stop flag in " + t.getName(), e);
			}
		};
		setDaemonThreadRunning(this.cleanOldTransactionsThread, n
				+ ".oldTransactionCleaner", handler);
		setDaemonThreadRunning(this.transactionLeases,
				"Transactional leases");
	}

	public static HasThread setDaemonThreadRunning(final HasThread t,
			final String name, final UncaughtExceptionHandler handler) {
		t.setName(name);
		if (handler != null) {
			t.setUncaughtExceptionHandler(handler);
		}
		t.setDaemon(true);

		t.start();
		return t;
	}

	public static HasThread setDaemonThreadRunning(final HasThread t,
			final String name) {
		return setDaemonThreadRunning(t, name, null);
	}

	private void initializeTHLog() throws IOException {
		// We keep in the same directory as the core HLog.
		Path oldLogDir = new Path(getRootDir(), HLogSplitter.RECOVERED_EDITS);
		Path logdir = new Path(getRootDir(),
				HLog.getHLogDirectoryName(super.getServerName().getServerName()));

		trxHLog = new THLog(getFileSystem(), logdir, oldLogDir, conf, null);
	}

	@Override
	public void postOpenDeployTasks(final HRegion r, final CatalogTracker ct,
			final boolean daughter) throws KeeperException, IOException {
		if (r instanceof TransactionalRegion) {
			TransactionalRegion trxRegion = (TransactionalRegion) r;
			trxRegion.setTransactionLog(trxHLog);
			trxRegion.setTransactionalLeases(getTransactionalLeases());
		}
		super.postOpenDeployTasks(r, ct, daughter);
	}

	protected TransactionalRegion getTransactionalRegion(final byte[] regionName)
			throws NotServingRegionException {
		return (TransactionalRegion) super.getRegion(regionName);
	}

	protected Leases getTransactionalLeases() {
		return this.transactionLeases;
	}

	/**
	 * We want to delay the close region for a bit if we have commit pending
	 * transactions.
	 * 
	 * @throws NotServingRegionException
	 */
	@Override
	protected boolean closeRegion(final HRegionInfo region,
			final boolean abort, final boolean zk) {
		try {
			getTransactionalRegion(region.getRegionName()).prepareToClose();
		} catch (NotServingRegionException e) {
			LOG.warn(
					"Failed to wait for uncommitted transactions to commit during region close.",
					e);
		}
		return super.closeRegion(region, abort, zk);
	}

	/**
	 * Close the transaction log.
	 */
	private void closeTransactionWAL() {
		if (null != trxHLog) {
			try {
				trxHLog.close();
			} catch (Throwable e) {
				LOG.error("Close and delete of trx WAL failed",
						RemoteExceptionHandler.checkThrowable(e));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void abortTransaction(final byte[] regionName,
			final long transactionId) throws IOException {
		checkOpen();
		super.getRequestCount().incrementAndGet();
		try {
			getTransactionalRegion(regionName).abort(transactionId);
		} catch (NotServingRegionException e) {
			LOG.info("Got not serving region durring abort. Ignoring.");
		} catch (IOException e) {
			checkFileSystem();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void commit(final byte[] regionName, final long transactionId)
			throws IOException {
		checkOpen();
		super.getRequestCount().incrementAndGet();
		try {
			getTransactionalRegion(regionName).commit(transactionId);
		} catch (IOException e) {
			checkFileSystem();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int commitRequest(final byte[] regionName, final long transactionId)
			throws IOException {
		checkOpen();
		super.getRequestCount().incrementAndGet();
		try {
			return getTransactionalRegion(regionName).commitRequest(
					transactionId);
		} catch (IOException e) {
			checkFileSystem();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean commitIfPossible(final byte[] regionName,
			final long transactionId) throws IOException {
		checkOpen();
		super.getRequestCount().incrementAndGet();
		try {
			return getTransactionalRegion(regionName).commitIfPossible(
					transactionId);
		} catch (IOException e) {
			checkFileSystem();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long openScanner(final long transactionId, final byte[] regionName,
			final Scan scan) throws IOException {
		checkOpen();
		NullPointerException npe = null;
		if (regionName == null) {
			npe = new NullPointerException("regionName is null");
		} else if (scan == null) {
			npe = new NullPointerException("scan is null");
		}
		if (npe != null) {
			throw new IOException("Invalid arguments to openScanner", npe);
		}
		super.getRequestCount().incrementAndGet();
		try {
			TransactionalRegion r = getTransactionalRegion(regionName);
			RegionScanner s = r.getScanner(transactionId, scan);
			long scannerId = addScanner(s);
			return scannerId;
		} catch (IOException e) {
			LOG.error("Error opening scanner (fsOk: " + this.fsOk + ")",
					RemoteExceptionHandler.checkIOException(e));
			checkFileSystem();
			throw e;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beginTransaction(final long transactionId,
			final byte[] regionName) throws IOException {
		getTransactionalRegion(regionName).beginTransaction(transactionId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final long transactionId, final byte[] regionName,
			final Delete delete) throws IOException {

		SingleVersionDeleteNotSupported.validateDelete(delete);

		getTransactionalRegion(regionName).delete(transactionId, delete);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Result get(final long transactionId, final byte[] regionName,
			final Get get) throws IOException {
		return getTransactionalRegion(regionName).get(transactionId, get);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(final long transactionId, final byte[] regionName,
			final Put put) throws IOException {
		getTransactionalRegion(regionName).put(transactionId, put);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int put(final long transactionId, final byte[] regionName,
			final Put[] puts) throws IOException {
		getTransactionalRegion(regionName).put(transactionId, puts);
		return puts.length; // ??
	}
}
