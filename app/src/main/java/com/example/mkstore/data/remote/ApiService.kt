package com.example.mkstore.data.remote

import com.example.mkstore.data.remote.dto.ProductDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    companion object {
        const val BASE_URL = "https://fakestoreapi.com/"
    }
}
