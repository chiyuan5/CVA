package me.weishu.reflection;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Minimal replacement for the original free_reflection API used by this project.
 */
public final class Reflection {
    private static final String TAG = "ReflectionCompat";

    private Reflection() {
    }

    public static int unseal(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return 0;
        }
        try {
            Class<?> vmRuntimeClass = Class.forName("dalvik.system.VMRuntime");
            Method getRuntime = vmRuntimeClass.getDeclaredMethod("getRuntime");
            Method setHiddenApiExemptions = vmRuntimeClass.getDeclaredMethod(
                    "setHiddenApiExemptions", String[].class);
            getRuntime.setAccessible(true);
            setHiddenApiExemptions.setAccessible(true);
            Object vmRuntime = getRuntime.invoke(null);
            setHiddenApiExemptions.invoke(vmRuntime, (Object) new String[]{"L"});
            return 0;
        } catch (Throwable throwable) {
            Log.w(TAG, "Hidden API exemption failed", throwable);
            return -1;
        }
    }
}
