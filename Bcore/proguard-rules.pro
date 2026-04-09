# ============================================================
# ChiyuanVA — Bcore 模块 ProGuard 规则
# ============================================================

# ── Generic Signature 属性保留 ─────────────────────────────────
# R8 默认会移除 Signature 属性，导致运行时通过 getGenericSuperclass()
# 获取泛型参数时抛出 ClassCastException。
# 即使已把 getTClass() 改为抽象方法（根本修复），保留此属性作为安全网，
# 防止项目中其他依赖泛型签名的代码出现同类问题。
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ── 反射框架 ─────────────────────────────────────────────────
-keep class com.chiyuan.va.reflection.** { *; }
-keep @com.chiyuan.va.reflection.annotation.BClass class * { *; }
-keep @com.chiyuan.va.reflection.annotation.BClassName class * { *; }
-keep @com.chiyuan.va.reflection.annotation.BClassNameNotProcess class * { *; }
-keepclasseswithmembernames class * {
    @com.chiyuan.va.reflection.annotation.BField.*                  <methods>;
    @com.chiyuan.va.reflection.annotation.BFieldNotProcess.*        <methods>;
    @com.chiyuan.va.reflection.annotation.BFieldSetNotProcess.*     <methods>;
    @com.chiyuan.va.reflection.annotation.BFieldCheckNotProcess.*   <methods>;
    @com.chiyuan.va.reflection.annotation.BMethod.*                 <methods>;
    @com.chiyuan.va.reflection.annotation.BStaticField.*            <methods>;
    @com.chiyuan.va.reflection.annotation.BStaticMethod.*           <methods>;
    @com.chiyuan.va.reflection.annotation.BMethodCheckNotProcess.*  <methods>;
    @com.chiyuan.va.reflection.annotation.BConstructor.*            <methods>;
    @com.chiyuan.va.reflection.annotation.BConstructorNotProcess.*  <methods>;
}

# ── 公开 API — 宿主 App 继承/调用，必须完整保留 ───────────────
# ClientConfiguration 是宿主 App 必须继承的抽象类，
# 方法名称必须保留，否则 override 时方法名被混淆导致编译/运行时报错。
-keep public abstract class com.chiyuan.va.app.configuration.ClientConfiguration {
    public *;
    protected *;
}
-keep public class com.chiyuan.va.app.configuration.AppLifecycleCallback {
    public *;
    protected *;
}
-keep public class com.chiyuan.va.app.configuration.** { public *; protected *; }

# ChiyuanVACore 主入口
-keep public class com.chiyuan.va.ChiyuanVACore { public *; protected *; }

# 所有公开接口
-keep public interface com.chiyuan.va.** { *; }

# ── 实体/数据类 — Parcelable/Serializable，字段名不可混淆 ────
-keep class com.chiyuan.va.entity.** { *; }

# ── Native (.so) 에서 문자열로 직접 참조하는 클래스/메서드 ──────
# 이 클래스들은 C++ 코드에서 FindClass / GetStaticMethodID 로 이름을
# 하드코딩해서 조회하므로, ProGuard 가 이름을 바꾸면 NoSuchMethodError
# 또는 ClassNotFoundException 이 발생한다.
#
# BoxCore.cpp → VMCORE_CLASS = "com/chiyuan/va/core/NativeCore"
#   + RegisterNatives: init, enableIO, addIORule, hideXposed,
#     disableHiddenApi, disableResourceLoading, enableProcSpoof
#   + GetStaticMethodID: getCallingUid, redirectPath (x2), loadEmptyDex
-keep class com.chiyuan.va.core.NativeCore {
    public static native void init(int);
    public static native void enableIO();
    public static native void addIORule(java.lang.String, java.lang.String);
    public static native void hideXposed();
    public static native boolean disableHiddenApi();
    public static native boolean disableResourceLoading();
    public static int getCallingUid(int);
    public static java.lang.String redirectPath(java.lang.String);
    public static java.io.File redirectPath(java.io.File);
    public static long[] loadEmptyDex();
    public static void startProcSpoof(java.lang.String);
    private static native void enableProcSpoof(java.lang.String, java.lang.String);
}

# JniHook.cpp → "com/chiyuan/va/jnihook/jni/JniHook"
#   + GetStaticMethodID: nativeOffset, nativeOffset2
# JniHook.cpp → "com/chiyuan/va/jnihook/MethodUtils"
#   + GetStaticMethodID: getDesc, getMethodName
-keep class com.chiyuan.va.jnihook.jni.JniHook {
    public static native void nativeOffset();
    public static native void nativeOffset2();
    public static native void setAccessible(java.lang.Class, java.lang.reflect.Method);
    public static native void setAccessible(java.lang.Class, java.lang.reflect.Field);
}
-keep class com.chiyuan.va.jnihook.MethodUtils {
    public static java.lang.String getDesc(java.lang.reflect.Method);
    public static java.lang.String getMethodName(java.lang.reflect.Method);
}
-keep class com.chiyuan.va.jnihook.** { *; }

# ── BlackManager 框架层 — Service 代理类，运行时通过 Class.forName 查找 ─
# BlackManager.getService() 通过 getTClass().getName() + "$Stub" 拼接类名
# 再用反射调用 asInterface()，因此所有 IB*Service 接口必须保留类名。
-keep class com.chiyuan.va.fake.frameworks.** { *; }
-keep interface com.chiyuan.va.core.system.** { *; }
-keep class com.chiyuan.va.core.system.** { *; }

# ── AIDL Stub ─────────────────────────────────────────────────
-keep class com.chiyuan.va.core.system.SystemCallProvider { *; }
-keep class com.chiyuan.va.fake.provider.FileProvider { *; }

# ── Android 系统组件 — 系统通过类名实例化，必须保留 ────────────
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.job.JobService

# ── 壳组件 — manifest 中 android:name 直接写死类名，不能混淆 ──
-keep class com.chiyuan.va.proxy.** { *; }

# ── 依赖库 ────────────────────────────────────────────────────
-keep class mirror.** { *; }
-keep class black.**  { *; }
-keep class android.** { *; }
-keep class com.android.** { *; }
