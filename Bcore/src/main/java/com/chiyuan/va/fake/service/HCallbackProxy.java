package com.chiyuan.va.fake.service;

import static com.chiyuan.va.ChiyuanVACore.mainThread;
import static com.chiyuan.va.ChiyuanVACore.mainHandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Printer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.entity.VBinder;
import com.chiyuan.va.fake.hook.IInjectHook;
import com.chiyuan.va.fake.hook.MethodHook;
import com.chiyuan.va.fake.hook.ProxyMethod;
import com.chiyuan.va.utils.MethodParameterUtils;
import com.chiyuan.va.utils.ReflectUtils;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.utils.Str;
import com.chiyuan.va.utils.compat.BuildCompat;
import com.chiyuan.va.utils.compat.VAContentProviderCompat;


public class HCallbackProxy implements Handler.Callback, IInjectHook {
    private static final String TAG = "HCB";

    
    private static final byte[] _mCallback = {
        (byte)0x57, (byte)0x32, (byte)0xA4, (byte)0xFE, (byte)0x84,
        (byte)0x2D, (byte)0xD2, (byte)0x7E, (byte)0x0C
    };
    private static final byte[] _android_os_Handler = {
        (byte)0x5B, (byte)0x1F, (byte)0xA1, (byte)0xE0, (byte)0x87,
        (byte)0x26, (byte)0xD7, (byte)0x33, (byte)0x08, (byte)0x89,
        (byte)0xAA, (byte)0x14, (byte)0xB8, (byte)0x48, (byte)0x6F,
        (byte)0xCD, (byte)0x5F, (byte)0x03
    };

    private static final HCallbackProxy sCallback = new HCallbackProxy();
    private Handler.Callback mOldCallback;
    private boolean isEnable = false;
    private volatile boolean mReentrantGuard = false;
    private Printer printer;

    private static String dec(byte[] encoded) {
        return Str.dec(encoded);
    }

    public static HCallbackProxy get() {
        return sCallback;
    }

    public Handler.Callback getOldCallback() {
        return mOldCallback;
    }

    @Override
    public void injectHook() {
        if (isEnable) {
            Slog.d(TAG, "mCallback has been injected!");
            return;
        }
        try {
            Handler handler = mainHandler();
            Field callbackField = findCallbackField(handler);
            if (callbackField != null) {
                callbackField.setAccessible(true);
                mOldCallback = (Handler.Callback) callbackField.get(handler);
                callbackField.set(handler, this);
                isEnable = true;
            } else {
                Slog.e(TAG, "callbackField is null!");
            }
        } catch (Exception e) {
            Slog.e(TAG, "injectHook error", e);
        }
    }

    private Field findCallbackField(Handler handler) {
        try {
            
            Class<?> handlerClass = Class.forName(dec(_android_os_Handler));
            Field f = handlerClass.getDeclaredField(dec(_mCallback));
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            
            try {
                for (Field f : Handler.class.getDeclaredFields()) {
                    if (Handler.Callback.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        return f;
                    }
                }
            } catch (Exception ex) {
                Slog.e(TAG, "findCallbackField error", ex);
            }
            return null;
        }
    }

    @Override
    public boolean isBadEnv() {
        Handler handler = mainHandler();
        if (handler == null) {
            return false;
        }
        try {
            Field callbackField = findCallbackField(handler);
            if (callbackField != null) {
                callbackField.setAccessible(true);
                Handler.Callback cb = (Handler.Callback) callbackField.get(handler);
                if (cb == this) {
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mReentrantGuard) {
            return false;
        }
        mReentrantGuard = true;
        try {
            if (printer != null) {
                printer.println(">>>>> Dispatching to " + msg.target + " "
                        + msg.getCallback() + ": " + msg.what);
            }
            Handler handler = mainHandler();
            if (handler == null) {
                return false;
            }
            android.app.ActivityThread at = (android.app.ActivityThread) mainThread();
            if (at == null) {
                return false;
            }

            boolean intercept = false;
            try {
                if (BuildCompat.isS()) {
                    if (msg.what == 159) {
                        Object obj = msg.obj;
                        if (obj != null) {
                            List<?> transactionItems = (List<?>) ReflectUtils.readField(obj,
                                    "mTransactionItems");
                            if (transactionItems != null) {
                                for (Object item : transactionItems) {
                                    if (item != null) {
                                        intercept = VAContentProviderCompat.installVAContentProvider(
                                                ChiyuanVACore.get().getHostPkg(),
                                                ChiyuanVACore.get().getUserId());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

            if (mOldCallback != null && !intercept) {
                mOldCallback.handleMessage(msg);
            }
            return true;
        } catch (Exception e) {
            Slog.e(TAG, "handleMessage error", e);
            return false;
        } finally {
            mReentrantGuard = false;
            if (printer != null) {
                printer.println("<<<<< Finished to " + msg.target + " " + msg.getCallback());
            }
        }
    }

    public boolean isEnable() {
        return isEnable;
    }
}
