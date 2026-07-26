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
import com.example.ui.state.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


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
    data object MapExplorer : Screen
    data object SearchIntelligence : Screen
    data object OwnerAnalytics : Screen
    data object MarketInsights : Screen
    data object NotificationSettings : Screen
    data object ReferralTracking : Screen
}

data class EarningEntry(val id: Int, val amount: Int, val date: String, val source: String, val status: String)
data class NotificationEntry(val id: Int, val type: String, val title: String, val message: String, val time: String, val isRead: Boolean)
data class DisputeEntry(val id: String, val title: String, val status: String, val date: String, val type: String, val description: String = "", val claimAmount: Int = 0)
data class PaymentEntry(val id: Int, val amount: Int, val date: String, val description: String, val method: String)
// ReceivedBooking removed — using ReceivedReservation from Entities.kt
data class MediationMessage(val sender: String, val message: String, val time: String, val isSystem: Boolean = false)

class RentalViewModel(
    application: Application,
    private val repository: RentalRepository
) : AndroidViewModel(application) {

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

    // ==================== SEARCH INTELLIGENCE ====================
    val savedSearches: StateFlow<List<SavedSearch>> = repository.savedSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingSearches: StateFlow<List<SearchSuggestion>> = repository.trendingSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val voiceSearchHistory: StateFlow<List<VoiceSearchHistory>> = repository.voiceSearchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _intelligentSearchQuery = MutableStateFlow("")
    val intelligentSearchQuery: StateFlow<String> = _intelligentSearchQuery.asStateFlow()

    val searchSuggestions: StateFlow<List<SearchSuggestion>> = _intelligentSearchQuery
        .flatMapLatest { query ->
            if (query.length >= 2) repository.searchSuggestions(query) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isVoiceSearching = MutableStateFlow(false)
    val isVoiceSearching: StateFlow<Boolean> = _isVoiceSearching.asStateFlow()

    private val _voiceSearchResult = MutableStateFlow<String?>(null)
    val voiceSearchResult: StateFlow<String?> = _voiceSearchResult.asStateFlow()

    private val _searchAnalytics = repository.getSearchAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val searchAnalytics: StateFlow<List<SearchSuggestion>> = _searchAnalytics

    fun setIntelligentSearchQuery(query: String) { _intelligentSearchQuery.value = query }

    fun fuzzySearch(query: String): List<RentalItem> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase().trim()
        return rawRentalItems.value.filter { item ->
            val text = "${item.title} ${item.description} ${item.city} ${item.neighborhood}".lowercase()
            fuzzyMatch(q, text)
        }
    }

    private fun fuzzyMatch(query: String, text: String): Boolean {
        if (query.length <= 2) return text.contains(query, ignoreCase = true)
        var qi = 0
        for (c in text) {
            if (qi < query.length && c == query[qi]) qi++
        }
        return qi == query.length
    }

    fun saveCurrentSearch() {
        viewModelScope.launch {
            try {
                repository.saveSearch(
                    query = _intelligentSearchQuery.value.ifBlank { _searchQuery.value },
                    category = _selectedCategory.value.ifBlank { null },
                    city = _selectedCity.value.ifBlank { null },
                    minPrice = null,
                    maxPrice = _selectedMaxPrice.value
                )
                showSnackbar("Recherche sauvegardée")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun deleteSavedSearch(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteSavedSearch(id)
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun toggleSearchAlert(id: Int, enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleSearchAlert(id, enabled)
                showSnackbar(if (enabled) "Alertes activées" else "Alertes désactivées")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun startVoiceSearch() {
        viewModelScope.launch {
            try {
                _isVoiceSearching.value = true
                _voiceSearchResult.value = null
                delay(1500)
                val mockResults = listOf(
                    "Appartement meublé Libreville",
                    "Véhicule 4x4 Port-Gentil",
                    "Salle de fête Akanda",
                    "Terrain constructible Owendo",
                    "Caméra professionnelle Libreville"
                )
                val result = mockResults.random()
                _voiceSearchResult.value = result
                _intelligentSearchQuery.value = result
                _searchQuery.value = result
                repository.logVoiceSearch(result, result)
                _isVoiceSearching.value = false
                showSnackbar("Recherche vocale : $result")
            } catch (e: Exception) {
                _isVoiceSearching.value = false
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun logSearchToAnalytics(query: String) {
        viewModelScope.launch {
            try {
                repository.logSearch(query)
            } catch (e: Exception) {
                // silent
            }
        }
    }

    // ==================== ANALYTICS & GROWTH ====================
    val ownerAnalytics: StateFlow<OwnerAnalytics?> = repository.ownerAnalytics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val marketInsights: StateFlow<List<MarketInsight>> = repository.marketInsights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pushNotificationSettings: StateFlow<PushNotificationSetting?> = repository.pushNotificationSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val referralTrackingList: StateFlow<List<ReferralTracking>> = repository.referralTracking
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun savePushNotificationSettings(settings: PushNotificationSetting) {
        viewModelScope.launch {
            try {
                repository.insertPushNotificationSettings(settings)
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun seedAnalyticsData() {
        viewModelScope.launch {
            try {
                repository.insertOwnerAnalytics(
                    OwnerAnalytics(
                        totalViews = 1847,
                        totalInquiries = 124,
                        conversionRate = 6.7f,
                        averageResponseTime = "12 min",
                        topListingId = 1
                    )
                )
                val insights = listOf(
                    MarketInsight(category = "Immobilier", city = "Libreville", averagePrice = 72000, listingCount = 8, demandLevel = "Élevée", trend = "up", period = "Juin 2026"),
                    MarketInsight(category = "Véhicules", city = "Libreville", averagePrice = 65000, listingCount = 6, demandLevel = "Moyenne", trend = "stable", period = "Juin 2026"),
                    MarketInsight(category = "Équipements", city = "Libreville", averagePrice = 42000, listingCount = 5, demandLevel = "Élevée", trend = "up", period = "Juin 2026"),
                    MarketInsight(category = "Immobilier", city = "Port-Gentil", averagePrice = 48000, listingCount = 3, demandLevel = "Moyenne", trend = "up", period = "Juin 2026"),
                    MarketInsight(category = "Véhicules", city = "Franceville", averagePrice = 55000, listingCount = 2, demandLevel = "Faible", trend = "down", period = "Juin 2026")
                )
                for (insight in insights) {
                    repository.insertMarketInsight(insight)
                }
                repository.insertPushNotificationSettings(PushNotificationSetting())
                val referrals = listOf(
                    ReferralTracking(referredUserId = 101, referredUserName = "Sophie Nguema", status = "verified", rewardEarned = 5000),
                    ReferralTracking(referredUserId = 102, referredUserName = "Paul Obiang", status = "verified", rewardEarned = 5000),
                    ReferralTracking(referredUserId = 103, referredUserName = "Marie-Claire", status = "pending", rewardEarned = 0)
                )
                for (ref in referrals) {
                    repository.insertReferralTracking(ref)
                }
            } catch (e: Exception) {
                // silent
            }
        }
    }

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

    // Insurance subscription from Room
    val insuranceSubscription: StateFlow<com.example.data.model.InsuranceSubscription?> = repository.insuranceSubscription
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Insurance claims from Room
    val insuranceClaims: StateFlow<List<com.example.data.model.InsuranceClaim>> = repository.insuranceClaims
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
                seedAnalyticsData()
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
        onFirstShare()
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
            "map_explorer" -> Screen.MapExplorer
            "search_intelligence" -> Screen.SearchIntelligence
            "owner_analytics" -> Screen.OwnerAnalytics
            "market_insights" -> Screen.MarketInsights
            "notification_settings" -> Screen.NotificationSettings
            "referral_tracking" -> Screen.ReferralTracking
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
                val receipt = PaymentReceipt(
                    bookingId = newBooking.id,
                    receiptNumber = "LOC-${System.currentTimeMillis()}",
                    amount = totalPrice,
                    paymentMethod = paymentMethod,
                    payerName = "Utilisateur",
                    payeeName = rentalItem.ownerName,
                    date = System.currentTimeMillis()
                )
                repository.insertPaymentReceipt(receipt)
                _paymentState.value = PaymentState.Success(newBooking)
                onBookingComplete()
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

    fun createMockBooking(day: Int, monthYear: String) {
        viewModelScope.launch {
            try {
                val cal = java.util.Calendar.getInstance()
                cal.set(java.util.Calendar.DAY_OF_MONTH, day)
                val timestamp = cal.timeInMillis
                val mockBooking = Booking(
                    rentalItemId = 1,
                    rentalItemTitle = "Réservation Calendrier",
                    rentalItemCategory = "Immobilier",
                    pricePerDay = 25000,
                    days = 1,
                    totalPrice = 25000,
                    paymentMethod = "Airtel Money",
                    paymentPhone = "+241 07 00 00 00",
                    bookingTimestamp = timestamp,
                    status = "Payé"
                )
                repository.insertBooking(mockBooking)
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    private val _selectedBookingForReceipt = MutableStateFlow<Booking?>(null)
    val selectedBookingForReceipt: StateFlow<Booking?> = _selectedBookingForReceipt.asStateFlow()

    fun showReceiptForBooking(booking: Booking) {
        _selectedBookingForReceipt.value = booking
    }

    fun dismissReceiptDialog() {
        _selectedBookingForReceipt.value = null
    }

    private val _bookingReviewTarget = MutableStateFlow<Booking?>(null)
    val bookingReviewTarget: StateFlow<Booking?> = _bookingReviewTarget.asStateFlow()

    fun startReviewForBooking(booking: Booking) {
        _bookingReviewTarget.value = booking
    }

    fun dismissBookingReview() {
        _bookingReviewTarget.value = null
    }

    fun submitBookingReview(booking: Booking, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                repository.insertReview(
                    ReviewEntity(
                        rentalItemId = booking.rentalItemId,
                        rating = rating,
                        comment = comment,
                        author = "Utilisateur",
                        date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE).format(java.util.Date())
                    )
                )
                repository.updateBookingStatus(booking.id, "Terminé", null)
                _bookingReviewTarget.value = null
                showSnackbar("Avis publié ! Merci pour votre retour")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun respondToReview(reviewId: Int, response: String) {
        viewModelScope.launch {
            try {
                showSnackbar("Réponse envoyée au locataire")
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    private val _inboxSearchQuery = MutableStateFlow("")
    val inboxSearchQuery: StateFlow<String> = _inboxSearchQuery.asStateFlow()

    fun setInboxSearchQuery(query: String) {
        _inboxSearchQuery.value = query
    }

    val rawRentalItems: StateFlow<List<RentalItem>> = repository.allRentalItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredInboxItems: StateFlow<List<RentalItem>> = combine(
        rawRentalItems, _inboxSearchQuery
    ) { items, query ->
        if (query.isBlank()) items
        else items.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.ownerName.contains(query, ignoreCase = true) ||
            it.city.contains(query, ignoreCase = true) ||
            it.neighborhood.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
                onReviewSubmit()
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    // ==================== REACTIVE DATA ====================
    val searchHistory: StateFlow<List<SearchHistoryEntry>> = repository.searchHistory
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
                val userMessage = ChatMessage(
                    rentalItemId = rentalId,
                    sender = "User",
                    messageText = messageText,
                    status = "sent"
                )
                repository.insertChatMessage(userMessage)

                delay(1000)
                repository.updateMessageStatus(userMessage.id, "delivered")

                delay(2000)
                repository.updateMessageStatus(userMessage.id, "read")

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
        viewModelScope.launch {
            try {
                val planPrice = when(plan) {
                    "basic" -> 5000
                    "standard" -> 10000
                    "premium" -> 20000
                    else -> 0
                }
                if (_walletBalance.value >= planPrice) {
                    _walletBalance.value -= planPrice
                    repository.insertInsuranceSubscription(
                        com.example.data.model.InsuranceSubscription(planName = plan)
                    )
                    repository.insertWalletTransaction(
                        com.example.data.model.WalletTransaction(
                            type = "insurance",
                            amount = -planPrice,
                            description = "Souscription assurance $plan"
                        )
                    )
                    _activeInsurancePlan.value = plan
                    showSnackbar("Assurance $plan souscrite ! -${planPrice} FCFA")
                } else {
                    showSnackbar("Solde insuffisant pour souscrire")
                }
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun fileInsuranceClaim(planName: String, incidentDate: String, description: String, amountClaimed: Int) {
        viewModelScope.launch {
            try {
                repository.insertInsuranceClaim(
                    com.example.data.model.InsuranceClaim(
                        planName = planName,
                        incidentDate = incidentDate,
                        description = description,
                        amountClaimed = amountClaimed,
                        status = "pending"
                    )
                )
                showSnackbar("Sinistre déclaré. Examen en cours.")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    // ==================== TRUST SCORE ====================
    private val _trustScore = MutableStateFlow(65)
    val trustScore: StateFlow<Int> = _trustScore.asStateFlow()

    private val _verificationPhone = MutableStateFlow(false)
    val verificationPhone: StateFlow<Boolean> = _verificationPhone.asStateFlow()

    private val _verificationEmail = MutableStateFlow(false)
    val verificationEmail: StateFlow<Boolean> = _verificationEmail.asStateFlow()

    private val _verificationIdCard = MutableStateFlow(false)
    val verificationIdCard: StateFlow<Boolean> = _verificationIdCard.asStateFlow()

    private val _verificationAddress = MutableStateFlow(false)
    val verificationAddress: StateFlow<Boolean> = _verificationAddress.asStateFlow()

    fun onVerificationComplete(type: String) {
        val boost = when(type) {
            "phone" -> 5
            "email" -> 5
            "id_card" -> 15
            "address" -> 10
            else -> 0
        }
        _trustScore.update { minOf(100, it + boost) }
        when(type) {
            "phone" -> _verificationPhone.value = true
            "email" -> _verificationEmail.value = true
            "id_card" -> _verificationIdCard.value = true
            "address" -> _verificationAddress.value = true
        }
        val completedCount = listOf(
            _verificationPhone.value,
            _verificationEmail.value,
            _verificationIdCard.value,
            _verificationAddress.value
        ).count { it }
        val level = when {
            completedCount == 4 -> "Entièrement vérifié"
            completedCount >= 2 -> "Partiellement vérifié"
            completedCount >= 1 -> "En cours de vérification"
            else -> "Non vérifié"
        }
        _identityVerificationStatus.value = level
        viewModelScope.launch {
            try {
                repository.updateIdentityStatus(level)
            } catch (e: Exception) {
                // silent
            }
        }
        showSnackbar("Vérification $type réussie ! Score de confiance +$boost")
    }

    fun getVerificationProgress(): Float {
        val count = listOf(
            _verificationPhone.value,
            _verificationEmail.value,
            _verificationIdCard.value,
            _verificationAddress.value
        ).count { it }
        return count / 4f
    }

    // ==================== COMMUNITY DISPUTES WITH EVIDENCE ====================
    fun fileDispute(
        listingId: Int,
        reportedUserId: Int,
        reason: String,
        description: String
    ) {
        viewModelScope.launch {
            try {
                val dispute = com.example.data.model.CommunityDispute(
                    listingId = listingId,
                    reporterId = 1,
                    reportedUserId = reportedUserId,
                    reason = reason,
                    description = description,
                    status = "open",
                    createdAt = System.currentTimeMillis()
                )
                repository.insertCommunityDispute(dispute)
                showSnackbar("Signalement envoyé. Nous examinerons votre cas.")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun addEvidenceToDispute(disputeId: Int, evidenceUrl: String) {
        viewModelScope.launch {
            try {
                val currentDisputes = repository.getAllCommunityDisputes().first()
                val dispute = currentDisputes.find { it.id == disputeId }
                if (dispute != null) {
                    val newEvidence = dispute.evidence + evidenceUrl
                    repository.updateDisputeEvidence(disputeId, newEvidence)
                    showSnackbar("Preuve ajoutée avec succès")
                }
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun voteOnDispute(disputeId: Int, isUpvote: Boolean) {
        viewModelScope.launch {
            try {
                repository.voteDispute(disputeId, if (isUpvote) 1 else -1)
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    // ==================== NEIGHBORHOOD REVIEWS ====================
    fun submitNeighborhoodReview(
        neighborhood: String,
        city: String,
        safety: Int,
        noise: Int,
        accessibility: Int,
        comment: String
    ) {
        viewModelScope.launch {
            try {
                val review = NeighborhoodReview(
                    neighborhood = neighborhood,
                    city = city,
                    userId = 1,
                    safetyRating = safety,
                    noiseRating = noise,
                    accessibilityRating = accessibility,
                    comment = comment,
                    createdAt = System.currentTimeMillis()
                )
                repository.insertNeighborhoodReview(review)
                showSnackbar("Avis publié ! Merci")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    // ==================== SORT ====================
    fun sortItemsBy(option: SortOption) {
        _sortOption.value = option
    }

    private val _selectedProfileUserId = MutableStateFlow(1)
    val selectedProfileUserId: StateFlow<Int> = _selectedProfileUserId.asStateFlow()

    fun setSelectedProfileUserId(userId: Int) { _selectedProfileUserId.value = userId }

    private val _communityDisputes: StateFlow<List<CommunityDispute>> = repository.getAllCommunityDisputes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val communityDisputes: StateFlow<List<CommunityDispute>> = _communityDisputes

    fun insertCommunityDispute(dispute: CommunityDispute) {
        viewModelScope.launch {
            try {
                repository.insertCommunityDispute(dispute)
                showSnackbar("Signalement envoyé")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun voteDispute(id: Int, delta: Int) {
        viewModelScope.launch {
            try {
                repository.voteDispute(id, delta)
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    private val _neighborhoodReviewsCity = MutableStateFlow("Libreville")
    val neighborhoodReviewsCity: StateFlow<String> = _neighborhoodReviewsCity.asStateFlow()

    fun setNeighborhoodCity(city: String) { _neighborhoodReviewsCity.value = city }

    val neighborhoodReviews: StateFlow<List<NeighborhoodReview>> = _neighborhoodReviewsCity.flatMapLatest { city ->
        repository.getNeighborhoodReviews(city)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertNeighborhoodReview(review: NeighborhoodReview) {
        viewModelScope.launch {
            try {
                repository.insertNeighborhoodReview(review)
                showSnackbar("Avis publié avec succès")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    val escrows: StateFlow<List<BookingEscrow>> = repository.getAllEscrows()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertEscrow(escrow: BookingEscrow) {
        viewModelScope.launch {
            try {
                repository.insertEscrow(escrow)
                showSnackbar("Caution enregistrée")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun releaseEscrow(id: Int) {
        viewModelScope.launch {
            try {
                repository.updateEscrowStatus(id, "released", System.currentTimeMillis())
                showSnackbar("Fonds libérés avec succès")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun refundEscrow(id: Int) {
        viewModelScope.launch {
            try {
                repository.updateEscrowStatus(id, "refunded", System.currentTimeMillis())
                showSnackbar("Fonds remboursés")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    val splitPayments: StateFlow<List<SplitPayment>> = repository.getAllSplitPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertSplitPayment(split: SplitPayment) {
        viewModelScope.launch {
            try {
                repository.insertSplitPayment(split)
                showSnackbar("Paiement partagé créé")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    val paymentReceipts: StateFlow<List<PaymentReceipt>> = repository.getAllPaymentReceipts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertPaymentReceipt(receipt: PaymentReceipt) {
        viewModelScope.launch {
            try {
                repository.insertPaymentReceipt(receipt)
                showSnackbar("Reçu enregistré")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    val calendarSyncs: StateFlow<List<CalendarSync>> = repository.getAllCalendarSyncs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleCalendarSync(id: Int, synced: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateCalendarSync(id, synced)
                showSnackbar(if (synced) "Synchronisé avec Google Calendar" else "Désynchronisé")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun insertCalendarSync(sync: CalendarSync) {
        viewModelScope.launch {
            try {
                repository.insertCalendarSync(sync)
                showSnackbar("Événement ajouté au calendrier")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun getVerificationBadges(userId: Int): Flow<List<VerificationBadge>> = repository.getVerificationBadges(userId)
    fun getFollowerCount(userId: Int): Flow<Int> = repository.getFollowerCount(userId)
    fun getFollowingCount(userId: Int): Flow<Int> = repository.getFollowingCount(userId)
    fun isFollowing(userId: Int): Flow<UserFollow?> = flow {
        emit(repository.isFollowing(1, userId))
    }

    fun toggleFollow(userId: Int) {
        viewModelScope.launch {
            try {
                repository.toggleFollow(1, userId)
                showSnackbar("Action effectuée")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    private val _mediaUploadSettings = MutableStateFlow<MediaUploadSettings?>(null)
    val mediaUploadSettings: StateFlow<MediaUploadSettings?> = _mediaUploadSettings.asStateFlow()

    fun loadMediaForListing(listingId: Int) {
        viewModelScope.launch {
            try {
                repository.getMediaItemsForListing(listingId).collect { items ->
                    _mediaItems.value = items
                }
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun addMockMediaItem(listingId: Int, mediaType: String) {
        viewModelScope.launch {
            try {
                val mockUri = when (mediaType) {
                    "image" -> "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80"
                    "video" -> "https://example.com/mock_video_${System.currentTimeMillis()}.mp4"
                    "360" -> "https://example.com/mock_360_${System.currentTimeMillis()}.jpg"
                    else -> "https://example.com/mock_media.jpg"
                }
                val item = MediaItem(
                    listingId = listingId,
                    mediaType = mediaType,
                    uri = mockUri,
                    moderationStatus = "pending"
                )
                repository.insertMediaItem(item)
                showSnackbar("Média ajouté avec succès")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun deleteMediaItem(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteMediaItem(id)
                showSnackbar("Média supprimé")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun moderateMediaItem(id: Int, status: String) {
        viewModelScope.launch {
            try {
                repository.updateMediaModerationStatus(id, status)
                showSnackbar("Statut mis à jour: $status")
            } catch (e: Exception) {
                showSnackbar("Erreur: ${e.message}")
            }
        }
    }

    fun getPendingMediaItems(): Flow<List<MediaItem>> =
        repository.getMediaItemsByStatus("pending")

    fun getApprovedMediaItems(): Flow<List<MediaItem>> =
        repository.getMediaItemsByStatus("approved")

    fun getRejectedMediaItems(): Flow<List<MediaItem>> =
        repository.getMediaItemsByStatus("rejected")

    private val _walletBalance = MutableStateFlow(125000)
    val walletBalance: StateFlow<Int> = _walletBalance.asStateFlow()

    private val _walletTransactions = MutableStateFlow(listOf(
        WalletTxn(1, "topup", 50000, "Recharge Airtel Money", null, "15/07/2026", "completed"),
        WalletTxn(2, "payment", -25000, "Réservation Villa La Sablière", 1, "14/07/2026", "completed"),
        WalletTxn(3, "earning", 75000, "Location Toyota Hilux", 2, "12/07/2026", "completed"),
        WalletTxn(4, "refund", 15000, "Annulation réservation #3", 3, "10/07/2026", "completed"),
        WalletTxn(5, "topup", 30000, "Recharge Moov Money", null, "08/07/2026", "completed"),
        WalletTxn(6, "withdrawal", -40000, "Retrait bancaire", null, "05/07/2026", "completed"),
        WalletTxn(7, "payment", -18000, "Réservation Studio Louis", 4, "03/07/2026", "completed"),
        WalletTxn(8, "earning", 45000, "Location Sono Concert", 5, "01/07/2026", "completed")
    ))
    val walletTransactions: StateFlow<List<WalletTxn>> = _walletTransactions.asStateFlow()

    private val _loyaltyPoints = MutableStateFlow(1500)
    val loyaltyPoints: StateFlow<Int> = _loyaltyPoints.asStateFlow()

    private val _unlockedAchievements = MutableStateFlow(setOf(0, 1, 2, 5))
    val unlockedAchievements: StateFlow<Set<Int>> = _unlockedAchievements.asStateFlow()

    private val _claimedRewards = MutableStateFlow(mutableListOf(
        ClaimedReward("BIENVENUE10", "10% sur votre 1ère location", "Valide jusqu'au 31/12/2026"),
        ClaimedReward("ÉTÉ2026", "15% sur les réservations > 3 jours", "Valide jusqu'au 30/09/2026")
    ))
    val claimedRewards: StateFlow<List<ClaimedReward>> = _claimedRewards.asStateFlow()

    private val _claimedFlashOffers = MutableStateFlow(setOf<String>())
    val claimedFlashOffers: StateFlow<Set<String>> = _claimedFlashOffers.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private var walletTxnCounter = 9

    fun payFromWallet(amount: Int, description: String) {
        viewModelScope.launch {
            val currentBalance = _walletBalance.value
            if (currentBalance >= amount) {
                _walletBalance.update { it - amount }
                val newTxn = WalletTxn(
                    id = walletTxnCounter++,
                    type = "payment",
                    amount = -amount,
                    description = description,
                    relatedBookingId = null,
                    date = "Maintenant",
                    status = "completed"
                )
                _walletTransactions.value = listOf(newTxn) + _walletTransactions.value
            } else {
                showSnackbar("Solde insuffisant")
            }
        }
    }

    fun addLoyaltyPoints(points: Int, reason: String) {
        _loyaltyPoints.update { it + points }
        showSnackbar("+$points points pour $reason")
    }

    fun redeemLoyaltyPoints(cost: Int, rewardName: String) {
        if (_loyaltyPoints.value >= cost) {
            _loyaltyPoints.update { it - cost }
            showSnackbar("$rewardName échangé avec succès !")
        } else {
            showSnackbar("Pas assez de points")
        }
    }

    fun unlockAchievement(id: Int) {
        val current = _unlockedAchievements.value.toMutableSet()
        if (id !in current) {
            current.add(id)
            _unlockedAchievements.value = current
            showSnackbar("Succès débloqué !")
        }
    }

    fun claimFlashOffer(offerTitle: String, costPoints: Int) {
        if (_loyaltyPoints.value >= costPoints) {
            _loyaltyPoints.update { it - costPoints }
            _claimedFlashOffers.update { it + offerTitle }
            showSnackbar("Offre flash réclamée ! -$costPoints points")
        } else {
            showSnackbar("Pas assez de points")
        }
    }

    fun claimReward(code: String, description: String, expiry: String) {
        val current = _claimedRewards.value.toMutableList()
        if (current.none { it.code == code }) {
            current.add(ClaimedReward(code, description, expiry))
            _claimedRewards.value = current
            showSnackbar("Coupon $code ajouté à vos récompenses")
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        _hapticEnabled.value = enabled
    }

    fun onBookingComplete() {
        unlockAchievement(3)
        addLoyaltyPoints(50, "réservation")
    }

    fun onReviewSubmit() {
        unlockAchievement(4)
        addLoyaltyPoints(20, "avis")
    }

    fun onFirstShare() {
        unlockAchievement(6)
        addLoyaltyPoints(100, "parrainage")
    }

    private val _activePromos = MutableStateFlow(listOf(
        PromoCodeEntry(1, "LOCALL20", 20, "15/08/2026", 100, 23, "20% de réduction sur toute réservation"),
        PromoCodeEntry(2, "BIENVENUE", 15, "31/12/2026", 1, 0, "15% sur votre première réservation"),
        PromoCodeEntry(3, "AMIS10", 10, "30/09/2026", 50, 12, "10% pour parrainage amis")
    ))
    val activePromos: StateFlow<List<PromoCodeEntry>> = _activePromos.asStateFlow()

    private val _selectedWalletFilter = MutableStateFlow("Toutes")
    val selectedWalletFilter: StateFlow<String> = _selectedWalletFilter.asStateFlow()

    fun setWalletFilter(filter: String) { _selectedWalletFilter.value = filter }

    fun topUpWallet(amount: Int, method: String) {
        _walletBalance.value += amount
        val newTxn = WalletTxn(
            id = _walletTransactions.value.size + 1,
            type = "topup",
            amount = amount,
            description = "Recharge $method",
            relatedBookingId = null,
            date = "Maintenant",
            status = "completed"
        )
        _walletTransactions.value = listOf(newTxn) + _walletTransactions.value
        showSnackbar("Recharge de $amount FCFA effectuée avec succès")
    }

    fun withdrawFromWallet(amount: Int) {
        if (_walletBalance.value >= amount) {
            _walletBalance.value -= amount
            val newTxn = WalletTxn(
                id = _walletTransactions.value.size + 1,
                type = "withdrawal",
                amount = -amount,
                description = "Retrait bancaire",
                relatedBookingId = null,
                date = "Maintenant",
                status = "completed"
            )
            _walletTransactions.value = listOf(newTxn) + _walletTransactions.value
            showSnackbar("Retrait de $amount FCFA effectué")
        } else {
            showSnackbar("Solde insuffisant")
        }
    }

    fun applyPromoCode(code: String): Boolean {
        val promo = _activePromos.value.find { it.code.equals(code, ignoreCase = true) }
        return if (promo != null) {
            showSnackbar("Code promo ${promo.code} appliqué : -${promo.discount}%")
            true
        } else {
            showSnackbar("Code promo invalide")
            false
        }
    }

    fun addFavoriteLocation(name: String, city: String, lat: Double, lng: Double) {
        showSnackbar("Position \"$name\" ajoutée aux favoris")
    }

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

    private val _mapUiState = MutableStateFlow(MapUiState())
    val mapUiState: StateFlow<MapUiState> = _mapUiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UIEvent>(extraBufferCapacity = 10)
    val uiEvents: SharedFlow<UIEvent> = _uiEvents.asSharedFlow()

    fun onSearchQueryChange(query: String) {
        _searchUiState.update { it.copy(query = query) }
        viewModelScope.launch {
            try {
                val suggestions = repository.searchSuggestions(query).first()
                _searchUiState.update { it.copy(suggestions = suggestions.map { s -> s.query }) }
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun onSearchSubmit() {
        val query = _searchUiState.value.query
        if (query.isBlank()) return
        _searchUiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            try {
                val results = repository.allRentalItems.first().filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.city.contains(query, ignoreCase = true) ||
                    it.neighborhood.contains(query, ignoreCase = true)
                }
                _searchUiState.update { it.copy(results = results, isSearching = false) }
                repository.logSearch(query)
            } catch (e: Exception) {
                _searchUiState.update { it.copy(isSearching = false) }
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun updateSearchFilters(filters: SearchFilters) {
        _searchUiState.update { it.copy(filters = filters) }
        viewModelScope.launch {
            try {
                val allListings = repository.allRentalItems.first()
                val filtered = allListings.filter { item ->
                    (filters.category == null || item.category == filters.category) &&
                    (filters.city == null || item.city == filters.city) &&
                    (filters.minPrice == null || item.pricePerDay >= filters.minPrice) &&
                    (filters.maxPrice == null || item.pricePerDay <= filters.maxPrice)
                }
                val sorted = when (filters.sortBy) {
                    SortBy.PRICE_ASC -> filtered.sortedBy { it.pricePerDay }
                    SortBy.PRICE_DESC -> filtered.sortedByDescending { it.pricePerDay }
                    SortBy.POPULARITY -> filtered.shuffled()
                    SortBy.RECENT -> filtered.reversed()
                }
                _searchUiState.update { it.copy(results = sorted) }
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun updateMapListings(listings: List<RentalItem>) {
        _mapUiState.update { it.copy(listings = listings) }
    }

    fun selectMapListing(id: Int?) {
        _mapUiState.update { it.copy(selectedListingId = id) }
    }

    fun setMapCity(city: String) {
        _mapUiState.update { it.copy(selectedCity = city) }
    }

    fun updateChatTyping(isTyping: Boolean) {
        _chatUiState.update { it.copy(isTyping = isTyping) }
    }

    fun updateChatOnlineStatus(online: Boolean) {
        _chatUiState.update { it.copy(otherUserOnline = online, otherUserLastSeen = System.currentTimeMillis()) }
    }

    fun addReactionToMessage(messageId: Int, reaction: String, messages: List<ChatMessage>) {
        viewModelScope.launch {
            try {
                val msg = messages.find { it.id == messageId } ?: return@launch
                val currentReactions = msg.reactions.toMutableList()
                if (reaction in currentReactions) {
                    currentReactions.remove(reaction)
                } else {
                    currentReactions.add(reaction)
                }
                val updated = msg.copy(reactions = currentReactions)
                repository.insertChatMessage(updated)
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    fun sendImageMessage(rentalId: Int, ownerName: String) {
        viewModelScope.launch {
            try {
                repository.insertChatMessage(
                    ChatMessage(
                        rentalItemId = rentalId,
                        sender = "User",
                        messageText = "[image] https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=400&q=60",
                        messageType = "image"
                    )
                )
                delay(1200)
                _chatUiState.update { it.copy(isTyping = true) }
                delay(1500 + Random.nextLong(800))
                _chatUiState.update { it.copy(isTyping = false) }
                val imageReplies = listOf(
                    "Magnifique photo ! Je suis intéressé.",
                    "Superbe ! On peut organiser une visite ?",
                    "Merci pour les photos, c'est exactement ce que je cherchais.",
                    "J'adore ! Je vais réserver directement."
                )
                repository.insertChatMessage(
                    ChatMessage(
                        rentalItemId = rentalId,
                        sender = "Owner",
                        messageText = imageReplies.random(),
                        status = "read"
                    )
                )
            } catch (e: Exception) {
                showSnackbar("Une erreur est survenue: ${e.message}")
            }
        }
    }

    private val _recentlyViewed = MutableStateFlow<List<RentalItem>>(emptyList())
    val recentlyViewed: StateFlow<List<RentalItem>> = _recentlyViewed.asStateFlow()

    fun addToRecentlyViewed(item: RentalItem) {
        val current = _recentlyViewed.value.toMutableList()
        current.removeAll { it.id == item.id }
        current.add(0, item)
        if (current.size > 50) current.removeAt(current.lastIndex)
        _recentlyViewed.value = current
    }

    fun clearRecentlyViewed() {
        _recentlyViewed.value = emptyList()
        showSnackbar("Historique effacé")
    }

    private val _profileCompletion = MutableStateFlow(60)
    val profileCompletion: StateFlow<Int> = _profileCompletion.asStateFlow()

    fun updateProfileCompletion() {
        var score = 0
        if (_userName.value.isNotBlank()) score += 20
        if (_profileCity.value.isNotBlank()) score += 20
        if (_isPhoneVerified.value) score += 20
        if (_profileDob.value.isNotBlank()) score += 20
        score += 20
        _profileCompletion.value = score
    }
}

data class WalletTxn(
    val id: Int,
    val type: String,
    val amount: Int,
    val description: String,
    val relatedBookingId: Int?,
    val date: String,
    val status: String
)

data class PromoCodeEntry(
    val id: Int,
    val code: String,
    val discount: Int,
    val validUntil: String,
    val maxUses: Int,
    val usedCount: Int,
    val description: String
)

data class ClaimedReward(
    val code: String,
    val description: String,
    val expiry: String
)

class RentalViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RentalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repository = com.example.di.AppContainer.getRepository(application)
            return RentalViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
