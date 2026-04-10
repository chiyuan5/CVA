package com.chiyuan.va.utils;

import java.util.Random;


public final class ProxyNameGen {
    
    private static final long SEED = 0x5A8C3D91E2F7L;

    private static final String[] ACTIVITY_PREFIXES = {
        "com.chiyuan.va.ui.widget.Widget",
        "com.chiyuan.va.ui.view.Status",
        "com.chiyuan.va.ui.page.Detail",
        "com.chiyuan.va.ui.page.Settings",
        "com.chiyuan.va.ui.component.Dialog",
        "com.chiyuan.va.ui.activity.Launch",
        "com.chiyuan.va.ui.activity.Main",
        "com.chiyuan.va.ui.activity.Splash",
        "com.chiyuan.va.ui.fragment.List",
        "com.chiyuan.va.ui.fragment.Tab",
    };

    private static final String[] SERVICE_PREFIXES = {
        "com.chiyuan.va.service.core.Core",
        "com.chiyuan.va.service.push.Push",
        "com.chiyuan.va.service.sync.Sync",
        "com.chiyuan.va.service.notify.Notify",
        "com.chiyuan.va.service.update.Update",
        "com.chiyuan.va.service.stats.Stats",
        "com.chiyuan.va.service.config.Config",
        "com.chiyuan.va.service.cache.Cache",
    };

    private static final String[] PROCESS_NAMES = {
        ":remote", ":push", ":worker", ":core", ":sync",
        ":media", ":service", ":render", ":data", ":web",
        ":net", ":io", ":plugin", ":aux", ":background",
        ":persist", ":system", ":tools", ":app", ":secure",
    };

    private static final String[] PROVIDER_SUFFIXES = {
        "data_provider", "file_provider", "cache_provider",
        "config_provider", "settings_provider", "info_provider",
        "state_provider", "log_provider", "sync_provider",
        "asset_provider", "media_provider", "db_provider",
    };

    private static final String SUFFIX_CHARS = "abcdefghijklmnopqrstuvwxyz";

    private static Random createRng(int index) {
        return new Random(SEED ^ (index * 0x9E3779B97F4A7C15L));
    }

    private static String randomSuffix(Random rng, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(SUFFIX_CHARS.charAt(rng.nextInt(SUFFIX_CHARS.length())));
        }
        return sb.toString();
    }

    
    public static String activityName(int index) {
        Random rng = createRng(index);
        String prefix = ACTIVITY_PREFIXES[rng.nextInt(ACTIVITY_PREFIXES.length)];
        char letter = (char) ('a' + (index % 26));
        return prefix + "$" + letter + randomSuffix(rng, 3);
    }

    
    public static String serviceName(int index) {
        Random rng = createRng(index + 1000);
        String prefix = SERVICE_PREFIXES[rng.nextInt(SERVICE_PREFIXES.length)];
        char letter = (char) ('a' + (index % 26));
        return prefix + "$" + letter + randomSuffix(rng, 3);
    }

    
    public static String pendingActivityName(int index) {
        Random rng = createRng(index + 2000);
        String prefix = ACTIVITY_PREFIXES[rng.nextInt(ACTIVITY_PREFIXES.length)];
        char letter = (char) ('p' + (index % 10));
        return prefix + "$" + letter + randomSuffix(rng, 4);
    }

    
    public static String processName(int index) {
        return PROCESS_NAMES[index % PROCESS_NAMES.length];
    }

    
    public static String providerAuthority(int userId) {
        Random rng = createRng(userId + 3000);
        String suffix = PROVIDER_SUFFIXES[rng.nextInt(PROVIDER_SUFFIXES.length)];
        return "com.chiyuan.va." + suffix + "." + userId;
    }

    
    public static String providerClassName(int index) {
        String[] bases = {
            "com.chiyuan.va.data.DbProvider",
            "com.chiyuan.va.data.FileProvider",
            "com.chiyuan.va.data.CacheProvider",
            "com.chiyuan.va.data.ConfigProvider",
        };
        Random rng = createRng(index + 4000);
        String base = bases[rng.nextInt(bases.length)];
        char letter = (char) ('a' + (index % 26));
        return base + "$" + letter + randomSuffix(rng, 3);
    }
}
