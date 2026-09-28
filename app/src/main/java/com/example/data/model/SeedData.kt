package com.example.data.model

data class SeedData(
    val rentalItems: List<RentalItem> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val notifications: List<NotificationEntity> = emptyList(),
    val reviews: List<ReviewEntity> = emptyList()
)
