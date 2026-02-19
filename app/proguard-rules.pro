# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in Android Studio ProGuard configuration.

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keep class com.example.agenthq.data.remote.rest.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt
-keep class dagger.hilt.** { *; }

# Apollo
-dontwarn com.apollographql.**
-keep class com.example.agenthq.graphql.** { *; }

# Keep domain models
-keep class com.example.agenthq.domain.model.** { *; }
