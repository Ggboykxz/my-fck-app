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

    // Reviews State
    private val _reviews = MutableStateFlow<Map<Int, List<RentalReview>>>(emptyMap())
    val reviews: StateFlow<Map<Int, List<RentalReview>>> = _reviews.asStateFlow()

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

    // Unread messages count
    private val _unreadMessageCount = MutableStateFlow(3)
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount.asStateFlow()

    // Referral system
    private val _referralCount = MutableStateFlow(3)
    val referralCount: StateFlow<Int> = _referralCount.asStateFlow()

    private val _referralEarnings = MutableStateFlow(15000)
    val referralEarnings: StateFlow<Int> = _referralEarnings.asStateFlow()

    // Earnings data
    private val _earnings = MutableStateFlow(listOf(
        EarningEntry(1, 45000, "22/06/2026", "Location Toyota Hilux - 3 jours", "Versé"),
        EarningEntry(2, 120000, "18/06/2026", "Location Villa La Sablière - 7 jours", "Versé"),
        EarningEntry(3, 25000, "15/06/2026", "Location Pack Sono Concert - 1 jour", "En attente"),
        EarningEntry(4, 85000, "10/06/2026", "Location Prado Port-Gentil - 5 jours", "Versé")
    ))
    val earnings: StateFlow<List<EarningEntry>> = _earnings.asStateFlow()

    // Notifications data
    private val _notifications = MutableStateFlow(listOf(
        NotificationEntry(1, "reservation", "Réservation confirmée", "Votre réservation Toyota Hilux a été confirmée", "Il y a 2h", false),
        NotificationEntry(2, "message", "Nouveau message", "Kofi Mensah a envoyé un message", "Il y a 4h", false),
        NotificationEntry(3, "payment", "Paiement reçu", "45 000 F CFA reçus pour Hilux", "Il y a 1 jour", true),
        NotificationEntry(4, "system", "Mise à jour", "LocAll v1.0.1 disponible", "Il y a 2 jours", true),
        NotificationEntry(5, "reservation", "Réservation annulée", "Paul a annulé Mitsubishi L200", "Il y a 3 jours", true),
        NotificationEntry(6, "payment", "Remboursé", "15 000 F CFA remboursés", "Il y a 5 jours", true),
        NotificationEntry(7, "message", "Demande de réservation", "Sophie souhaite réserver l'Appartement Vue Mer", "Il y a 6 jours", false),
        NotificationEntry(8, "system", "Vérification réussie", "Identité vérifiée. Badge Vérifié activé !", "Il y a 1 semaine", true),
        NotificationEntry(9, "reservation", "Rappel de retour", "Retourner Pack Sono demain avant 18h", "Il y a 1 semaine", false),
        NotificationEntry(10, "payment", "Point de fidélité", "+250 points pour dernière réservation", "Il y a 2 semaines", true),
        NotificationEntry(11, "promotion", "Offre spéciale", "-15% sur Immobilier ce week-end", "Il y a 2 semaines", true),
        NotificationEntry(12, "system", "Sécurité", "Nouveau mot de passe configuré", "Il y a 3 semaines", true),
        NotificationEntry(13, "reservation", "Modification acceptée", "Changement de dates accepté", "Il y a 3 semaines", true),
        NotificationEntry(14, "message", "Relance propriétaire", "Marie-Claire vous a envoyé un rappel", "Il y a 1 mois", false),
        NotificationEntry(15, "payment", "Facture disponible", "Facture location Hilux en téléchargement", "Il y a 1 mois", true),
        NotificationEntry(16, "system", "Nouvelle fonctionnalité", "Chat vidéo disponible !", "Il y a 1 mois", true),
        NotificationEntry(17, "promotion", "Parrainage réussi", "Rodrigue a rejoint. +5 000 F CFA !", "Il y a 1 mois", true),
        NotificationEntry(18, "reservation", "Réservation refusée", "Jean refusé (indisponible)", "Il y a 2 mois", true),
        NotificationEntry(19, "alert", "Litige en cours", "Litige ouvert pour Moto NMAX", "Il y a 2 mois", false),
        NotificationEntry(20, "payment", "Retrait effectué", "50 000 F CFA retirés via Airtel", "Il y a 2 mois", true)
    ))
    val notifications: StateFlow<List<NotificationEntry>> = _notifications.asStateFlow()

    // Disputes data
    private val _disputes = MutableStateFlow(listOf(
        DisputeEntry("LIT-001", "Dommage Toyota Hilux", "En cours", "20/06/2026", "Dommage", "Le pare-chocs avant a été endommagé lors de la location", 150000),
        DisputeEntry("LIT-002", "Annulation tardive Pack Sono", "Résolu", "15/06/2026", "Annulation", "Annulation moins de 24h avant l'événement", 50000)
    ))
    val disputes: StateFlow<List<DisputeEntry>> = _disputes.asStateFlow()

    // Payment history data
    private val _paymentHistory = MutableStateFlow(listOf(
        PaymentEntry(1, 45000, "22/06/2026", "Location Toyota Hilux - 3 jours", "Airtel Money"),
        PaymentEntry(2, 120000, "18/06/2026", "Location Villa La Sablière - 7 jours", "Moov Money"),
        PaymentEntry(3, 25000, "15/06/2026", "Location Pack Sono - 1 jour", "Carte Bancaire"),
        PaymentEntry(4, 85000, "10/06/2026", "Location Prado - 5 jours", "Airtel Money")
    ))
    val paymentHistory: StateFlow<List<PaymentEntry>> = _paymentHistory.asStateFlow()

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
        val initialReviews = mapOf(
            1 to listOf(
                RentalReview(1, 5, "Superbe villa, très propre et spacieuse. La piscine est un vrai plus. Quartier résidentiel très sécurisé, idéal pour les familles.", "Stéphane Koumba", "20/06/2026"),
                RentalReview(1, 4, "Belle vue sur la mer, accès facile. La climatisation fonctionne parfaitement. Je recommande.", "Patricia Ndong", "15/05/2026"),
                RentalReview(1, 5, "Séjour exceptionnel ! Le propriétaire est très arrangeant. La villa correspond exactement aux photos.", "Cécilia Mba", "01/04/2026")
            ),
            2 to listOf(
                RentalReview(2, 5, "Appartement moderne avec une vue imprenable sur l'estuaire. La terrasse est magnifique au coucher du soleil.", "Rodrigue Mintsa", "18/06/2026"),
                RentalReview(2, 4, "Bon emplacement, parking pratique. Le quartier est calme la nuit. Petit bémol sur le WiFi.", "Sylvie Obiang", "10/05/2026"),
                RentalReview(2, 3, "L'appartement est bien mais le bruit de la route est gênant le matin. À améliorer pour l'insonorisation.", "Patrice Oyé", "20/04/2026")
            ),
            3 to listOf(
                RentalReview(3, 5, "Appartement très moderne et bien climatisé. Équipements de cuisine haut de gamme. Parfait pour un court séjour.", "Inès Bongo", "22/06/2026"),
                RentalReview(3, 4, "Studio cozy et bien équipé. La localisation près de l'aéroport est très pratique pour les voyages d'affaires.", "Bernadette Nguéma", "15/05/2026")
            ),
            4 to listOf(
                RentalReview(4, 4, "Bon matériel professionnel, idéal pour de l'événementiel sur Akanda. Le son est puissant et clair.", "David Ogoula", "18/06/2026"),
                RentalReview(4, 3, "Matériel correct mais le transport jusqu'au lieu d'événement n'est pas inclus. Prévoir un véhicule.", "Ghislain Mboumba", "01/06/2026")
            ),
            5 to listOf(
                RentalReview(5, 5, "Le Prado est impeccable, très robuste pour circuler sur les routes de Port-Gentil. Consommation raisonnable.", "Marc Aubame", "10/06/2026"),
                RentalReview(5, 4, "Véhicule propre et confortable. Bon retour de caution sans accroc. Le GPS fonctionne parfaitement.", "Yannick Mba", "02/06/2026"),
                RentalReview(5, 2, "Le réservoir était à moitié vide à la récupération. Pour ce prix, on attend un plein complet.", "Françoise Limbaka", "15/05/2026")
            ),
            6 to listOf(
                RentalReview(6, 5, "Ford Ranger solide et fiable. Parfait pour les déplacements sur chantier. Le 4x4 fonctionne à merveille.", "Alain Nzébi", "12/06/2026")
            ),
            9 to listOf(
                RentalReview(9, 5, "Pack sono exceptionnel pour notre mariage ! Les invités étaient émerveillés par la qualité du son et des lumières.", "Hélène Ovono", "25/06/2026"),
                RentalReview(9, 4, "Très bon matériel, le technicien était ponctuel et professionnel. Le montage a pris 2h comme promis.", "Aimée Mboumba", "10/06/2026")
            ),
            10 to listOf(
                RentalReview(10, 4, "Piscine facile à monter, la filtration fonctionne bien. Les transats sont un peu usés mais corrects.", "Christelle Ongouma", "05/06/2026")
            ),
            16 to listOf(
                RentalReview(16, 5, "Bureau spacieux et bien équipé. L'emplacement est stratégique pour recevoir des clients. Je recommande.", "Fabrice Mikala", "20/06/2026"),
                RentalReview(16, 4, "Bon rapport qualité-prix pour un bureau meublé à Libreville. La fibre optique est un vrai plus.", "Josiane Nkoghe", "10/06/2026")
            ),
            17 to listOf(
                RentalReview(17, 3, "Le terrain est bien situé mais l'accès est difficile lors de la saison des pluies. À prévoir.", "Emmanuel Mansogui", "15/05/2026")
            ),
            18 to listOf(
                RentalReview(18, 5, "Camion en parfait état, la benne hydraulique fonctionne parfaitement. Le chauffeur était compétent.", "Service Mines Gabon", "08/06/2026")
            ),
            22 to listOf(
                RentalReview(22, 5, "Van très confortable pour notre transfert aéroport. Le chauffeur était ponctuel et courtois.", "Tourisme Plus Gabon", "20/05/2026"),
                RentalReview(22, 4, "Bonne prestation, le van est propre et climatisé. Seul bémol : le coffre est un peu petit pour 14 passagers.", "Organisation Loisirs", "01/05/2026")
            )
        )
        _reviews.value = initialReviews

        viewModelScope.launch {
            repository.seedDatabase()
        }

        // Compute unread count from actual chat conversations
        viewModelScope.launch {
            delay(500) // Wait for seed
            val chatCounts = repository.getAllChatMessages().first().groupBy { it.rentalItemId }
            _unreadMessageCount.value = chatCounts.size.coerceAtMost(99)
        }

        // Simulate loading delays for skeleton screens
        viewModelScope.launch {
            delay(1200)
            _isHomeLoading.value = false
        }
        viewModelScope.launch {
            delay(800)
            _isBookmarksLoading.value = false
        }
        viewModelScope.launch {
            delay(1000)
            _isBookingsLoading.value = false
        }
        viewModelScope.launch {
            delay(600)
            _isInboxLoading.value = false
        }
    }

    // ==================== FILTER ACTIONS (encapsulated) ====================
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                repository.insertSearchHistory(SearchHistoryEntry(query = query))
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

    fun markNotificationRead(id: Int) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun acceptReceivedBooking(id: String) {
        _receivedBookings.value = _receivedBookings.value.map { if (it.id == id) it.copy(status = "Confirmée") else it }
    }

    fun refuseReceivedBooking(id: String) {
        _receivedBookings.value = _receivedBookings.value.map { if (it.id == id) it.copy(status = "Refusée") else it }
    }

    fun unreadNotificationCount(): Int = _notifications.value.count { !it.isRead }

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
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

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
            repository.updateBookmarkStatus(item.id, !item.isBookmarked)
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = _selectedItem.value?.copy(isBookmarked = !item.isBookmarked)
            }
            showSnackbar(
                if (!item.isBookmarked) "Ajouté aux favoris" else "Retiré des favoris"
            )
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
            _paymentState.value = PaymentState.Processing("Initialisation de la transaction pour " + rentalItem.title + "...")
            delay(1500)
            _paymentState.value = PaymentState.Processing(
                "Demande de paiement de " + (rentalItem.pricePerDay * days) + " F CFA envoyée à " + paymentMethod + " (" + phoneInput + ")."
            )
            delay(1500)
            _paymentState.value = PaymentState.AwaitingPin(rentalItem, days, paymentMethod, phoneInput)
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
        }
    }

    fun cancelBooking(bookingId: Int, reason: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, "Annulé", reason)
            showSnackbar("Réservation annulée")
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    // ==================== REVIEWS ====================
    fun addReview(rentalItemId: Int, rating: Int, comment: String) {
        val current = _reviews.value.toMutableMap()
        val list = current[rentalItemId]?.toMutableList() ?: mutableListOf()
        val authorName = "Visiteur Gabonais"
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        list.add(0, RentalReview(rentalItemId, rating, comment, authorName, currentDate))
        current[rentalItemId] = list
        _reviews.value = current
        showSnackbar("Avis publié avec succès")
    }

    fun reviewsFor(itemId: Int): Flow<List<RentalReview>> = _reviews.map { map -> map[itemId] ?: emptyList() }

    // ==================== REACTIVE DATA ====================
    val rawRentalItems: StateFlow<List<RentalItem>> = repository.allRentalItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItems: StateFlow<List<RentalItem>> = repository.bookmarkedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRentalItems: StateFlow<List<RentalItem>> = combine(
        rawRentalItems,
        _searchQuery,
        _selectedCategory,
        _selectedCity,
        _selectedMaxPrice,
        _sortOption
    ) { values ->
        val items = values[0] as List<RentalItem>
        val query = values[1] as String
        val category = values[2] as String
        val city = values[3] as String
        val maxPrice = values[4] as Int
        val sort = values[5] as SortOption
        items.filter { item ->
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.neighborhood.contains(query, ignoreCase = true)
            val matchesCategory = category == "Tous" || item.category.equals(category, ignoreCase = true)
            val matchesCity = city == "Tous" || item.city.equals(city, ignoreCase = true)
            val matchesPrice = item.pricePerDay >= 0 && (maxPrice == 0 || item.pricePerDay <= maxPrice)
            matchesQuery && matchesCategory && matchesCity && matchesPrice
        }.let { filtered ->
            when (sort) {
                SortOption.PRICE_ASC -> filtered.sortedBy { it.pricePerDay }
                SortOption.PRICE_DESC -> filtered.sortedByDescending { it.pricePerDay }
                SortOption.RECENT -> filtered.sortedByDescending { it.createdAt }
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
        }
    }

    fun sendChatMessage(rentalId: Int, messageText: String, ownerName: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
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
            val img = if (imageUrl.isBlank()) {
                when (category) {
                    "Immobilier" -> "https://images.unsplash.com/photo-1570129477492-45c003edd2be?auto=format&fit=crop&w=800&q=80"
                    "Véhicules" -> "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80"
                    else -> "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80"
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
        }
    }

    fun deleteListing(itemId: Int) {
        viewModelScope.launch {
            repository.deleteRentalItem(itemId)
            showSnackbar("Annonce supprimée")
        }
    }

    fun updateUserProfile(name: String, phone: String) {
        viewModelScope.launch {
            val current = repository.getUserProfileOnce()
            if (current != null) {
                repository.upsertUserProfile(current.copy(fullName = name, phone = phone))
            } else {
                repository.upsertUserProfile(UserProfile(id = 1, fullName = name, phone = phone, dob = "", gender = "", city = "", profession = ""))
            }
        }
        _userName.value = name
        showSnackbar("Profil mis à jour avec succès")
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
