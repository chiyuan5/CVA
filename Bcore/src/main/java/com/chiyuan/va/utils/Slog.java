package com.chiyuan.va.utils;

import android.util.Log;


public final class Slog {
    public static final int LOG_ID_SYSTEM = 3;

    
    private static final byte[] _TAG = {
        (byte)0x61, (byte)0x1E, (byte)0xB5, (byte)0xE6, (byte)0x9F, (byte)0x3D, (byte)0xC6
    };

    private static String sTag;

    public static String getTag() {
        if (sTag == null) {
            sTag = Str.dec(_TAG);
        }
        return sTag;
    }

    private Slog() {
    }

    public static int v(String tag, String msg) {
        return Log.println(Log.VERBOSE, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return Log.println(Log.VERBOSE, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    public static int d(String tag, String msg) {
        return Log.println(Log.DEBUG, tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return Log.println(Log.DEBUG, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    public static int i(String tag, String msg) {
        return Log.println(Log.INFO, tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return Log.println(Log.INFO, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    public static int w(String tag, String msg) {
        return Log.println(Log.WARN, tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return Log.println(Log.WARN, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    public static int w(String tag, Throwable tr) {
        return Log.println(Log.WARN, tag, Log.getStackTraceString(tr));
    }

    public static int e(String tag, String msg) {
        return Log.println(Log.ERROR, tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return Log.println(Log.ERROR, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    public static int println(int priority, String tag, String msg) {
        return Log.println(priority, tag, msg);
    }
}
