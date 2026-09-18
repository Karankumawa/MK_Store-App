package com.example.mkstore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartEntity::class], version = 1, exportSchema = false)
abstract class MkStoreDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        const val DATABASE_NAME = "mk_store_db"
    }
}
