package com.adintellig.ella.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adintellig.ella.dao.RequestCountDaoImpl;
import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;

public class PersistDisk {
	private static Logger logger = LoggerFactory.getLogger(PersistDisk.class);

	public static void persistMysqlToFile(String tableName, String output) {
		logger.info("Persist mysql data to local file: " + output);
		RequestCountDaoImpl impl = new RequestCountDaoImpl();
		if (null != tableName && tableName.trim().length() > 0) {
			try {
				List<RequestCount> reqs = impl.listDetails(tableName);
				if (null != reqs && reqs.size() > 0) {
					// 3 files
					BufferedWriter bwWrite = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(output
									+ "write.tsv")));
					BufferedWriter bwRead = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(output
									+ "read.tsv")));
					BufferedWriter bwTotal = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(output
									+ "total.tsv")));
					// init header
					bwWrite.write("date\tclose");
					bwWrite.newLine();

					bwRead.write("date\tclose");
					bwRead.newLine();

					bwTotal.write("date\tclose");
					bwTotal.newLine();

					// persist data
					for (RequestCount req : reqs) {
						if (req instanceof TableRequestCount) {
							TableRequestCount treq = (TableRequestCount) req;

							treq.getTableName();
							long wc = treq.getWriteCount();
							long rc = treq.getReadCount();
							long tc = treq.getTotalCount();
							String uts = DateFormatUtil.formatToTime(treq
									.getUpdateTime());
							bwWrite.write(uts + "\t" + wc);
							bwWrite.newLine();

							bwRead.write(uts + "\t" + rc);
							bwRead.newLine();

							bwTotal.write(uts + "\t" + tc);
							bwTotal.newLine();
						}
					}// for

					bwWrite.close();
					bwRead.close();
					bwTotal.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("Persist over!");
		}
	}

	public static void main(String[] args) {
		PersistDisk
				.persistMysqlToFile("message_user",
						"/tmp/jetty-0.0.0.0-8080-ella-0.1.war-_ella-0.1-any-/webapp/data/");
	}

}
