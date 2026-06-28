# ProGuard rules for LocAll

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Moshi
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.**$$serializer { *; }
-keepclassmembers class com.example.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Navigation Compose serialized routes
-keep class com.example.ui.navigation.Route* { *; }

# General
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Coil
-dontwarn coil.**
-keep class coil.** { *; }
-keep class coil.request.** { *; }
-keep class coil.size.** { *; }
-keep class coil.cache.** { *; }
