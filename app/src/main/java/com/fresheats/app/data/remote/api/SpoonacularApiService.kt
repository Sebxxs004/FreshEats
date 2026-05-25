package com.fresheats.app.data.remote.api

import com.fresheats.app.data.remote.model.RecipeByIngredientsDto
import com.fresheats.app.data.remote.model.RecipeInformationDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ─────────────────────────────────────────────────────────────────────────────
// SpoonacularApiService — Interfaz Retrofit
//
// Define los endpoints de la API de Spoonacular que consume FreshEats.
// La API Key se inyecta como @Query en cada llamada (ver nota de seguridad
// en NetworkModule.kt sobre cómo moverla a gradle.properties).
// ─────────────────────────────────────────────────────────────────────────────
interface SpoonacularApiService {

    /**
     * Buscar recetas según los ingredientes disponibles.
     *
     * Endpoint: GET https://api.spoonacular.com/recipes/findByIngredients
     * Docs: https://spoonacular.com/food-api/docs#Search-Recipes-by-Ingredients
     * Costo: 1 punto base + 0.01 puntos por receta devuelta.
     *
     * @param ingredients  Lista de ingredientes separados por coma.
     *                     Ej: "apples,flour,sugar"
     * @param number       Número máximo de resultados a devolver (1–100).
     *                     Por defecto 10.
     * @param ranking      Estrategia de ordenación:
     *                       1 = maximizar ingredientes usados (precompra)
     *                       2 = minimizar ingredientes faltantes (postcompra)
     * @param ignorePantry Si es true, ignora items básicos de despensa
     *                     (agua, sal, harina) para el conteo de faltantes.
     * @param apiKey       API Key de Spoonacular.
     *                     Valor inyectado desde [NetworkModule.SPOONACULAR_API_KEY].
     *
     * @return Lista de [RecipeByIngredientsDto] con los resultados.
     */
    @GET("recipes/findByIngredients")
    suspend fun findRecipesByIngredients(
        @Query("ingredients")   ingredients:   String,
        @Query("number")        number:        Int     = 10,
        @Query("ranking")       ranking:       Int     = 1,
        @Query("ignorePantry")  ignorePantry:  Boolean = true,
        @Query("apiKey")        apiKey:        String
    ): List<RecipeByIngredientsDto>

    /** Obtener información detallada de una receta por ID */
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id")          id:     Int,
        @Query("apiKey")     apiKey: String
    ): RecipeInformationDto

    /** Autocompletado de ingredientes para inventario */
    @GET("food/ingredients/autocomplete")
    suspend fun autocompleteIngredient(
        @Query("query")      query:  String,
        @Query("number")     number: Int = 5,
        @Query("apiKey")     apiKey: String
    ): List<com.fresheats.app.data.remote.model.AutocompleteIngredientDto>

    // ─────────────────────────────────────────────────────────────────────────
    // 💡 ENDPOINTS SUGERIDOS PARA PRÓXIMAS ITERACIONES:
    // /** Buscar recetas con filtros avanzados (dieta, cocina, calorías...) */
    // @GET("recipes/complexSearch")
    // suspend fun searchRecipesComplex(
    //     @Query("query")      query:  String,
    //     @Query("diet")       diet:   String? = null,
    //     @Query("number")     number: Int = 10,
    //     @Query("apiKey")     apiKey: String
    // ): ComplexSearchResponseDto
    // ─────────────────────────────────────────────────────────────────────────
}
