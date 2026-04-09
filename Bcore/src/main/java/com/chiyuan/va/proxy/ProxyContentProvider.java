package com.chiyuan.va.proxy;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.entity.AppConfig;
import com.chiyuan.va.utils.compat.BundleCompat;

/** ★ 内部类改为 C00~C31 */
public class ProxyContentProvider extends ContentProvider {
    @Override public boolean onCreate() { return false; }

    @Nullable @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        if (method.equals("_Black_|_init_process_")) {
            assert extras != null;
            extras.setClassLoader(AppConfig.class.getClassLoader());
            AppConfig appConfig = extras.getParcelable(AppConfig.KEY);
            ChiyuanVACore.currentActivityThread().initProcess(appConfig);
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "_Black_|_client_", ChiyuanVACore.currentActivityThread());
            return bundle;
        }
        return super.call(method, arg, extras);
    }

    @Nullable @Override public Cursor query(@NonNull Uri u, @Nullable String[] p, @Nullable String s, @Nullable String[] sa, @Nullable String so) { return null; }
    @Nullable @Override public String getType(@NonNull Uri u) { return null; }
    @Nullable @Override public Uri insert(@NonNull Uri u, @Nullable ContentValues v) { return null; }
    @Override public int delete(@NonNull Uri u, @Nullable String s, @Nullable String[] sa) { return 0; }
    @Override public int update(@NonNull Uri u, @Nullable ContentValues v, @Nullable String s, @Nullable String[] sa) { return 0; }

    public static class C00 extends ProxyContentProvider {}
    public static class C01 extends ProxyContentProvider {}
    public static class C02 extends ProxyContentProvider {}
    public static class C03 extends ProxyContentProvider {}
    public static class C04 extends ProxyContentProvider {}
    public static class C05 extends ProxyContentProvider {}
    public static class C06 extends ProxyContentProvider {}
    public static class C07 extends ProxyContentProvider {}
    public static class C08 extends ProxyContentProvider {}
    public static class C09 extends ProxyContentProvider {}
    public static class C0a extends ProxyContentProvider {}
    public static class C0b extends ProxyContentProvider {}
    public static class C0c extends ProxyContentProvider {}
    public static class C0d extends ProxyContentProvider {}
    public static class C0e extends ProxyContentProvider {}
    public static class C0f extends ProxyContentProvider {}
    public static class C10 extends ProxyContentProvider {}
    public static class C11 extends ProxyContentProvider {}
    public static class C12 extends ProxyContentProvider {}
    public static class C13 extends ProxyContentProvider {}
    public static class C14 extends ProxyContentProvider {}
    public static class C15 extends ProxyContentProvider {}
    public static class C16 extends ProxyContentProvider {}
    public static class C17 extends ProxyContentProvider {}
    public static class C18 extends ProxyContentProvider {}
    public static class C19 extends ProxyContentProvider {}
    public static class C1a extends ProxyContentProvider {}
    public static class C1b extends ProxyContentProvider {}
    public static class C1c extends ProxyContentProvider {}
    public static class C1d extends ProxyContentProvider {}
    public static class C1e extends ProxyContentProvider {}
    public static class C1f extends ProxyContentProvider {}
    public static class C20 extends ProxyContentProvider {}
    public static class C21 extends ProxyContentProvider {}
    public static class C22 extends ProxyContentProvider {}
    public static class C23 extends ProxyContentProvider {}
    public static class C24 extends ProxyContentProvider {}
    public static class C25 extends ProxyContentProvider {}
    public static class C26 extends ProxyContentProvider {}
    public static class C27 extends ProxyContentProvider {}
    public static class C28 extends ProxyContentProvider {}
    public static class C29 extends ProxyContentProvider {}
    public static class C2a extends ProxyContentProvider {}
    public static class C2b extends ProxyContentProvider {}
    public static class C2c extends ProxyContentProvider {}
    public static class C2d extends ProxyContentProvider {}
    public static class C2e extends ProxyContentProvider {}
    public static class C2f extends ProxyContentProvider {}
    public static class C30 extends ProxyContentProvider {}
    public static class C31 extends ProxyContentProvider {}
}
