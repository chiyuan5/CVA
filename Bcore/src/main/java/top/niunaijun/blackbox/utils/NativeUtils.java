package top.niunaijun.blackbox.utils;

import android.os.Build;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Created by Milk on 2/24/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class NativeUtils {
    public static final String TAG = "VirtualM";

    public static void copyNativeLib(File apk, File nativeLibDir) throws Exception {
        long startTime = System.currentTimeMillis();
        if (!nativeLibDir.exists()) {
            nativeLibDir.mkdirs();
        }
        try (ZipFile zipfile = new ZipFile(apk.getAbsolutePath())) {
            List<String> abiOrder = buildAbiPreferenceOrder(zipfile);
            Log.d(TAG, "ABI order for " + apk.getName() + ": " + abiOrder);
            for (String abi : abiOrder) {
                if (findAndCopyNativeLib(zipfile, abi, nativeLibDir)) {
                    return;
                }
            }
        } finally {
            Log.d(TAG, "Done! +" + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    public static String resolvePrimaryCpuAbi(File apk) {
        try (ZipFile zipFile = new ZipFile(apk.getAbsolutePath())) {
            List<String> abiOrder = buildAbiPreferenceOrder(zipFile);
            for (String abi : abiOrder) {
                if (hasAbi(zipFile, abi)) {
                    return abi;
                }
            }
        } catch (Throwable e) {
            Log.w(TAG, "resolvePrimaryCpuAbi failed for " + apk, e);
        }
        return Build.CPU_ABI;
    }

    private static List<String> buildAbiPreferenceOrder(ZipFile zipFile) {
        Set<String> abiSet = collectAbis(zipFile);
        List<String> abiOrder = new ArrayList<>();

        if (hasNativeBridge()) {
            addIfPresent(abiOrder, abiSet, "arm64-v8a");
            addIfPresent(abiOrder, abiSet, "armeabi-v7a");
            addIfPresent(abiOrder, abiSet, "armeabi");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                addIfPresent(abiOrder, abiSet, abi);
            }
        } else {
            addIfPresent(abiOrder, abiSet, Build.CPU_ABI);
            addIfPresent(abiOrder, abiSet, Build.CPU_ABI2);
        }

        addIfPresent(abiOrder, abiSet, "arm64-v8a");
        addIfPresent(abiOrder, abiSet, "armeabi-v7a");
        addIfPresent(abiOrder, abiSet, "armeabi");
        addIfPresent(abiOrder, abiSet, "x86_64");
        addIfPresent(abiOrder, abiSet, "x86");

        if (abiOrder.isEmpty()) {
            abiOrder.add(Build.CPU_ABI);
            abiOrder.add("armeabi");
        }
        return abiOrder;
    }

    private static Set<String> collectAbis(ZipFile zipFile) {
        Set<String> abiSet = new HashSet<>();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!name.startsWith("lib/") || name.endsWith("/")) {
                continue;
            }
            String[] parts = name.split("/");
            if (parts.length >= 3) {
                abiSet.add(parts[1]);
            }
        }
        return abiSet;
    }

    private static boolean hasAbi(ZipFile zipFile, String abi) {
        if (abi == null || abi.length() == 0) return false;
        String libPrefix = "lib/" + abi + "/";
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().startsWith(libPrefix) && entry.getName().endsWith(".so")) {
                return true;
            }
        }
        return false;
    }

    private static void addIfPresent(List<String> abiOrder, Set<String> abiSet, String abi) {
        if (abi == null || abi.length() == 0) return;
        if (abiSet.contains(abi) && !abiOrder.contains(abi)) {
            abiOrder.add(abi);
        }
    }

    private static boolean hasNativeBridge() {
        return new File("/system/lib64/libhoudini.so").exists()
                || new File("/system/lib/libhoudini.so").exists()
                || new File("/system/lib64/libndk_translation.so").exists()
                || new File("/system/lib/libndk_translation.so").exists();
    }

    private static boolean findAndCopyNativeLib(ZipFile zipfile, String cpuArch, File nativeLibDir) throws Exception {
        Log.d(TAG, "Try to copy plugin's cup arch: " + cpuArch);
        boolean findLib = false;
        boolean findSo = false;
        byte buffer[] = null;
        String libPrefix = "lib/" + cpuArch + "/";
        ZipEntry entry;
        Enumeration e = zipfile.entries();

        while (e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            String entryName = entry.getName();
            if (!findLib && !entryName.startsWith("lib/")) {
                continue;
            }
            findLib = true;
            if (!entryName.endsWith(".so") || !entryName.startsWith(libPrefix)) {
                continue;
            }

            if (buffer == null) {
                findSo = true;
                Log.d(TAG, "Found plugin's cup arch dir: " + cpuArch);
                buffer = new byte[8192];
            }

            String libName = entryName.substring(entryName.lastIndexOf('/') + 1);
            Log.d(TAG, "verify so " + libName);
//            File abiDir = new File(nativeLibDir, cpuArch);
//            if (!abiDir.exists()) {
//                abiDir.mkdirs();
//            }

            File libFile = new File(nativeLibDir, libName);
            if (libFile.exists() && libFile.length() == entry.getSize()) {
                Log.d(TAG, libName + " skip copy");
                continue;
            }
            FileOutputStream fos = new FileOutputStream(libFile);
            Log.d(TAG, "copy so " + entry.getName() + " of " + cpuArch);
            copySo(buffer, zipfile.getInputStream(entry), fos);
        }

        if (!findLib) {
            Log.d(TAG, "Fast skip all!");
            return true;
        }

        return findSo;
    }

    private static void copySo(byte[] buffer, InputStream input, OutputStream output) throws IOException {
        BufferedInputStream bufferedInput = new BufferedInputStream(input);
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
        int count;

        while ((count = bufferedInput.read(buffer)) > 0) {
            bufferedOutput.write(buffer, 0, count);
        }
        bufferedOutput.flush();
        bufferedOutput.close();
        output.close();
        bufferedInput.close();
        input.close();
    }
}
