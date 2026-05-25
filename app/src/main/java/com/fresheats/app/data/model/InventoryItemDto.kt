package com.fresheats.app.data.model

data class InventoryItemDto(
    val nombre: String = "",
    val fechaAgregado: Long = 0L,
    val imagenUrl: String? = null,
    val amount: Double = 0.0,
    val unit: String = ""
)
