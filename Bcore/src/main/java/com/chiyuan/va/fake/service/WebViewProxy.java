package com.chiyuan.va.fake.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.webkit.WebSettings;

import java.io.File;
import java.lang.reflect.Method;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.fake.hook.ClassInvocationStub;
import com.chiyuan.va.fake.hook.MethodHook;
import com.chiyuan.va.fake.hook.ProxyMethod;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.app.BActivityThread;


public class WebViewProxy extends ClassInvocationStub {
    public static final String TAG = "WVP";

    public WebViewProxy() {
        super();
    }

    @Override
    protected Object getWho() {
        return null;
    }

    @Override
    protected void inject(Object baseInvocation, Object proxyInvocation) {
    }

    @Override
    public boolean isBadEnv() {
        return false;
    }

    @ProxyMethod("<init>")
    public static class Constructor extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            Context context = null;
            try {
                if (args != null && args.length > 0 && args[0] instanceof Context) {
                    context = (Context) args[0];
                } else {
                    context = ChiyuanVACore.getContext();
                }

                if (context != null) {
                    String userId = String.valueOf(BActivityThread.getUserId());
                    String uniqueDataDir = context.getApplicationInfo().dataDir + "/webview_" + userId + "_" + android.os.Process.myPid();

                    File dataDir = new File(uniqueDataDir);
                    if (!dataDir.exists()) {
                        dataDir.mkdirs();
                    }

                    System.setProperty("webview.data.dir", uniqueDataDir);
                    System.setProperty("webview.cache.dir", uniqueDataDir + "/cache");
                    System.setProperty("webview.cookies.dir", uniqueDataDir + "/cookies");
                }

                Object result = method.invoke(who, args);

                if (result instanceof WebView) {
                    configureWebView((WebView) result);
                }

                return result;
            } catch (Exception e) {
                Slog.w(TAG, "wv init fail", e);
                return createFallbackWebView(context);
            }
        }

        private void configureWebView(WebView webView) {
            try {
                WebSettings settings = webView.getSettings();
                if (settings == null) return;

                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setDatabaseEnabled(true);
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);

                try {
                    Method setAppCacheEnabled = settings.getClass().getMethod("setAppCacheEnabled", boolean.class);
                    setAppCacheEnabled.invoke(settings, true);
                    if (webView.getContext() != null) {
                        Method setAppCachePath = settings.getClass().getMethod("setAppCachePath", String.class);
                        setAppCachePath.invoke(settings, webView.getContext().getCacheDir().getAbsolutePath());
                    }
                } catch (Throwable ignored) {
                }

                settings.setBlockNetworkLoads(false);
                settings.setBlockNetworkImage(false);

                settings.setAllowFileAccess(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    settings.setAllowFileAccessFromFileURLs(true);
                    settings.setAllowUniversalAccessFromFileURLs(true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }

                // Do NOT touch user agent - leave it as the system default
                // Appending any custom string here is a detection vector

                try {
                    webView.setNetworkAvailable(true);
                } catch (Exception ignored) {
                }

                settings.setAllowContentAccess(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    settings.setSafeBrowsingEnabled(false);
                }
            } catch (Exception e) {
                Slog.w(TAG, "wv cfg fail", e);
            }
        }

        private WebView createFallbackWebView(Context context) {
            try {
                if (context != null) {
                    WebView webView = new WebView(context);
                    WebSettings settings = webView.getSettings();
                    if (settings != null) {
                        settings.setJavaScriptEnabled(true);
                        settings.setDomStorageEnabled(true);
                    }
                    return webView;
                }
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    @ProxyMethod("setDataDirectorySuffix")
    public static class SetDataDirectorySuffix extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                if (args != null && args.length > 0) {
                    String suffix = (String) args[0];
                    String userId = String.valueOf(BActivityThread.getUserId());
                    args[0] = suffix + "_" + userId + "_" + android.os.Process.myPid();
                }
                return method.invoke(who, args);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @ProxyMethod("getDataDirectory")
    public static class GetDataDirectory extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                Context context = ChiyuanVACore.getContext();
                if (context != null) {
                    String userId = String.valueOf(BActivityThread.getUserId());
                    String uniqueDir = context.getApplicationInfo().dataDir + "/webview_" + userId + "_" + android.os.Process.myPid();
                    File dir = new File(uniqueDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    return uniqueDir;
                }
                return method.invoke(who, args);
            } catch (Exception e) {
                return "/data/data/" + ChiyuanVACore.getHostPkg() + "/webview_fallback";
            }
        }
    }

    @ProxyMethod("getInstance")
    public static class GetWebViewDatabaseInstance extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            try {
                Context context = ChiyuanVACore.getContext();
                if (context != null) {
                    String userId = String.valueOf(BActivityThread.getUserId());
                    String uniqueDbPath = context.getApplicationInfo().dataDir + "/webview_db_" + userId + "_" + android.os.Process.myPid();
                    System.setProperty("webview.database.path", uniqueDbPath);
                }
                return method.invoke(who, args);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @ProxyMethod("loadUrl")
    public static class LoadUrl extends MethodHook {
        @Override
        protected Object hook(Object who, Method method, Object[] args) throws Throwable {
            return method.invoke(who, args);
        }
    }
}
