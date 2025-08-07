package com.intelijake.mall.common.util;

/**
 * ClassName: ThreadLocalUtil
 * Description:
 * <p>
 * Datetime: 16/06/2025 21:45
 * Author: @Likun.Fang
 * Version: 1.0
 */
@SuppressWarnings("all")
public class ThreadLocalUtil {
    //提供ThreadLocal对象,
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键获取值
    public static <T> T get() {
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }


    //清除ThreadLocal 防止内存泄漏
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
