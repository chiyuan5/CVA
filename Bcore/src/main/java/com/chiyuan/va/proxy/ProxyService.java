package com.chiyuan.va.proxy;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.dispatcher.AppServiceDispatcher;
import com.chiyuan.va.utils.compat.BuildCompat;


public class ProxyService extends Service {
    public static final String TAG = "StubService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return AppServiceDispatcher.get().onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppServiceDispatcher.get().onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppServiceDispatcher.get().onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppServiceDispatcher.get().onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AppServiceDispatcher.get().onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        AppServiceDispatcher.get().onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        AppServiceDispatcher.get().onUnbind(intent);
        return false;
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getPackageName() + ".core_service")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        if (BuildCompat.isOreo()) {
            startForeground(ChiyuanVACore.getHostPkg().hashCode(), builder.build());
        }
    }

    public static class abzc extends ProxyService {

    }

    public static class bhqs extends ProxyService {

    }

    public static class cqum extends ProxyService {

    }

    public static class dvcd extends ProxyService {

    }

    public static class eche extends ProxyService {

    }

    public static class fopc extends ProxyService {

    }

    public static class gqlx extends ProxyService {

    }

    public static class hkxq extends ProxyService {

    }

    public static class ircb extends ProxyService {

    }

    public static class jlfl extends ProxyService {

    }

    public static class ktkk extends ProxyService {

    }

    public static class lvrg extends ProxyService {

    }

    public static class mtui extends ProxyService {

    }

    public static class nmii extends ProxyService {

    }

    public static class opyk extends ProxyService {

    }

    public static class pgps extends ProxyService {

    }

    public static class qsbo extends ProxyService {

    }

    public static class rlxa extends ProxyService {

    }

    public static class soqy extends ProxyService {

    }

    public static class twjg extends ProxyService {

    }

    public static class uayf extends ProxyService {

    }

    public static class vpch extends ProxyService {

    }

    public static class wovm extends ProxyService {

    }

    public static class xsld extends ProxyService {

    }

    public static class yite extends ProxyService {

    }

    public static class zuht extends ProxyService {

    }

    public static class acrw extends ProxyService {

    }

    public static class bwmk extends ProxyService {

    }

    public static class cnyy extends ProxyService {

    }

    public static class dgmo extends ProxyService {

    }

    public static class eumv extends ProxyService {

    }

    public static class fjqp extends ProxyService {

    }

    public static class gsuo extends ProxyService {

    }

    public static class hzyl extends ProxyService {

    }

    public static class ijhi extends ProxyService {

    }

    public static class jkqd extends ProxyService {

    }

    public static class kxcw extends ProxyService {

    }

    public static class ldhm extends ProxyService {

    }

    public static class mbto extends ProxyService {

    }

    public static class njpb extends ProxyService {

    }

    public static class otuc extends ProxyService {

    }

    public static class pjot extends ProxyService {

    }

    public static class qpie extends ProxyService {

    }

    public static class rivv extends ProxyService {

    }

    public static class suez extends ProxyService {

    }

    public static class tauy extends ProxyService {

    }

    public static class uopy extends ProxyService {

    }

    public static class vgnz extends ProxyService {

    }

    public static class wyxq extends ProxyService {

    }

    public static class xahv extends ProxyService {

    }
}
