package com.chiyuan.va.proxy;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.res.Configuration;

import com.chiyuan.va.app.dispatcher.AppJobServiceDispatcher;


public class ProxyJobService extends JobService {
    public static final String TAG = "StubJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        return AppJobServiceDispatcher.get().onStartJob(params);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return AppJobServiceDispatcher.get().onStopJob(params);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppJobServiceDispatcher.get().onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppJobServiceDispatcher.get().onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        AppJobServiceDispatcher.get().onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        AppJobServiceDispatcher.get().onTrimMemory(level);
    }

    public static class jywz extends ProxyJobService {

    }

    public static class kgcv extends ProxyJobService {

    }

    public static class lwff extends ProxyJobService {

    }

    public static class mfdy extends ProxyJobService {

    }

    public static class nnwb extends ProxyJobService {

    }

    public static class jobw extends ProxyJobService {

    }

    public static class kkdq extends ProxyJobService {

    }

    public static class legl extends ProxyJobService {

    }

    public static class mhsv extends ProxyJobService {

    }

    public static class nozz extends ProxyJobService {

    }

    public static class jxwu extends ProxyJobService {

    }

    public static class knii extends ProxyJobService {

    }

    public static class lvka extends ProxyJobService {

    }

    public static class mfyf extends ProxyJobService {

    }

    public static class nyoc extends ProxyJobService {

    }

    public static class jivb extends ProxyJobService {

    }

    public static class kypf extends ProxyJobService {

    }

    public static class lfgd extends ProxyJobService {

    }

    public static class mkbq extends ProxyJobService {

    }

    public static class neqp extends ProxyJobService {

    }

    public static class jpgk extends ProxyJobService {

    }

    public static class kbrh extends ProxyJobService {

    }

    public static class lwjp extends ProxyJobService {

    }

    public static class mpje extends ProxyJobService {

    }

    public static class nsjj extends ProxyJobService {

    }

    public static class jjlt extends ProxyJobService {

    }

    public static class kmgz extends ProxyJobService {

    }

    public static class ltxy extends ProxyJobService {

    }

    public static class mugt extends ProxyJobService {

    }

    public static class nbnc extends ProxyJobService {

    }

    public static class jeju extends ProxyJobService {

    }

    public static class klvu extends ProxyJobService {

    }

    public static class lrvw extends ProxyJobService {

    }

    public static class mqjo extends ProxyJobService {

    }

    public static class nguf extends ProxyJobService {

    }

    public static class jcdn extends ProxyJobService {

    }

    public static class kfob extends ProxyJobService {

    }

    public static class lyqs extends ProxyJobService {

    }

    public static class mvde extends ProxyJobService {

    }

    public static class nfln extends ProxyJobService {

    }

    public static class jfos extends ProxyJobService {

    }

    public static class kmgg extends ProxyJobService {

    }

    public static class ltka extends ProxyJobService {

    }

    public static class mjrg extends ProxyJobService {

    }

    public static class nimx extends ProxyJobService {

    }

    public static class jjik extends ProxyJobService {

    }

    public static class kpyb extends ProxyJobService {

    }

    public static class lkfd extends ProxyJobService {

    }

    public static class mdac extends ProxyJobService {

    }

    public static class nkcg extends ProxyJobService {

    }
}
