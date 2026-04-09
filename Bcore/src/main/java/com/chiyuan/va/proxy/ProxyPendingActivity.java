package com.chiyuan.va.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.proxy.record.ProxyPendingRecord;
import com.chiyuan.va.utils.Slog;

/**
 * PendingIntent 对应的 Stub Activity 也不能直接在宿主拉起真实目标，
 * 必须回到虚拟 AMS 里处理。
 */
public class ProxyPendingActivity extends Activity {

    public static final String TAG = "E";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProxyPendingRecord r;
        try {
            r = ProxyPendingRecord.create(getIntent());
        } catch (Throwable e) {
            Slog.e(TAG, "Failed to parse ProxyPendingRecord", e);
            finish();
            return;
        }

        Slog.d(TAG, "pending: " + r);

        if (r == null || r.mTarget == null) {
            finish();
            return;
        }

        try {
            Intent target = new Intent(r.mTarget);
            target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            target.setExtrasClassLoader(ChiyuanVACore.getApplication().getClassLoader());

            Slog.d(TAG, "Redirect pending launch to virtual AMS: " + target.getComponent());

            // 关键修复：不能直接 startActivity(target)
            ChiyuanVACore.getBActivityManager().startActivity(target, 0);
        } catch (Throwable e) {
            Slog.e(TAG, "Failed to redirect pending launch to virtual AMS", e);
        } finally {
            finish();
        }
    }

    public static class E00 extends ProxyPendingActivity {}
    public static class E01 extends ProxyPendingActivity {}
    public static class E02 extends ProxyPendingActivity {}
    public static class E03 extends ProxyPendingActivity {}
    public static class E04 extends ProxyPendingActivity {}
    public static class E05 extends ProxyPendingActivity {}
    public static class E06 extends ProxyPendingActivity {}
    public static class E07 extends ProxyPendingActivity {}
    public static class E08 extends ProxyPendingActivity {}
    public static class E09 extends ProxyPendingActivity {}
    public static class E0a extends ProxyPendingActivity {}
    public static class E0b extends ProxyPendingActivity {}
    public static class E0c extends ProxyPendingActivity {}
    public static class E0d extends ProxyPendingActivity {}
    public static class E0e extends ProxyPendingActivity {}
    public static class E0f extends ProxyPendingActivity {}
    public static class E10 extends ProxyPendingActivity {}
    public static class E11 extends ProxyPendingActivity {}
    public static class E12 extends ProxyPendingActivity {}
    public static class E13 extends ProxyPendingActivity {}
    public static class E14 extends ProxyPendingActivity {}
    public static class E15 extends ProxyPendingActivity {}
    public static class E16 extends ProxyPendingActivity {}
    public static class E17 extends ProxyPendingActivity {}
    public static class E18 extends ProxyPendingActivity {}
    public static class E19 extends ProxyPendingActivity {}
    public static class E1a extends ProxyPendingActivity {}
    public static class E1b extends ProxyPendingActivity {}
    public static class E1c extends ProxyPendingActivity {}
    public static class E1d extends ProxyPendingActivity {}
    public static class E1e extends ProxyPendingActivity {}
    public static class E1f extends ProxyPendingActivity {}
    public static class E20 extends ProxyPendingActivity {}
    public static class E21 extends ProxyPendingActivity {}
    public static class E22 extends ProxyPendingActivity {}
    public static class E23 extends ProxyPendingActivity {}
    public static class E24 extends ProxyPendingActivity {}
    public static class E25 extends ProxyPendingActivity {}
    public static class E26 extends ProxyPendingActivity {}
    public static class E27 extends ProxyPendingActivity {}
    public static class E28 extends ProxyPendingActivity {}
    public static class E29 extends ProxyPendingActivity {}
    public static class E2a extends ProxyPendingActivity {}
    public static class E2b extends ProxyPendingActivity {}
    public static class E2c extends ProxyPendingActivity {}
    public static class E2d extends ProxyPendingActivity {}
    public static class E2e extends ProxyPendingActivity {}
    public static class E2f extends ProxyPendingActivity {}
    public static class E30 extends ProxyPendingActivity {}
    public static class E31 extends ProxyPendingActivity {}
}