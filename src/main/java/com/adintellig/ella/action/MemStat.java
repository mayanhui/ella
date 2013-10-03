package com.adintellig.ella.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * 依托内存实现在线用户统计功能。适合于在线用户数量较少的情况。
 */
public class MemStat implements IStatStore {

    private static Map userMap=null;
    static {
        userMap = new HashMap();
    }

    public void login(String username) {
        userMap.put(username, new Date());//如果存在，会覆盖已有的值
    }

    public void logoff(String username) {
        userMap.remove(username);
    }

    public List getUsers() {
        List list = new LinkedList();
        String user = null;
        for(Iterator it = userMap.keySet().iterator();it.hasNext();){
            user = (String) it.next();
            list.add(new Object[]{user,userMap.get(user)});
        }
        return list;
    }

    public int getCount() {
        return userMap.size();
    }

}