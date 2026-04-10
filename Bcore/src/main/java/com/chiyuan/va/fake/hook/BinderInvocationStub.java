package com.chiyuan.va.fake.hook;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.chiyuan.va.utils.CloseUtils;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.utils.Str;


public class BinderInvocationStub implements InvocationHandler, IInjectHook {
    private static final String TAG = "BIS";

    
    private static final byte[] _isProxyClass = {
        (byte)0x53, (byte)0x02, (byte)0x95, (byte)0xE0, (byte)0x87,
        (byte)0x37, (byte)0xCA, (byte)0x5E, (byte)0x0B, (byte)0x9B,
        (byte)0xF7, (byte)0x2F
    };

    private final Map<Method, MethodHook> mHookMethodMap = new HashMap<>();
    private final Map<String, MethodHook> mHookMethodNameMap = new HashMap<>();
    private Object mBase;
    private Object mProxy;
    private boolean mEnable;

    public BinderInvocationStub() {
    }

    public void setProxyInfo(Object base, Object... proxyTypes) {
        mBase = base;
        if (proxyTypes != null && proxyTypes.length > 0) {
            Class<?>[] interfaces = new Class<?>[proxyTypes.length];
            for (int i = 0; i < proxyTypes.length; i++) {
                if (proxyTypes[i] instanceof Class) {
                    interfaces[i] = (Class<?>) proxyTypes[i];
                }
            }
            mProxy = Proxy.newProxyInstance(base.getClass().getClassLoader(), interfaces, this);
        }
    }

    public void addMethod(MethodHook methodHook) {
        mHookMethodMap.put(null, methodHook);
    }

    public void addMethod(String methodName, MethodHook methodHook) {
        mHookMethodNameMap.put(methodName, methodHook);
    }

    public Object getProxy() {
        return mProxy;
    }

    public Object getBase() {
        return mBase;
    }

    public boolean isEnable() {
        return mEnable;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodHook hook = mHookMethodNameMap.get(method.getName());
        if (hook != null && hook.isEnable()) {
            return hook.invoke(mBase, method, args);
        }
        for (MethodHook h : mHookMethodMap.values()) {
            if (h != null && h.isEnable()) {
                return h.invoke(mBase, method, args);
            }
        }
        return method.invoke(mBase, args);
    }

    @Override
    public void injectHook() {
        mEnable = true;
    }

    @Override
    public boolean isBadEnv() {
        return !mEnable;
    }

    
    public static boolean isProxyInstance(Object obj) {
        if (obj == null) return false;
        
        return obj instanceof IBinder && Proxy.isProxyClass(obj.getClass());
    }

    
    public static boolean hasProxyInterface(Object obj) {
        if (obj == null) return false;
        Class<?> clazz = obj.getClass();
        for (Class<?> iface : clazz.getInterfaces()) {
            if (IInterface.class.isAssignableFrom(iface)) {
                return true;
            }
        }
        return false;
    }

    public static abstract class MethodHook {
        public abstract boolean isEnable();
        public abstract Object invoke(Object thiz, Method method, Object[] args);
    }
}
