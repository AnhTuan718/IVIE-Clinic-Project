    plugins {
        alias(libs.plugins.android.application)
        id("com.google.gms.google-services")
    }

    android {
        namespace = "com.example.userpage"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.userpage"
            minSdk = 26
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        buildFeatures {
            viewBinding = true
        }
    }

    dependencies {
        implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-database")
        implementation("com.google.firebase:firebase-firestore")
        implementation("com.google.firebase:firebase-analytics")

        // Các thư viện khác
        implementation("com.github.bumptech.glide:glide:4.16.0")
        implementation("androidx.gridlayout:gridlayout:1.0.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("androidx.cardview:cardview:1.0.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("com.google.code.gson:gson:2.10.1")
        implementation("de.hdodenhof:circleimageview:3.1.0")
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.constraintlayout)
        implementation(libs.navigation.fragment)
        implementation(libs.navigation.ui)
        implementation(libs.credentials)
        implementation(libs.credentials.play.services.auth)
        implementation(libs.googleid)

        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }