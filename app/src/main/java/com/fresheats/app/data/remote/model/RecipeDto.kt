package com.fresheats.app.data.remote.model

import com.google.gson.annotations.SerializedName

// ╔══════════════════════════════════════════════════════════════════════════╗
// ║  FreshEats — DTOs de la API Spoonacular                                ║
// ║                                                                        ║
// ║  Endpoint: GET /recipes/findByIngredients                              ║
// ║  Respuesta: List<RecipeByIngredientsDto>                               ║
// ║                                                                        ║
// ║  Todos los campos llevan @SerializedName para desacoplar el contrato   ║
// ║  de la API del modelo de dominio interno de la app.                    ║
// ╚══════════════════════════════════════════════════════════════════════════╝

// ─────────────────────────────────────────────────────────────────────────────
// IngredientDto
//
// Representa un ingrediente dentro de las listas:
//   - usedIngredients   → ingredientes que SÍ tienes en casa
//   - missedIngredients → ingredientes que te FALTAN para la receta
//   - unusedIngredients → ingredientes que tienes pero la receta no usa
//
// Ejemplo de JSON real:
// {
//   "id": 9003,
//   "name": "apples",
//   "amount": 6.0,
//   "unit": "large",
//   "unitLong": "larges",
//   "unitShort": "large",
//   "aisle": "Produce",
//   "original": "6 large baking apples",
//   "originalName": "baking apples",
//   "image": "https://img.spoonacular.com/ingredients_100x100/apple.jpg",
//   "meta": []
// }
// ─────────────────────────────────────────────────────────────────────────────
data class IngredientDto(

    /** ID único del ingrediente en la base de datos Spoonacular */
    @SerializedName("id")
    val id: Int,

    /** Nombre canónico del ingrediente (ej. "baking powder") */
    @SerializedName("name")
    val name: String,

    /** Cantidad necesaria para esta receta (ej. 6.0) */
    @SerializedName("amount")
    val amount: Double,

    /** Unidad de medida corta (ej. "tsp") */
    @SerializedName("unit")
    val unit: String,

    /** Unidad de medida larga (ej. "teaspoon") */
    @SerializedName("unitLong")
    val unitLong: String,

    /** Unidad de medida abreviada alternativa (ej. "tsp") */
    @SerializedName("unitShort")
    val unitShort: String,

    /**
     * Pasillo del supermercado donde se encuentra este ingrediente.
     * Ej: "Baking", "Produce", "Milk, Eggs, Other Dairy"
     */
    @SerializedName("aisle")
    val aisle: String,

    /**
     * Texto completo del ingrediente tal como aparece en la receta.
     * Ej: "6 large baking apples"
     */
    @SerializedName("original")
    val original: String,

    /**
     * Nombre original sin la cantidad/unidad.
     * Ej: "baking apples"
     */
    @SerializedName("originalName")
    val originalName: String,

    /**
     * URL de la imagen del ingrediente (100×100px).
     * Ej: "https://img.spoonacular.com/ingredients_100x100/apple.jpg"
     * Nota: algunas entradas pueden tener solo el nombre del archivo sin host.
     */
    @SerializedName("image")
    val image: String,

    /**
     * Metadatos adicionales del ingrediente.
     * Ej: ["unsalted", "cold"] para "cold unsalted butter"
     */
    @SerializedName("meta")
    val meta: List<String> = emptyList(),

    /**
     * Nombre extendido con adjetivos (solo aparece en algunos ingredientes).
     * Ej: "unsalted butter" cuando name = "butter"
     * Marcado como nullable porque no siempre está presente en el JSON.
     */
    @SerializedName("extendedName")
    val extendedName: String? = null
)

// ─────────────────────────────────────────────────────────────────────────────
// RecipeByIngredientsDto
//
// Elemento raíz de la lista devuelta por /recipes/findByIngredients.
//
// Ejemplo de JSON real (simplificado):
// {
//   "id": 73420,
//   "title": "Apple Or Peach Strudel",
//   "image": "https://img.spoonacular.com/recipes/73420-312x231.jpg",
//   "imageType": "jpg",
//   "likes": 0,
//   "usedIngredientCount": 1,
//   "missedIngredientCount": 3,
//   "usedIngredients": [ ... ],
//   "missedIngredients": [ ... ],
//   "unusedIngredients": []
// }
// ─────────────────────────────────────────────────────────────────────────────
data class RecipeByIngredientsDto(

    /** ID único de la receta en Spoonacular */
    @SerializedName("id")
    val id: Int,

    /** Nombre de la receta. Ej: "Apple Or Peach Strudel" */
    @SerializedName("title")
    val title: String,

    /**
     * URL completa de la imagen de la receta (312×231px).
     * Ej: "https://img.spoonacular.com/recipes/73420-312x231.jpg"
     * Para obtener otras resoluciones, reemplaza "312x231" por:
     *   - "556x370" (grande)
     *   - "636x393" (HD)
     */
    @SerializedName("image")
    val image: String,

    /**
     * Extensión del archivo de imagen.
     * Ej: "jpg", "png"
     */
    @SerializedName("imageType")
    val imageType: String,

    /** Número de "me gusta" que tiene la receta en Spoonacular */
    @SerializedName("likes")
    val likes: Int,

    /**
     * Cantidad de ingredientes de la receta que el usuario SÍ tiene.
     * Maximizar este valor → ranking = 1
     */
    @SerializedName("usedIngredientCount")
    val usedIngredientCount: Int,

    /**
     * Cantidad de ingredientes que le FALTAN al usuario.
     * Minimizar este valor → ranking = 2
     */
    @SerializedName("missedIngredientCount")
    val missedIngredientCount: Int,

    /** Lista detallada de ingredientes que el usuario YA TIENE */
    @SerializedName("usedIngredients")
    val usedIngredients: List<IngredientDto> = emptyList(),

    /** Lista detallada de ingredientes que el usuario NECESITA COMPRAR */
    @SerializedName("missedIngredients")
    val missedIngredients: List<IngredientDto> = emptyList(),

    /**
     * Ingredientes que el usuario tiene pero la receta NO usa.
     * Útil para mostrar "ingredientes sobrantes" en la UI.
     */
    @SerializedName("unusedIngredients")
    val unusedIngredients: List<IngredientDto> = emptyList()
)
