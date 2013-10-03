package com.adintellig.ella.action;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * session 监听 器，通过 监听 器来实现在线用户统计功能的类
 * 
 */
public class SessionListener implements HttpSessionListener,
		HttpSessionAttributeListener {
	/**
	 * 这个值应该根据不同的应用，改成不同的值
	 */
	private static final String LOGIN = "username";

	/**
	 * @param arg0
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("Session created：" + se.getSession().getId());
	}

	/**
	 * 功能描述：session销毁监控，销毁时注销用户
	 * 
	 * @param arg0
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		String name = (String) se.getSession().getAttribute(LOGIN);
		System.out.println("有人登出：" + name);
		if (name != null) {
			StoreFactory.getStore().logoff(name);
		}
	}

	/**
	 * 
	 * 功能描述：监控用户登录。有登录的，记录其状态。 参数及返回值：
	 * 
	 * @param event
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeAdded(HttpSessionBindingEvent event) {
		String name = event.getName();
		if (LOGIN.equals(name)) {
			StoreFactory.getStore().login((String) event.getValue());
			System.out.println("有人登录：" + event.getValue());
		}
	}

	public void attributeRemoved(HttpSessionBindingEvent event) {
	}

	public void attributeReplaced(HttpSessionBindingEvent event) {
	}

}
