# ============================================================
# ChiyuanVA — Bcore 模块 ProGuard 规则
# ============================================================

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
