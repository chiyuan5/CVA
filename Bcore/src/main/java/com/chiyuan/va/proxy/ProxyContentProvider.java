package com.chiyuan.va.proxy;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.BActivityThread;
import com.chiyuan.va.entity.AppConfig;
import com.chiyuan.va.utils.compat.BundleCompat;


public class ProxyContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
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

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static class angz extends ProxyContentProvider {

    }

    public static class bjwd extends ProxyContentProvider {

    }

    public static class czsm extends ProxyContentProvider {

    }

    public static class dumt extends ProxyContentProvider {

    }

    public static class eoiw extends ProxyContentProvider {

    }

    public static class fnjz extends ProxyContentProvider {

    }

    public static class geil extends ProxyContentProvider {

    }

    public static class hqhf extends ProxyContentProvider {

    }

    public static class ixzy extends ProxyContentProvider {

    }

    public static class jtjh extends ProxyContentProvider {

    }

    public static class klif extends ProxyContentProvider {

    }

    public static class ljmc extends ProxyContentProvider {

    }

    public static class mxdi extends ProxyContentProvider {

    }

    public static class nbkn extends ProxyContentProvider {

    }

    public static class oghj extends ProxyContentProvider {

    }

    public static class paov extends ProxyContentProvider {

    }

    public static class qnuw extends ProxyContentProvider {

    }

    public static class rwuc extends ProxyContentProvider {

    }

    public static class srvo extends ProxyContentProvider {

    }

    public static class trej extends ProxyContentProvider {

    }

    public static class uvcp extends ProxyContentProvider {

    }

    public static class vzza extends ProxyContentProvider {

    }

    public static class woli extends ProxyContentProvider {

    }

    public static class xtrm extends ProxyContentProvider {

    }

    public static class ycso extends ProxyContentProvider {

    }

    public static class ztyg extends ProxyContentProvider {

    }

    public static class ajwq extends ProxyContentProvider {

    }

    public static class bruk extends ProxyContentProvider {

    }

    public static class cezy extends ProxyContentProvider {

    }

    public static class djyy extends ProxyContentProvider {

    }

    public static class esof extends ProxyContentProvider {

    }

    public static class fqhg extends ProxyContentProvider {

    }

    public static class gctz extends ProxyContentProvider {

    }

    public static class hppe extends ProxyContentProvider {

    }

    public static class idpd extends ProxyContentProvider {

    }

    public static class jtcm extends ProxyContentProvider {

    }

    public static class kuvw extends ProxyContentProvider {

    }

    public static class lgms extends ProxyContentProvider {

    }

    public static class mykx extends ProxyContentProvider {

    }

    public static class nvfq extends ProxyContentProvider {

    }

    public static class oyls extends ProxyContentProvider {

    }

    public static class prir extends ProxyContentProvider {

    }

    public static class qysp extends ProxyContentProvider {

    }

    public static class rgwm extends ProxyContentProvider {

    }

    public static class sesj extends ProxyContentProvider {

    }

    public static class tysh extends ProxyContentProvider {

    }

    public static class ucoi extends ProxyContentProvider {

    }

    public static class vllq extends ProxyContentProvider {

    }

    public static class wnkn extends ProxyContentProvider {

    }

    public static class xdnu extends ProxyContentProvider {

    }
}
