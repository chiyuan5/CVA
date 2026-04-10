package com.chiyuan.va.fake.service.libcore;

import android.os.Process;
import android.system.ErrnoException;
import android.system.OsConstants;

import java.lang.reflect.Method;

import black.libcore.io.BRLibcore;
import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.BActivityThread;
import com.chiyuan.va.core.IOCore;
import com.chiyuan.va.fake.hook.ClassInvocationStub;
import com.chiyuan.va.fake.hook.MethodHook;
import com.chiyuan.va.fake.hook.ProxyMethod;
import com.chiyuan.va.utils.AceAntiDetect;
import com.chiyuan.va.utils.Reflector;
import com.chiyuan.va.utils.Slog;


public class OsStub extends ClassInvocationStub {
    public static final String TAG = "OS";
    private Object mBase;

    public OsStub() {
        mBase = BRLibcore.get().os();
    }

    @Override
    protected Object getWho() {
        return mBase;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
        BRLibcore.get()._set_os(proxyInvocation);
    }

    @Override
    protected void onBindMethod() {
    }

    @Override
    public boolean isBadEnv() {
        return BRLibcore.get().os() != getProxyInvocation();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null)
                    continue;
                if (args[i] instanceof String && ((String) args[i]).startsWith("/")) {
                    String orig = (String) args[i];

                    // ACE detection: block access to VA marker files
                    // ACE checks /dev/virtpipe-sec (0x262520, 0x256F94, 0x24AA44)
                    // ACE checks /dev/virtpipe-common-* (0x286850, 0x28688C, 0x2868B8)
                    if (AceAntiDetect.isVaMarkerFile(orig)) {
                        // Make it look like the file doesn't exist
                        throw new ErrnoException(method.getName(), OsConstants.ENOENT);
                    }

                    args[i] = IOCore.get().redirectPath(orig);
                }
            }
        }
        return super.invoke(proxy, method, args);
    }

    @ProxyMethod("getuid")
    public static class getuid extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            int callUid = (int) method.invoke(who, args);
            return getFakeUid(callUid);
        }
    }

    @ProxyMethod("stat")
    public static class stat extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            // ACE uses stat() on /dev/virtpipe-sec and VA marker paths
            if (args != null && args.length > 0 && args[0] instanceof String) {
                String path = (String) args[0];
                if (AceAntiDetect.isVaMarkerFile(path)) {
                    throw new ErrnoException("stat", OsConstants.ENOENT);
                }
            }

            Object invoke = null;
            try {
                invoke = method.invoke(who, args);
            } catch (Throwable e) {
                throw e.getCause();
            }
            Reflector.with(invoke).field("st_uid").set(getFakeUid(-1));
            return invoke;
        }
    }

    /**
     * ACE calls access() to check for VA detection files
     * such as /dev/virtpipe-sec, /system/zygisk_magic, etc.
     */
    @ProxyMethod("access")
    public static class access extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            if (args != null && args.length > 0 && args[0] instanceof String) {
                String path = (String) args[0];
                if (AceAntiDetect.isVaMarkerFile(path)) {
                    throw new ErrnoException("access", OsConstants.ENOENT);
                }
            }
            return method.invoke(who, args);
        }
    }

    /**
     * ACE calls readlink to resolve /proc/self/fd/* symlinks
     * to detect VA file descriptors.
     */
    @ProxyMethod("readlink")
    public static class readlink extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            String result = (String) method.invoke(who, args);
            if (result != null && AceAntiDetect.isVaMarkerFile(result)) {
                throw new ErrnoException("readlink", OsConstants.ENOENT);
            }
            // Also hide our framework paths from readlink results
            if (result != null && result.contains("chiyuan")) {
                // Redirect to something innocent
                throw new ErrnoException("readlink", OsConstants.ENOENT);
            }
            return result;
        }
    }

    private static int getFakeUid(int callUid) {
        if (callUid > 0 && callUid <= Process.FIRST_APPLICATION_UID)
            return callUid;

        if (BActivityThread.isThreadInit() && BActivityThread.currentActivityThread().isInit()) {
            return BActivityThread.getBAppId();
        } else {
            return ChiyuanVACore.getHostUid();
        }
    }
}
