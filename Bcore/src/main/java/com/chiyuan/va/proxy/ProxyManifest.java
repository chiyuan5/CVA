package com.chiyuan.va.proxy;

import java.util.Locale;

import com.chiyuan.va.ChiyuanVACore;


public class ProxyManifest {
    public static final int FREE_COUNT = 50;

    // Obfuscated naming: "ext_data_provider_" instead of "ext_data_provider_"
    // "ExtActivity$Pxx" instead of "ProxyActivity$Pxx" etc.
    // Note: these must match AndroidManifest.xml declarations

    public static boolean isProxy(String msg) {
        return getBindProvider().equals(msg) || msg.contains("ext_data_provider_");
    }

    public static String getBindProvider() {
        return ChiyuanVACore.getHostPkg() + ".core.SystemCallProvider";
    }

    public static String getProxyAuthorities(int index) {
        return String.format(Locale.CHINA, "%s.ext_data_provider_%d", ChiyuanVACore.getHostPkg(), index);
    }

    public static String getProxyPendingActivity(int index) {
        return String.format(Locale.CHINA, "com.chiyuan.va.proxy.ProxyPendingActivity$P%d", index);
    }

    public static String getProxyActivity(int index) {
        return String.format(Locale.CHINA, "com.chiyuan.va.proxy.ProxyActivity$P%d", index);
    }

    public static String TransparentProxyActivity(int index) {
        return String.format(Locale.CHINA, "com.chiyuan.va.proxy.TransparentProxyActivity$P%d", index);
    }

    public static String getProxyService(int index) {
        return String.format(Locale.CHINA, "com.chiyuan.va.proxy.ProxyService$P%d", index);
    }

    public static String getProxyJobService(int index) {
        return String.format(Locale.CHINA, "com.chiyuan.va.proxy.ProxyJobService$P%d", index);
    }

    public static String getProxyFileProvider() {
        return ChiyuanVACore.getHostPkg() + ".core.FileProvider";
    }

    public static String getProxyReceiver() {
        return ChiyuanVACore.getHostPkg() + ".ext_receiver";
    }

    public static String getProcessName(int bPid) {
        // Use "w" prefix instead of "p" to look less like "process slot"
        return ChiyuanVACore.getHostPkg() + ":w" + bPid;
    }
}
