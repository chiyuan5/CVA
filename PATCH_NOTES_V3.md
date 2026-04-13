BlackBox patch v3

This patch focuses on runtime stability for clone/virtual app launch.

Changes:
1. HCallbackProxy.java
   - Avoid NullPointerException when ActivityThread$H hidden message codes are blocked by reflection.
   - Added fallback message-code handling for EXECUTE_TRANSACTION / LAUNCH_ACTIVITY / CREATE_SERVICE.
   - Added class-name based fallback detection for ClientTransaction / ActivityClientRecord / CreateServiceData.

2. BundleCompat.java
   - getBinder(Bundle, String) now returns null safely when bundle is null.

3. BlackBoxCore.java
   - getService(String) no longer crashes or caches null binders when provider reply is null.
   - Added warning log for null provider response.

4. build.gradle
   - targetSdkVersion lowered from 30 to 28 for improved hidden-API compatibility on some environments.

Note:
- Your runtime log shows hidden API denial and multiple hook errors on an x86/Houdini environment.
- This patch removes the confirmed fatal crash in HCallbackProxy and reduces follow-up null crashes,
  but emulator / ROM compatibility can still affect virtualization features.
