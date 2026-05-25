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
    val analyzedInstructions: List<AnalyzedInstructionDto> = emptyList()
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
