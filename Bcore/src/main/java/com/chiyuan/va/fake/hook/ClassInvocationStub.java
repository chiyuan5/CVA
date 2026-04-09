package com.chiyuan.va.fake.hook;

import android.os.Binder;
import android.text.TextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.chiyuan.va.utils.MethodParameterUtils;

public abstract class ClassInvocationStub implements InvocationHandler, IInjectHook {
    public static final String TAG = "CIS";

    private final Map<String, MethodHook> mMethodHookMap = new HashMap<>();
    private Object mBase;
    private Object mProxyInvocation;
    private boolean onlyProxy;

    protected abstract Object getWho();
    protected abstract void inject(Object baseInvocation, Object proxyInvocation);
    protected void onBindMethod() {}

    protected Object getProxyInvocation() { return mProxyInvocation; }
    protected Object getBase() { return mBase; }
    protected void onlyProxy(boolean o) { onlyProxy = o; }

    @Override
    public void injectHook() {
        mBase = getWho();
        if (mBase == null) return;

        // 先创建内层 JDK Proxy 作为实际拦截器
        Object innerProxy = Proxy.newProxyInstance(
                mBase.getClass().getClassLoader(),
                MethodParameterUtils.getAllInterface(mBase.getClass()),
                this);

        // ★ 用 NonProxyWrapper 包装，令 Proxy.isProxyClass() 返回 false
        mProxyInvocation = NonProxyWrapper.wrap(
                mBase, innerProxy,
                MethodParameterUtils.getAllInterface(mBase.getClass()));

        if (!onlyProxy) {
            inject(mBase, mProxyInvocation);
        }

        onBindMethod();
        for (Class<?> dc : this.getClass().getDeclaredClasses()) {
            initAnnotation(dc);
        }
        ScanClass scanClass = this.getClass().getAnnotation(ScanClass.class);
        if (scanClass != null) {
            for (Class<?> aClass : scanClass.value()) {
                for (Class<?> dc : aClass.getDeclaredClasses()) {
                    initAnnotation(dc);
                }
            }
        }
    }

    protected void initAnnotation(Class<?> clazz) {
        ProxyMethod proxyMethod = clazz.getAnnotation(ProxyMethod.class);
        if (proxyMethod != null) {
            final String name = proxyMethod.value();
            if (!TextUtils.isEmpty(name)) {
                try { addMethodHook(name, (MethodHook) clazz.newInstance()); }
                catch (Throwable t) { t.printStackTrace(); }
            }
        }
        ProxyMethods proxyMethods = clazz.getAnnotation(ProxyMethods.class);
        if (proxyMethods != null) {
            for (String name : proxyMethods.value()) {
                try { addMethodHook(name, (MethodHook) clazz.newInstance()); }
                catch (Throwable t) { t.printStackTrace(); }
            }
        }
    }

    protected void addMethodHook(MethodHook methodHook) {
        mMethodHookMap.put(methodHook.getMethodName(), methodHook);
    }
    protected void addMethodHook(String name, MethodHook methodHook) {
        mMethodHookMap.put(name, methodHook);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // ★ 清除 Binder 调用链中间层身份标记，避免向系统服务转发时携带 VA 进程痕迹
        final long token = Binder.clearCallingIdentity();
        try {
            MethodHook methodHook = mMethodHookMap.get(method.getName());
            if (methodHook == null || !methodHook.isEnable()) {
                try {
                    return method.invoke(mBase, args);
                } catch (Throwable e) {
                    Throwable cause = e.getCause();
                    throw (cause != null) ? cause : e;
                }
            }
            Object result = methodHook.beforeHook(mBase, method, args);
            if (result != null) return result;
            result = methodHook.hook(mBase, method, args);
            return methodHook.afterHook(result);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }
}
