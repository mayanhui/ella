package org.apache.hadoop.hbase.client.transactional;

import java.io.IOException;

import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.ScannerCallable;
import org.apache.hadoop.hbase.ipc.TransactionalRegionInterface;

class TransactionScannerCallable extends ScannerCallable {

  private TransactionState transactionState;

  TransactionScannerCallable(final TransactionState transactionState,
      final HConnection connection, final byte[] tableName, Scan scan) {
    super(connection, tableName,  scan, null);
    this.transactionState = transactionState;
  }

  @Override
  protected long openScanner() throws IOException {
    if (transactionState.addRegion(location)) {
      ((TransactionalRegionInterface) server).beginTransaction(transactionState
          .getTransactionId(), location.getRegionInfo().getRegionName());
    }
    return ((TransactionalRegionInterface) server).openScanner(transactionState
        .getTransactionId(), this.location.getRegionInfo().getRegionName(),
        getScan());
  }
}
