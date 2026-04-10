

package com.chiyuan.va.utils;

import android.util.Log;


public final class Slog {
     public static final int LOG_ID_SYSTEM = 3;

    // Set to false for release builds to suppress all framework logs
    private static volatile boolean sDebugMode = false;

    // Obfuscated tag prefix to avoid pattern matching on "CVA" / "ChiyuanVA" / "Hook" etc.
    private static final String TAG_PREFIX = "sys.fw.";

    private Slog() {
    }

    public static void setDebugMode(boolean debug) {
        sDebugMode = debug;
    }

    private static String sanitizeTag(String tag) {
        if (tag == null) return TAG_PREFIX + "u";
        // Strip any identifiable keywords from tags
        return TAG_PREFIX + Integer.toHexString(tag.hashCode() & 0xFFFF);
    }

    private static String sanitizeMsg(String msg) {
        if (msg == null) return "";
        // In release mode, don't output messages at all
        return msg;
    }

    public static int v(String tag, String msg) {
        if (!sDebugMode) return 0;
        return Log.println(Log.VERBOSE, sanitizeTag(tag), sanitizeMsg(msg));
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (!sDebugMode) return 0;
        return Log.println(Log.VERBOSE, sanitizeTag(tag),
                sanitizeMsg(msg) + '\n' + Log.getStackTraceString(tr));
    }

    
    public static int d(String tag, String msg) {
        if (!sDebugMode) return 0;
        return Log.println(Log.DEBUG, sanitizeTag(tag), sanitizeMsg(msg));
    }

    
    public static int d(String tag, String msg, Throwable tr) {
        if (!sDebugMode) return 0;
        return Log.println(Log.DEBUG, sanitizeTag(tag),
                sanitizeMsg(msg) + '\n' + Log.getStackTraceString(tr));
    }

    
    public static int i(String tag, String msg) {
        if (!sDebugMode) return 0;
        return Log.println(Log.INFO, sanitizeTag(tag), sanitizeMsg(msg));
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (!sDebugMode) return 0;
        return Log.println(Log.INFO, sanitizeTag(tag),
                sanitizeMsg(msg) + '\n' + Log.getStackTraceString(tr));
    }

    
    public static int w(String tag, String msg) {
        if (!sDebugMode) return 0;
        return Log.println(Log.WARN, sanitizeTag(tag), sanitizeMsg(msg));
    }

    
    public static int w(String tag, String msg, Throwable tr) {
        if (!sDebugMode) return 0;
        return Log.println(Log.WARN, sanitizeTag(tag),
                sanitizeMsg(msg) + '\n' + Log.getStackTraceString(tr));
    }

    public static int w(String tag, Throwable tr) {
        if (!sDebugMode) return 0;
        return Log.println(Log.WARN, sanitizeTag(tag), Log.getStackTraceString(tr));
    }

    
    public static int e(String tag, String msg) {
        if (!sDebugMode) return 0;
        return Log.println(Log.ERROR, sanitizeTag(tag), sanitizeMsg(msg));
    }

    
    public static int e(String tag, String msg, Throwable tr) {
        if (!sDebugMode) return 0;
        return Log.println(Log.ERROR, sanitizeTag(tag),
                sanitizeMsg(msg) + '\n' + Log.getStackTraceString(tr));
    }

    public static int println(int priority, String tag, String msg) {
        if (!sDebugMode) return 0;
        return Log.println(priority, sanitizeTag(tag), sanitizeMsg(msg));
    }
}

