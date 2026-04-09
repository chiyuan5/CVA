package com.chiyuan.va.proxy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.chiyuan.va.utils.Slog;

public class ProxyVpnService extends VpnService {
    private static final String TAG     = "VS";
    private static final int    NID     = 1001;
    // ★ 渠道 ID/名称不含框架关键词
    private static final String CH_ID   = "va_net";
    private static final String CH_NAME = "Network";

    private ParcelFileDescriptor mVpnInterface = null;
    private boolean              mIsEstablished = false;
    private Thread               mNetworkThread = null;

    @Override public void onCreate()   { super.onCreate(); createChannel(); }
    @Override public void onRevoke()   { super.onRevoke(); stopVpn(); }
    @Override public void onTrimMemory(int l) { super.onTrimMemory(l); }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(NID, buildNotification(),
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(NID, buildNotification());
            }
            new Thread(this::establishVpn, "net").start();
        } catch (Exception e) {
            Slog.e(TAG, "onStartCommand: " + e.getMessage(), e);
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    @Override public void onDestroy() { super.onDestroy(); stopVpn(); }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CH_ID, CH_NAME, NotificationManager.IMPORTANCE_LOW);
            ch.setShowBadge(false);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(ch);
        }
    }

    private Notification buildNotification() {
        Notification.Builder b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CH_ID)
                : new Notification.Builder(this);
        return b.setContentTitle("VPN")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_LOW)
                .build();
    }

    protected void establishVpn() {
        try {
            Builder b = new Builder();
            b.setSession("vpn");
            b.addAddress("10.0.0.2", 32);
            b.addRoute("0.0.0.0", 0);
            b.addDnsServer("8.8.8.8");
            b.addAllowedApplication(getPackageName());
            mVpnInterface = b.establish();
            if (mVpnInterface != null) {
                mIsEstablished = true;
                startNetworkHandling();
            }
        } catch (Exception e) {
            Slog.e(TAG, "establishVpn: " + e.getMessage());
            mIsEstablished = false;
        }
    }

    private void startNetworkHandling() {
        if (mNetworkThread != null && mNetworkThread.isAlive()) mNetworkThread.interrupt();
        mNetworkThread = new Thread(() -> {
            try {
                while (mIsEstablished && mVpnInterface != null && !Thread.interrupted()) {
                    Thread.sleep(10000);
                    if (mVpnInterface != null) {
                        try { mVpnInterface.getFd(); }
                        catch (Exception e) { reestablishVpn(); break; }
                    }
                }
            } catch (InterruptedException ignored) {
            } catch (Exception e) { Slog.e(TAG, "net monitor: " + e.getMessage()); }
        }, "nm");
        mNetworkThread.start();
    }

    private void reestablishVpn() {
        try { stopVpn(); Thread.sleep(1000); establishVpn(); }
        catch (Exception e) { Slog.e(TAG, "reestablish: " + e.getMessage()); }
    }

    private void stopVpn() {
        mIsEstablished = false;
        if (mNetworkThread != null) { mNetworkThread.interrupt(); mNetworkThread = null; }
        if (mVpnInterface != null) {
            try { mVpnInterface.close(); } catch (Exception ignored) {}
            mVpnInterface = null;
        }
    }

    public boolean isEstablished() { return mIsEstablished && mVpnInterface != null; }
    public ParcelFileDescriptor getVpnInterface() { return mVpnInterface; }
}
