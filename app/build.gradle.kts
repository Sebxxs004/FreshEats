plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Kotlin Symbol Processing (KSP) — necesario para Room
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.fresheats.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fresheats.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ── API Key de Spoonacular ───────────────────────────────────────────
        // El valor se lee desde gradle.properties para que NO quede en el código.
        // Agrega esta línea en gradle.properties (nivel de proyecto):
        //   SPOONACULAR_API_KEY="d0fc710568fe4450a1b158bb0e83062c"
        // Y en .gitignore: gradle.properties
        buildConfigField(
            "String",
            "SPOONACULAR_API_KEY",
            "\"${project.findProperty("SPOONACULAR_API_KEY") ?: ""}\""
        )

        // Room: exportar esquema de BD para migraciones
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// ──────────────────────────────────────────────
// KSP — Room schema export path
// ──────────────────────────────────────────────
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

dependencies {

    // ── AndroidX Core ────────────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ── Jetpack Compose (BOM — gestiona versiones automáticamente) ───────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // ── Navegación Compose ───────────────────────────────────────────────────
    implementation(libs.androidx.navigation.compose)

    // ── ViewModel + Lifecycle Compose ────────────────────────────────────────
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ─────────────────────────────────────────────────────────────────────────
    // RETROFIT + GSON  (API REST)
    // ─────────────────────────────────────────────────────────────────────────
    // Retrofit 2 — cliente HTTP type-safe para Android
    implementation(libs.retrofit)
    // Convertidor Gson: serializa/deserializa JSON ↔ data classes Kotlin
    implementation(libs.retrofit.converter.gson)
    // Gson (standalone, por si necesitas instancias personalizadas)
    implementation(libs.gson)
    // OkHttp Logging Interceptor — logging de peticiones en debug
    implementation(libs.okhttp.logging.interceptor)

    // ─────────────────────────────────────────────────────────────────────────
    // COIL  (carga de imágenes desde URLs)
    // ─────────────────────────────────────────────────────────────────────────
    // Coil — Image loading library optimizada para Kotlin/Coroutines
    implementation(libs.coil.compose)
    // Soporte para SVG (opcional, útil para iconos de comida)
    implementation(libs.coil.svg)

    // ─────────────────────────────────────────────────────────────────────────
    // ROOM  (base de datos local — favoritos)
    // ─────────────────────────────────────────────────────────────────────────
    // Room Runtime — capa de abstracción sobre SQLite
    implementation(libs.room.runtime)
    // Room KTX — extensiones de Kotlin y soporte para Coroutines/Flow
    implementation(libs.room.ktx)
    // Procesador de anotaciones Room (KSP, más rápido que KAPT)
    ksp(libs.room.compiler)

    // ─────────────────────────────────────────────────────────────────────────
    // COROUTINES
    // ─────────────────────────────────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)

    // ─────────────────────────────────────────────────────────────────────────
    // TESTING
    // ─────────────────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.room.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
