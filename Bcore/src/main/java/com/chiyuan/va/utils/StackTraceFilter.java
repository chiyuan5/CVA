package com.chiyuan.va.utils;

public class StackTraceFilter {

    // Comprehensive filter covering ACE's VA class name blacklist (0x27D234-0x27D324)
    // plus all ChiyuanVA internal class patterns
    private static final String[] SUSPICIOUS_KEYWORDS = {
        // ChiyuanVA framework internals
        "chiyuan", "chiyuanva",
        // Generic hook/proxy/fake patterns
        "hook", "proxy", "fake", "delegate", "stub",
        "invocationstub", "binderinvocation", "classinvocation",
        "methodhook", "injecthook", "hookmanager",
        // Our renamed components
        "dispatchactivity", "workservice", "scheduledservice",
        "overlayactivity", "pendingdispatchactivity",
        // ACE's known VA class patterns (from string table)
        "stubactivity", "shadowactivity", "activityproxy",
        "guestactivitystub", "normalactivity", "supermesbactivity",
        "vmosforkappmanager", "vmosmanager", "gameplugin",
        "pitactivity", "droidplugin", "doubleagent",
        // Internal framework classes
        "appinstrumentation", "baseinstrumentationdel",
        "bactivitythread", "hcallbackproxy",
        "contentproviderdelegate", "innerreceiverdelegate",
        "serviceconnectiondelegate",
        "blackmanager", "bpackagemanager", "bactivitymanager",
        "blocationmanager", "bnotificationmanager",
        "bjobmanager", "busermanager", "bstoragemanager",
        "bresourcesmanager", "baccountmanager",
        "virtualruntime", "benvironment", "appsystemenv",
        "nativecore", "iocore", "gmscore", "fakecore",
        "systemhookmanager", "uidspoof", "aceanti",
        // java.lang.reflect.Proxy generated classes
        "$proxy"
    };

    static {
        install();
    }

    public static void install() {
        try {
            Thread.UncaughtExceptionHandler existing = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                cleanThrowable(e);
                if (existing != null) {
                    existing.uncaughtException(t, e);
                }
            });
        } catch (Throwable ignored) {}
    }

    public static void cleanThrowable(Throwable e) {
        Throwable current = e;
        while (current != null) {
            try {
                StackTraceElement[] original = current.getStackTrace();
                current.setStackTrace(filterStackTrace(original));
            } catch (Throwable ignored) {}
            current = current.getCause();
        }
    }

    public static StackTraceElement[] filterStackTrace(StackTraceElement[] stack) {
        if (stack == null) return new StackTraceElement[0];
        int count = 0;
        for (StackTraceElement element : stack) {
            if (!isSuspicious(element.getClassName())) {
                count++;
            }
        }
        StackTraceElement[] filtered = new StackTraceElement[count];
        int idx = 0;
        for (StackTraceElement element : stack) {
            if (!isSuspicious(element.getClassName())) {
                filtered[idx++] = element;
            }
        }
        return filtered;
    }

    public static StackTraceElement[] filterCurrentThreadStack() {
        return filterStackTrace(Thread.currentThread().getStackTrace());
    }

    private static boolean isSuspicious(String className) {
        if (className == null) return false;
        String lower = className.toLowerCase();
        for (String keyword : SUSPICIOUS_KEYWORDS) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        if (className.startsWith("com.sun.proxy.$Proxy") || className.contains("$Proxy")) {
            return true;
        }
        return false;
    }
}
