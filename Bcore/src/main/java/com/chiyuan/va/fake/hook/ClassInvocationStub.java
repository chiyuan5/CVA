package com.chiyuan.va.fake.hook;

import android.text.TextUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.chiyuan.va.utils.AceAntiDetect;
import com.chiyuan.va.utils.MethodParameterUtils;
import com.chiyuan.va.utils.StackTraceFilter;


public abstract class ClassInvocationStub implements InvocationHandler, IInjectHook {
    public static final String TAG = "CIS";

    private final Map<String, MethodHook> mMethodHookMap = new HashMap<>();
    private Object mBase;
    private Object mProxyInvocation;
    private boolean onlyProxy;

    protected abstract Object getWho();

    protected abstract void inject(Object baseInvocation, Object proxyInvocation);

    protected void onBindMethod() {
    }

    protected Object getProxyInvocation() {
        return mProxyInvocation;
    }

    protected Object getBase() {
        return mBase;
    }

    protected void onlyProxy(boolean o) {
        onlyProxy = o;
    }

    @Override
    public void injectHook() {
        mBase = getWho();
        if (mBase == null) {
            return;
        }
        mProxyInvocation = Proxy.newProxyInstance(
            mBase.getClass().getClassLoader(),
            MethodParameterUtils.getAllInterface(mBase.getClass()),
            this
        );

        // Register with ACE anti-detect so isProxyClass returns false for this
        AceAntiDetect.registerProxy(mProxyInvocation);

        if (!onlyProxy) {
            inject(mBase, mProxyInvocation);
        }

        onBindMethod();
        Class<?>[] declaredClasses = this.getClass().getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            initAnnotation(declaredClass);
        }
        ScanClass scanClass = this.getClass().getAnnotation(ScanClass.class);
        if (scanClass != null) {
            for (Class<?> aClass : scanClass.value()) {
                for (Class<?> declaredClass : aClass.getDeclaredClasses()) {
                    initAnnotation(declaredClass);
                }
            }
        }
    }

    protected void initAnnotation(Class<?> clazz) {
        ProxyMethod proxyMethod = clazz.getAnnotation(ProxyMethod.class);
        if (proxyMethod != null) {
            final String name = proxyMethod.value();
            if (!TextUtils.isEmpty(name)) {
                try {
                    addMethodHook(name, (MethodHook) clazz.newInstance());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        ProxyMethods proxyMethods = clazz.getAnnotation(ProxyMethods.class);
        if (proxyMethods != null) {
            String[] value = proxyMethods.value();
            for (String name : value) {
                try {
                    addMethodHook(name, (MethodHook) clazz.newInstance());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
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
        String methodName = method.getName();

        // === ACE Anti-Detection: hide Proxy nature ===

        // ACE calls Proxy.isProxyClass() via JNI (0x27C090)
        // Intercept if called through our proxy chain
        if ("isProxyClass".equals(methodName) && args != null && args.length == 1
                && args[0] instanceof Class) {
            Class<?> targetClass = (Class<?>) args[0];
            if (AceAntiDetect.shouldHideProxyClass(targetClass)) {
                return Boolean.FALSE;
            }
        }

        // ACE may call getClass() to check if an object is a Proxy subclass
        if ("getClass".equals(methodName) && (args == null || args.length == 0)) {
            if (mBase != null) {
                return mBase.getClass();
            }
        }

        // Hide $ProxyN in toString() output
        if ("toString".equals(methodName) && (args == null || args.length == 0)) {
            if (mBase != null) {
                try { return mBase.toString(); } catch (Throwable ignored) {}
            }
        }

        // Consistent hashCode with base object
        if ("hashCode".equals(methodName) && (args == null || args.length == 0)) {
            if (mBase != null) {
                return System.identityHashCode(mBase);
            }
        }

        // equals should work with both proxy and base
        if ("equals".equals(methodName) && args != null && args.length == 1) {
            return proxy == args[0] || mBase == args[0];
        }

        MethodHook methodHook = mMethodHookMap.get(methodName);
        if (methodHook == null || !methodHook.isEnable()) {
            try {
                return method.invoke(mBase, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
        }

        Object result = methodHook.beforeHook(mBase, method, args);
        if (result != null) {
            return result;
        }
        result = methodHook.hook(mBase, method, args);
        result = methodHook.afterHook(result);
        return result;
    }
}
