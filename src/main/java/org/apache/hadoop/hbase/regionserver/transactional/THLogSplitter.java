package org.apache.hadoop.hbase.regionserver.transactional;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.regionserver.wal.HLog;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Entry;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Reader;
import org.apache.hadoop.hbase.regionserver.wal.HLog.Writer;
import org.apache.hadoop.hbase.regionserver.wal.HLogSplitter;

/**
 * Extend core THLog splitter to make it also split the transactional logs.
 * 
 * @author clint.morgan
 */
public class THLogSplitter extends HLogSplitter {

    public THLogSplitter(final Configuration conf, final Path rootDir, final Path srcDir, final Path oldLogDir,
            final FileSystem fs) {
        super(conf, rootDir, srcDir, oldLogDir, fs);
    }

    private boolean isTrxLog(final Path logfile) {
        return logfile.getName().contains(THLog.THLOG_DATFILE);
    }

    @Override
    protected Writer createWriter(final FileSystem fs, final Path logfile, final Configuration conf) throws IOException {
        return new DualWriter(fs, logfile, conf);
    }

    @Override
    protected Reader getReader(final FileSystem fs, final Path logfile, final Configuration conf) throws IOException {
        if (isTrxLog(logfile)) {
            return THLog.getReader(fs, logfile, conf);
        }
        return HLog.getReader(fs, logfile, conf);
    }

    private static class DualWriter implements Writer {

        private final FileSystem fs;
        private final Path logfile;
        private final Configuration conf;

        private Writer coreWriter = null;
        private Writer trxWriter = null;

        public DualWriter(final FileSystem fs, final Path logfile, final Configuration conf) {
            this.fs = fs;
            this.logfile = logfile;
            this.conf = conf;
        }

        private boolean isTrxEntry(final Entry entry) {
            return entry.getKey() instanceof THLogKey;
        }

        private synchronized Writer getCoreWriter() throws IOException {
            if (coreWriter == null) {
                coreWriter = HLog.createWriter(fs, logfile, conf);
            }
            return coreWriter;
        }

        private synchronized Writer getTrxWriter() throws IOException {
            if (trxWriter == null) {
                Path trxPath = new Path(logfile.getParent(), THLog.HREGION_OLD_THLOGFILE_NAME);
                trxWriter = THLog.createWriter(fs, trxPath, conf);
            }
            return trxWriter;
        }

        @Override
        public void append(final Entry entry) throws IOException {
            if (isTrxEntry(entry)) {
                getTrxWriter().append(entry);
            } else {
                getCoreWriter().append(entry);
            }

        }

        @Override
        public void close() throws IOException {
            if (coreWriter != null) {
                coreWriter.close();
            }
            if (trxWriter != null) {
                trxWriter.close();
            }
        }

        @Override
        public long getLength() throws IOException {
            throw new UnsupportedOperationException("Not expected to be called");
        }

        @Override
        public void init(final FileSystem fs, final Path path, final Configuration c) throws IOException {
            throw new UnsupportedOperationException("Not expected to be called");
        }

        @Override
        public void sync() throws IOException {
            if (coreWriter != null) {
                coreWriter.sync();
            }
            if (trxWriter != null) {
                trxWriter.sync();
            }
        }

    }
}
