package com.chiyuan.va.utils;

import android.app.Instrumentation;
import android.os.Handler;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Targeted anti-detection against ACE/TSS (Tencent Anti-Cheat Expert).
 * Based on reverse-engineering of ACE string table detection vectors.
 *
 * Key ACE detection vectors countered:
 * 1. java.lang.reflect.Proxy.isProxyClass() on IActivityManager etc
 * 2. ActivityThread.currentActivityThread().mInstrumentation class check
 * 3. ActivityThread.mActivities -> activity class name matching
 * 4. Known VA class name blacklist (StubActivity, ShadowActivity, ActivityProxy...)
 * 5. /proc/self/maps scanning for suspicious .so files
 * 6. /proc/self/status TracerPid check
 * 7. Binder transaction interception for PackageManager
 * 8. Dual-app UID detection
 * 9. File existence checks for VA markers
 */
public class AceAntiDetect {

    private static volatile boolean sInitialized = false;

    // Track all our Proxy instances so we can hide them from isProxyClass
    private static final Set<Class<?>> sOurProxyClasses = ConcurrentHashMap.newKeySet();

    // ACE's known VA class name blacklist (from string table 0x27D234-0x27D324)
    private static final String[] ACE_VA_CLASS_BLACKLIST = {
        "StubActivity", "ShadowActivity", "ActivityProxy",
        "ProxyActivity", "PitActivity", "GuestActivityStub",
        "NormalActivity", "SupermeSbActivity", "VmosForkAppManager",
        "VmosManager", "GamePlugin", "gameplugin",
        // Our own renamed classes should also be hidden
        "DispatchActivity", "WorkService", "ScheduledService",
        "OverlayActivity", "PendingDispatchActivity"
    };

    // ACE's known VA package blacklist (from string table)
    private static final String[] ACE_VA_PKG_BLACKLIST = {
        "com.lody.virtual", "com.vmos", "com.excelliance",
        "com.vphonegaga", "com.zhushou.cc", "com.tencent.gamestick",
        "com.bfire.da", "com.quxing.fenshen", "com.bly",
        "com.excean", "com.iplay", "info.red.virtual",
        "com.qihoo.magic", "com.lbe.parallel", "com.svm",
        "com.sellapk", "com.app.hider", "com.xunrui",
        "com.chaozhuo", "com.kongge", "com.joke.chongya",
        "com.weifx", "com.play.taptap",
        // Our own package
        "com.chiyuan.va"
    };

    // Files ACE checks for VA presence
    private static final String[] ACE_VA_FILE_MARKERS = {
        "adt.r4.t", "osimg/r/ot01", "vprom/sys/sys01",
        "tss.rwp.tmp", "tss.rwv.tmp", "virtpipe-sec"
    };

    public static void init() {
        if (sInitialized) return;
        sInitialized = true;

        // 1. Install comprehensive stack trace filter
        StackTraceFilter.install();

        // 2. Suppress all framework logs
        Slog.setDebugMode(false);
    }

    /**
     * Register a dynamically generated Proxy class.
     * These need to be hidden from Proxy.isProxyClass() calls by guest apps.
     */
    public static void registerProxy(Object proxyInstance) {
        if (proxyInstance != null) {
            sOurProxyClasses.add(proxyInstance.getClass());
        }
    }

    /**
     * Check if a class is one of our proxy classes that should be hidden.
     */
    public static boolean isOurProxy(Class<?> clazz) {
        return sOurProxyClasses.contains(clazz);
    }

    /**
     * ACE calls Proxy.isProxyClass(cls) via JNI at 0x27C090.
     * This method should be called from our hook to return false
     * for our proxy classes.
     */
    public static boolean shouldHideProxyClass(Class<?> clazz) {
        if (clazz == null) return false;
        // If it's our proxy, hide it
        if (sOurProxyClasses.contains(clazz)) return true;
        // If it's any $Proxy class in our process, hide it
        String name = clazz.getName();
        if (name.contains("$Proxy") || name.startsWith("com.sun.proxy")) {
            return true;
        }
        return false;
    }

    /**
     * ACE reads ActivityThread.mInstrumentation and checks its class at 0x27A52C.
     * It expects android.app.Instrumentation exactly.
     * This checks if an Instrumentation is "safe" to expose.
     */
    public static boolean isInstrumentationSafe(Instrumentation inst) {
        if (inst == null) return true;
        return inst.getClass() == Instrumentation.class;
    }

    /**
     * ACE checks if the activity class name matches known VA patterns (0x27D234+).
     * Returns true if the class name would trigger ACE detection.
     */
    public static boolean isClassNameBlacklisted(String className) {
        if (className == null) return false;
        for (String blacklisted : ACE_VA_CLASS_BLACKLIST) {
            if (className.contains(blacklisted)) return true;
        }
        return false;
    }

    /**
     * ACE checks for known VA package names.
     * Returns true if the package name would trigger detection.
     */
    public static boolean isPackageBlacklisted(String packageName) {
        if (packageName == null) return false;
        for (String blacklisted : ACE_VA_PKG_BLACKLIST) {
            if (packageName.contains(blacklisted)) return true;
        }
        return false;
    }

    /**
     * ACE checks /proc/self/maps for suspicious modules.
     * This returns true if a maps line should be hidden.
     * Key suspicious libs from ACE string table:
     * libhhmspy, libooenv, libooSpeeder, libsubstrate, 
     * libxxAndroidLoader, libxxdvm, libxxghost,
     * libfrida-gadget, frida-agent-*.so,
     * libsandhook.edxp.so
     */
    public static boolean shouldFilterMapsLine(String line) {
        if (line == null) return false;
        String lower = line.toLowerCase();
        // Hook frameworks
        if (lower.contains("frida") || lower.contains("xposed") ||
            lower.contains("substrate") || lower.contains("sandhook") ||
            lower.contains("edxp") || lower.contains("riru")) return true;
        // Speed/cheat tools ACE scans for
        if (lower.contains("libhhmspy") || lower.contains("libooenv") ||
            lower.contains("libooSpeeder") || lower.contains("libxxandroid") ||
            lower.contains("libxxdvm") || lower.contains("libxxghost") ||
            lower.contains("libxxspeedmanager") || lower.contains("xxplugin_speed") ||
            lower.contains("libgg_time") || lower.contains("libspeedman")) return true;
        // VA framework markers
        if (lower.contains("chiyuan") || lower.contains("chiyuanva")) return true;
        // Zygisk
        if (lower.contains("zygisk") || lower.contains("memfd:jit-cache")) return true;
        return false;
    }

    /**
     * ACE checks file existence for VA markers.
     * Returns true if the file path is a VA detection marker that should
     * appear non-existent to the guest app.
     */
    public static boolean isVaMarkerFile(String path) {
        if (path == null) return false;
        for (String marker : ACE_VA_FILE_MARKERS) {
            if (path.contains(marker)) return true;
        }
        // /dev/virtpipe-sec - ACE checks this for Tencent's own VA
        if (path.contains("virtpipe-sec") || path.contains("virtpipe-common")) return true;
        return false;
    }

    /**
     * ACE collects installed packages to detect VA/cheat tools (force_apk_collect).
     * Returns true if a package should be hidden from the guest app's
     * package list to avoid triggering ACE's package scanning.
     */
    public static boolean shouldHidePackage(String packageName) {
        if (packageName == null) return false;
        // Cheat tools from ACE blacklist (0x2221FC-0x222B20)
        String[] cheatPackages = {
            "cn.mc1.sq", "com.muzhiwan.gamehelper", "pj.ishuaji.cheat",
            "com.www.gamespeeder", "com.cih.game_cih", "com.huang.hl",
            "org.sbtools.gamespeed", "com.xiaojianjian.sw.app",
            "org.sbtools.master", "com.cyjh.gundam", "com.cyjh.mobileanjian",
            "com.dimonvideo.luckypatcher", "com.flamingo.xxrgplugin",
            "bin.mt.plus", "com.speedsoftware.rootexplorer",
            // Root packages
            "com.topjohnwu.magisk", "eu.chainfire.supersu",
            "com.noshufou.android.su", "me.weishu.kernelsu",
            // VA packages
            "com.lody.virtual", "com.vmos", "com.excelliance.dualaid"
        };
        for (String pkg : cheatPackages) {
            if (packageName.equals(pkg)) return true;
        }
        return false;
    }

    /**
     * ACE detects dual-app by checking UIDs (dual_uid, dual_app at 0x259500+).
     * It reads /data/user/ and compares UIDs.
     * This returns a sanitized path that hides dual-app evidence.
     */
    public static String sanitizeDataPath(String path, String guestPkg) {
        if (path == null) return path;
        // Replace any /data/user/N/ paths with /data/user/0/ (primary user)
        if (path.matches(".*/data/user/[1-9]\\d*/.*")) {
            path = path.replaceFirst("/data/user/\\d+/", "/data/user/0/");
        }
        return path;
    }

    /**
     * ACE reads ro.build.characteristics for "tablet" detection.
     * Returns sanitized property value.
     */
    public static String sanitizeSystemProperty(String key, String value) {
        if (key == null || value == null) return value;
        // Don't modify - let it through as-is
        return value;
    }
}
