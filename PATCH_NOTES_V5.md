# PATCH_NOTES_V5

## Runtime fix for splash exit

This patch targets apps that launch inside BlackBox, open permission settings during splash,
and then call `System.exit(1)` when the sandboxed package cannot satisfy host-level checks.

### Changes

- `IPackageManagerProxy.canRequestPackageInstalls()` now returns `true` for virtual apps.
- `IAppOpsManagerProxy.checkOperation()` now returns `MODE_ALLOWED`.
- `IAppOpsManagerProxy.noteOperation()` now returns `MODE_ALLOWED`.

### Why

Apps like `com.chiyuan.box` were starting normally, reaching `SplashActivity`, then issuing:

- `android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION`
- `android.settings.MANAGE_UNKNOWN_APP_SOURCES`

and immediately exiting the process. This patch fakes the relevant permission/app-op results so the
virtualized app can continue past splash instead of killing itself.

### Caveat

If the target app still crashes later with a native `SIGSEGV` on x86/Houdini environments,
that is likely a separate ABI/runtime issue inside the target app or emulator environment.
