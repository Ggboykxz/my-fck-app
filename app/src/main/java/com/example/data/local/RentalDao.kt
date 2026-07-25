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

    // Saved Searches
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedSearch(search: SavedSearch)

    @Query("SELECT * FROM saved_searches ORDER BY createdAt DESC")
    fun getSavedSearches(): Flow<List<SavedSearch>>

    @Query("DELETE FROM saved_searches WHERE id = :id")
    suspend fun deleteSavedSearch(id: Int)

    @Query("UPDATE saved_searches SET alertEnabled = :enabled WHERE id = :id")
    suspend fun toggleSearchAlert(id: Int, enabled: Boolean)

    // Search Suggestions
    @Query("SELECT * FROM search_suggestions WHERE query LIKE '%' || :prefix || '%' ORDER BY searchCount DESC LIMIT :limit")
    fun getSearchSuggestions(prefix: String, limit: Int = 5): Flow<List<SearchSuggestion>>

    @Query("INSERT OR IGNORE INTO search_suggestions (query, searchCount, lastSearchedAt) VALUES (:query, 1, :timestamp)")
    suspend fun insertSearchSuggestionIfNotExists(query: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE search_suggestions SET searchCount = searchCount + 1, lastSearchedAt = :timestamp WHERE query = :query")
    suspend fun incrementSearchSuggestion(query: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM search_suggestions ORDER BY searchCount DESC LIMIT :limit")
    fun getTrendingSearches(limit: Int = 10): Flow<List<SearchSuggestion>>

    // Voice Search History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceSearch(history: VoiceSearchHistory)

    @Query("SELECT * FROM voice_search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getVoiceSearchHistory(limit: Int = 20): Flow<List<VoiceSearchHistory>>

    // Search Analytics
    @Query("SELECT * FROM search_suggestions ORDER BY searchCount DESC LIMIT 10")
    fun getSearchAnalytics(): Flow<List<SearchSuggestion>>

    // Owner Analytics
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnerAnalytics(analytics: OwnerAnalytics)

    @Query("SELECT * FROM owner_analytics WHERE id = 1")
    fun getOwnerAnalytics(): Flow<OwnerAnalytics?>

    // Market Insights
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketInsight(insight: MarketInsight)

    @Query("SELECT * FROM market_insights ORDER BY id DESC")
    fun getMarketInsights(): Flow<List<MarketInsight>>

    // Push Notification Settings
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPushNotificationSettings(settings: PushNotificationSetting)

    @Query("SELECT * FROM push_notification_settings WHERE id = 1")
    fun getPushNotificationSettings(): Flow<PushNotificationSetting?>

    // Referral Tracking
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferralTracking(referral: ReferralTracking)

    @Query("SELECT * FROM referral_tracking ORDER BY referredAt DESC")
    fun getReferralTracking(): Flow<List<ReferralTracking>>

    @Query("DELETE FROM saved_searches")
    suspend fun deleteAllSavedSearches()

    @Query("DELETE FROM search_suggestions")
    suspend fun deleteAllSearchSuggestions()

    @Query("DELETE FROM voice_search_history")
    suspend fun deleteAllVoiceSearchHistory()

    @Query("DELETE FROM owner_analytics")
    suspend fun deleteAllOwnerAnalytics()

    @Query("DELETE FROM market_insights")
    suspend fun deleteAllMarketInsights()

    @Query("DELETE FROM push_notification_settings")
    suspend fun deleteAllPushNotificationSettings()

    @Query("DELETE FROM referral_tracking")
    suspend fun deleteAllReferralTracking()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFollow(follow: UserFollow)

    @Query("SELECT * FROM user_follows WHERE followerId = :followerId AND followedId = :followedId")
    suspend fun getFollow(followerId: Int, followedId: Int): UserFollow?

    @Query("DELETE FROM user_follows WHERE followerId = :followerId AND followedId = :followedId")
    suspend fun unfollow(followerId: Int, followedId: Int)

    @Query("SELECT COUNT(*) FROM user_follows WHERE followedId = :userId")
    fun getFollowerCount(userId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_follows WHERE followerId = :userId")
    fun getFollowingCount(userId: Int): Flow<Int>

    @Query("SELECT * FROM verification_badges WHERE userId = :userId")
    fun getVerificationBadges(userId: Int): Flow<List<VerificationBadge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationBadge(badge: VerificationBadge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityDispute(dispute: CommunityDispute)

    @Query("SELECT * FROM community_disputes ORDER BY createdAt DESC")
    fun getAllCommunityDisputes(): Flow<List<CommunityDispute>>

    @Query("SELECT * FROM community_disputes WHERE reporterId = :userId ORDER BY createdAt DESC")
    fun getUserDisputes(userId: Int): Flow<List<CommunityDispute>>

    @Query("UPDATE community_disputes SET communityVotes = communityVotes + :delta WHERE id = :id")
    suspend fun voteDispute(id: Int, delta: Int)

    @Query("SELECT * FROM neighborhood_reviews WHERE city = :city ORDER BY createdAt DESC")
    fun getNeighborhoodReviews(city: String): Flow<List<NeighborhoodReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNeighborhoodReview(review: NeighborhoodReview)

    @Query("SELECT * FROM booking_escrows ORDER BY heldAt DESC")
    fun getAllEscrows(): Flow<List<BookingEscrow>>

    @Query("SELECT * FROM booking_escrows WHERE status = :status ORDER BY heldAt DESC")
    fun getEscrowsByStatus(status: String): Flow<List<BookingEscrow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscrow(escrow: BookingEscrow)

    @Query("UPDATE booking_escrows SET status = :status, releasedAt = :releasedAt WHERE id = :id")
    suspend fun updateEscrowStatus(id: Int, status: String, releasedAt: Long? = null)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSplitPayment(split: SplitPayment)

    @Query("SELECT * FROM split_payments ORDER BY createdAt DESC")
    fun getAllSplitPayments(): Flow<List<SplitPayment>>

    @Query("SELECT * FROM split_payments WHERE bookingId = :bookingId")
    fun getSplitPaymentForBooking(bookingId: Int): Flow<List<SplitPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentReminder(reminder: PaymentReminder)

    @Query("SELECT * FROM payment_reminders ORDER BY scheduledFor DESC")
    fun getAllPaymentReminders(): Flow<List<PaymentReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentReceipt(receipt: PaymentReceipt)

    @Query("SELECT * FROM payment_receipts ORDER BY date DESC")
    fun getAllPaymentReceipts(): Flow<List<PaymentReceipt>>

    @Query("SELECT * FROM payment_receipts WHERE bookingId = :bookingId")
    fun getReceiptsForBooking(bookingId: Int): Flow<List<PaymentReceipt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarSync(sync: CalendarSync)

    @Query("SELECT * FROM calendar_syncs ORDER BY startDate DESC")
    fun getAllCalendarSyncs(): Flow<List<CalendarSync>>

    @Query("UPDATE calendar_syncs SET syncedToGoogle = :synced WHERE id = :id")
    suspend fun updateCalendarSync(id: Int, synced: Boolean)

    @Query("SELECT * FROM media_items WHERE listingId = :listingId ORDER BY uploadedAt DESC")
    fun getMediaItemsForListing(listingId: Int): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getMediaItemById(id: Int): MediaItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItem(mediaItem: MediaItem): Long

    @Update
    suspend fun updateMediaItem(mediaItem: MediaItem)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteMediaItem(id: Int)

    @Query("UPDATE media_items SET moderationStatus = :status WHERE id = :id")
    suspend fun updateMediaModerationStatus(id: Int, status: String)

    @Query("SELECT * FROM media_items WHERE moderationStatus = :status ORDER BY uploadedAt DESC")
    fun getMediaItemsByStatus(status: String): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMediaUploadSettings(settings: MediaUploadSettings)

    @Query("SELECT * FROM media_upload_settings WHERE id = 1")
    fun getMediaUploadSettings(): Flow<MediaUploadSettings?>

    @Query("DELETE FROM media_items WHERE listingId = :listingId")
    suspend fun deleteMediaItemsForListing(listingId: Int)

    @Query("UPDATE community_disputes SET evidence = :evidence WHERE id = :id")
    suspend fun updateDisputeEvidence(id: Int, evidence: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsuranceClaim(claim: InsuranceClaim)

    @Query("SELECT * FROM insurance_claims ORDER BY createdAt DESC")
    fun getAllInsuranceClaims(): Flow<List<InsuranceClaim>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsuranceSubscription(sub: InsuranceSubscription)

    @Query("SELECT * FROM insurance_subscriptions WHERE id = 1")
    fun getInsuranceSubscription(): Flow<InsuranceSubscription?>

    @Query("UPDATE user_profile SET isPhoneVerified = :verified WHERE id = 1")
    suspend fun updatePhoneVerified(verified: Boolean)

    @Query("UPDATE user_profile SET identityStatus = :status WHERE id = 1")
    suspend fun updateIdentityStatus(status: String)

    @Query("UPDATE chat_messages SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: Int, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalletTransaction(txn: WalletTransaction)
}
