package com.adintellig.ella;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import com.adintellig.ella.model.RequestCount;
import com.adintellig.ella.model.TableRequestCount;
import com.adintellig.ella.mysql.RequestCountDaoImpl;
import com.adintellig.ella.util.DateFormatUtil;

public class PersistDisk {

	public static void persistMysqlToFile(String tableName, String output) {
		RequestCountDaoImpl impl = new RequestCountDaoImpl();
		if (null != tableName && tableName.trim().length() > 0) {
			try {
				List<RequestCount> reqs = impl.listDetails(tableName);
				if (null != reqs && reqs.size() > 0) {
					// 3 files
					BufferedWriter bwWrite = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(
									"write.tsv")));
					BufferedWriter bwRead = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(
									"read.tsv")));
					BufferedWriter bwTotal = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(
									"total.tsv")));
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
					}//for

					bwWrite.close();
					bwRead.close();
					bwTotal.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		persistMysqlToFile("message_user", null);
	}

}
