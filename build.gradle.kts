// ─────────────────────────────────────────────────────────────────────────────
// FreshEats — Project-level build.gradle.kts
// Configura plugins de construcción para todo el proyecto.
// ─────────────────────────────────────────────────────────────────────────────

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android)      apply false
    alias(libs.plugins.kotlin.compose)      apply false
    alias(libs.plugins.ksp)                 apply false
}
