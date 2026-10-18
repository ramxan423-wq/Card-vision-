plugins { id("com.android.application") }
android {
 namespace = "com.cardvision.ai"
 compileSdk = 35
 defaultConfig {
  applicationId = "com.cardvision.ai"
  minSdk = 26
  targetSdk = 35
  versionCode = 5
  versionName = "0.5.0"
 }
}
dependencies { implementation("com.google.mlkit:text-recognition:16.0.1") }
