package com.example.di

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.repository.RentalRepository

object AppContainer {
    private var repository: RentalRepository? = null

    fun getRepository(context: Context): RentalRepository {
        return repository ?: synchronized(this) {
            val db = AppDatabase.getDatabase(context.applicationContext)
            RentalRepository(db.rentalDao()).also { repository = it }
        }
    }
}
