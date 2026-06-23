package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "rental_items")
data class RentalItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // "Immobilier", "Véhicules", "Équipements"
    val pricePerDay: Int, // in F CFA
    val city: String, // e.g., "Libreville", "Port-Gentil", "Franceville", "Oyem", "Akanda"
    val neighborhood: String,
    val ownerName: String,
    val ownerPhone: String,
    val ownerRating: Float = 4.8f,
    val imageUrl: String? = null,
    val isVerified: Boolean = true,
    val isBookmarked: Boolean = false
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
    val paymentMethod: String, // "Airtel Money", "Moov Money", "Carte Bancaire"
    val paymentPhone: String,
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val status: String = "Confirmé" // "Confirmé", "Annulé"
) : Serializable

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val rentalItemId: Int,
    val sender: String, // "User" or "Owner"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
