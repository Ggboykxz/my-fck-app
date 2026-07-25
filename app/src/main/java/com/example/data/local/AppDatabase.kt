package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
    entities = [
        RentalItem::class,
        Booking::class,
        ChatMessage::class,
        UserProfile::class,
        SearchHistoryEntry::class,
        NotificationEntity::class,
        DisputeEntity::class,
        EarningEntity::class,
        ReviewEntity::class,
        PaymentHistoryEntity::class,
        SavedSearch::class,
        SearchSuggestion::class,
        VoiceSearchHistory::class,
        OwnerAnalytics::class,
        MarketInsight::class,
        PushNotificationSetting::class,
        ReferralTracking::class,
        UserFollow::class,
        VerificationBadge::class,
        CommunityDispute::class,
        NeighborhoodReview::class,
        BookingEscrow::class,
        SplitPayment::class,
        PaymentReminder::class,
        PaymentReceipt::class,
        CalendarSync::class,
        MediaItem::class,
        MediaUploadSettings::class,
        InsuranceClaim::class,
        InsuranceSubscription::class,
        Wallet::class,
        WalletTransaction::class,
        PaymentMethodLocal::class,
        PromoCode::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rentalDao(): RentalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "localall_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
