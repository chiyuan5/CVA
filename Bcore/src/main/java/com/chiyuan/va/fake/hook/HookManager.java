package com.chiyuan.va.fake.hook;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.fake.delegate.AppInstrumentation;

import com.chiyuan.va.fake.service.HCallbackProxy;
import com.chiyuan.va.fake.service.IAccessibilityManagerProxy;
import com.chiyuan.va.fake.service.IAccountManagerProxy;
import com.chiyuan.va.fake.service.IActivityClientProxy;
import com.chiyuan.va.fake.service.IActivityManagerProxy;
import com.chiyuan.va.fake.service.IActivityTaskManagerProxy;
import com.chiyuan.va.fake.service.IAlarmManagerProxy;
import com.chiyuan.va.fake.service.IAppOpsManagerProxy;
import com.chiyuan.va.fake.service.IAppWidgetManagerProxy;
import com.chiyuan.va.fake.service.IAttributionSourceProxy;
import com.chiyuan.va.fake.service.IAutofillManagerProxy;
import com.chiyuan.va.fake.service.ISensitiveContentProtectionManagerProxy;
import com.chiyuan.va.fake.service.ISettingsSystemProxy;
import com.chiyuan.va.fake.service.IConnectivityManagerProxy;
import com.chiyuan.va.fake.service.ISystemSensorManagerProxy;
import com.chiyuan.va.fake.service.IContentProviderProxy;
import com.chiyuan.va.fake.service.IXiaomiAttributionSourceProxy;
import com.chiyuan.va.fake.service.IXiaomiSettingsProxy;
import com.chiyuan.va.fake.service.IXiaomiMiuiServicesProxy;
import com.chiyuan.va.fake.service.IDnsResolverProxy;
import com.chiyuan.va.fake.service.IContextHubServiceProxy;
import com.chiyuan.va.fake.service.IDeviceIdentifiersPolicyProxy;
import com.chiyuan.va.fake.service.IDevicePolicyManagerProxy;
import com.chiyuan.va.fake.service.IDisplayManagerProxy;
import com.chiyuan.va.fake.service.IFingerprintManagerProxy;
import com.chiyuan.va.fake.service.IGraphicsStatsProxy;
import com.chiyuan.va.fake.service.IJobServiceProxy;
import com.chiyuan.va.fake.service.ILauncherAppsProxy;
import com.chiyuan.va.fake.service.ILocationManagerProxy;
import com.chiyuan.va.fake.service.IMediaRouterServiceProxy;
import com.chiyuan.va.fake.service.IMediaSessionManagerProxy;
import com.chiyuan.va.fake.service.IAudioServiceProxy;
import com.chiyuan.va.fake.service.ISensorPrivacyManagerProxy;
import com.chiyuan.va.fake.service.ContentResolverProxy;
import com.chiyuan.va.fake.service.IWebViewUpdateServiceProxy;
import com.chiyuan.va.fake.service.IMiuiSecurityManagerProxy;
import com.chiyuan.va.fake.service.SystemLibraryProxy;
import com.chiyuan.va.fake.service.ReLinkerProxy;
import com.chiyuan.va.fake.service.WebViewProxy;
import com.chiyuan.va.fake.service.WebViewFactoryProxy;
import com.chiyuan.va.fake.service.MediaRecorderProxy;
import com.chiyuan.va.fake.service.AudioRecordProxy;
import com.chiyuan.va.fake.service.MediaRecorderClassProxy;
import com.chiyuan.va.fake.service.SQLiteDatabaseProxy;
import com.chiyuan.va.fake.service.ClassLoaderProxy;
import com.chiyuan.va.fake.service.FileSystemProxy;
import com.chiyuan.va.fake.service.GmsProxy;
import com.chiyuan.va.fake.service.LevelDbProxy;
import com.chiyuan.va.fake.service.DeviceIdProxy;
import com.chiyuan.va.fake.service.GoogleAccountManagerProxy;
import com.chiyuan.va.fake.service.AuthenticationProxy;
import com.chiyuan.va.fake.service.AndroidIdProxy;
import com.chiyuan.va.fake.service.AudioPermissionProxy;

import com.chiyuan.va.fake.service.INetworkManagementServiceProxy;
import com.chiyuan.va.fake.service.INotificationManagerProxy;
import com.chiyuan.va.fake.service.IPackageManagerProxy;
import com.chiyuan.va.fake.service.IPermissionManagerProxy;
import com.chiyuan.va.fake.service.IPersistentDataBlockServiceProxy;
import com.chiyuan.va.fake.service.IPhoneSubInfoProxy;
import com.chiyuan.va.fake.service.IPowerManagerProxy;
import com.chiyuan.va.fake.service.ApkAssetsProxy;
import com.chiyuan.va.fake.service.ResourcesManagerProxy;
import com.chiyuan.va.fake.service.IShortcutManagerProxy;
import com.chiyuan.va.fake.service.IStorageManagerProxy;
import com.chiyuan.va.fake.service.IStorageStatsManagerProxy;
import com.chiyuan.va.fake.service.ISystemUpdateProxy;
import com.chiyuan.va.fake.service.ITelephonyManagerProxy;
import com.chiyuan.va.fake.service.ITelephonyRegistryProxy;
import com.chiyuan.va.fake.service.IUserManagerProxy;
import com.chiyuan.va.fake.service.IVibratorServiceProxy;
import com.chiyuan.va.fake.service.IVpnManagerProxy;
import com.chiyuan.va.fake.service.IWifiManagerProxy;
import com.chiyuan.va.fake.service.IWifiScannerProxy;
import com.chiyuan.va.fake.service.IWindowManagerProxy;
import com.chiyuan.va.fake.service.context.ContentServiceStub;
import com.chiyuan.va.fake.service.context.RestrictionsManagerStub;
import com.chiyuan.va.fake.service.libcore.OsStub;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.utils.Str;
import com.chiyuan.va.utils.compat.BuildCompat;
import com.chiyuan.va.fake.service.ISettingsProviderProxy;
import com.chiyuan.va.fake.service.FeatureFlagUtilsProxy;
import com.chiyuan.va.fake.service.WorkManagerProxy;


public class HookManager {
    
    private static final byte[] _TAG = {
        (byte)0x72, (byte)0x1E, (byte)0xAA, (byte)0xF9, (byte)0xA5,
        (byte)0x2E, (byte)0xDD, (byte)0x7C, (byte)0x00, (byte)0x9F, (byte)0xF6
    };
    public static final String TAG = Str.dec(_TAG);

    private static final HookManager sHookManager = new HookManager();

    private final Map<Class<?>, IInjectHook> mInjectors = new HashMap<>();

    public static HookManager get() {
        return sHookManager;
    }

    public void init() {
        if (ChiyuanVACore.get().isBlackProcess() || ChiyuanVACore.get().isServerProcess()) {
            addInjector(new IDisplayManagerProxy());
            addInjector(new OsStub());
            addInjector(new IActivityManagerProxy());
            addInjector(new IPackageManagerProxy());
            addInjector(new ITelephonyManagerProxy());
            addInjector(new HCallbackProxy());
            addInjector(new IAppOpsManagerProxy());
            addInjector(new INotificationManagerProxy());
            addInjector(new IAlarmManagerProxy());
            addInjector(new IAppWidgetManagerProxy());
            addInjector(new ContentServiceStub());
            addInjector(new IWindowManagerProxy());
            addInjector(new IUserManagerProxy());
            addInjector(new RestrictionsManagerStub());
            addInjector(new IMediaSessionManagerProxy());
            addInjector(new IAudioServiceProxy());
            addInjector(new ISensorPrivacyManagerProxy());
            addInjector(new ContentResolverProxy());
            addInjector(new IWebViewUpdateServiceProxy());
            addInjector(new SystemLibraryProxy());
            addInjector(new ReLinkerProxy());
            addInjector(new WebViewProxy());
            addInjector(new WebViewFactoryProxy());
            addInjector(new WorkManagerProxy());
            addInjector(new MediaRecorderProxy());
            addInjector(new AudioRecordProxy());
            addInjector(new IMiuiSecurityManagerProxy());
            addInjector(new ISettingsProviderProxy());
            addInjector(new FeatureFlagUtilsProxy());
            addInjector(new MediaRecorderClassProxy());
            addInjector(new SQLiteDatabaseProxy());
            addInjector(new ClassLoaderProxy());
            addInjector(new FileSystemProxy());
            addInjector(new GmsProxy());
            addInjector(new LevelDbProxy());
            addInjector(new DeviceIdProxy());
            addInjector(new GoogleAccountManagerProxy());
            addInjector(new AuthenticationProxy());
            addInjector(new AndroidIdProxy());
            addInjector(new AudioPermissionProxy());
            addInjector(new ILocationManagerProxy());
            addInjector(new IStorageManagerProxy());
            addInjector(new ILauncherAppsProxy());
            addInjector(new IJobServiceProxy());
            addInjector(new IAccessibilityManagerProxy());
            addInjector(new ITelephonyRegistryProxy());
            addInjector(new IDevicePolicyManagerProxy());
            addInjector(new IAccountManagerProxy());
            addInjector(new IConnectivityManagerProxy());
            addInjector(new IDnsResolverProxy());
                    addInjector(new IAttributionSourceProxy());
        addInjector(new IContentProviderProxy());
        addInjector(new ISettingsSystemProxy());
        addInjector(new ISystemSensorManagerProxy());
        
        
        addInjector(new IXiaomiAttributionSourceProxy());
        addInjector(new IXiaomiSettingsProxy());
        addInjector(new IXiaomiMiuiServicesProxy());
            addInjector(new IPhoneSubInfoProxy());
            addInjector(new IMediaRouterServiceProxy());
            addInjector(new IPowerManagerProxy());
            addInjector(new IContextHubServiceProxy());
            
            addInjector(new IVibratorServiceProxy());
            addInjector(new IPersistentDataBlockServiceProxy());
            addInjector(AppInstrumentation.get());
            
            addInjector(new IWifiManagerProxy());
            addInjector(new IWifiScannerProxy());
            addInjector(new ApkAssetsProxy());
            addInjector(new ResourcesManagerProxy());
            
            if (BuildCompat.isS()) {
                addInjector(new IActivityClientProxy(null));
                addInjector(new IVpnManagerProxy());
            }
            
            if (BuildCompat.isS()) {
                addInjector(new ISensitiveContentProtectionManagerProxy());
            }
            
            if (BuildCompat.isR()) {
                addInjector(new IPermissionManagerProxy());
            }
            
            if (BuildCompat.isQ()) {
                addInjector(new IActivityTaskManagerProxy());
            }
            
            if (BuildCompat.isPie()) {
                addInjector(new ISystemUpdateProxy());
            }
            
            if (BuildCompat.isOreo()) {
                addInjector(new IAutofillManagerProxy());
                addInjector(new IDeviceIdentifiersPolicyProxy());
                addInjector(new IStorageStatsManagerProxy());
            }
            
            if (BuildCompat.isN_MR1()) {
                addInjector(new IShortcutManagerProxy());
            }
            
            if (BuildCompat.isN()) {
                addInjector(new INetworkManagementServiceProxy());
            }
            
            if (BuildCompat.isM()) {
                addInjector(new IFingerprintManagerProxy());
                addInjector(new IGraphicsStatsProxy());
            }
            
            if (BuildCompat.isL()) {
                addInjector(new IJobServiceProxy());
            }
        }
        injectAll();
    }

    
    private static final byte[] _cls_IActivityManagerProxy = {
        (byte)0x73, (byte)0x30, (byte)0xA6, (byte)0xE6, (byte)0x81, (byte)0x39,
        (byte)0xDA, (byte)0x69, (byte)0x1E, (byte)0xB7, (byte)0xE5, (byte)0x32,
        (byte)0xB8, (byte)0x41, (byte)0x6E, (byte)0xD3, (byte)0x6A, (byte)0x03,
        (byte)0xAA, (byte)0xEA, (byte)0x91
    };
    private static final byte[] _cls_IPackageManagerProxy = {
        (byte)0x73, (byte)0x21, (byte)0xA4, (byte)0xF1, (byte)0x83, (byte)0x2E,
        (byte)0xD4, (byte)0x78, (byte)0x2A, (byte)0x9B, (byte)0xEA, (byte)0x3D,
        (byte)0xBE, (byte)0x43, (byte)0x79, (byte)0xF1, (byte)0x48, (byte)0x1E,
        (byte)0xBD, (byte)0xEB
    };
    private static final byte[] _cls_WebViewProxy = {
        (byte)0x6D, (byte)0x14, (byte)0xA7, (byte)0xC4, (byte)0x81, (byte)0x2A,
        (byte)0xC4, (byte)0x4D, (byte)0x15, (byte)0x95, (byte)0xFC, (byte)0x25
    };
    private static final byte[] _cls_IContentProviderProxy = {
        (byte)0x73, (byte)0x32, (byte)0xAA, (byte)0xFC, (byte)0x9C, (byte)0x2A,
        (byte)0xDD, (byte)0x69, (byte)0x37, (byte)0x88, (byte)0xEB, (byte)0x2A,
        (byte)0xB0, (byte)0x42, (byte)0x6E, (byte)0xD3, (byte)0x6A, (byte)0x03,
        (byte)0xAA, (byte)0xEA, (byte)0x91
    };

    private static final byte[][] CRITICAL_HOOK_NAMES = {
        _cls_IActivityManagerProxy,
        _cls_IPackageManagerProxy,
        _cls_WebViewProxy,
        _cls_IContentProviderProxy,
    };

    public void checkEnv(Class<?> clazz) {
        IInjectHook iInjectHook = mInjectors.get(clazz);
        if (iInjectHook != null && iInjectHook.isBadEnv()) {
            iInjectHook.injectHook();
        }
    }

    public void checkAll() {
        for (Class<?> aClass : mInjectors.keySet()) {
            IInjectHook iInjectHook = mInjectors.get(aClass);
            if (iInjectHook != null && iInjectHook.isBadEnv()) {
                iInjectHook.injectHook();
            }
        }
    }

    void addInjector(IInjectHook injectHook) {
        mInjectors.put(injectHook.getClass(), injectHook);
    }

    void injectAll() {
        for (IInjectHook value : mInjectors.values()) {
            try {
                value.injectHook();
            } catch (Exception e) {
                handleHookError(value, e);
            }
        }
    }

    private void handleHookError(IInjectHook hook, Exception e) {
        String hookName = hook.getClass().getSimpleName();
        Slog.e(TAG, "init error: " + hookName, e);
        for (byte[] criticalName : CRITICAL_HOOK_NAMES) {
            if (Str.eq(criticalName, hookName)) {
                try {
                    if (hook.isBadEnv()) {
                        hook.injectHook();
                    }
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }

    public boolean areCriticalHooksInstalled() {
        for (byte[] criticalName : CRITICAL_HOOK_NAMES) {
            String name = Str.dec(criticalName);
            boolean found = false;
            for (Class<?> hookClass : mInjectors.keySet()) {
                if (hookClass.getSimpleName().equals(name)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public void reinitializeHooks() {
        mInjectors.clear();
        init();
    }
}
