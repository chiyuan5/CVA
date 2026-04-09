package com.chiyuan.va.utils;

import java.util.Arrays;

/**
 * StackTraceFilter
 *
 * 过滤异常调用栈中暴露框架内部结构的帧，防止 crash 上报或被检测器扫描调用栈时
 * 识别出 VA 框架类名。
 *
 * ★ 扩展了过滤关键词，同时覆盖混淆前的完整包路径。
 */
public class StackTraceFilter {
    static { install(); }

    public static void install() {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                StackTraceElement[] original = e.getStackTrace();
                e.setStackTrace(filter(original));
                // 同步过滤 cause 链
                Throwable cause = e.getCause();
                while (cause != null) {
                    cause.setStackTrace(filter(cause.getStackTrace()));
                    cause = cause.getCause();
                }
            });
        } catch (Throwable ignored) {}
    }

    private static StackTraceElement[] filter(StackTraceElement[] stack) {
        return Arrays.stream(stack)
                .filter(e -> !isSuspicious(e.getClassName()))
                .toArray(StackTraceElement[]::new);
    }

    private static boolean isSuspicious(String cls) {
        if (cls == null) return false;
        String lo = cls.toLowerCase();
        // 框架核心包路径
        if (lo.contains("com.chiyuan.va")) return true;
        // hook / proxy 关键词（混淆后短名也会保留 "hook" 语义）
        if (lo.contains("xposed"))         return true;
        if (lo.contains("epic"))           return true;
        if (lo.contains("virtual"))        return true;
        // 不过滤 "hook" 短词——过度过滤会误删正常帧；只过滤含完整包名的
        return false;
    }
}
