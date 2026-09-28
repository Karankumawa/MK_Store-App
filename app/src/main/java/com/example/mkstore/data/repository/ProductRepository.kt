package com.example.mkstore.data.repository

import com.example.mkstore.data.model.Product
import com.example.mkstore.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor() {

    private val client = SupabaseClientProvider.client

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            client.from("products")
                .select()
                .decodeList<Product>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertProduct(product: Product) = withContext(Dispatchers.IO) {
        client.from("products")
            .insert(product)
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        client.from("products")
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun uploadProductImage(imageBytes: ByteArray, fileName: String): String = withContext(Dispatchers.IO) {
        val bucket = client.storage.from("product-images")
        bucket.upload(fileName, imageBytes) {
            upsert = true
        }
        bucket.publicUrl(fileName)
    }
}
