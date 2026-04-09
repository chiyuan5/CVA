package com.chiyuan.va.fake.delegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.Choreographer;

import java.lang.reflect.Field;

import black.android.app.BRActivity;
import black.android.app.BRActivityThread;
import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.BActivityThread;
import com.chiyuan.va.fake.hook.HookManager;
import com.chiyuan.va.fake.hook.IInjectHook;
import com.chiyuan.va.fake.service.HCallbackProxy;
import com.chiyuan.va.fake.service.IActivityClientProxy;
import com.chiyuan.va.utils.HackAppUtils;
import com.chiyuan.va.utils.compat.ActivityCompat;
import com.chiyuan.va.utils.compat.ActivityManagerCompat;
import com.chiyuan.va.utils.compat.ContextCompat;

public final class AppInstrumentation extends BaseInstrumentationDelegate implements IInjectHook {

    // ★ TAG 不暴露类名
    private static final String TAG = "Instrumentation";

    private static AppInstrumentation sAppInstrumentation;

    public static AppInstrumentation get() {
        if (sAppInstrumentation == null) {
            synchronized (AppInstrumentation.class) {
                if (sAppInstrumentation == null) {
                    sAppInstrumentation = new AppInstrumentation();
                }
            }
        }
        return sAppInstrumentation;
    }

    public AppInstrumentation() {}

    @Override
    public void injectHook() {
        try {
            Instrumentation curr = getCurrInstrumentation();
            if (curr == this || checkInstrumentation(curr)) return;
            mBaseInstrumentation = curr;
            // ★ 注入匿名包装类：getClass().getName() 不含任何 VA 框架关键词
            Instrumentation wrapper = buildWrapper(this);
            BRActivityThread.get(ChiyuanVACore.mainThread())._set_mInstrumentation(wrapper);
            mInjectedWrapper = wrapper;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 保存注入的包装实例，用于 isBadEnv 判断
    private Instrumentation mInjectedWrapper;

    /**
     * 构造匿名 Instrumentation 子类，完全委托给 delegate。
     * 匿名类的 getClass().getEnclosingClass() == AppInstrumentation.class，
     * 可在 checkInstrumentation() 中识别，同时对外不暴露框架类名。
     */
    private static Instrumentation buildWrapper(final AppInstrumentation d) {
        return new Instrumentation() {
            @Override public void onCreate(Bundle a)                   { d.onCreate(a); }
            @Override public void start()                              { d.start(); }
            @Override public void onStart()                            { d.onStart(); }
            @Override public boolean onException(Object o, Throwable e){ return d.onException(o,e); }
            @Override public void finish(int r, Bundle b)              { d.finish(r,b); }
            @Override public void onDestroy()                          { d.onDestroy(); }
            @Override public Context getContext()                      { return d.getContext(); }
            @Override public Context getTargetContext()                 { return d.getTargetContext(); }
            @Override public android.content.ComponentName getComponentName(){ return d.getComponentName(); }
            @Override public boolean isProfiling()                     { return d.isProfiling(); }
            @Override public void startProfiling()                     { d.startProfiling(); }
            @Override public void stopProfiling()                      { d.stopProfiling(); }
            @Override public void waitForIdle(Runnable r)              { d.waitForIdle(r); }
            @Override public void waitForIdleSync()                    { d.waitForIdleSync(); }
            @Override public void runOnMainSync(Runnable r)            { d.runOnMainSync(r); }
            @Override public android.app.UiAutomation getUiAutomation(){ return d.getUiAutomation(); }
            @Override public void sendStatus(int c, Bundle b)          { d.sendStatus(c,b); }
            @Override public Application newApplication(ClassLoader cl, String cls, Context ctx)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException {
                return d.newApplication(cl, cls, ctx);
            }
            @Override public Activity newActivity(ClassLoader cl, String cls, Intent i)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException {
                return d.newActivity(cl, cls, i);
            }
            @Override public Activity newActivity(Class<?> clazz, Context ctx, IBinder token,
                    Application app, Intent i, ActivityInfo info, CharSequence title,
                    Activity parent, String id, Object last)
                    throws IllegalAccessException, InstantiationException {
                return d.newActivity(clazz, ctx, token, app, i, info, title, parent, id, last);
            }
            @Override public void callApplicationOnCreate(Application app) { d.callApplicationOnCreate(app); }
            @Override public void callActivityOnCreate(Activity a, Bundle b)                     { d.callActivityOnCreate(a,b); }
            @Override public void callActivityOnCreate(Activity a, Bundle b, PersistableBundle p){ d.callActivityOnCreate(a,b,p); }
            @Override public void callActivityOnDestroy(Activity a)   { d.callActivityOnDestroy(a); }
            @Override public void callActivityOnStart(Activity a)     { d.callActivityOnStart(a); }
            @Override public void callActivityOnStop(Activity a)      { d.callActivityOnStop(a); }
            @Override public void callActivityOnResume(Activity a)    { d.callActivityOnResume(a); }
            @Override public void callActivityOnPause(Activity a)     { d.callActivityOnPause(a); }
            @Override public void callActivityOnRestart(Activity a)   { d.callActivityOnRestart(a); }
            @Override public void callActivityOnNewIntent(Activity a, Intent i){ d.callActivityOnNewIntent(a,i); }
            @Override public void callActivityOnUserLeaving(Activity a){ d.callActivityOnUserLeaving(a); }
            @Override public void callActivityOnSaveInstanceState(Activity a, Bundle b){ d.callActivityOnSaveInstanceState(a,b); }
            @Override public void callActivityOnSaveInstanceState(Activity a, Bundle b, PersistableBundle p){ d.callActivityOnSaveInstanceState(a,b,p); }
            @Override public void callActivityOnRestoreInstanceState(Activity a, Bundle b){ d.callActivityOnRestoreInstanceState(a,b); }
            @Override public void callActivityOnRestoreInstanceState(Activity a, Bundle b, PersistableBundle p){ d.callActivityOnRestoreInstanceState(a,b,p); }
            @Override public void callActivityOnPostCreate(Activity a, Bundle b){ d.callActivityOnPostCreate(a,b); }
            @Override public void callActivityOnPostCreate(Activity a, Bundle b, PersistableBundle p){ d.callActivityOnPostCreate(a,b,p); }
            // ★ execStartActivity 委托 — 确保匿名 wrapper 也路由到 AppInstrumentation 的拦截逻辑
            public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2, Activity act, Intent i, int req, Bundle opt) throws Throwable {
                return d.execStartActivity(ctx, b1, b2, act, i, req, opt);
            }
            public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2, String str, Intent i, int req, Bundle opt) throws Throwable {
                return d.execStartActivity(ctx, b1, b2, str, i, req, opt);
            }
            @SuppressLint("NewApi")
            public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2, Fragment frag, Intent i, int req) throws Throwable {
                return d.execStartActivity(ctx, b1, b2, frag, i, req);
            }
            public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2, Activity act, Intent i, int req) throws Throwable {
                return d.execStartActivity(ctx, b1, b2, act, i, req);
            }
            @SuppressLint("NewApi")
            public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2, Fragment frag, Intent i, int req, Bundle opt) throws Throwable {
                return d.execStartActivity(ctx, b1, b2, frag, i, req, opt);
            }
            @SuppressLint("NewApi")
            public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2, Activity act, Intent i, int req, Bundle opt, UserHandle uh) throws Throwable {
                return d.execStartActivity(ctx, b1, b2, act, i, req, opt, uh);
            }
        };
    }

    private Instrumentation getCurrInstrumentation() {
        return BRActivityThread.get(ChiyuanVACore.mainThread()).mInstrumentation();
    }

    @Override
    public boolean isBadEnv() {
        return !checkInstrumentation(getCurrInstrumentation());
    }

    private boolean checkInstrumentation(Instrumentation inst) {
        if (inst == null) return false;
        if (inst == this || inst == mInjectedWrapper) return true;
        // 匿名包装类的 enclosingClass 是 AppInstrumentation
        if (AppInstrumentation.class.equals(inst.getClass().getEnclosingClass())) return true;
        // 兼容：字段遍历查找内部的 AppInstrumentation 引用
        Class<?> clazz = inst.getClass();
        while (clazz != null && !Instrumentation.class.equals(clazz)) {
            for (Field f : clazz.getDeclaredFields()) {
                if (Instrumentation.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    try {
                        Object obj = f.get(inst);
                        if (obj instanceof AppInstrumentation) return true;
                    } catch (Exception ignored) {}
                }
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

    // ★ 异步：通过 Choreographer 帧回调检查，不出现在 Activity 生命周期调用栈帧里
    private void scheduleCheckHCallback() {
        try {
            Choreographer.getInstance().postFrameCallback(
                    frameTimeNanos -> HookManager.get().checkEnv(HCallbackProxy.class));
        } catch (Throwable t) {
            HookManager.get().checkEnv(HCallbackProxy.class);
        }
    }

    private void checkActivity(Activity activity) {
        Log.d(TAG, "callActivityOnCreate: " + activity.getClass().getName());
        HackAppUtils.enableQQLogOutput(activity.getPackageName(), activity.getClassLoader());
        scheduleCheckHCallback();
        HookManager.get().checkEnv(IActivityClientProxy.class);
        ActivityInfo info = BRActivity.get(activity).mActivityInfo();
        ContextCompat.fix(activity);
        ActivityCompat.fix(activity);
        if (info.theme != 0) activity.getTheme().applyStyle(info.theme, true);
        ActivityManagerCompat.setActivityOrientation(activity, info.screenOrientation);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ContextCompat.fix(context);
        return super.newApplication(cl, className, context);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle ps) {
        checkActivity(activity);
        super.callActivityOnCreate(activity, icicle, ps);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        checkActivity(activity);
        super.callActivityOnCreate(activity, icicle);
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        scheduleCheckHCallback();
        super.callApplicationOnCreate(app);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return super.newActivity(cl, className, intent);
        } catch (ClassNotFoundException e) {
            return mBaseInstrumentation.newActivity(cl, className, intent);
        }
    }

    // =========================================================================
    // ★ execStartActivity 不再拦截。
    //
    //   之前的实现会在 Instrumentation 层拦截 startActivity 并调用
    //   BActivityManager.startActivity(intent, userId)，但该方法不传递
    //   resultTo（调用者 Activity 的 token），导致 ActivityStack 无法
    //   找到源 Activity，从而错误地以宿主 Context 创建新 Task，使得
    //   分身应用在打开第二个 Activity 时崩溃并回退到宿主。
    //
    //   正确的路由由 ActivityManagerCommonProxy.StartActivity（IActivityManager
    //   的 hook）完成——它在系统 AM 层拦截，能正确传递 resultTo、requestCode
    //   等全部调用者上下文信息给 startActivityAms()，确保 ActivityStack
    //   能找到源 Activity 并在同一 Task 内启动新 Activity。
    //
    //   因此，这里所有 execStartActivity 重载均直接委托给 super（即原始
    //   Instrumentation），让调用正常流入系统 AM，由
    //   ActivityManagerCommonProxy 在那里完成拦截和路由。
    // =========================================================================

    // --- Activity caller ---
    public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2,
            Activity activity, Intent intent, int requestCode, Bundle options) throws Throwable {
        return super.execStartActivity(ctx, b1, b2, activity, intent, requestCode, options);
    }

    // --- String caller (API 23+) ---
    public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2,
            String str, Intent intent, int requestCode, Bundle options) throws Throwable {
        return super.execStartActivity(ctx, b1, b2, str, intent, requestCode, options);
    }

    // --- Fragment caller (legacy) ---
    @SuppressLint("NewApi")
    public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2,
            Fragment fragment, Intent intent, int requestCode) throws Throwable {
        return super.execStartActivity(ctx, b1, b2, fragment, intent, requestCode);
    }

    // --- Activity caller, no Bundle (older API) ---
    public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2,
            Activity activity, Intent intent, int requestCode) throws Throwable {
        return super.execStartActivity(ctx, b1, b2, activity, intent, requestCode);
    }

    // --- Fragment caller, with Bundle ---
    @SuppressLint("NewApi")
    public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2,
            Fragment fragment, Intent intent, int requestCode, Bundle options) throws Throwable {
        return super.execStartActivity(ctx, b1, b2, fragment, intent, requestCode, options);
    }

    // --- UserHandle variant ---
    @SuppressLint("NewApi")
    public ActivityResult execStartActivity(Context ctx, IBinder b1, IBinder b2,
            Activity activity, Intent intent, int requestCode, Bundle options,
            UserHandle userHandle) throws Throwable {
        return super.execStartActivity(ctx, b1, b2, activity, intent, requestCode, options, userHandle);
    }
}
