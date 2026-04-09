package com.chiyuan.va.proxy;

import java.util.Locale;
import com.chiyuan.va.ChiyuanVACore;

public class ProxyManifest {
    public static final int FREE_COUNT = 50;

    // 检测：是否是我们的 authority
    public static boolean isProxy(String msg) {
        return getBindProvider().equals(msg)
                || (msg.startsWith(ChiyuanVACore.getHostPkg()) && msg.contains(".c0"));
    }

    // 旧: <pkg>.chiyuanva.SystemCallProvider  →  <pkg>.sc.p
    public static String getBindProvider() {
        return ChiyuanVACore.getHostPkg() + ".sc.p";
    }

    // 旧: <pkg>.proxy_content_provider_%d  →  <pkg>.c0%02x
    public static String getProxyAuthorities(int index) {
        return String.format(Locale.US, "%s.c0%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // 旧: com.chiyuan.va.proxy.ProxyPendingActivity$P%d  →  <pkg>.stub.E%02x
    public static String getProxyPendingActivity(int index) {
        return String.format(Locale.US, "%s.stub.E%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // 旧: com.chiyuan.va.proxy.ProxyActivity$P%d  →  <pkg>.stub.A%02x
    public static String getProxyActivity(int index) {
        return String.format(Locale.US, "%s.stub.A%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // 旧: com.chiyuan.va.proxy.TransparentProxyActivity$P%d  →  <pkg>.stub.T%02x
    public static String TransparentProxyActivity(int index) {
        return String.format(Locale.US, "%s.stub.T%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // 旧: com.chiyuan.va.proxy.ProxyService$P%d  →  <pkg>.stub.S%02x
    public static String getProxyService(int index) {
        return String.format(Locale.US, "%s.stub.S%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // 旧: com.chiyuan.va.proxy.ProxyJobService$P%d  →  <pkg>.stub.J%02x
    public static String getProxyJobService(int index) {
        return String.format(Locale.US, "%s.stub.J%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // 旧: <pkg>.chiyuanva.FileProvider  →  <pkg>.sc.f
    public static String getProxyFileProvider() {
        return ChiyuanVACore.getHostPkg() + ".sc.f";
    }

    // 旧: <pkg>.stub_receiver  →  <pkg>.r
    public static String getProxyReceiver() {
        return ChiyuanVACore.getHostPkg() + ".r";
    }

    // 旧: <pkg>:p%d  →  <pkg>:s%02x
    public static String getProcessName(int bPid) {
        return String.format(Locale.US, "%s:s%02x", ChiyuanVACore.getHostPkg(), bPid);
    }

    // taskAffinity: 旧: com.chiyuan.va.task_affinity  →  <pkg>.t
    public static String getTaskAffinity() {
        return ChiyuanVACore.getHostPkg() + ".t";
    }
}
