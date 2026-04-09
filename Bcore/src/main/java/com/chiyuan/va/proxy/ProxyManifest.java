package com.chiyuan.va.proxy;

import java.util.Locale;
import com.chiyuan.va.ChiyuanVACore;

/**
 * ProxyManifest — 壳组件命名中枢
 *
 * 反检测命名规则说明：
 *
 * ① 组件 FQCN（android:name / 运行时 ComponentName）
 *    必须与 AndroidManifest.xml 中 android:name 完全一致，不可随意改名。
 *    内部类分隔符 $P → $A/$S/$T/$E/$J/$C，去掉语义化 "P" 前缀即可，
 *    类名前缀 "com.chiyuan.va.proxy." 保持不变（系统需要通过完整类名实例化组件）。
 *
 * ② ContentProvider authority、进程名、FileProvider authority 等字符串
 *    不需要与类名一致，可以改为中性短名消除特征。
 */
public class ProxyManifest {
    public static final int FREE_COUNT = 50;

    // ── authority 检测 ──────────────────────────────────────────────────────
    public static boolean isProxy(String msg) {
        return getBindProvider().equals(msg)
                || (msg.startsWith(ChiyuanVACore.getHostPkg()) && msg.contains(".c0"));
    }

    // ── SystemCallProvider authority ────────────────────────────────────────
    // 旧: <pkg>.chiyuanva.SystemCallProvider  →  <pkg>.sc.p  （消除 chiyuanva 特征）
    public static String getBindProvider() {
        return ChiyuanVACore.getHostPkg() + ".sc.p";
    }

    // ── ContentProvider stubs authority ─────────────────────────────────────
    // 旧: <pkg>.proxy_content_provider_%d  →  <pkg>.c0%02x
    public static String getProxyAuthorities(int index) {
        return String.format(Locale.US, "%s.c0%02x", ChiyuanVACore.getHostPkg(), index);
    }

    // ── Activity 壳组件 FQCN ─────────────────────────────────────────────────
    // ★ 必须与 AndroidManifest.xml android:name 一致，不可改为 hostPkg 前缀
    // 内部类名：P%d → A%02x（去掉 "P" 前缀特征，hex 编号）
    public static String getProxyPendingActivity(int index) {
        return String.format(Locale.US, "com.chiyuan.va.proxy.ProxyPendingActivity$E%02x", index);
    }

    public static String getProxyActivity(int index) {
        return String.format(Locale.US, "com.chiyuan.va.proxy.ProxyActivity$A%02x", index);
    }

    public static String TransparentProxyActivity(int index) {
        return String.format(Locale.US, "com.chiyuan.va.proxy.TransparentProxyActivity$T%02x", index);
    }

    // ── Service / JobService 壳组件 FQCN ──────────────────────────────────────
    public static String getProxyService(int index) {
        return String.format(Locale.US, "com.chiyuan.va.proxy.ProxyService$S%02x", index);
    }

    public static String getProxyJobService(int index) {
        return String.format(Locale.US, "com.chiyuan.va.proxy.ProxyJobService$J%02x", index);
    }

    // ── FileProvider authority ──────────────────────────────────────────────
    // 旧: <pkg>.chiyuanva.FileProvider  →  <pkg>.sc.f
    public static String getProxyFileProvider() {
        return ChiyuanVACore.getHostPkg() + ".sc.f";
    }

    // ── BroadcastReceiver action ────────────────────────────────────────────
    // 旧: <pkg>.stub_receiver  →  <pkg>.r
    public static String getProxyReceiver() {
        return ChiyuanVACore.getHostPkg() + ".r";
    }

    // ── 进程名 ──────────────────────────────────────────────────────────────
    // 旧: <pkg>:p%d  →  <pkg>:s%02x  （去掉 ":p" 顺序编号特征）
    public static String getProcessName(int bPid) {
        return String.format(Locale.US, "%s:s%02x", ChiyuanVACore.getHostPkg(), bPid);
    }

    // ── taskAffinity ────────────────────────────────────────────────────────
    // 旧: com.chiyuan.va.task_affinity  →  <pkg>.t
    public static String getTaskAffinity() {
        return ChiyuanVACore.getHostPkg() + ".t";
    }
}
