package com.example.ui.state

import com.example.data.model.RentalItem
import com.example.data.model.SavedSearch
import com.example.data.model.ChatMessage
import com.example.data.model.Booking

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retryAction: (() -> Unit)? = null) : UiState<Nothing>()
}

data class SearchUiState(
    val query: String = "",
    val results: List<RentalItem> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val isSearching: Boolean = false,
    val filters: SearchFilters = SearchFilters(),
    val savedSearches: List<SavedSearch> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val trendingSearches: List<String> = emptyList()
)

data class SearchFilters(
    val category: String? = null,
    val city: String? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val sortBy: SortBy = SortBy.RECENT
)

enum class SortBy { RECENT, PRICE_ASC, PRICE_DESC, POPULARITY }

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val otherUserOnline: Boolean = false,
    val otherUserLastSeen: Long = 0,
    val unreadCount: Int = 0
)

data class BookingUiState(
    val bookings: List<Booking> = emptyList(),
    val selectedBooking: Booking? = null,
    val isLoading: Boolean = false
)

data class MapUiState(
    val listings: List<RentalItem> = emptyList(),
    val selectedListingId: Int? = null,
    val selectedCity: String = "Libreville",
    val userLat: Double = 0.3763,
    val userLng: Double = 9.4536
)
