package com.chiyuan.va.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.fake.hook.HookManager;
import com.chiyuan.va.fake.service.HCallbackProxy;
import com.chiyuan.va.proxy.record.ProxyActivityRecord;

/** ★ 内部类从 P0~P49 改为 A00~A31 (hex)，去掉语义化 "P" 前缀 */
public class ProxyActivity extends Activity {
    public static final String TAG = "A";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        // ★ post 延迟执行，不污染 onCreate 调用栈帧
        getWindow().getDecorView().post(() -> HookManager.get().checkEnv(HCallbackProxy.class));
        ProxyActivityRecord record = ProxyActivityRecord.create(getIntent());
        if (record.mTarget != null) {
            record.mTarget.setExtrasClassLoader(ChiyuanVACore.getApplication().getClassLoader());
            startActivity(record.mTarget);
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
