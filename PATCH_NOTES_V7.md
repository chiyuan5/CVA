# V7 patch notes

- Prefer arm64-v8a / armeabi-v7a native library extraction when a native bridge (Houdini / ndk_translation) is present.
- Resolve ApplicationInfo.primaryCpuAbi from the installed APK instead of always forcing Build.CPU_ABI.
- This targets x86/x86_64 emulator + native bridge environments where cloned ARM apps load the wrong JNI libraries and then fail in OAID/MDID code paths.
