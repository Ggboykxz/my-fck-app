package com.example.data.repository

import android.content.Context
import com.example.data.local.RentalDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RentalRepository(private val rentalDao: RentalDao) {

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
    }

    val allRentalItems: Flow<List<RentalItem>> = rentalDao.getAllRentalItems()
    val bookmarkedItems: Flow<List<RentalItem>> = rentalDao.getBookmarkedItems()
    val allBookings: Flow<List<Booking>> = rentalDao.getAllBookings()
    val userProfile: Flow<UserProfile?> = rentalDao.getUserProfile()
    val searchHistory: Flow<List<SearchHistoryEntry>> = rentalDao.getSearchHistory()

    suspend fun getUserProfileOnce(): UserProfile? {
        return rentalDao.getUserProfileOnce()
    }

    fun getRentalItemsByCategory(category: String): Flow<List<RentalItem>> =
        rentalDao.getRentalItemsByCategory(category)

    suspend fun getRentalItemById(id: Int): RentalItem? =
        rentalDao.getRentalItemById(id)

    fun getSimilarItems(excludeId: Int, category: String): Flow<List<RentalItem>> =
        rentalDao.getSimilarItems(excludeId, category)

    suspend fun insertRentalItem(item: RentalItem) =
        rentalDao.insertRentalItem(item)

    suspend fun updateRentalItem(item: RentalItem) =
        rentalDao.updateRentalItem(item)

    suspend fun deleteRentalItem(id: Int) =
        rentalDao.deleteRentalItem(id)

    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean) =
        rentalDao.updateBookmarkStatus(id, isBookmarked)

    suspend fun insertBooking(booking: Booking) =
        rentalDao.insertBooking(booking)

    suspend fun getBookingById(id: Int): Booking? =
        rentalDao.getBookingById(id)

    suspend fun updateBookingStatus(id: Int, status: String, reason: String? = null) =
        rentalDao.updateBookingStatus(id, status, reason)

    fun getChatMessagesForRental(itemId: Int): Flow<List<ChatMessage>> =
        rentalDao.getChatMessagesForRental(itemId)

    fun getAllChatMessages(): Flow<List<ChatMessage>> =
        rentalDao.getAllChatMessages()

    suspend fun insertChatMessage(message: ChatMessage) =
        rentalDao.insertChatMessage(message)

    suspend fun updateMessageStatus(id: Int, status: String) =
        rentalDao.updateMessageStatus(id, status)

    suspend fun getLastOwnerMessage(itemId: Int): ChatMessage? =
        rentalDao.getLastOwnerMessage(itemId)

    suspend fun upsertUserProfile(profile: UserProfile) =
        rentalDao.upsertUserProfile(profile)

    suspend fun insertSearchHistory(entry: SearchHistoryEntry) =
        rentalDao.insertSearchHistory(entry)

    suspend fun clearSearchHistory() =
        rentalDao.clearSearchHistory()

    suspend fun deleteSearchHistoryEntry(query: String) =
        rentalDao.deleteSearchHistoryEntry(query)

    val notifications: Flow<List<NotificationEntity>> = rentalDao.getAllNotifications()

    suspend fun insertNotification(notification: NotificationEntity) =
        rentalDao.insertNotification(notification)

    suspend fun markNotificationRead(id: Int) =
        rentalDao.markNotificationRead(id)

    suspend fun markAllNotificationsRead() =
        rentalDao.markAllNotificationsRead()

    suspend fun deleteNotification(id: Int) =
        rentalDao.deleteNotification(id)

    suspend fun clearAllNotifications() =
        rentalDao.clearAllNotifications()

    fun getUnreadNotificationCount(): Flow<Int> =
        rentalDao.getUnreadNotificationCount()

    val disputes: Flow<List<DisputeEntity>> = rentalDao.getAllDisputes()

    suspend fun insertDispute(dispute: DisputeEntity) =
        rentalDao.insertDispute(dispute)

    val earnings: Flow<List<EarningEntity>> = rentalDao.getAllEarnings()

    suspend fun insertEarning(earning: EarningEntity) =
        rentalDao.insertEarning(earning)

    suspend fun updateUserProfileFields(name: String, phone: String, email: String, dob: String, gender: String, profession: String, city: String) =
        rentalDao.updateUserProfileFields(name, phone, email, dob, gender, profession, city)

    suspend fun updateProfileImage(url: String) =
        rentalDao.updateProfileImage(url)

    suspend fun deleteAllUserData() {
        rentalDao.deleteUserProfile()
        rentalDao.deleteAllBookings()
        rentalDao.deleteAllChatMessages()
        rentalDao.deleteAllSearchHistory()
        rentalDao.deleteAllNotifications()
        rentalDao.deleteAllDisputes()
        rentalDao.deleteAllEarnings()
        rentalDao.deleteAllReviews()
        rentalDao.deleteAllPaymentHistory()
    }

    fun getReviewsForItem(itemId: Int): Flow<List<ReviewEntity>> =
        rentalDao.getReviewsForItem(itemId)

    suspend fun insertReview(review: ReviewEntity) =
        rentalDao.insertReview(review)

    val paymentHistory: Flow<List<PaymentHistoryEntity>> = rentalDao.getAllPaymentHistory()

    suspend fun insertPaymentHistory(payment: PaymentHistoryEntity) =
        rentalDao.insertPaymentHistory(payment)

    val savedSearches: Flow<List<SavedSearch>> = rentalDao.getSavedSearches()

    fun searchSuggestions(prefix: String): Flow<List<SearchSuggestion>> =
        rentalDao.getSearchSuggestions(prefix)

    val trendingSearches: Flow<List<SearchSuggestion>> = rentalDao.getTrendingSearches()

    val voiceSearchHistory: Flow<List<VoiceSearchHistory>> = rentalDao.getVoiceSearchHistory()

    suspend fun saveSearch(query: String, category: String?, city: String?, minPrice: Int?, maxPrice: Int?) =
        rentalDao.insertSavedSearch(SavedSearch(query = query, category = category, city = city, minPrice = minPrice, maxPrice = maxPrice))

    suspend fun deleteSavedSearch(id: Int) = rentalDao.deleteSavedSearch(id)

    suspend fun toggleSearchAlert(id: Int, enabled: Boolean) = rentalDao.toggleSearchAlert(id, enabled)

    suspend fun logSearch(query: String) {
        val now = System.currentTimeMillis()
        val existing = rentalDao.getSearchSuggestions(query, 1).first()
        if (existing.isNotEmpty()) {
            rentalDao.incrementSearchSuggestion(query, now)
        } else {
            rentalDao.insertSearchSuggestionIfNotExists(query, now)
        }
    }

    suspend fun logVoiceSearch(spoken: String, interpreted: String) =
        rentalDao.insertVoiceSearch(VoiceSearchHistory(spokenText = spoken, interpretedQuery = interpreted))

    fun getSearchAnalytics(): Flow<List<SearchSuggestion>> = rentalDao.getSearchAnalytics()

    val ownerAnalytics: Flow<OwnerAnalytics?> = rentalDao.getOwnerAnalytics()

    suspend fun insertOwnerAnalytics(analytics: OwnerAnalytics) = rentalDao.insertOwnerAnalytics(analytics)

    val marketInsights: Flow<List<MarketInsight>> = rentalDao.getMarketInsights()

    suspend fun insertMarketInsight(insight: MarketInsight) = rentalDao.insertMarketInsight(insight)

    val pushNotificationSettings: Flow<PushNotificationSetting?> = rentalDao.getPushNotificationSettings()

    suspend fun insertPushNotificationSettings(settings: PushNotificationSetting) = rentalDao.insertPushNotificationSettings(settings)

    val referralTracking: Flow<List<ReferralTracking>> = rentalDao.getReferralTracking()

    suspend fun insertReferralTracking(referral: ReferralTracking) = rentalDao.insertReferralTracking(referral)

    suspend fun toggleFollow(followerId: Int, followedId: Int) {
        val existing = rentalDao.getFollow(followerId, followedId)
        if (existing != null) rentalDao.unfollow(followerId, followedId)
        else rentalDao.insertUserFollow(UserFollow(followerId = followerId, followedId = followedId))
    }

    fun getFollowerCount(userId: Int): Flow<Int> = rentalDao.getFollowerCount(userId)
    fun getFollowingCount(userId: Int): Flow<Int> = rentalDao.getFollowingCount(userId)
    suspend fun isFollowing(followerId: Int, followedId: Int): UserFollow? = rentalDao.getFollow(followerId, followedId)

    fun getVerificationBadges(userId: Int): Flow<List<VerificationBadge>> = rentalDao.getVerificationBadges(userId)
    suspend fun insertVerificationBadge(badge: VerificationBadge) = rentalDao.insertVerificationBadge(badge)

    suspend fun insertCommunityDispute(dispute: CommunityDispute) = rentalDao.insertCommunityDispute(dispute)
    fun getAllCommunityDisputes(): Flow<List<CommunityDispute>> = rentalDao.getAllCommunityDisputes()
    fun getUserDisputes(userId: Int): Flow<List<CommunityDispute>> = rentalDao.getUserDisputes(userId)
    suspend fun voteDispute(id: Int, delta: Int) = rentalDao.voteDispute(id, delta)

    fun getNeighborhoodReviews(city: String): Flow<List<NeighborhoodReview>> = rentalDao.getNeighborhoodReviews(city)
    suspend fun insertNeighborhoodReview(review: NeighborhoodReview) = rentalDao.insertNeighborhoodReview(review)

    fun getAllEscrows(): Flow<List<BookingEscrow>> = rentalDao.getAllEscrows()
    fun getEscrowsByStatus(status: String): Flow<List<BookingEscrow>> = rentalDao.getEscrowsByStatus(status)
    suspend fun insertEscrow(escrow: BookingEscrow) = rentalDao.insertEscrow(escrow)
    suspend fun updateEscrowStatus(id: Int, status: String, releasedAt: Long? = null) = rentalDao.updateEscrowStatus(id, status, releasedAt)

    suspend fun insertSplitPayment(split: SplitPayment) = rentalDao.insertSplitPayment(split)
    fun getAllSplitPayments(): Flow<List<SplitPayment>> = rentalDao.getAllSplitPayments()
    fun getSplitPaymentForBooking(bookingId: Int): Flow<List<SplitPayment>> = rentalDao.getSplitPaymentForBooking(bookingId)

    suspend fun insertPaymentReminder(reminder: PaymentReminder) = rentalDao.insertPaymentReminder(reminder)
    fun getAllPaymentReminders(): Flow<List<PaymentReminder>> = rentalDao.getAllPaymentReminders()

    suspend fun insertPaymentReceipt(receipt: PaymentReceipt) = rentalDao.insertPaymentReceipt(receipt)
    fun getAllPaymentReceipts(): Flow<List<PaymentReceipt>> = rentalDao.getAllPaymentReceipts()
    fun getReceiptsForBooking(bookingId: Int): Flow<List<PaymentReceipt>> = rentalDao.getReceiptsForBooking(bookingId)

    suspend fun insertCalendarSync(sync: CalendarSync) = rentalDao.insertCalendarSync(sync)
    fun getAllCalendarSyncs(): Flow<List<CalendarSync>> = rentalDao.getAllCalendarSyncs()
    suspend fun updateCalendarSync(id: Int, synced: Boolean) = rentalDao.updateCalendarSync(id, synced)

    fun getMediaItemsForListing(listingId: Int): Flow<List<MediaItem>> =
        rentalDao.getMediaItemsForListing(listingId)

    suspend fun getMediaItemById(id: Int): MediaItem? =
        rentalDao.getMediaItemById(id)

    suspend fun insertMediaItem(mediaItem: MediaItem): Long =
        rentalDao.insertMediaItem(mediaItem)

    suspend fun updateMediaItem(mediaItem: MediaItem) =
        rentalDao.updateMediaItem(mediaItem)

    suspend fun deleteMediaItem(id: Int) =
        rentalDao.deleteMediaItem(id)

    suspend fun updateMediaModerationStatus(id: Int, status: String) =
        rentalDao.updateMediaModerationStatus(id, status)

    fun getMediaItemsByStatus(status: String): Flow<List<MediaItem>> =
        rentalDao.getMediaItemsByStatus(status)

    suspend fun upsertMediaUploadSettings(settings: MediaUploadSettings) =
        rentalDao.upsertMediaUploadSettings(settings)

    fun getMediaUploadSettings(): Flow<MediaUploadSettings?> =
        rentalDao.getMediaUploadSettings()

    suspend fun deleteMediaItemsForListing(listingId: Int) =
        rentalDao.deleteMediaItemsForListing(listingId)

    suspend fun updateDisputeEvidence(id: Int, evidence: List<String>) =
        rentalDao.updateDisputeEvidence(id, evidence)

    suspend fun insertInsuranceClaim(claim: InsuranceClaim) =
        rentalDao.insertInsuranceClaim(claim)

    val insuranceClaims: Flow<List<InsuranceClaim>> = rentalDao.getAllInsuranceClaims()

    suspend fun insertInsuranceSubscription(sub: InsuranceSubscription) =
        rentalDao.insertInsuranceSubscription(sub)

    val insuranceSubscription: Flow<InsuranceSubscription?> = rentalDao.getInsuranceSubscription()

    suspend fun updatePhoneVerified(verified: Boolean) =
        rentalDao.updatePhoneVerified(verified)

    suspend fun updateIdentityStatus(status: String) =
        rentalDao.updateIdentityStatus(status)

    suspend fun insertWalletTransaction(txn: WalletTransaction) =
        rentalDao.insertWalletTransaction(txn)

    suspend fun seedDatabase(context: Context) {
        val currentItems = allRentalItems.first()
        if (currentItems.isEmpty()) {
            try {
                val inputStream = context.assets.open("seed/seed_data.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val jsonObject = json.parseToJsonElement(jsonString).jsonObject
                val seedData = SeedData(
                    rentalItems = jsonObject["rentalItems"]?.let { element ->
                        element.jsonArray.map { item ->
                            val obj = item.jsonObject
                            RentalItem(
                                title = obj["title"]!!.jsonPrimitive.content,
                                description = obj["description"]!!.jsonPrimitive.content,
                                category = obj["category"]!!.jsonPrimitive.content,
                                pricePerDay = obj["pricePerDay"]!!.jsonPrimitive.content.toInt(),
                                city = obj["city"]!!.jsonPrimitive.content,
                                neighborhood = obj["neighborhood"]!!.jsonPrimitive.content,
                                ownerName = obj["ownerName"]!!.jsonPrimitive.content,
                                ownerPhone = obj["ownerPhone"]!!.jsonPrimitive.content,
                                ownerRating = obj["ownerRating"]!!.jsonPrimitive.content.toFloat(),
                                imageUrl = obj["imageUrl"]?.jsonPrimitive?.content,
                                isVerified = obj["isVerified"]!!.jsonPrimitive.content.toBoolean()
                            )
                        }
                    } ?: emptyList(),
                    chatMessages = jsonObject["chatMessages"]?.let { element ->
                        element.jsonArray.map { msg ->
                            val obj = msg.jsonObject
                            ChatMessage(
                                rentalItemId = obj["rentalItemId"]!!.jsonPrimitive.content.toInt(),
                                sender = obj["sender"]!!.jsonPrimitive.content,
                                messageText = obj["messageText"]!!.jsonPrimitive.content,
                                timestamp = obj["timestamp"]!!.jsonPrimitive.content.toLong()
                            )
                        }
                    } ?: emptyList(),
                    notifications = jsonObject["notifications"]?.let { element ->
                        element.jsonArray.map { notif ->
                            val obj = notif.jsonObject
                            NotificationEntity(
                                type = obj["type"]!!.jsonPrimitive.content,
                                title = obj["title"]!!.jsonPrimitive.content,
                                message = obj["message"]!!.jsonPrimitive.content,
                                time = obj["time"]!!.jsonPrimitive.content,
                                isRead = obj["isRead"]!!.jsonPrimitive.content.toBoolean()
                            )
                        }
                    } ?: emptyList(),
                    reviews = jsonObject["reviews"]?.let { element ->
                        element.jsonArray.map { review ->
                            val obj = review.jsonObject
                            ReviewEntity(
                                rentalItemId = obj["rentalItemId"]!!.jsonPrimitive.content.toInt(),
                                rating = obj["rating"]!!.jsonPrimitive.content.toInt(),
                                comment = obj["comment"]!!.jsonPrimitive.content,
                                author = obj["author"]!!.jsonPrimitive.content,
                                date = obj["date"]!!.jsonPrimitive.content
                            )
                        }
                    } ?: emptyList()
                )

                for (item in seedData.rentalItems) {
                    rentalDao.insertRentalItem(item)
                }
                for (msg in seedData.chatMessages) {
                    rentalDao.insertChatMessage(msg)
                }
                for (notification in seedData.notifications) {
                    rentalDao.insertNotification(notification)
                }
                for (review in seedData.reviews) {
                    rentalDao.insertReview(review)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
