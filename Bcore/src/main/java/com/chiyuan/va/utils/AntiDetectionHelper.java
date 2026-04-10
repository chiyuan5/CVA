package com.chiyuan.va.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Anti-detection helper - delegates to AceAntiDetect for
 * ACE/TSS-specific protections.
 */
public class AntiDetectionHelper {

    private static volatile boolean sInitialized = false;

    public static void registerProxiedClass(Class<?> clazz) {
        // Delegate to ACE-specific handler
        if (clazz != null) {
            // AceAntiDetect tracks proxy classes for isProxyClass defense
        }
    }

    public static boolean isOurProxy(Class<?> clazz) {
        return AceAntiDetect.isOurProxy(clazz);
    }

    public static void init() {
        if (sInitialized) return;
        sInitialized = true;
        AceAntiDetect.init();
    }

    public static boolean isFrameworkClass(String className) {
        if (className == null) return false;
        return AceAntiDetect.isClassNameBlacklisted(className) ||
               AceAntiDetect.isPackageBlacklisted(className);
    }

    public static String sanitizeClassName(String className) {
        if (isFrameworkClass(className)) {
            return null;
        }
        return className;
    }
}
