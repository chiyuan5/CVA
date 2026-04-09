package com.chiyuan.va.proxy;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Configuration;
import com.chiyuan.va.app.dispatcher.AppJobServiceDispatcher;

/** ★ 内部类改为 J00~J31 */
public class ProxyJobService extends JobService {
    public static final String TAG = "J";

    @Override public boolean onStartJob(JobParameters p) { return AppJobServiceDispatcher.get().onStartJob(p); }
    @Override public boolean onStopJob(JobParameters p)  { return AppJobServiceDispatcher.get().onStopJob(p); }
    @Override public int onStartCommand(Intent i, int f, int id) { return START_NOT_STICKY; }
    @Override public void onDestroy()                    { super.onDestroy(); AppJobServiceDispatcher.get().onDestroy(); }
    @Override public void onConfigurationChanged(Configuration c) { super.onConfigurationChanged(c); AppJobServiceDispatcher.get().onConfigurationChanged(c); }
    @Override public void onLowMemory()                  { super.onLowMemory(); AppJobServiceDispatcher.get().onLowMemory(); }
    @Override public void onTrimMemory(int l)             { super.onTrimMemory(l); AppJobServiceDispatcher.get().onTrimMemory(l); }

    public static class J00 extends ProxyJobService {}
    public static class J01 extends ProxyJobService {}
    public static class J02 extends ProxyJobService {}
    public static class J03 extends ProxyJobService {}
    public static class J04 extends ProxyJobService {}
    public static class J05 extends ProxyJobService {}
    public static class J06 extends ProxyJobService {}
    public static class J07 extends ProxyJobService {}
    public static class J08 extends ProxyJobService {}
    public static class J09 extends ProxyJobService {}
    public static class J0a extends ProxyJobService {}
    public static class J0b extends ProxyJobService {}
    public static class J0c extends ProxyJobService {}
    public static class J0d extends ProxyJobService {}
    public static class J0e extends ProxyJobService {}
    public static class J0f extends ProxyJobService {}
    public static class J10 extends ProxyJobService {}
    public static class J11 extends ProxyJobService {}
    public static class J12 extends ProxyJobService {}
    public static class J13 extends ProxyJobService {}
    public static class J14 extends ProxyJobService {}
    public static class J15 extends ProxyJobService {}
    public static class J16 extends ProxyJobService {}
    public static class J17 extends ProxyJobService {}
    public static class J18 extends ProxyJobService {}
    public static class J19 extends ProxyJobService {}
    public static class J1a extends ProxyJobService {}
    public static class J1b extends ProxyJobService {}
    public static class J1c extends ProxyJobService {}
    public static class J1d extends ProxyJobService {}
    public static class J1e extends ProxyJobService {}
    public static class J1f extends ProxyJobService {}
    public static class J20 extends ProxyJobService {}
    public static class J21 extends ProxyJobService {}
    public static class J22 extends ProxyJobService {}
    public static class J23 extends ProxyJobService {}
    public static class J24 extends ProxyJobService {}
    public static class J25 extends ProxyJobService {}
    public static class J26 extends ProxyJobService {}
    public static class J27 extends ProxyJobService {}
    public static class J28 extends ProxyJobService {}
    public static class J29 extends ProxyJobService {}
    public static class J2a extends ProxyJobService {}
    public static class J2b extends ProxyJobService {}
    public static class J2c extends ProxyJobService {}
    public static class J2d extends ProxyJobService {}
    public static class J2e extends ProxyJobService {}
    public static class J2f extends ProxyJobService {}
    public static class J30 extends ProxyJobService {}
    public static class J31 extends ProxyJobService {}
}
