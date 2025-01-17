import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.protobuf") version "0.8.19"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        create("prod") {
            storeFile = gradleLocalProperties(rootDir)["STORE_FILE"]?.let { file(it) }
            storePassword = gradleLocalProperties(rootDir)["STORE_PASSWORD"] as String
            keyAlias = gradleLocalProperties(rootDir)["KEY_ALIAS"] as String
            keyPassword = gradleLocalProperties(rootDir)["KEY_PASSWORD"] as String
        }
    }
    namespace = "com.github.pakka_papad"
    compileSdk = Api.compileSdk

    defaultConfig {
        applicationId = "com.github.pakka_papad"
        minSdk = Api.minSdk
        targetSdk = Api.targetSdk
        versionCode = AppVersion.Code
        versionName = AppVersion.Name

        resValue("integer","app_version_code",versionCode.toString())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            arguments {
                arg("room.schemaLocation","$projectDir/schemas")
                arg("room.incremental","true")
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            resValue("string","app_version_name",AppVersion.Name+versionNameSuffix)
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("prod")
            resValue("string","app_version_name",AppVersion.Name)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.androidxComposeCompiler
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(Libraries.androidxCore)

    implementation(Libraries.androidxLifecycle)
    implementation(Libraries.androidxLifecycleRuntimeCompose)

    implementation(Libraries.androidxActivityCompose)

    implementation(Libraries.androidxComposeUi)
    implementation(Libraries.androidxComposeUiToolingPreview)
    debugImplementation(Libraries.androidxComposeUiTooling)
    debugImplementation(Libraries.androidxComposeUiTestManifest)
    androidTestImplementation(Libraries.androidxComposeUiTestJunit4)
    implementation(Libraries.androidxComposeConstraintLayout)
    implementation(Libraries.androidxSplashScreen)

    implementation(Libraries.androidxGlance)
    implementation(Libraries.androidxGlanceAppWidget)
    implementation(Libraries.androidxGlanceMaterial)
    implementation(Libraries.androidxGlanceMaterial3)

    implementation(Libraries.androidxComposeMaterial)
    implementation(Libraries.material3)
    implementation(Libraries.material3WindowSizeClass)

    implementation(Libraries.accompanistSystemUiController)
    implementation(Libraries.accompanistPermissions)
    implementation(Libraries.accompanistPager)

    implementation(Libraries.appCompat)
    implementation(Libraries.navigationUi)
    implementation(Libraries.navigationFragment)

    testImplementation(Libraries.junit)
    androidTestImplementation(Libraries.androidxJunit)
    androidTestImplementation(Libraries.androidxEspresso)

    debugImplementation(Libraries.leakcanary)

    implementation(Libraries.hilt)
    kapt(AnnotationProcessors.hiltCompiler)

    implementation(Libraries.timber)

    implementation(Libraries.roomRuntime)
    implementation(Libraries.roomKtx)
    kapt(AnnotationProcessors.roomCompiler)
    implementation(Libraries.datastore)
    implementation(Libraries.kotlinLite)

    implementation(platform(Libraries.firebaseBom))
    implementation(Libraries.firebaseCrashlytics)

    implementation(Libraries.exoPlayer)
    implementation(Libraries.media3ExoPlayer)
    implementation(Libraries.media3Transformer)
    implementation(Libraries.exoPlayerUi)

    implementation(Libraries.coilCompose)
    implementation(Libraries.palette)
    implementation(Libraries.lottie)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.kotlinLite}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                val java by registering {
                    option("lite")
                }
                val kotlin by registering {
                    option("lite")
                }
            }
        }
    }
}