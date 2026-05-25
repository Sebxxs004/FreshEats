package com.fresheats.app.data.remote.model

import com.google.gson.annotations.SerializedName

data class AutocompleteIngredientDto(
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String?
)
