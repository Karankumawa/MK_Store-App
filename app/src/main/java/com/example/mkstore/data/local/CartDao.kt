package com.example.mkstore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE id = :productId")
    suspend fun removeFromCart(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
