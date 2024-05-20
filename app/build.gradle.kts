plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.protobuf").version("0.9.4")
}

android {
    namespace = "top.youngxhui.wallet"
    compileSdk = 34

    defaultConfig {
        applicationId = "top.youngxhui.wallet"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets["main"].java {
        srcDirs("src/proto")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.material:material:1.6.6")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.lightspark:compose-qr-code:1.0.1")
    implementation(libs.proto.google.common.protos)
//    implementation("com.google.protobuf:protobuf-javalite:4.26.1")
    implementation(project(":lndmobile"))
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation( "androidx.camera:camera-camera2:1.3.3")
    implementation( "androidx.camera:camera-lifecycle:1.3.3")
    implementation( "androidx.camera:camera-view:1.3.3")
    implementation( "com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("info.guardianproject:tor-android:0.4.7.14")

    implementation ("info.guardianproject:jtorctl:0.4.5.7")
    implementation ("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
}



protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.1"
    }
    plugins {
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("java") {
                        option("lite")
                    }
                }
            }
        }
    }
}