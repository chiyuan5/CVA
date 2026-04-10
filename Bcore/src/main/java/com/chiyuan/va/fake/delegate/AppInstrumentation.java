package com.chiyuan.va.fake.delegate;

import static com.chiyuan.va.ChiyuanVACore.*;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.text.TextUtils;

import java.lang.reflect.Method;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.entity.AppConfig;
import com.chiyuan.va.fake.hook.ClassInvocationStub;
import com.chiyuan.va.fake.hook.HookManager;
import com.chiyuan.va.fake.hook.IInjectHook;
import com.chiyuan.va.fake.service.HCallbackProxy;
import com.chiyuan.va.proxy.ProxyActivity;
import com.chiyuan.va.proxy.record.ProxyActivityRecord;
import com.chiyuan.va.utils.ComponentUtils;
import com.chiyuan.va.utils.ReflectUtils;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.utils.Str;

public class AppInstrumentation extends Instrumentation implements IInjectHook {
    public static final String TAG = "AppIn";

    
    private static final byte[] _mInstrumentation = {
        (byte)0x57, (byte)0x38, (byte)0xAB, (byte)0xE1, (byte)0x9C, (byte)0x3D,
        (byte)0xC6, (byte)0x70, (byte)0x02, (byte)0x94, (byte)0xF0, (byte)0x3D,
        (byte)0xAD, (byte)0x4F, (byte)0x64, (byte)0xCF
    };
    private static final byte[] _currentActivityThread = {
        (byte)0x59, (byte)0x04, (byte)0xB7, (byte)0xE0, (byte)0x8D, (byte)0x21,
        (byte)0xC7, (byte)0x5C, (byte)0x04, (byte)0x8E, (byte)0xED, (byte)0x2A,
        (byte)0xB0, (byte)0x52, (byte)0x72, (byte)0xF5, (byte)0x52, (byte)0x03,
        (byte)0xA0, (byte)0xF3, (byte)0x8C
    };
    private static final byte[] _android_app_ActivityThread = {
        (byte)0x5B, (byte)0x1F, (byte)0xA1, (byte)0xE0, (byte)0x87, (byte)0x26,
        (byte)0xD7, (byte)0x33, (byte)0x06, (byte)0x8A, (byte)0xF4, (byte)0x72,
        (byte)0x98, (byte)0x45, (byte)0x7F, (byte)0xC8, (byte)0x4C, (byte)0x18,
        (byte)0xB1, (byte)0xEB, (byte)0xBC, (byte)0x27, (byte)0xC1, (byte)0x78,
        (byte)0x06, (byte)0x9E
    };

    private static final AppInstrumentation sInstance = new AppInstrumentation();
    private Instrumentation mBase;
    private int mCallingProcess;

    public AppInstrumentation() {
    }

    public static AppInstrumentation get() {
        return sInstance;
    }

    private static String dec(byte[] encoded) {
        return Str.dec(encoded);
    }

    public void setBase(Instrumentation proxy) {
        mBase = proxy;
    }

    public void setCallingProcess(int id) {
        mCallingProcess = id;
    }

    @Override
    public void injectHook() {
        try {
            Object at = currentActivityThread();
            Instrumentation old = (Instrumentation) ReflectUtils.readField(at,
                    dec(_mInstrumentation));
            if (old == null || old == this) {
                old = getInstrumentation();
            }
            if (old != null) {
                setBase(old);
                String mainProcessName = ChiyuanVACore.get().getMainProcessName();
                boolean isMainProcess = mainProcessName != null
                        && mainProcessName.equals(ChiyuanVACore.get().getProcessName());
                if (ChiyuanVACore.get().isVAProcess() || isMainProcess) {
                    Application app = getActivityThreadApplication();
                    if (app != null) {
                        app.unregisterActivityLifecycleCallbacks((Application.ActivityLifecycleCallbacks) old);
                    }
                }
                ClassInvocationStub hook = new ClassInvocationStub();
                Object o = android.app.Instrumentation.class.getClassLoader();
                hook.setProxyInfo(old, o);
                hook.addMethod(new EX());
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    hook.addMethod(new EZ());
                }
                hook.setMethodProxy(new EY());
                Instrumentation proxy = (Instrumentation) hook.getProxy();
                if (proxy != null) {
                    ReflectUtils.writeField(at, dec(_mInstrumentation), proxy);
                    android.os.Handler handler = ChiyuanVACore.get().getHandler();
                    if (handler != null) {
                        if (!HCallbackProxy.get().isEnable()) {
                            HCallbackProxy.get().injectHook();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Slog.e(TAG, "injectHook error", e);
        }
    }

    @Override
    public boolean isBadEnv() {
        try {
            Object at = currentActivityThread();
            Instrumentation ins = (Instrumentation) ReflectUtils.readField(at,
                    dec(_mInstrumentation));
            if (ins == null) {
                return false;
            }
            if (ins != this) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static Object currentActivityThread() {
        try {
            Class<?> atClass = Class.forName(dec(_android_app_ActivityThread));
            Method m = atClass.getDeclaredMethod(dec(_currentActivityThread));
            m.setAccessible(true);
            return m.invoke(null);
        } catch (Exception e) {
            Slog.e(TAG, "currentActivityThread error", e);
            return null;
        }
    }

    public static Application getActivityThreadApplication() {
        try {
            Object at = currentActivityThread();
            if (at == null) {
                return null;
            }
            return (Application) ReflectUtils.readField(at, "mInitialApplication");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws InstantiationException, IllegalAccessException {
        try {
            return super.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        } catch (Exception e) {
            Slog.e(TAG, "newActivity error", e);
            return super.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        super.callActivityOnCreate(activity, icicle, persistentState);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return super.newActivity(cl, className, intent);
        } catch (Exception e) {
            Slog.e(TAG, "newActivity2 error", e);
            return super.newActivity(cl, className, intent);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        try {
            super.callActivityOnCreate(activity, icicle);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnCreate error", e);
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        try {
            super.callActivityOnDestroy(activity);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnDestroy error", e);
        }
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        try {
            super.callActivityOnResume(activity);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnResume error", e);
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        try {
            super.callActivityOnPause(activity);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnPause error", e);
        }
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        try {
            super.callActivityOnStop(activity);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnStop error", e);
        }
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        try {
            super.callActivityOnStart(activity);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnStart error", e);
        }
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        try {
            super.callActivityOnRestart(activity);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnRestart error", e);
        }
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        try {
            super.callActivityOnNewIntent(activity, intent);
        } catch (Exception e) {
            Slog.e(TAG, "callActivityOnNewIntent error", e);
        }
    }

    public Instrumentation getInstrumentation() {
        return mBase;
    }

    static class EX extends ClassInvocationStub.MethodName {

        @Override
        public String getMethodName() {
            return "newActivity";
        }

        @Override
        public Object invoke(Object thiz, Method method, Object[] args) {
            return null;
        }
    }

    static class EY extends ClassInvocationStub.MethodHandler {
        @Override
        public boolean isEnable() {
            return true;
        }

        @Override
        public Object invoke(Object thiz, Method method, Object[] args) {
            return null;
        }
    }

    static class EZ extends ClassInvocationStub.MethodName {

        @Override
        public String getMethodName() {
            return "execStartActivity";
        }

        @Override
        public Object invoke(Object thiz, Method method, Object[] args) {
            return null;
        }
    }
}
