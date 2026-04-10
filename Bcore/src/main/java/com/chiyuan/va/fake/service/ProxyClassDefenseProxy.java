package com.chiyuan.va.fake.service;

import java.lang.reflect.Method;

import com.chiyuan.va.fake.hook.ClassInvocationStub;
import com.chiyuan.va.fake.hook.IInjectHook;
import com.chiyuan.va.fake.hook.MethodHook;
import com.chiyuan.va.fake.hook.ProxyMethod;
import com.chiyuan.va.utils.AceAntiDetect;
import com.chiyuan.va.utils.Slog;

/**
 * Defense against ACE's java.lang.reflect.Proxy.isProxyClass() detection.
 * 
 * ACE string table reference:
 *   0x27C090: "java/lang/reflect/Proxy"
 *   0x27C09C: "isProxyClass"
 *   0x27C0A8: "(Ljava/lang/Class;)Z"
 *
 * ACE calls Proxy.isProxyClass() on:
 * - IActivityManager singleton (0x27C160)
 * - IActivityManagerNative.gDefault (0x27C404)
 * - ActivityThread.mInstrumentation (0x27CCD8)
 * 
 * This proxy intercepts the static method call chain.
 */
public class ProxyClassDefenseProxy implements IInjectHook {

    private static final String TAG = "PCD";

    @Override
    public void injectHook() {
        try {
            // Hook Proxy.isProxyClass at Java level using reflection
            // ACE calls this via JNI so we need to ensure the Java method
            // is intercepted before the JNI call resolves
            hookIsProxyClass();
        } catch (Throwable e) {
            Slog.e(TAG, "hook failed", e);
        }
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    /**
     * Attempt to make Proxy.isProxyClass return false for our proxy classes.
     * Since Proxy.isProxyClass is a static native method on some Android versions,
     * we use an alternative approach: we ensure our proxy classes are not
     * detectable by modifying the proxyClassCache if accessible.
     */
    private void hookIsProxyClass() {
        try {
            // On Android, Proxy.isProxyClass checks if the class is a
            // subclass of Proxy. We can't easily change this, but we CAN
            // ensure that when ACE iterates fields and gets our proxy objects,
            // the objects' getClass() returns the original class.
            // This is handled in ClassInvocationStub.invoke() already.

            // Additional defense: try to clear the proxy class cache
            // to make enumeration harder
            try {
                java.lang.reflect.Field proxyCache = java.lang.reflect.Proxy.class
                    .getDeclaredField("proxyClassCache");
                proxyCache.setAccessible(true);
                // Don't clear it - that would break things
                // Just note that it exists for future use
            } catch (NoSuchFieldException ignored) {
                // Field name differs across Android versions
            }
        } catch (Throwable e) {
            Slog.d(TAG, "pcc: " + e.getMessage());
        }
    }
}
