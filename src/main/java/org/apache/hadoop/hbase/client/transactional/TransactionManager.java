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
package org.apache.hadoop.hbase.client.transactional;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.ipc.TransactionalRegionInterface;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;

/**
 * Transaction Manager. Responsible for committing transactions.
 */
public class TransactionManager {

    static final Log LOG = LogFactory.getLog(TransactionManager.class);

    private final HConnection connection;
    private final TransactionLogger transactionLogger;
    private JtaXAResource xAResource;

    /**
     * @param conf
     * @throws ZooKeeperConnectionException
     */
    public TransactionManager(final Configuration conf) throws ZooKeeperConnectionException {
        this(LocalTransactionLogger.getInstance(), conf);
    }

    /**
     * @param transactionLogger
     * @param conf
     * @throws ZooKeeperConnectionException
     */
    public TransactionManager(final TransactionLogger transactionLogger, final Configuration conf)
            throws ZooKeeperConnectionException {
        this.transactionLogger = transactionLogger;
        connection = HConnectionManager.getConnection(conf);
    }

    /**
     * Called to start a transaction.
     * 
     * @return new transaction state
     */
    public TransactionState beginTransaction() {
        long transactionId = transactionLogger.createNewTransactionLog();
        LOG.debug("Begining transaction " + transactionId);
        return new TransactionState(transactionId);
    }

    /**
     * Prepare to commit a transaction.
     * 
     * @param transactionState
     * @return commitStatusCode (see {@link TransactionalRegionInterface})
     * @throws IOException
     * @throws CommitUnsuccessfulException
     */
    public int prepareCommit(final TransactionState transactionState) throws CommitUnsuccessfulException, IOException {
        boolean allReadOnly = true;
        try {

            for (HRegionLocation location : transactionState.getParticipatingRegions()) {

                @SuppressWarnings("deprecation")
				TransactionalRegionInterface transactionalRegionServer = (TransactionalRegionInterface) connection
                        .getHRegionConnection(location.getServerAddress());
                int commitStatus = transactionalRegionServer.commitRequest(location.getRegionInfo().getRegionName(),
                    transactionState.getTransactionId());
                boolean canCommit = true;
                switch (commitStatus) {
                    case TransactionalRegionInterface.COMMIT_OK:
                        allReadOnly = false;
                        break;
                    case TransactionalRegionInterface.COMMIT_OK_READ_ONLY:
                        transactionState.addRegionToIgnore(location); // No need to doCommit for read-onlys
                        break;
                    case TransactionalRegionInterface.COMMIT_UNSUCESSFUL:
                        canCommit = false;
                        transactionState.addRegionToIgnore(location); // No need to re-abort.
                        break;
                    default:
                        throw new CommitUnsuccessfulException("Unexpected return code from prepareCommit: "
                                + commitStatus);
                }

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Region [" + location.getRegionInfo().getRegionNameAsString() + "] votes "
                            + (canCommit ? "to commit" : "to abort") + " transaction "
                            + transactionState.getTransactionId());
                }

                if (!canCommit) {
                    LOG.debug("Aborting [" + transactionState.getTransactionId() + "]");
                    abort(transactionState);
                    return TransactionalRegionInterface.COMMIT_UNSUCESSFUL;
                }
            }
        } catch (Exception e) {
            LOG.debug("Commit of transaction [" + transactionState.getTransactionId() + "] was unsucsessful", e);
            // This happens on a NSRE that is triggered by a split
            try {
                abort(transactionState);
            } catch (Exception abortException) {
                LOG.warn("Exeption durring abort", abortException);
            }
            throw new CommitUnsuccessfulException(e);
        }
        return allReadOnly ? TransactionalRegionInterface.COMMIT_OK_READ_ONLY : TransactionalRegionInterface.COMMIT_OK;
    }

    /**
     * Try and commit a transaction. This does both phases of the 2-phase protocol: prepare and commit.
     * 
     * @param transactionState
     * @throws IOException
     * @throws CommitUnsuccessfulException
     */
    public void tryCommit(final TransactionState transactionState) throws CommitUnsuccessfulException, IOException {
        long startTime = EnvironmentEdgeManager.currentTimeMillis();
        LOG.trace("atempting to commit trasaction: " + transactionState.toString());
        int status = prepareCommit(transactionState);

        if (status == TransactionalRegionInterface.COMMIT_OK) {
            doCommit(transactionState);
        } else if (status == TransactionalRegionInterface.COMMIT_OK_READ_ONLY) {
            transactionLogger.forgetTransaction(transactionState.getTransactionId());
        } else if (status == TransactionalRegionInterface.COMMIT_UNSUCESSFUL) {
            // We have already aborted at this point
            throw new CommitUnsuccessfulException();
        }
        LOG.trace("Committed transaction [" + transactionState.getTransactionId() + "] in ["
                + ((EnvironmentEdgeManager.currentTimeMillis() - startTime)) + "]ms");
    }

    /**
     * Do the commit. This is the 2nd phase of the 2-phase protocol.
     * 
     * @param transactionState
     * @throws CommitUnsuccessfulException
     */
    void doCommit(final TransactionState transactionState) throws CommitUnsuccessfulException {
        try {
            LOG.trace("Commiting [" + transactionState.getTransactionId() + "]");

            transactionLogger.setStatusForTransaction(transactionState.getTransactionId(),
                TransactionLogger.TransactionStatus.COMMITTED);

            for (HRegionLocation location : transactionState.getParticipatingRegions()) {
                if (transactionState.getRegionsToIngore().contains(location)) {
                    continue;
                }
                @SuppressWarnings("deprecation")
				TransactionalRegionInterface transactionalRegionServer = (TransactionalRegionInterface) connection
                        .getHRegionConnection(location.getServerAddress());
                transactionalRegionServer.commit(location.getRegionInfo().getRegionName(),
                    transactionState.getTransactionId());
            }
        } catch (Exception e) {
            LOG.info("Commit of transaction [" + transactionState.getTransactionId() + "] was unsucsessful", e);
            // This happens on a NSRE that is triggered by a split
            try {
                abort(transactionState);
            } catch (Exception abortException) {
                LOG.warn("Exeption durring abort", abortException);
            }
            throw new CommitUnsuccessfulException(e);
        }
        transactionLogger.forgetTransaction(transactionState.getTransactionId());
    }

    /**
     * Abort a s transaction.
     * 
     * @param transactionState
     * @throws IOException
     */
    public void abort(final TransactionState transactionState) throws IOException {
        transactionLogger.setStatusForTransaction(transactionState.getTransactionId(),
            TransactionLogger.TransactionStatus.ABORTED);

        for (HRegionLocation location : transactionState.getParticipatingRegions()) {
            if (transactionState.getRegionsToIngore().contains(location)) {
                continue;
            }
            try {
                @SuppressWarnings("deprecation")
				TransactionalRegionInterface transactionalRegionServer = (TransactionalRegionInterface) connection
                        .getHRegionConnection(location.getServerAddress());

                transactionalRegionServer.abortTransaction(location.getRegionInfo().getRegionName(),
                    transactionState.getTransactionId());
            } catch (UnknownTransactionException e) {
                LOG.info("Got unknown transaciton exception durring abort. Transaction: ["
                        + transactionState.getTransactionId() + "], region: ["
                        + location.getRegionInfo().getRegionNameAsString() + "]. Ignoring.");
            } catch (NotServingRegionException e) {
                LOG.info("Got NSRE durring abort. Transaction: [" + transactionState.getTransactionId() + "], region: ["
                        + location.getRegionInfo().getRegionNameAsString() + "]. Ignoring.");
            }
        }
        transactionLogger.forgetTransaction(transactionState.getTransactionId());
    }

    public synchronized JtaXAResource getXAResource() {
        if (xAResource == null) {
            xAResource = new JtaXAResource(this);
        }
        return xAResource;
    }
}
