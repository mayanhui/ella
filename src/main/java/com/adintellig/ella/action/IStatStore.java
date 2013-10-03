package com.adintellig.ella.action;

import java.util.List;

/**
 * 在线人数统计的接口，可以使用多种方式实现，如：内存、数据库等，只要实现这个接口就可以了。
 */
public interface IStatStore {
	/**
	 * 记录登录状态
	 * 
	 * @param username
	 */
	public void login(String username);

	/**
	 * 取消登录状态
	 * 
	 * @param username
	 */
	public void logoff(String username);

	/**
	 * 返回登录用户及信息。
	 * 
	 * @return　链表里的对象是个数组，数组包含两个元素，０：用户名，１：登录时间。这样是为了便于为jstl标签处理
	 */
	public List getUsers();

	/**
	 * 在线用户数量
	 */
	public int getCount();
}