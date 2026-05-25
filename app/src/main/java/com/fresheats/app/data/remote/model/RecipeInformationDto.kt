package com.fresheats.app.data.remote.model

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────────────────────────────────────
// DTOs para el endpoint: GET /recipes/{id}/information
// ─────────────────────────────────────────────────────────────────────────────

data class RecipeInformationDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,

    @SerializedName("servings")
    val servings: Int,

    @SerializedName("instructions")
    val instructions: String? = null,

    @SerializedName("analyzedInstructions")
    val analyzedInstructions: List<AnalyzedInstructionDto> = emptyList(),

    @SerializedName("extendedIngredients")
    val extendedIngredients: List<ExtendedIngredientDto> = emptyList(),

    @SerializedName("nutrition")
    val nutrition: NutritionDto? = null
)

data class ExtendedIngredientDto(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("unit") val unit: String
)

data class NutritionDto(
    @SerializedName("nutrients") val nutrients: List<NutrientDto> = emptyList()
)

data class NutrientDto(
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("unit") val unit: String
)

data class AnalyzedInstructionDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("steps")
    val steps: List<InstructionStepDto> = emptyList()
)

data class InstructionStepDto(
    @SerializedName("number")
    val number: Int,

    @SerializedName("step")
    val step: String
)
