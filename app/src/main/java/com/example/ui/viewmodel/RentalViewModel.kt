package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.RentalRepository
import com.example.ui.components.SortOption
import com.example.ui.model.RentalCategory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class RentalReview(
    val rentalItemId: Int,
    val rating: Int,
    val comment: String,
    val author: String,
    val date: String
)

sealed interface PaymentState {
    object Idle : PaymentState
    data class Processing(val status: String) : PaymentState
    data class AwaitingPin(
        val rentalItem: RentalItem,
        val days: Int,
        val paymentMethod: String,
        val phoneInput: String
    ) : PaymentState
    data class Success(val booking: Booking) : PaymentState
}

sealed interface Screen {
    data object Home : Screen
    data object Details : Screen
    data object Bookmarks : Screen
    data object Bookings : Screen
    data object Messages : Screen
    data object Chat : Screen
    data object PostListing : Screen
    data object Profile : Screen
}

data class EarningEntry(val id: Int, val amount: Int, val date: String, val source: String, val status: String)
data class NotificationEntry(val id: Int, val type: String, val title: String, val message: String, val time: String, val isRead: Boolean)
data class DisputeEntry(val id: String, val title: String, val status: String, val date: String, val type: String, val description: String = "", val claimAmount: Int = 0)
data class PaymentEntry(val id: Int, val amount: Int, val date: String, val description: String, val method: String)
// ReceivedBooking removed — using ReceivedReservation from Entities.kt
data class MediationMessage(val sender: String, val message: String, val time: String, val isSystem: Boolean = false)

class RentalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RentalRepository(db.rentalDao())

    // Reviews wired to Room
    private val _reviews = MutableStateFlow<Map<Int, List<RentalReview>>>(emptyMap())
    val reviews: StateFlow<Map<Int, List<RentalReview>>> = _reviews.asStateFlow()

    fun reviewsFor(itemId: Int): Flow<List<RentalReview>> =
        repository.getReviewsForItem(itemId).map { entities ->
            entities.map { e -> RentalReview(e.rentalItemId, e.rating, e.comment, e.author, e.date) }
        }

    // Onboarding
    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    // Authentication
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authState = MutableStateFlow("login")
    val authState: StateFlow<String> = _authState.asStateFlow()

    // Profile (encapsulated via functions)
    private val _profileDob = MutableStateFlow("")
    val profileDob: StateFlow<String> = _profileDob.asStateFlow()

    private val _profileGender = MutableStateFlow("")
    val profileGender: StateFlow<String> = _profileGender.asStateFlow()

    private val _profileProfession = MutableStateFlow("")
    val profileProfession: StateFlow<String> = _profileProfession.asStateFlow()

    private val _profileCity = MutableStateFlow("")
    val profileCity: StateFlow<String> = _profileCity.asStateFlow()

    private val _isPhoneVerified = MutableStateFlow(false)
    val isPhoneVerified: StateFlow<Boolean> = _isPhoneVerified.asStateFlow()

    private val _isSocialLinked = MutableStateFlow(false)
    val isSocialLinked: StateFlow<Boolean> = _isSocialLinked.asStateFlow()

    private val _profilePhotoEnabled = MutableStateFlow(true)
    val profilePhotoEnabled: StateFlow<Boolean> = _profilePhotoEnabled.asStateFlow()

    private val _userName = MutableStateFlow("Marie-Claire Nzamba")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("+241 77 12 34 56")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _isOwnerMode = MutableStateFlow(false)
    val isOwnerMode: StateFlow<Boolean> = _isOwnerMode.asStateFlow()

    private val _profileLanguage = MutableStateFlow("Français")
    val profileLanguage: StateFlow<String> = _profileLanguage.asStateFlow()

    private val _identityVerificationStatus = MutableStateFlow("Non vérifié")
    val identityVerificationStatus: StateFlow<String> = _identityVerificationStatus.asStateFlow()

    private val _withdrawableBalance = MutableStateFlow(850000)
    val withdrawableBalance: StateFlow<Int> = _withdrawableBalance.asStateFlow()

    // Navigation (typed)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Profile navigation state (passed between screens)
    private val _selectedDisputeId = MutableStateFlow<String?>(null)
    val selectedDisputeId: StateFlow<String?> = _selectedDisputeId.asStateFlow()

    private val _activeDamageSelection = MutableStateFlow<com.example.data.model.ReceivedReservation?>(null)
    val activeDamageSelection: StateFlow<com.example.data.model.ReceivedReservation?> = _activeDamageSelection.asStateFlow()

    private val _activeReviewSelection = MutableStateFlow<com.example.data.model.ReceivedReservation?>(null)
    val activeReviewSelection: StateFlow<com.example.data.model.ReceivedReservation?> = _activeReviewSelection.asStateFlow()

    fun setSelectedDispute(id: String) { _selectedDisputeId.value = id }
    fun setDamageSelection(res: com.example.data.model.ReceivedReservation?) { _activeDamageSelection.value = res }
    fun setReviewSelection(res: com.example.data.model.ReceivedReservation?) { _activeReviewSelection.value = res }

    // Filters (encapsulated)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tous")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedCity = MutableStateFlow("Tous")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedMaxPrice = MutableStateFlow(0)
    val selectedMaxPrice: StateFlow<Int> = _selectedMaxPrice.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RECENT)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Selected item
    private val _selectedItem = MutableStateFlow<RentalItem?>(null)
    val selectedItem: StateFlow<RentalItem?> = _selectedItem.asStateFlow()

    // Payment
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    // Unread messages count (derived from chat data)
    private val allChats: StateFlow<List<ChatMessage>> = repository.getAllChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadMessageCount: StateFlow<Int> = allChats.map { chats ->
        val counts = mutableMapOf<String, Int>()
        for (chat in chats) {
            if (chat.sender != "me" && chat.sender != "User") {
                counts[chat.sender] = (counts[chat.sender] ?: 0) + 1
            }
        }
        counts.size.coerceAtMost(99)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Referral system
    private val _referralCount = MutableStateFlow(3)
    val referralCount: StateFlow<Int> = _referralCount.asStateFlow()

    private val _referralEarnings = MutableStateFlow(15000)
    val referralEarnings: StateFlow<Int> = _referralEarnings.asStateFlow()

    // Earnings wired to Room
    private val _earnings: StateFlow<List<EarningEntry>> = repository.earnings.map { entities ->
        entities.map { e -> EarningEntry(e.id, e.amount, e.date, e.source, e.status) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val earnings: StateFlow<List<EarningEntry>> = _earnings

    // Notifications wired to Room
    private val _notifications: StateFlow<List<NotificationEntry>> = repository.notifications.map { entities ->
        entities.map { e ->
            NotificationEntry(
                id = e.id,
                type = e.type,
                title = e.title,
                message = e.message,
                time = e.time,
                isRead = e.isRead
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notifications: StateFlow<List<NotificationEntry>> = _notifications

    val unreadNotificationCount: StateFlow<Int> = repository.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markNotificationRead(id: Int) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
            showSnackbar("Toutes les notifications marquées comme lues")
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch { repository.deleteNotification(id) }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
            showSnackbar("Notifications effacées")
        }
    }

    // Disputes wired to Room
    private val _disputes: StateFlow<List<DisputeEntry>> = repository.disputes.map { entities ->
        entities.map { e ->
            DisputeEntry(
                id = "LIT-${e.id.toString().padStart(3, '0')}",
                title = e.title,
                status = e.status,
                date = e.date,
                type = e.type,
                description = e.description,
                claimAmount = e.claimAmount
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val disputes: StateFlow<List<DisputeEntry>> = _disputes

    // Insurance state
    private val _activeInsurancePlan = MutableStateFlow<String?>(null)
    val activeInsurancePlan: StateFlow<String?> = _activeInsurancePlan.asStateFlow()

    // Payment history wired to Room
    private val _paymentHistory: StateFlow<List<PaymentEntry>> = repository.paymentHistory.map { entities ->
        entities.map { e -> PaymentEntry(e.id, e.amount, e.date, e.description, e.method) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val paymentHistory: StateFlow<List<PaymentEntry>> = _paymentHistory

    // Received bookings (owner view)
    private val _receivedBookings = MutableStateFlow(listOf(
        ReceivedReservation("RB-001", "Sophie Nguema", 4.8f, "Appartement Vue Mer", "Immobilier", "En attente", "20-25 juin 2026", 5, 75000, "+241 06 12 34 56"),
        ReceivedReservation("RB-002", "Paul Obiang", 4.5f, "Toyota Hilux", "Véhicules", "Confirmée", "1-3 juillet 2026", 3, 45000, "+241 07 23 45 67"),
        ReceivedReservation("RB-003", "Marie-Claire", 4.9f, "Villa La Sablière", "Immobilier", "Confirmée", "10-17 juillet 2026", 7, 120000, "+241 06 34 56 78")
    ))
    val receivedBookings: StateFlow<List<ReceivedReservation>> = _receivedBookings.asStateFlow()

    // Mediation messages
    private val _mediationMessages = MutableStateFlow(listOf(
        MediationMessage("Système", "Litige ouvert pour 'Dommage Toyota Hilux'", "20/06/2026 14:30", true),
        MediationMessage("Vous", "Le pare-chocs avant a été endommagé lors de la location", "20/06/2026 14:35"),
        MediationMessage("Kwame Asante", "J'ai récupéré le véhicule dans cet état. Ce n'est pas de ma faute.", "20/06/2026 15:10"),
        MediationMessage("Médiateur LocAll", "Nous examinons les photos et les témoignages. Délai estimé : 48h.", "20/06/2026 16:00", true),
        MediationMessage("Système", "Enquête en cours — Les deux parties ont été contactées", "21/06/2026 09:00", true)
    ))
    val mediationMessages: StateFlow<List<MediationMessage>> = _mediationMessages.asStateFlow()

    // Loading states for skeleton screens
    private val _isHomeLoading = MutableStateFlow(true)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading.asStateFlow()

    private val _isBookmarksLoading = MutableStateFlow(true)
    val isBookmarksLoading: StateFlow<Boolean> = _isBookmarksLoading.asStateFlow()

    private val _isBookingsLoading = MutableStateFlow(true)
    val isBookingsLoading: StateFlow<Boolean> = _isBookingsLoading.asStateFlow()

    private val _isInboxLoading = MutableStateFlow(true)
    val isInboxLoading: StateFlow<Boolean> = _isInboxLoading.asStateFlow()

    // Snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.seedDatabase()
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }



        // Simulate loading delays for skeleton screens
        viewModelScope.launch {
            try {
                delay(1200)
                _isHomeLoading.value = false
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
        viewModelScope.launch {
            try {
                delay(800)
                _isBookmarksLoading.value = false
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
        viewModelScope.launch {
            try {
                delay(1000)
                _isBookingsLoading.value = false
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
        viewModelScope.launch {
            try {
                delay(600)
                _isInboxLoading.value = false
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    // ==================== FILTER ACTIONS (encapsulated) ====================
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                try {
                    repository.insertSearchHistory(SearchHistoryEntry(query = query))
                } catch (e: Exception) {
                    showSnackbar("Une erreur est survenue: ${e.message}")
                }
            }
        }
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedCity(city: String) {
        _selectedCity.value = city
    }

    fun setSelectedMaxPrice(maxPrice: Int) {
        _selectedMaxPrice.value = maxPrice
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    // Advanced filters: date range and distance
    private val _startDate = MutableStateFlow<String?>(null)
    val startDate: StateFlow<String?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<String?>(null)
    val endDate: StateFlow<String?> = _endDate.asStateFlow()

    private val _maxDistance = MutableStateFlow(50f)
    val maxDistance: StateFlow<Float> = _maxDistance.asStateFlow()

    fun setStartDate(date: String?) { _startDate.value = date }
    fun setEndDate(date: String?) { _endDate.value = date }
    fun setMaxDistance(distance: Float) { _maxDistance.value = distance }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = "Tous"
        _selectedCity.value = "Tous"
        _selectedMaxPrice.value = 0
        _sortOption.value = SortOption.RECENT
        _startDate.value = null
        _endDate.value = null
        _maxDistance.value = 50f
    }

    fun addReferral() {
        _referralCount.value += 1
        _referralEarnings.value += 5000
        showSnackbar("5 000 F CFA de crédit ajouté pour le parrainage !")
    }

    fun acceptReceivedBooking(id: String) {
        _receivedBookings.value = _receivedBookings.value.map { if (it.id == id) it.copy(status = "Confirmée") else it }
    }

    fun refuseReceivedBooking(id: String) {
        _receivedBookings.value = _receivedBookings.value.map { if (it.id == id) it.copy(status = "Refusée") else it }
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

    // ==================== PROFILE ACTIONS ====================
    fun setOwnerMode(enabled: Boolean) {
        _isOwnerMode.value = enabled
    }

    fun setProfileLanguage(lang: String) {
        _profileLanguage.value = lang
    }

    fun setIdentityVerificationStatus(status: String) {
        _identityVerificationStatus.value = status
    }

    fun withdrawFunds(amount: Int) {
        if (_withdrawableBalance.value >= amount) {
            _withdrawableBalance.value -= amount
            showSnackbar("Retrait de ${amount} F effectué avec succès")
        }
    }

    fun updateProfile(dob: String, gender: String, profession: String, city: String) {
        _profileDob.value = dob
        _profileGender.value = gender
        _profileProfession.value = profession
        _profileCity.value = city
        viewModelScope.launch {
            try {
                repository.upsertUserProfile(
                    UserProfile(
                        fullName = "Marie-Claire Nzamba",
                        phone = "+241 77 12 34 56",
                        dob = dob,
                        gender = gender,
                        profession = profession,
                        city = city,
                        language = _profileLanguage.value,
                        isOwnerMode = _isOwnerMode.value,
                        isPhoneVerified = _isPhoneVerified.value,
                        isSocialLinked = _isSocialLinked.value,
                        identityStatus = _identityVerificationStatus.value
                    )
                )
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun setPhoneVerified(verified: Boolean) {
        _isPhoneVerified.value = verified
    }

    fun setSocialLinked(linked: Boolean) {
        _isSocialLinked.value = linked
    }

    fun setAuthState(state: String) {
        _authState.value = state
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _isLoggedIn.value = loggedIn
    }

    // ==================== NAVIGATION ====================
    fun navigateTo(screen: String) {
        _currentScreen.value = when (screen) {
            "home" -> Screen.Home
            "details" -> Screen.Details
            "bookmarks" -> Screen.Bookmarks
            "bookings" -> Screen.Bookings
            "messages" -> Screen.Messages
            "chat" -> Screen.Chat
            "post_listing" -> Screen.PostListing
            "profile" -> Screen.Profile
            else -> Screen.Home
        }
    }

    fun selectItem(item: RentalItem) {
        _selectedItem.value = item
    }

    // ==================== ONBOARDING ====================
    fun nextOnboarding() {
        val current = _onboardingStep.value
        if (current < 3) {
            _onboardingStep.value = current + 1
        } else {
            _onboardingStep.value = 4
        }
    }

    fun skipOnboarding() {
        _onboardingStep.value = 4
    }

    fun restartOnboarding() {
        _onboardingStep.value = 0
        _currentScreen.value = Screen.Home
    }

    // ==================== BOOKMARK ====================
    fun toggleBookmark(item: RentalItem) {
        viewModelScope.launch {
            try {
                repository.updateBookmarkStatus(item.id, !item.isBookmarked)
                if (_selectedItem.value?.id == item.id) {
                    _selectedItem.value = _selectedItem.value?.copy(isBookmarked = !item.isBookmarked)
                }
                showSnackbar(
                    if (!item.isBookmarked) "Ajouté aux favoris" else "Retiré des favoris"
                )
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    // ==================== BOOKING ====================
    fun initiateBooking(
        rentalItem: RentalItem,
        days: Int,
        paymentMethod: String,
        phoneInput: String
    ) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.Processing("Initialisation de la transaction pour " + rentalItem.title + "...")
                delay(1500)
                _paymentState.value = PaymentState.Processing(
                    "Demande de paiement de " + (rentalItem.pricePerDay * days) + " F CFA envoyée à " + paymentMethod + " (" + phoneInput + ")."
                )
                delay(1500)
                _paymentState.value = PaymentState.AwaitingPin(rentalItem, days, paymentMethod, phoneInput)
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun confirmBookingPayment(
        rentalItem: RentalItem,
        days: Int,
        paymentMethod: String,
        phoneInput: String,
        pinCode: String
    ) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.Processing("Validation du code PIN et sécurisation des fonds de caution...")
                delay(2000)
                val totalPrice = rentalItem.pricePerDay * days
                val newBooking = Booking(
                    rentalItemId = rentalItem.id,
                    rentalItemTitle = rentalItem.title,
                    rentalItemCategory = rentalItem.category,
                    pricePerDay = rentalItem.pricePerDay,
                    days = days,
                    totalPrice = totalPrice,
                    paymentMethod = paymentMethod,
                    paymentPhone = phoneInput,
                    status = "Payé"
                )
                repository.insertBooking(newBooking)
                _paymentState.value = PaymentState.Success(newBooking)
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun cancelBooking(bookingId: Int, reason: String) {
        viewModelScope.launch {
            try {
                repository.updateBookingStatus(bookingId, "Annulé", reason)
                showSnackbar("Réservation annulée")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun updateBookingStatus(bookingId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateBookingStatus(bookingId, newStatus, null)
                showSnackbar("Réservation ${newStatus.lowercase()} !")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    // ==================== REVIEWS ====================
    fun addReview(rentalItemId: Int, rating: Int, comment: String) {
        val authorName = "Visiteur Gabonais"
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        viewModelScope.launch {
            try {
                repository.insertReview(
                    ReviewEntity(
                        rentalItemId = rentalItemId,
                        rating = rating,
                        comment = comment,
                        author = authorName,
                        date = currentDate
                    )
                )
                showSnackbar("Avis publié avec succès")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    // ==================== REACTIVE DATA ====================
    val searchHistory: StateFlow<List<SearchHistoryEntry>> = repository.searchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawRentalItems: StateFlow<List<RentalItem>> = repository.allRentalItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItems: StateFlow<List<RentalItem>> = repository.bookmarkedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRentalItems: StateFlow<List<RentalItem>> = combine(
        rawRentalItems, _searchQuery, _selectedCategory, _selectedCity, _selectedMaxPrice, _sortOption, _startDate, _endDate, _maxDistance
    ) { args ->
        val allItems = args[0] as List<RentalItem>
        val query = args[1] as String
        val category = args[2] as String
        val city = args[3] as String
        val maxPrice = args[4] as Int
        val sort = args[5] as SortOption
        val startDate = args[6] as String?
        val endDate = args[7] as String?
        val maxDistance = args[8] as Float

        allItems.filter { item ->
            val matchesQuery = query.isEmpty() || item.title.contains(query, ignoreCase = true) || item.description.contains(query, ignoreCase = true) || item.neighborhood.contains(query, ignoreCase = true)
            val matchesCategory = category == "Tous" || item.category.equals(category, ignoreCase = true)
            val matchesCity = city == "Tous" || item.city.equals(city, ignoreCase = true)
            val matchesPrice = maxPrice == 0 || item.pricePerDay <= maxPrice
            // Date and distance filters are UI-only for now (no real geolocation data)
            matchesQuery && matchesCategory && matchesCity && matchesPrice
        }.let { filtered ->
            when (sort) {
                SortOption.PRICE_ASC -> filtered.sortedBy { it.pricePerDay }
                SortOption.PRICE_DESC -> filtered.sortedByDescending { it.pricePerDay }
                SortOption.RECENT -> filtered.sortedByDescending { it.id }
                SortOption.RATING -> filtered.sortedByDescending { it.ownerRating }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val similarItems: StateFlow<List<RentalItem>> = _selectedItem.flatMapLatest { item ->
        if (item != null) {
            repository.getSimilarItems(item.id, item.category)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat
    private val _activeChatRentalId = MutableStateFlow<Int?>(null)
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatRentalId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getChatMessagesForRental(id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ==================== CHAT ACTIONS ====================
    fun openChatFor(item: RentalItem) {
        _activeChatRentalId.value = item.id
        viewModelScope.launch {
            try {
                val currentMsgs = repository.getChatMessagesForRental(item.id).first()
                if (currentMsgs.isEmpty()) {
                    repository.insertChatMessage(
                        ChatMessage(
                            rentalItemId = item.id,
                            sender = "Owner",
                            messageText = "Bonjour, je suis ${item.ownerName}, propriétaire de l'offre [${item.title}]. En quoi puis-je vous aider aujourd'hui ? Le bien est disponible sur ${item.city} (${item.neighborhood})."
                        )
                    )
                }
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun sendChatMessage(rentalId: Int, messageText: String, ownerName: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
            try {
                repository.insertChatMessage(
                    ChatMessage(
                        rentalItemId = rentalId,
                        sender = "User",
                        messageText = messageText
                    )
                )

                delay(1500)
                val responseText = when {
                    messageText.contains("disponible", ignoreCase = true) || messageText.contains("dispo", ignoreCase = true) -> {
                        "Absolument! L'offre est entièrement disponible. Vous pouvez effectuer la réservation directement sur LocAll avec Airtel Money ou Moov Money pour bloquer les dates!"
                    }
                    messageText.contains("prix", ignoreCase = true) || messageText.contains("tarif", ignoreCase = true) || messageText.contains("reduction", ignoreCase = true) -> {
                        "Le tarif est fixé à la journée. Si vous louez pour plus d'une semaine, je peux vous faire un geste commercial. N'hésitez pas à lancer la réservation pour en discuter."
                    }
                    messageText.contains("visite", ignoreCase = true) || messageText.contains("voir", ignoreCase = true) -> {
                        "Bien sûr, la visite est tout à fait possible. Dites-moi quand vous seriez disponible !"
                    }
                    else -> {
                        "Merci pour votre message ! C'est noté. Que souhaitez-vous savoir d'autre sur cette location pour finaliser notre accord ?"
                    }
                }
                repository.insertChatMessage(
                    ChatMessage(
                        rentalItemId = rentalId,
                        sender = "Owner",
                        messageText = responseText
                    )
                )
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    // ==================== LISTING ACTIONS ====================
    fun postNewListing(
        title: String,
        description: String,
        category: String,
        price: Int,
        city: String,
        neighborhood: String,
        ownerName: String,
        ownerPhone: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            try {
                val img = if (imageUrl.isBlank()) {
                    val rentalCat = RentalCategory.fromString(category)
                    when (rentalCat) {
                        RentalCategory.IMMOBILIER -> "https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.VEHICULES -> "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.EQUIPEMENTS -> "https://images.unsplash.com/photo-1504148455328-c376907d081c?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.EVENEMENTIEL -> "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.MODE_BEAUTE -> "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.SERVICES -> "https://images.unsplash.com/photo-1521791136064-7986c2920216?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.ESPACES -> "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.MATERIEL_PRO -> "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.MARINE_FLUVIAL -> "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80"
                        RentalCategory.SPORT_LOISIRS -> "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=800&q=80"
                    }
                } else imageUrl

                val newItem = RentalItem(
                    title = title,
                    description = description,
                    category = category,
                    pricePerDay = price,
                    city = city,
                    neighborhood = neighborhood,
                    ownerName = ownerName,
                    ownerPhone = ownerPhone,
                    ownerRating = 5.0f,
                    imageUrl = img,
                    isVerified = true
                )
                repository.insertRentalItem(newItem)
                showSnackbar("Annonce publiée avec succès !")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun deleteListing(itemId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteRentalItem(itemId)
                showSnackbar("Annonce supprimée")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun updateUserProfile(name: String, phone: String) {
        viewModelScope.launch {
            try {
                val current = repository.getUserProfileOnce()
                if (current != null) {
                    repository.updateUserProfileFields(name, phone, current.email, current.dob, current.gender, current.profession, current.city)
                } else {
                    repository.upsertUserProfile(UserProfile(id = 1, fullName = name, phone = phone, dob = "", gender = "", city = "", profession = ""))
                }
                _userName.value = name
                _userPhone.value = phone
                showSnackbar("Profil mis à jour avec succès")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun updateProfileFull(
        name: String, phone: String, email: String, dob: String,
        gender: String, profession: String, city: String
    ) {
        viewModelScope.launch {
            try {
                repository.updateUserProfileFields(name, phone, email, dob, gender, profession, city)
                _userName.value = name
                _userPhone.value = phone
                _profileDob.value = dob
                _profileGender.value = gender
                _profileProfession.value = profession
                _profileCity.value = city
                showSnackbar("Profil mis à jour avec succès")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                repository.deleteAllUserData()
                _userName.value = ""
                _userPhone.value = ""
                _profileDob.value = ""
                _profileGender.value = ""
                _profileProfession.value = ""
                _profileCity.value = ""
                _isLoggedIn.value = false
                showSnackbar("Compte supprimé avec succès")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun addDispute(description: String, type: String) {
        viewModelScope.launch {
            try {
                repository.insertDispute(
                    DisputeEntity(
                        title = "$type - $type",
                        status = "En cours",
                        date = "Aujourd'hui",
                        type = type,
                        description = description,
                        claimAmount = 0
                    )
                )
                _mediationMessages.value = listOf(
                    MediationMessage("Système", "Nouveau litige '$type' ouvert", "Maintenant", true),
                    MediationMessage("Vous", description, "Maintenant")
                ) + _mediationMessages.value
                showSnackbar("Litige créé avec succès")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun subscribeInsurance(plan: String) {
        _activeInsurancePlan.value = plan
        showSnackbar("Assurance $plan souscrite !")
    }

    // ==================== SORT ====================
    fun sortItemsBy(option: SortOption) {
        _sortOption.value = option
    }
}

class RentalViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RentalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RentalViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
