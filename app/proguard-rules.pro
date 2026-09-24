# ============================================================
# KOSHPAL - Release R8 / ProGuard Rules

# ------------------------------------------------------------
# 1. Attributes required by reflection / serialization / Room
# ------------------------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod


# ------------------------------------------------------------
# 2. Kotlin Serialization
# Keep generated serializers / serialized model members working.
# Allow class names themselves to be obfuscated.
# ------------------------------------------------------------
-keep,allowoptimization,allowobfuscation @kotlinx.serialization.Serializable class * {
    <fields>;
}

-keepclassmembers,allowoptimization,allowobfuscation class * {
    @kotlinx.serialization.SerialName <fields>;
}


# ------------------------------------------------------------
# 3. Room
# Keep generated database/DAO implementations and annotations
# required for Room runtime behavior.
# ------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao


# ------------------------------------------------------------
# 4. Enums
# Preserve enum values needed at runtime while allowing
# surrounding code to be optimized/obfuscated.
# ------------------------------------------------------------
-keepclassmembers,allowoptimization class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# ------------------------------------------------------------
# 5. App/domain models
# Preserve members needed by serialization/Room without keeping
# the entire classes/names unobfuscated.
# ------------------------------------------------------------
-keepclassmembers,allowoptimization,allowobfuscation class com.app.koshpal.app.domain.model.** {
    <fields>;
}

-keepclassmembers,allowoptimization,allowobfuscation class com.app.koshpal.core.data.remote.dto.** {
    <fields>;
}

# ------------------------------------------------------------
# 6. DO NOT globally keep all Ktor / OkHttp classes.
#
# Their libraries provide their own consumer rules.
# Keeping entire libraries defeats useful R8 optimization/
# obfuscation and is unnecessary unless a concrete runtime issue
# is demonstrated.
# ------------------------------------------------------------


# ------------------------------------------------------------
# 7. Release logging
# Remove Timber debug/info/verbose calls from optimized release.
# Do not remove warning/error logging automatically.
# ------------------------------------------------------------
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

