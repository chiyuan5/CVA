package com.chiyuan.va.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.fake.hook.HookManager;
import com.chiyuan.va.fake.service.HCallbackProxy;
import com.chiyuan.va.proxy.record.ProxyActivityRecord;
import com.chiyuan.va.utils.Slog;

/**
 * Stub Activity 只能把启动请求重新路由回虚拟 AMS，
 * 绝不能在宿主进程里直接 startActivity(真实目标 Intent)，
 * 否则系统会尝试启动分身应用“本体”的 Activity。
 */
public class ProxyActivity extends Activity {

    public static final String TAG = "A";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 尽早确保回调环境就绪，避免第二个 Activity 切换时环境失效
        try {
            HookManager.get().checkEnv(HCallbackProxy.class);
        } catch (Throwable e) {
            Slog.w(TAG, "checkEnv(HCallbackProxy) failed: " + e.getMessage());
        }

        final ProxyActivityRecord record;
        try {
            record = ProxyActivityRecord.create(getIntent());
        } catch (Throwable e) {
            Slog.e(TAG, "Failed to parse ProxyActivityRecord", e);
            finish();
            return;
        }

        if (record == null || record.mTarget == null) {
            Slog.w(TAG, "Stub target intent is null, finish.");
            finish();
            return;
        }

        try {
            Intent target = new Intent(record.mTarget);
            target.setExtrasClassLoader(ChiyuanVACore.getApplication().getClassLoader());

            Slog.d(TAG, "Redirect stub launch to virtual AMS: " + target.getComponent()
                    + ", userId=" + record.mUserId);

            // 关键修复：
            // 不在宿主里直接 startActivity(target)
            // 必须重新交给虚拟 ActivityManager 处理
            ChiyuanVACore.getBActivityManager().startActivity(target, record.mUserId);
        } catch (Throwable e) {
            Slog.e(TAG, "Failed to redirect stub launch to virtual AMS", e);
        } finally {
            finish();
        }
    }

    public static class A00 extends ProxyActivity {}
    public static class A01 extends ProxyActivity {}
    public static class A02 extends ProxyActivity {}
    public static class A03 extends ProxyActivity {}
    public static class A04 extends ProxyActivity {}
    public static class A05 extends ProxyActivity {}
    public static class A06 extends ProxyActivity {}
    public static class A07 extends ProxyActivity {}
    public static class A08 extends ProxyActivity {}
    public static class A09 extends ProxyActivity {}
    public static class A0a extends ProxyActivity {}
    public static class A0b extends ProxyActivity {}
    public static class A0c extends ProxyActivity {}
    public static class A0d extends ProxyActivity {}
    public static class A0e extends ProxyActivity {}
    public static class A0f extends ProxyActivity {}
    public static class A10 extends ProxyActivity {}
    public static class A11 extends ProxyActivity {}
    public static class A12 extends ProxyActivity {}
    public static class A13 extends ProxyActivity {}
    public static class A14 extends ProxyActivity {}
    public static class A15 extends ProxyActivity {}
    public static class A16 extends ProxyActivity {}
    public static class A17 extends ProxyActivity {}
    public static class A18 extends ProxyActivity {}
    public static class A19 extends ProxyActivity {}
    public static class A1a extends ProxyActivity {}
    public static class A1b extends ProxyActivity {}
    public static class A1c extends ProxyActivity {}
    public static class A1d extends ProxyActivity {}
    public static class A1e extends ProxyActivity {}
    public static class A1f extends ProxyActivity {}
    public static class A20 extends ProxyActivity {}
    public static class A21 extends ProxyActivity {}
    public static class A22 extends ProxyActivity {}
    public static class A23 extends ProxyActivity {}
    public static class A24 extends ProxyActivity {}
    public static class A25 extends ProxyActivity {}
    public static class A26 extends ProxyActivity {}
    public static class A27 extends ProxyActivity {}
    public static class A28 extends ProxyActivity {}
    public static class A29 extends ProxyActivity {}
    public static class A2a extends ProxyActivity {}
    public static class A2b extends ProxyActivity {}
    public static class A2c extends ProxyActivity {}
    public static class A2d extends ProxyActivity {}
    public static class A2e extends ProxyActivity {}
    public static class A2f extends ProxyActivity {}
    public static class A30 extends ProxyActivity {}
    public static class A31 extends ProxyActivity {}
}