package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {
    @Query("SELECT * FROM rental_items ORDER BY createdAt DESC")
    fun getAllRentalItems(): Flow<List<RentalItem>>

    @Query("SELECT * FROM rental_items WHERE category = :category ORDER BY createdAt DESC")
    fun getRentalItemsByCategory(category: String): Flow<List<RentalItem>>

    @Query("SELECT * FROM rental_items WHERE id = :id")
    suspend fun getRentalItemById(id: Int): RentalItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRentalItem(item: RentalItem)

    @Update
    suspend fun updateRentalItem(item: RentalItem)

    @Query("DELETE FROM rental_items WHERE id = :id")
    suspend fun deleteRentalItem(id: Int)

    @Query("UPDATE rental_items SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean)

    @Query("SELECT * FROM rental_items WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedItems(): Flow<List<RentalItem>>

    @Query("SELECT * FROM rental_items WHERE id != :excludeId AND category = :category LIMIT 4")
    fun getSimilarItems(excludeId: Int, category: String): Flow<List<RentalItem>>

    @Query("SELECT * FROM bookings ORDER BY bookingTimestamp DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: Int): Booking?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Query("UPDATE bookings SET status = :status, cancelReason = :reason WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String, reason: String? = null)

    @Query("SELECT * FROM chat_messages WHERE rentalItemId = :itemId ORDER BY timestamp ASC")
    fun getChatMessagesForRental(itemId: Int): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllChatMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE rentalItemId = :itemId AND sender != 'User' ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastOwnerMessage(itemId: Int): ChatMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(entry: SearchHistoryEntry)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getSearchHistory(): Flow<List<SearchHistoryEntry>>

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    // Notifications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Int)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Int)

    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationCount(): Flow<Int>

    // Disputes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeEntity)

    @Query("SELECT * FROM disputes ORDER BY id DESC")
    fun getAllDisputes(): Flow<List<DisputeEntity>>

    // Earnings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarning(earning: EarningEntity)

    @Query("SELECT * FROM earnings ORDER BY id DESC")
    fun getAllEarnings(): Flow<List<EarningEntity>>

    // Search history
    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearchHistoryEntry(query: String)

    // User profile updates
    @Query("UPDATE user_profile SET fullName = :name, phone = :phone, email = :email, dob = :dob, gender = :gender, profession = :profession, city = :city WHERE id = 1")
    suspend fun updateUserProfileFields(name: String, phone: String, email: String, dob: String, gender: String, profession: String, city: String)

    @Query("UPDATE user_profile SET profileImageUrl = :url WHERE id = 1")
    suspend fun updateProfileImage(url: String)

    // Delete all user data
    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllChatMessages()

    @Query("DELETE FROM search_history")
    suspend fun deleteAllSearchHistory()

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()

    @Query("DELETE FROM disputes")
    suspend fun deleteAllDisputes()

    @Query("DELETE FROM earnings")
    suspend fun deleteAllEarnings()

    // Reviews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE rentalItemId = :itemId ORDER BY id DESC")
    fun getReviewsForItem(itemId: Int): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Query("DELETE FROM reviews")
    suspend fun deleteAllReviews()

    // Payment history
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentHistory(payment: PaymentHistoryEntity)

    @Query("SELECT * FROM payment_history ORDER BY id DESC")
    fun getAllPaymentHistory(): Flow<List<PaymentHistoryEntity>>

    @Query("DELETE FROM payment_history")
    suspend fun deleteAllPaymentHistory()
}
