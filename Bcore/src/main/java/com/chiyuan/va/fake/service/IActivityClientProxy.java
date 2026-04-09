package com.chiyuan.va.fake.service;

import android.app.ActivityManager;
import android.os.IBinder;

import java.lang.reflect.Method;

import black.android.app.BRActivityClient;
import black.android.util.BRSingleton;
import com.chiyuan.va.fake.frameworks.BActivityManager;
import com.chiyuan.va.fake.hook.ClassInvocationStub;
import com.chiyuan.va.fake.hook.MethodHook;
import com.chiyuan.va.fake.hook.NonProxyWrapper;
import com.chiyuan.va.fake.hook.ProxyMethod;
import com.chiyuan.va.fake.hook.ScanClass;
import com.chiyuan.va.utils.MethodParameterUtils;
import com.chiyuan.va.utils.compat.TaskDescriptionCompat;

@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityClientProxy extends ClassInvocationStub {
    // ★ 中性 TAG
    public static final String TAG = "IAC";
    private final Object who;

    public IActivityClientProxy(Object who) { this.who = who; }

    @Override
    protected Object getWho() {
        if (who != null) return who;
        Object instance  = BRActivityClient.get().getInstance();
        Object singleton = BRActivityClient.get(instance).INTERFACE_SINGLETON();
        return BRSingleton.get(singleton).get();
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        // ★ 注入前用 NonProxyWrapper 再包一层，使 isProxyClass() 检测失效
        Object wrapped = NonProxyWrapper.wrap(
                baseInvocation,
                proxyInvocation,
                MethodParameterUtils.getAllInterface(baseInvocation.getClass()));
        Object instance  = BRActivityClient.get().getInstance();
        Object singleton = BRActivityClient.get(instance).INTERFACE_SINGLETON();
        BRSingleton.get(singleton)._set_mInstance(wrapped);
    }

    @Override public boolean isBadEnv() { return false; }

    @Override public Object getProxyInvocation() { return super.getProxyInvocation(); }
    @Override public void onlyProxy(boolean o)   { super.onlyProxy(o); }

    @ProxyMethod("finishActivity")
    public static class FinishActivity extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onFinishActivity(token);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("activityResumed")
    public static class ActivityResumed extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onActivityResumed(token);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("activityDestroyed")
    public static class ActivityDestroyed extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            IBinder token = (IBinder) args[0];
            BActivityManager.get().onActivityDestroyed(token);
            return method.invoke(who, args);
        }
    }

    @ProxyMethod("setTaskDescription")
    public static class SetTaskDescription extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
            args[1] = TaskDescriptionCompat.fix(td);
            return method.invoke(who, args);
        }
    }
}
