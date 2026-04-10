package com.chiyuan.va.core.system;
import com.chiyuan.va.utils.Slog;

import android.util.Log;


public class JarManagerTest {
    private static final String TAG = "JarManagerTest";
    
    
    public static void testJarManager() {
        Log.i(TAG, "Starting JAR Manager test");
        
        try {
            JarManager jarManager = JarManager.getInstance();
            
            
            Slog.d(TAG, "Testing async initialization");
            jarManager.initializeAsync();
            
            
            Thread.sleep(2000);
            
            
            if (!jarManager.isReady()) {
                Slog.d(TAG, "Async initialization not complete, trying sync");
                jarManager.initializeSync();
            }
            
            
            Slog.d(TAG, "Testing JAR file retrieval");
            testJarFileRetrieval(jarManager);
            
            
            Slog.d(TAG, "Testing cache statistics");
            String stats = jarManager.getCacheStats();
            Log.i(TAG, "Cache stats: " + stats);
            
            
            Slog.d(TAG, "Testing individual JAR info");
            String emptyJarInfo = jarManager.getJarInfo("empty.jar");
            String junitJarInfo = jarManager.getJarInfo("junit.jar");
            
            Log.i(TAG, "Empty JAR info: " + emptyJarInfo);
            Log.i(TAG, "JUnit JAR info: " + junitJarInfo);
            
            Log.i(TAG, "JAR Manager test completed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "JAR Manager test failed", e);
        }
    }
    
    
    private static void testJarFileRetrieval(JarManager jarManager) {
        
        if (jarManager.getEmptyJar() != null) {
            Slog.d(TAG, "Empty JAR retrieved successfully");
        } else {
            Slog.w(TAG, "Empty JAR retrieval failed");
        }
        
        
        if (jarManager.getJunitJar() != null) {
            Slog.d(TAG, "JUnit JAR retrieved successfully");
        } else {
            Slog.w(TAG, "JUnit JAR retrieval failed");
        }
        
        
        if (jarManager.getJarFile("empty.jar") != null) {
            Slog.d(TAG, "Generic JAR retrieval for empty.jar successful");
        } else {
            Slog.w(TAG, "Generic JAR retrieval for empty.jar failed");
        }
    }
    
    
    public static void testConfiguration() {
        Log.i(TAG, "Testing JAR configuration");
        
        
        JarConfig.JarDefinition[] jars = JarConfig.getRequiredJars();
        Slog.d(TAG, "Found " + jars.length + " JAR definitions");
        
        for (JarConfig.JarDefinition jar : jars) {
            Slog.d(TAG, "JAR: " + jar.getAssetName() + 
                      ", File: " + jar.getFileName() + 
                      ", MinSize: " + jar.getMinSize() + 
                      ", Required: " + jar.isRequired() + 
                      ", Description: " + jar.getDescription());
        }
        
        
        int bufferSize = JarConfig.getOptimalBufferSize();
        Slog.d(TAG, "Optimal buffer size: " + bufferSize + " bytes");
        
        
        boolean enableValidation = JarConfig.ENABLE_SIZE_VALIDATION;
        boolean enableHashing = JarConfig.ENABLE_FILE_HASHING;
        boolean enableAsync = JarConfig.ENABLE_ASYNC_LOADING;
        
        Slog.d(TAG, "Validation enabled: " + enableValidation);
        Slog.d(TAG, "Hashing enabled: " + enableHashing);
        Slog.d(TAG, "Async loading enabled: " + enableAsync);
    }
}
