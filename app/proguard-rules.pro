# --- Koshpal Advanced ProGuard Rules ---

# 1. Identity & Integrity
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses, SourceFile, LineNumberTable

# 2. Domain Models & DTOs (The "ApplyWisee" Strategy)
# We must keep fields and constructors to prevent Serialization/Room crashes.
-keepclassmembers class com.app.koshpal.app.domain.model.** {
    <fields>;
    <init>(...);
}

-keepclassmembers class com.app.koshpal.core.data.remote.dto.** {
    <fields>;
    <init>(...);
}

# 3. Mappers & Enums
# Preservation of mappers is critical for the "Me/Unknown" identity resolution.
-keepclassmembers class com.app.koshpal.app.data.mapper.** {
    <fields>;
    <methods>;
}

-keepclassmembers enum * {
    **[] $VALUES;
    public *;
}

# 4. Kotlin Serialization (Ktor)
-keep,allowobfuscation,allowoptimization @kotlinx.serialization.Serializable class * {
    <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# 5. Room Database
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao

# 6. Koin Dependency Injection
-keepclassmembers class * {
    public <init>(...);
}

# 7. Network (Ktor & OkHttp)
-keep class io.ktor.** { *; }
-keep class okhttp3.** { *; }
-dontwarn io.ktor.**
-dontwarn okhttp3.**

# 8. Timber Strip
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
