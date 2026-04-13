# Patch Notes

## What was changed

- Replaced fragile remote dependencies with local compatibility modules:
  - `me.weishu:free_reflection` -> `:thirdparty:free-reflection`
  - `com.roger.catloadinglibrary:catloadinglibrary` -> `:thirdparty:catloadinglibrary`
  - `com.imuxuan:floatingview` -> `:thirdparty:floatingview`
  - `com.gitee.cbfg5210:RVAdapter` -> `:thirdparty:rvadapter`
  - `com.github.nukc.stateview:kotlin` -> `:thirdparty:stateview`
  - `com.github.Othershe:CornerLabelView` -> `:thirdparty:cornerlabelview`
- Removed `jcenter()` from the root repositories.
- Updated Android Gradle Plugin to `4.2.2`.
- Updated Gradle wrapper to `6.7.1`.
- Unified `compileSdkVersion`/`buildToolsVersion` usage across main modules.
- Updated `androidx.appcompat` to stable `1.3.1`.
- Updated `androidx.core:core-ktx` to `1.5.0`.
- Updated `BlackReflection` version to `1.1.4`.
- Added GitHub Actions workflow at `.github/workflows/build-apk-aar.yml`.

## GitHub Actions outputs

- APK: `out/apk/`
- AAR: `out/aar/`


## Additional fixes after GitHub Actions compile log

- Fixed Kotlin override signature in `BaseActivityLifecycleCallback` by changing `onActivitySaveInstanceState(..., Bundle?)` to `Bundle` to match `Application.ActivityLifecycleCallbacks`.
- Fixed nullable `pkg` handling in `FakeManagerActivity` before passing to `viewModel.setPattern()` and `viewModel.setLocation()`.
- Fixed `MainActivity.onOptionsItemSelected()` signature from `MenuItem?` to `MenuItem` and restored fallback to `super.onOptionsItemSelected(item)`.
- Replaced workflow with a JDK 17 -> Android SDK setup -> JDK 11 Gradle build sequence to satisfy newer `sdkmanager` while keeping the legacy Gradle/AGP build on Java 11.
