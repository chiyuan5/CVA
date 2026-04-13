package me.weishu.reflection;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

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
            HiddenApiBypass.addHiddenApiExemptions("");
            return 0;
        } catch (Throwable throwable) {
            Log.w(TAG, "Hidden API exemption failed", throwable);
            return -1;
        }
    }
}
