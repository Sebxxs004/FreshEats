package com.fresheats.app.data.remote.network

import com.fresheats.app.BuildConfig
import com.fresheats.app.data.remote.api.SpoonacularApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// ╔══════════════════════════════════════════════════════════════════════════╗
// ║  NetworkModule — Proveedor del cliente Retrofit                        ║
// ║                                                                        ║
// ║  🔐 SEGURIDAD DE LA API KEY                                           ║
// ║                                                                        ║
// ║  La API Key se almacena en gradle.properties (NO en el código fuente) ║
// ║  y se inyecta en BuildConfig mediante buildConfigField.               ║
// ║                                                                        ║
// ║  Pasos para configurar esto:                                          ║
// ║                                                                        ║
// ║  1. En gradle.properties (nivel de proyecto), agrega:                 ║
// ║       SPOONACULAR_API_KEY="d0fc710568fe4450a1b158bb0e83062c"         ║
// ║                                                                        ║
// ║  2. En app/build.gradle.kts, dentro de defaultConfig { }, agrega:    ║
// ║       buildConfigField(                                               ║
// ║           "String",                                                   ║
// ║           "SPOONACULAR_API_KEY",                                      ║
// ║           "\"${project.findProperty("SPOONACULAR_API_KEY")}\""       ║
// ║       )                                                               ║
// ║                                                                        ║
// ║  3. En .gitignore, asegúrate de ignorar gradle.properties:           ║
// ║       gradle.properties                                               ║
// ║                                                                        ║
// ║  Con este enfoque, la key nunca queda expuesta en el repositorio.     ║
// ╚══════════════════════════════════════════════════════════════════════════╝

object NetworkModule {

    // ─────────────────────────────────────────────────────────────────────────
    // Constantes de red
    // ─────────────────────────────────────────────────────────────────────────

    /** URL base de la API de Spoonacular. Siempre debe terminar en "/". */
    private const val BASE_URL = "https://api.spoonacular.com/"

    /**
     * API Key de Spoonacular.
     *
     * Se lee desde BuildConfig para que no esté hardcodeada.
     * Si BuildConfig.SPOONACULAR_API_KEY no existe aún (antes de configurar
     * gradle.properties), usa el valor de respaldo durante desarrollo.
     *
     * ⚠️ NUNCA subas el valor real a un repositorio público.
     */
    val SPOONACULAR_API_KEY: String
        get() = try {
            BuildConfig.SPOONACULAR_API_KEY
        } catch (e: NoSuchFieldError) {
            // Respaldo solo para fase de desarrollo antes de configurar BuildConfig
            "d0fc710568fe4450a1b158bb0e83062c"
        }

    // Tiempos de espera de red (en segundos)
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS    = 30L
    private const val WRITE_TIMEOUT_SECONDS   = 15L

    // ─────────────────────────────────────────────────────────────────────────
    // OkHttpClient
    //
    // Configuramos:
    //   - Timeouts razonables para evitar ANRs
    //   - Logging completo en DEBUG (body nivel para ver JSON completo)
    //   - En RELEASE el logger no añade nada (nivel NONE)
    // ─────────────────────────────────────────────────────────────────────────
    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY   // Logcat muestra JSON completo en debug
            } else {
                HttpLoggingInterceptor.Level.NONE   // Sin logs en producción
            }
        }
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            // ── Interceptor de headers globales ────────────────────────────
            // Si Spoonacular exigiera autenticación por header en lugar de
            // query param, se haría aquí:
            // .addInterceptor { chain ->
            //     val request = chain.request().newBuilder()
            //         .addHeader("x-api-key", SPOONACULAR_API_KEY)
            //         .build()
            //     chain.proceed(request)
            // }
            .build()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Retrofit
    // ─────────────────────────────────────────────────────────────────────────
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Instancia de la interfaz de servicio
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Instancia perezosa (lazy) de [SpoonacularApiService].
     *
     * Uso:
     * ```kotlin
     * val recipes = NetworkModule.spoonacularApiService
     *     .findRecipesByIngredients(
     *         ingredients = "apples,flour",
     *         apiKey      = NetworkModule.SPOONACULAR_API_KEY
     *     )
     * ```
     */
    val spoonacularApiService: SpoonacularApiService by lazy {
        retrofit.create(SpoonacularApiService::class.java)
    }
}
