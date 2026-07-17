package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("|||")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|||")
}

@Entity(tableName = "rental_items")
data class RentalItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val pricePerDay: Int,
    val city: String,
    val neighborhood: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerRating: Float = 4.8f,
    val imageUrl: String? = null,
    val isVerified: Boolean = true,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rentalItemId: Int,
    val rentalItemTitle: String,
    val rentalItemCategory: String,
    val pricePerDay: Int,
    val days: Int,
    val totalPrice: Int,
    val paymentMethod: String,
    val paymentPhone: String,
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val status: String = "Payé",
    val cancelReason: String? = null
) : Serializable

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rentalItemId: Int,
    val sender: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
) : Serializable

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "",
    val gender: String = "",
    val profession: String = "",
    val city: String = "",
    val language: String = "Français",
    val isOwnerMode: Boolean = false,
    val isPhoneVerified: Boolean = false,
    val isSocialLinked: Boolean = false,
    val identityStatus: String = "Non vérifié",
    val profileImageUrl: String? = null
) : Serializable

@Entity(tableName = "search_history")
data class SearchHistoryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)

@Entity(tableName = "disputes")
data class DisputeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val status: String,
    val date: String,
    val type: String,
    val description: String = "",
    val claimAmount: Int = 0
)

@Entity(tableName = "earnings")
data class EarningEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Int,
    val date: String,
    val source: String,
    val status: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rentalItemId: Int,
    val rating: Int,
    val comment: String,
    val author: String,
    val date: String
)

@Entity(tableName = "payment_history")
data class PaymentHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Int,
    val date: String,
    val description: String,
    val method: String
)

data class ReceivedReservation(
    val id: String,
    val tenantName: String,
    val tenantRating: Float,
    val itemTitle: String,
    val category: String,
    val status: String,
    val dates: String,
    val days: Int,
    val totalPrice: Int,
    val phone: String
)

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val mediaType: String,
    val uri: String,
    val isWatermarked: Boolean = false,
    val thumbnailUri: String? = null,
    val duration: Long? = null,
    val uploadedAt: Long = System.currentTimeMillis(),
    val moderationStatus: String = "pending"
)

@Entity(tableName = "media_upload_settings")
data class MediaUploadSettings(
    @PrimaryKey val id: Int = 1,
    val autoWatermark: Boolean = true,
    val compressionQuality: Int = 85,
    val maxImages: Int = 10,
    val maxVideoLength: Int = 60
)

@Entity(tableName = "user_follows")
data class UserFollow(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val followerId: Int,
    val followedId: Int,
    val followedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "verification_badges")
data class VerificationBadge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val badgeType: String,
    val verifiedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)

@Entity(tableName = "community_disputes")
data class CommunityDispute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val reporterId: Int,
    val reportedUserId: Int,
    val reason: String,
    val description: String,
    val status: String = "open",
    val evidence: List<String> = emptyList(),
    val communityVotes: Int = 0,
    val resolution: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)

@Entity(tableName = "neighborhood_reviews")
data class NeighborhoodReview(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val neighborhood: String,
    val city: String,
    val userId: Int,
    val safetyRating: Int,
    val noiseRating: Int,
    val accessibilityRating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "booking_escrows")
data class BookingEscrow(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val amount: Int,
    val status: String = "held",
    val heldAt: Long = System.currentTimeMillis(),
    val releasedAt: Long? = null
)

@Entity(tableName = "split_payments")
data class SplitPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val totalAmount: Int,
    val splits: List<String> = emptyList(),
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_reminders")
data class PaymentReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val reminderType: String,
    val scheduledFor: Long,
    val sentAt: Long? = null,
    val message: String
)

@Entity(tableName = "payment_receipts")
data class PaymentReceipt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val receiptNumber: String,
    val amount: Int,
    val paymentMethod: String,
    val payerName: String,
    val payeeName: String,
    val date: Long = System.currentTimeMillis(),
    val items: List<String> = emptyList()
)

@Entity(tableName = "calendar_syncs")
data class CalendarSync(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val eventName: String,
    val startDate: Long,
    val endDate: Long,
    val syncedToGoogle: Boolean = false,
    val reminder: Boolean = true
)

@Entity(tableName = "saved_searches")
data class SavedSearch(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val category: String?,
    val city: String?,
    val minPrice: Int?,
    val maxPrice: Int?,
    val createdAt: Long = System.currentTimeMillis(),
    val alertEnabled: Boolean = true
)

@Entity(tableName = "search_suggestions")
data class SearchSuggestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val searchCount: Int = 1,
    val lastSearchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "voice_search_history")
data class VoiceSearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val spokenText: String,
    val interpretedQuery: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "owner_analytics")
data class OwnerAnalytics(
    @PrimaryKey val id: Int = 1,
    val totalViews: Int = 0,
    val totalInquiries: Int = 0,
    val conversionRate: Float = 0f,
    val averageResponseTime: String = "N/A",
    val topListingId: Int? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "market_insights")
data class MarketInsight(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val city: String,
    val averagePrice: Int,
    val listingCount: Int,
    val demandLevel: String,
    val trend: String,
    val period: String
)

@Entity(tableName = "push_notification_settings")
data class PushNotificationSetting(
    @PrimaryKey val id: Int = 1,
    val newListings: Boolean = true,
    val priceDrops: Boolean = true,
    val bookingReminders: Boolean = true,
    val messages: Boolean = true,
    val promotions: Boolean = false,
    val communityUpdates: Boolean = true
)

@Entity(tableName = "referral_tracking")
data class ReferralTracking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val referredUserId: Int,
    val referredUserName: String,
    val referredAt: Long = System.currentTimeMillis(),
    val status: String = "pending",
    val rewardEarned: Int = 0
)
