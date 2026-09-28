package com.example.mkstore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Long? = null,
    val name: String = "",
    @SerialName("title")
    val title: String? = null,
    val price: Double = 0.0,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("image")
    val image: String? = null,
    @SerialName("imageUrl")
    val imgUrlCamel: String? = null,
    @SerialName("img")
    val img: String? = null,
    @SerialName("img_url")
    val imgUrlUnderscore: String? = null,
    @SerialName("photo")
    val photo: String? = null,
    @SerialName("picture")
    val picture: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
) {
    val displayName: String
        get() = name.ifBlank { title ?: "Product" }

    val displayImageUrl: String?
        get() {
            val list = listOfNotNull(imageUrl, image, imgUrlCamel, img, imgUrlUnderscore, photo, picture, url)
            return list.firstOrNull { it.isNotBlank() }
        }
}
