package com.chiyuan.va.utils;

public class StackTraceFilter {
    
    private static final byte[] _xposed = {
        (byte)0x4D, (byte)0x1E, (byte)0xA8, (byte)0xFC, (byte)0x94, (byte)0x23, (byte)0xD4
    };
    private static final byte[] _epic = {
        (byte)0x54, (byte)0x01, (byte)0xB5, (byte)0xF8, (byte)0x9C
    };
    private static final byte[] _virtual = {
        (byte)0x4C, (byte)0x18, (byte)0xB7, (byte)0xE6, (byte)0x98, (byte)0x34,
        (byte)0xC2, (byte)0x7B
    };
    private static final byte[] _hook = {
        (byte)0x52, (byte)0x1E, (byte)0xAA, (byte)0xF9
    };

    static {
        install();
    }

    public static void install() {
        try {
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                StackTraceElement[] original = e.getStackTrace();
                e.setStackTrace(filterStackTrace(original));
            });
        } catch (Throwable ignored) {}
    }

    private static StackTraceElement[] filterStackTrace(StackTraceElement[] stack) {
        return java.util.Arrays.stream(stack)
            .filter(element -> !isSuspicious(element.getClassName()))
            .toArray(StackTraceElement[]::new);
    }

    private static boolean isSuspicious(String className) {
        String lower = className.toLowerCase();
        return lower.contains(Str.dec(_xposed)) ||
               lower.contains(Str.dec(_epic)) ||
               lower.contains(Str.dec(_virtual)) ||
               lower.contains(Str.dec(_hook));
    }
}
