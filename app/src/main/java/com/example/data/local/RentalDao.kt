package com.example.data.local

import androidx.room.*
import com.example.data.model.RentalItem
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {
    // Rental Items Queries
    @Query("SELECT * FROM rental_items ORDER BY id DESC")
    fun getAllRentalItems(): Flow<List<RentalItem>>

    @Query("SELECT * FROM rental_items WHERE category = :category ORDER BY id DESC")
    fun getRentalItemsByCategory(category: String): Flow<List<RentalItem>>

    @Query("SELECT * FROM rental_items WHERE id = :id")
    suspend fun getRentalItemById(id: Int): RentalItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentalItem(item: RentalItem)

    @Update
    suspend fun updateRentalItem(item: RentalItem)

    @Query("UPDATE rental_items SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM rental_items WHERE isBookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedItems(): Flow<List<RentalItem>>

    // Bookings Queries
    @Query("SELECT * FROM bookings ORDER BY bookingTimestamp DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    // Chat Queries
    @Query("SELECT * FROM chat_messages WHERE rentalItemId = :itemId ORDER BY timestamp ASC")
    fun getChatMessagesForRental(itemId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)
}
