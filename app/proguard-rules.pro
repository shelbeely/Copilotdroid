# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in Android Studio ProGuard configuration.

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.example.agenthq.data.remote.** { *; }
