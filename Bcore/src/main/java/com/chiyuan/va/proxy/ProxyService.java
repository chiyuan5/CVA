package com.chiyuan.va.proxy;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.chiyuan.va.app.dispatcher.AppServiceDispatcher;

/** ★ 内部类改为 S00~S31 */
public class ProxyService extends Service {
    public static final String TAG = "S";

    @Nullable @Override public IBinder onBind(Intent i)  { return AppServiceDispatcher.get().onBind(i); }
    @Override public int onStartCommand(Intent i, int f, int id) { AppServiceDispatcher.get().onStartCommand(i,f,id); return START_NOT_STICKY; }
    @Override public void onDestroy()                    { super.onDestroy(); AppServiceDispatcher.get().onDestroy(); }
    @Override public void onConfigurationChanged(Configuration c) { super.onConfigurationChanged(c); AppServiceDispatcher.get().onConfigurationChanged(c); }
    @Override public void onLowMemory()                  { super.onLowMemory(); AppServiceDispatcher.get().onLowMemory(); }
    @Override public void onTrimMemory(int l)             { super.onTrimMemory(l); AppServiceDispatcher.get().onTrimMemory(l); }
    @Override public boolean onUnbind(Intent i)          { AppServiceDispatcher.get().onUnbind(i); return false; }

    public static class S00 extends ProxyService {}
    public static class S01 extends ProxyService {}
    public static class S02 extends ProxyService {}
    public static class S03 extends ProxyService {}
    public static class S04 extends ProxyService {}
    public static class S05 extends ProxyService {}
    public static class S06 extends ProxyService {}
    public static class S07 extends ProxyService {}
    public static class S08 extends ProxyService {}
    public static class S09 extends ProxyService {}
    public static class S0a extends ProxyService {}
    public static class S0b extends ProxyService {}
    public static class S0c extends ProxyService {}
    public static class S0d extends ProxyService {}
    public static class S0e extends ProxyService {}
    public static class S0f extends ProxyService {}
    public static class S10 extends ProxyService {}
    public static class S11 extends ProxyService {}
    public static class S12 extends ProxyService {}
    public static class S13 extends ProxyService {}
    public static class S14 extends ProxyService {}
    public static class S15 extends ProxyService {}
    public static class S16 extends ProxyService {}
    public static class S17 extends ProxyService {}
    public static class S18 extends ProxyService {}
    public static class S19 extends ProxyService {}
    public static class S1a extends ProxyService {}
    public static class S1b extends ProxyService {}
    public static class S1c extends ProxyService {}
    public static class S1d extends ProxyService {}
    public static class S1e extends ProxyService {}
    public static class S1f extends ProxyService {}
    public static class S20 extends ProxyService {}
    public static class S21 extends ProxyService {}
    public static class S22 extends ProxyService {}
    public static class S23 extends ProxyService {}
    public static class S24 extends ProxyService {}
    public static class S25 extends ProxyService {}
    public static class S26 extends ProxyService {}
    public static class S27 extends ProxyService {}
    public static class S28 extends ProxyService {}
    public static class S29 extends ProxyService {}
    public static class S2a extends ProxyService {}
    public static class S2b extends ProxyService {}
    public static class S2c extends ProxyService {}
    public static class S2d extends ProxyService {}
    public static class S2e extends ProxyService {}
    public static class S2f extends ProxyService {}
    public static class S30 extends ProxyService {}
    public static class S31 extends ProxyService {}
}
