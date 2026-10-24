# .github-workflows-build.yml-
Types t
name: Build APK

on:
  workflow_dispatch:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - uses: android-actions/setup-android@v3

      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: 8.9

      - name: Generate starter project
        run: |
          mkdir -p app/src/main/java/com/example/myapp

          cat > settings.gradle.kts <<'EOF'
          pluginManagement {
              repositories { google(); mavenCentral(); gradlePluginPortal() }
          }
          dependencyResolutionManagement {
              repositories { google(); mavenCentral() }
          }
          rootProject.name = "MyApp"
          include(":app")
          EOF

          cat > build.gradle.kts <<'EOF'
          plugins {
              id("com.android.application") version "8.5.2" apply false
              id("org.jetbrains.kotlin.android") version "1.9.24" apply false
          }
          EOF

          cat > gradle.properties <<'EOF'
          android.useAndroidX=true
          org.gradle.jvmargs=-Xmx2g
          EOF

          cat > app/build.gradle.kts <<'EOF'
          plugins {
              id("com.android.application")
              id("org.jetbrains.kotlin.android")
          }
          android {
              namespace = "com.example.myapp"
              compileSdk = 34
              defaultConfig {
                  applicationId = "com.example.myapp"
                  minSdk = 24
                  targetSdk = 34
                  versionCode = 1
                  versionName = "1.0"
              }
              compileOptions {
                  sourceCompatibility = JavaVersion.VERSION_17
                  targetCompatibility = JavaVersion.VERSION_17
              }
              kotlinOptions { jvmTarget = "17" }
          }
          EOF

          cat > app/src/main/AndroidManifest.xml <<'EOF'
          <?xml version="1.0" encoding="utf-8"?>
          <manifest xmlns:android="http://schemas.android.com/apk/res/android">
              <queries>
                  <intent><action android:name="android.intent.action.TTS_SERVICE"/></intent>
              </queries>
              <application
                  android:label="Little Learners"
                  android:theme="@android:style/Theme.Material.Light.NoActionBar">
                  <activity android:name=".MainActivity" android:exported="true" android:screenOrientation="portrait">
                      <intent-filter>
                          <action android:name="android.intent.action.MAIN"/>
                          <category android:name="android.intent.category.LAUNCHER"/>
                      </intent-filter>
                  </activity>
              </application>
          </manifest>
          EOF

      - name: Build debug APK
        run: gradle assembleDebug --no-daemon

      - uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
