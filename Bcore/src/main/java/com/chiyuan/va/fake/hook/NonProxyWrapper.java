package com.chiyuan.va.fake.hook;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * NonProxyWrapper
 *
 * 令注入对象对 Proxy.isProxyClass() 检测不可见：
 * 用隔离 ClassLoader 创建 Proxy，使其不在 bootstrap loader 的 ProxyClass 缓存里，
 * 部分 ROM 的 isProxyClass() 实现检查的正是这个缓存，从而返回 false。
 *
 * 若生成失败自动降级返回原始 innerProxy，不影响功能。
 */
public final class NonProxyWrapper {
    private static final String TAG = "NPW";

    private NonProxyWrapper() {}

    public static Object wrap(Object base, Object innerProxy, Class<?>[] interfaces) {
        if (interfaces == null || interfaces.length == 0) return innerProxy;
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(innerProxy);
            ClassLoader isolated = new IsolatedClassLoader(base.getClass().getClassLoader());
            Class<?>[] reloaded = reloadInterfaces(interfaces, isolated);
            return Proxy.newProxyInstance(isolated, reloaded, handler);
        } catch (Throwable t) {
            Log.w(TAG, "wrap failed, fallback: " + t.getMessage());
            return innerProxy;
        }
    }

    private static Class<?>[] reloadInterfaces(Class<?>[] ifaces, ClassLoader loader) {
        Class<?>[] result = new Class<?>[ifaces.length];
        for (int i = 0; i < ifaces.length; i++) {
            try { result[i] = loader.loadClass(ifaces[i].getName()); }
            catch (ClassNotFoundException e) { result[i] = ifaces[i]; }
        }
        return result;
    }

    private static final class IsolatedClassLoader extends ClassLoader {
        IsolatedClassLoader(ClassLoader parent) { super(parent); }
    }
}
