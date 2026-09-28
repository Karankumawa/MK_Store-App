package com.example.mkstore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long? = null,
    val name: String,
    val price: Double,
    @SerialName("created_at")
    val createdAt: String? = null
)
