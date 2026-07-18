package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.RentalRepository

object AppContainer {
    private var database: AppDatabase? = null
    private var repository: RentalRepository? = null

    fun getRepository(context: Context): RentalRepository {
        return repository ?: synchronized(this) {
            val db = database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "locall_database"
            ).fallbackToDestructiveMigration().build().also { database = it }
            RentalRepository(db.rentalDao()).also { repository = it }
        }
    }
}
