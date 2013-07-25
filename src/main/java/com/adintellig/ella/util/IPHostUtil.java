package com.adintellig.ella.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class IPHostUtil {
	public static void main(String[] args) {
		List<String> localIPs = getAllHostIPs("hbase-master");
		for (String ip : localIPs) {
			System.out.println(ip);
		}

		String str = "hbase-regionserver-63,60020,1366192188804";
		System.out.println(str.substring(0, str.indexOf(",")));
	}

	public static String getLocalHostIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	public static String getLocalHostName() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostName();
		} catch (Exception e) {
			return "";
		}
	}

	public static List<String> getAllHostIPs(String hostName) {
		List<String> ips = null;
		try {
			InetAddress[] addrs = InetAddress.getAllByName(hostName);
			if (null != addrs && addrs.length > 0) {
				ips = new ArrayList<String>();
				for (int i = 0; i < addrs.length; i++) {
					ips.add(addrs[i].getHostAddress());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ips;
	}

	public static List<String> getAllInternalHostIPs(String hostName) {
		List<String> ips = null;
		try {
			InetAddress[] addrs = InetAddress.getAllByName(hostName);
			if (null != addrs && addrs.length > 0) {
				ips = new ArrayList<String>();
				for (int i = 0; i < addrs.length; i++) {
					String ip = addrs[i].getHostAddress();
					if (isInnerIP(ip))
						ips.add(ip);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ips;
	}

	/**
	 * A=10.0.0.0-10.255.255.255 B=172.16.0.0-172.31.255.255
	 * C=192.168.0.0-192.168.255.255
	 **/
	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		long ipNum = getIpNum(ipAddress);
		long aBegin = getIpNum("10.0.0.0");
		long aEnd = getIpNum("10.255.255.255");
		long bBegin = getIpNum("172.16.0.0");
		long bEnd = getIpNum("172.31.255.255");
		long cBegin = getIpNum("192.168.0.0");
		long cEnd = getIpNum("192.168.255.255");
		isInnerIp = isInner(ipNum, aBegin, aEnd)
				|| isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
				|| ipAddress.equals("127.0.0.1");
		return isInnerIp;
	}

	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}

}