package com.reggie.common;
/*
基于ThreadLocal的封装工具类,用来存储用户的当前登录id值
 */
public class ThreadLocalUtil {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentId(long id) {
        threadLocal.set(id);
    }
}
