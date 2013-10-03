package com.adintellig.ella.action;

public class StoreFactory {
	/**
	 * 返回实现IStatStore接口的某种对象。
	 */
	public static IStatStore getStore() {
		// 这里应该设计成从配制文件中读取，暂时先这样写。
		// 如果想在系统中使用jdbc的方式来实现统计功能，则返回JdbcStat对象即可。
		return new MemStat();
	}
}
