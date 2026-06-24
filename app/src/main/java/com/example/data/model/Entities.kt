package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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
