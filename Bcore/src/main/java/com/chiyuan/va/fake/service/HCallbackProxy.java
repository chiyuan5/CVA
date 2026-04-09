package com.chiyuan.va.fake.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import black.android.app.ActivityThreadActivityClientRecordContext;
import black.android.app.BRActivityClient;
import black.android.app.BRActivityClientActivityClientControllerSingleton;
import black.android.app.BRActivityManagerNative;
import black.android.app.BRActivityThread;
import black.android.app.BRActivityThreadActivityClientRecord;
import black.android.app.BRActivityThreadCreateServiceData;
import black.android.app.BRActivityThreadH;
import black.android.app.BRIActivityManager;
import black.android.app.servertransaction.BRClientTransaction;
import black.android.app.servertransaction.BRLaunchActivityItem;
import black.android.app.servertransaction.LaunchActivityItemContext;
import black.android.os.BRHandler;
import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.BActivityThread;
import com.chiyuan.va.fake.hook.IInjectHook;
import com.chiyuan.va.fake.hook.NonProxyWrapper;
import com.chiyuan.va.proxy.ProxyManifest;
import com.chiyuan.va.proxy.record.ProxyActivityRecord;
import com.chiyuan.va.utils.MethodParameterUtils;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.utils.compat.BuildCompat;

public class HCallbackProxy implements IInjectHook, Handler.Callback {
    // ★ 中性 TAG，不暴露类名
    public static final String TAG = "H";

    private Handler.Callback mOtherCallback;
    private Handler.Callback mInjectedWrapper;   // 注入的匿名包装对象
    private AtomicBoolean mBeing = new AtomicBoolean(false);

    private Handler.Callback getHCallback() {
        return BRHandler.get(getH()).mCallback();
    }

    private Handler getH() {
        return BRActivityThread.get(ChiyuanVACore.mainThread()).mH();
    }

    @Override
    public void injectHook() {
        mOtherCallback = getHCallback();
        if (mOtherCallback != null && (mOtherCallback == this
                || mOtherCallback == mInjectedWrapper
                || mOtherCallback.getClass().getName().equals(this.getClass().getName()))) {
            mOtherCallback = null;
        }
        // ★ 注入匿名包装：getClass().getName() 不含 "HCallbackProxy"
        final HCallbackProxy delegate = this;
        Handler.Callback wrapper = new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                return delegate.handleMessage(msg);
            }
        };
        BRHandler.get(getH())._set_mCallback(wrapper);
        mInjectedWrapper = wrapper;
    }

    @Override
    public boolean isBadEnv() {
        Handler.Callback cb = getHCallback();
        if (cb == null) return false;
        if (cb == this || cb == mInjectedWrapper) return false;
        // 匿名包装的 enclosingClass 是 HCallbackProxy
        if (HCallbackProxy.class.equals(cb.getClass().getEnclosingClass())) return false;
        return true;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (!mBeing.getAndSet(true)) {
            try {
                if (BuildCompat.isPie()) {
                    if (msg.what == BRActivityThreadH.get().EXECUTE_TRANSACTION()) {
                        if (handleLaunchActivity(msg.obj)) {
                            getH().sendMessageAtFrontOfQueue(Message.obtain(msg));
                            return true;
                        }
                    }
                } else {
                    if (msg.what == BRActivityThreadH.get().LAUNCH_ACTIVITY()) {
                        if (handleLaunchActivity(msg.obj)) {
                            getH().sendMessageAtFrontOfQueue(Message.obtain(msg));
                            return true;
                        }
                    }
                }
                if (msg.what == BRActivityThreadH.get().CREATE_SERVICE()) {
                    return handleCreateService(msg.obj);
                }
                if (mOtherCallback != null) {
                    return mOtherCallback.handleMessage(msg);
                }
                return false;
            } finally {
                mBeing.set(false);
            }
        }
        return false;
    }

    private Object getLaunchActivityItem(Object clientTransaction) {
        List<Object> cbs = BRClientTransaction.get(clientTransaction).mActivityCallbacks();
        if (cbs == null) { Slog.e(TAG, "mActivityCallbacks null"); return null; }
        for (Object obj : cbs) {
            if (BRLaunchActivityItem.getRealClass().getName().equals(obj.getClass().getCanonicalName()))
                return obj;
        }
        return null;
    }

    private boolean handleLaunchActivity(Object client) {
        Object r = BuildCompat.isPie() ? getLaunchActivityItem(client) : client;
        if (r == null) return false;

        Intent intent;
        IBinder token;
        if (BuildCompat.isPie()) {
            intent = BRLaunchActivityItem.get(r).mIntent();
            token  = BRClientTransaction.get(client).mActivityToken();
        } else {
            ActivityThreadActivityClientRecordContext ctx = BRActivityThreadActivityClientRecord.get(r);
            intent = ctx.intent();
            token  = ctx.token();
        }
        if (intent == null) return false;

        ProxyActivityRecord stubRecord = ProxyActivityRecord.create(intent);
        ActivityInfo activityInfo = stubRecord.mActivityInfo;
        if (activityInfo != null) {
            if (BActivityThread.getAppConfig() == null) {
                ChiyuanVACore.getBActivityManager().restartProcess(
                        activityInfo.packageName, activityInfo.processName, stubRecord.mUserId);
                Intent launch = ChiyuanVACore.getBPackageManager()
                        .getLaunchIntentForPackage(activityInfo.packageName, stubRecord.mUserId);
                intent.setExtrasClassLoader(this.getClass().getClassLoader());
                ProxyActivityRecord.saveStub(intent, launch,
                        stubRecord.mActivityInfo, stubRecord.mActivityRecord, stubRecord.mUserId);
                if (BuildCompat.isPie()) {
                    LaunchActivityItemContext c = BRLaunchActivityItem.get(r);
                    c._set_mIntent(intent); c._set_mInfo(activityInfo);
                } else {
                    ActivityThreadActivityClientRecordContext c = BRActivityThreadActivityClientRecord.get(r);
                    c._set_intent(intent); c._set_activityInfo(activityInfo);
                }
                return true;
            }
            if (!BActivityThread.currentActivityThread().isInit()) {
                BActivityThread.currentActivityThread().bindApplication(
                        activityInfo.packageName, activityInfo.processName);
                return true;
            }
            int taskId = BRIActivityManager.get(BRActivityManagerNative.get().getDefault())
                    .getTaskForActivity(token, false);
            ChiyuanVACore.getBActivityManager().onActivityCreated(taskId, token, stubRecord.mActivityRecord);
            if (BuildCompat.isTiramisu()) {
                LaunchActivityItemContext c = BRLaunchActivityItem.get(r);
                c._set_mIntent(stubRecord.mTarget); c._set_mInfo(activityInfo);
            } else if (BuildCompat.isS()) {
                Object record = BRActivityThread.get(ChiyuanVACore.mainThread()).getLaunchingActivity(token);
                ActivityThreadActivityClientRecordContext c = BRActivityThreadActivityClientRecord.get(record);
                c._set_intent(stubRecord.mTarget);
                c._set_activityInfo(activityInfo);
                c._set_packageInfo(BActivityThread.currentActivityThread().getPackageInfo());
                checkActivityClient();
            } else if (BuildCompat.isPie()) {
                LaunchActivityItemContext c = BRLaunchActivityItem.get(r);
                c._set_mIntent(stubRecord.mTarget); c._set_mInfo(activityInfo);
            } else {
                ActivityThreadActivityClientRecordContext c = BRActivityThreadActivityClientRecord.get(r);
                c._set_intent(stubRecord.mTarget); c._set_activityInfo(activityInfo);
            }
        }
        return false;
    }

    private boolean handleCreateService(Object data) {
        if (BActivityThread.getAppConfig() != null) {
            String pkg = BActivityThread.getAppPackageName();
            assert pkg != null;
            ServiceInfo si = BRActivityThreadCreateServiceData.get(data).info();
            if (!si.name.equals(ProxyManifest.getProxyService(BActivityThread.getAppPid()))
                    && !si.name.equals(ProxyManifest.getProxyJobService(BActivityThread.getAppPid()))) {
                Slog.d(TAG, "handleCreateService: " + data);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(pkg, si.name));
                ChiyuanVACore.getBActivityManager().startService(intent, null, false, BActivityThread.getUserId());
                return true;
            }
        }
        return false;
    }

    private void checkActivityClient() {
        try {
            Object controller = BRActivityClient.get().getActivityClientController();
            // ★ 用反射调用 isProxyClass，避免字节码中出现直接的 Proxy 类引用
            boolean isRawProxy = isProxyClass(controller);
            if (!isRawProxy) {
                IActivityClientProxy proxy = new IActivityClientProxy(controller);
                proxy.onlyProxy(true);
                proxy.injectHook();
                // ★ 应用 NonProxyWrapper
                Object wrapped = NonProxyWrapper.wrap(
                        controller,
                        proxy.getProxyInvocation(),
                        MethodParameterUtils.getAllInterface(controller.getClass()));
                Object instance = BRActivityClient.get().getInstance();
                Object o = BRActivityClient.get(instance).INTERFACE_SINGLETON();
                BRActivityClientActivityClientControllerSingleton.get(o)._set_mKnownInstance(wrapped);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /** 用反射判断是否是裸 JDK Proxy，不在字节码中生成直接的 Proxy 常量引用 */
    private static boolean isProxyClass(Object obj) {
        if (obj == null) return false;
        try {
            Method m = Proxy.class.getMethod("isProxyClass", Class.class);
            return (Boolean) m.invoke(null, obj.getClass());
        } catch (Throwable t) {
            return obj instanceof Proxy;
        }
    }
}
