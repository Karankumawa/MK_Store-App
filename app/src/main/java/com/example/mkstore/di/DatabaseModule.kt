package com.example.mkstore.di

import android.content.Context
import androidx.room.Room
import com.example.mkstore.data.local.CartDao
import com.example.mkstore.data.local.MkStoreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMkStoreDatabase(
        @ApplicationContext context: Context
    ): MkStoreDatabase {
        return Room.databaseBuilder(
            context,
            MkStoreDatabase::class.java,
            MkStoreDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: MkStoreDatabase): CartDao {
        return database.cartDao()
    }
}
