package com.chiyuan.va.proxy;

import android.content.ComponentName;

import com.chiyuan.va.ChiyuanVACore;

import java.util.ArrayList;
import java.util.List;


public class ProxyManifest {
    private static final int MAX_PROXY_ACTIVITY_COUNT = 50;
    private static final int MAX_PROXY_SERVICE_COUNT = 50;
    private static final int MAX_PROXY_JOB_SERVICE_COUNT = 50;
    private static final int MAX_PROXY_PENDING_ACTIVITY_COUNT = 50;
    private static final int MAX_PROXY_VPN_SERVICE_COUNT = 5;

    private static String[] sProxyActivityNames;
    private static String[] sProxyServiceNames;
    private static String[] sProxyJobServiceNames;
    private static String[] sProxyPendingActivityNames;
    private static String[] sProxyVpnServiceNames;
    private static String[] sProxyProviderNames;
    private static String[] sProviderAuthorities;

    private static String[] discoverInnerClasses(Class<?> outerClass, int maxCount) {
        Class<?>[] declared = outerClass.getDeclaredClasses();
        List<String> names = new ArrayList<>();
        for (Class<?> c : declared) {
            if (names.size() >= maxCount) break;
            String simpleName = c.getSimpleName();
            if (!simpleName.isEmpty()) {
                names.add(c.getName());
            }
        }
        return names.toArray(new String[0]);
    }

    public static String getProxyActivity(int index) {
        if (sProxyActivityNames == null) {
            sProxyActivityNames = discoverInnerClasses(ProxyActivity.class, MAX_PROXY_ACTIVITY_COUNT);
        }
        return sProxyActivityNames[index % sProxyActivityNames.length];
    }

    public static String getProxyService(int index) {
        if (sProxyServiceNames == null) {
            sProxyServiceNames = discoverInnerClasses(ProxyService.class, MAX_PROXY_SERVICE_COUNT);
        }
        return sProxyServiceNames[index % sProxyServiceNames.length];
    }

    public static String getProxyJobService(int index) {
        if (sProxyJobServiceNames == null) {
            sProxyJobServiceNames = discoverInnerClasses(ProxyJobService.class, MAX_PROXY_JOB_SERVICE_COUNT);
        }
        return sProxyJobServiceNames[index % sProxyJobServiceNames.length];
    }

    public static String getProxyPendingActivity(int index) {
        if (sProxyPendingActivityNames == null) {
            sProxyPendingActivityNames = discoverInnerClasses(ProxyPendingActivity.class, MAX_PROXY_PENDING_ACTIVITY_COUNT);
        }
        return sProxyPendingActivityNames[index % sProxyPendingActivityNames.length];
    }

    public static String getProxyVpnService(int index) {
        if (sProxyVpnServiceNames == null) {
            sProxyVpnServiceNames = new String[]{ProxyVpnService.class.getName()};
        }
        return sProxyVpnServiceNames[0];
    }

    public static String getProxyProvider(int index) {
        if (sProxyProviderNames == null) {
            sProxyProviderNames = discoverInnerClasses(ProxyContentProvider.class, MAX_PROXY_ACTIVITY_COUNT);
        }
        return sProxyProviderNames[index % sProxyProviderNames.length];
    }

    public static ComponentName getProxyActivity(ComponentName origin) {
        return new ComponentName(ChiyuanVACore.get().getContext().getPackageName(),
                getProxyActivity(Math.abs(origin.hashCode()) % MAX_PROXY_ACTIVITY_COUNT));
    }

    public static ComponentName getProxyService(ComponentName origin) {
        return new ComponentName(ChiyuanVACore.get().getContext().getPackageName(),
                getProxyService(Math.abs(origin.hashCode()) % MAX_PROXY_SERVICE_COUNT));
    }

    public static ComponentName getProxyJobService(ComponentName origin) {
        return new ComponentName(ChiyuanVACore.get().getContext().getPackageName(),
                getProxyJobService(Math.abs(origin.hashCode()) % MAX_PROXY_JOB_SERVICE_COUNT));
    }

    public static ComponentName getProxyVpnService(ComponentName origin) {
        return new ComponentName(ChiyuanVACore.get().getContext().getPackageName(),
                getProxyVpnService(Math.abs(origin.hashCode()) % MAX_PROXY_VPN_SERVICE_COUNT));
    }

    public static ComponentName getProxyPendingActivity(ComponentName origin) {
        return new ComponentName(ChiyuanVACore.get().getContext().getPackageName(),
                getProxyPendingActivity(Math.abs(origin.hashCode()) % MAX_PROXY_PENDING_ACTIVITY_COUNT));
    }

    public static ComponentName getTransparentProxyActivity() {
        return new ComponentName(ChiyuanVACore.get().getContext().getPackageName(),
                TransparentProxyActivity.class.getName());
    }
}
