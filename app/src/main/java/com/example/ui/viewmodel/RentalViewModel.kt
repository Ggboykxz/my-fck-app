package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Booking
import com.example.data.model.ChatMessage
import com.example.data.model.RentalItem
import com.example.data.repository.RentalRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

class RentalViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RentalRepository(db.rentalDao())

    // Reviews State Flow
    private val _reviews = MutableStateFlow<Map<Int, List<RentalReview>>>(emptyMap())
    val reviews: StateFlow<Map<Int, List<RentalReview>>> = _reviews.asStateFlow()

    // Onboarding navigation state:
    // 0: Splash, 1: Onboarding Welcome, 2: Onboarding Payments, 3: Onboarding Trust, 4: Main App Dashboard
    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    // Authentication States
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authState = MutableStateFlow("login") // "login", "register", "forgot_password", "otp", "new_password"
    val authState: StateFlow<String> = _authState.asStateFlow()

    // Profile States (Gabon Rental Profile)
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

    private val _profilePhotoEnabled = MutableStateFlow(true) // Simulating dynamic uploads
    val profilePhotoEnabled: StateFlow<Boolean> = _profilePhotoEnabled.asStateFlow()

    private val _isOwnerMode = MutableStateFlow(false)
    val isOwnerMode: StateFlow<Boolean> = _isOwnerMode.asStateFlow()

    private val _profileLanguage = MutableStateFlow("Français")
    val profileLanguage: StateFlow<String> = _profileLanguage.asStateFlow()

    private val _identityVerificationStatus = MutableStateFlow("Non vérifié") // "Non vérifié", "Documents soumis", "Selfie soumis", "En révision", "Vérifié"
    val identityVerificationStatus: StateFlow<String> = _identityVerificationStatus.asStateFlow()

    private val _withdrawableBalance = MutableStateFlow(850000)
    val withdrawableBalance: StateFlow<Int> = _withdrawableBalance.asStateFlow()

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
        }
    }

    fun updateProfile(dob: String, gender: String, profession: String, city: String) {
        _profileDob.value = dob
        _profileGender.value = gender
        _profileProfession.value = profession
        _profileCity.value = city
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

    // Navigation and screen state inside the dashboard
    // Screen names: "home", "explore", "details", "bookings", "chat", "post_listing"
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Filters and Search
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Tous")
    val selectedCity = MutableStateFlow("Tous")
    val selectedMinPrice = MutableStateFlow(0)
    val selectedMaxPrice = MutableStateFlow(0) // 0 means no limit

    // Selected item for details/booking/chat
    private val _selectedItem = MutableStateFlow<RentalItem?>(null)
    val selectedItem: StateFlow<RentalItem?> = _selectedItem.asStateFlow()

    // Payment/Booking state
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    init {
        // Preseed reviews
        val initialReviews = mapOf(
            1 to listOf(
                RentalReview(1, 5, "Superbe villa, très propre et spacieuse. Quartier résidentiel très sécurisé !", "Stéphane Koumba", "20/06/2026"),
                RentalReview(1, 4, "Belle vue, accès facile. Idéal pour un séjour à Libreville.", "Patricia Ndong", "15/05/2026")
            ),
            2 to listOf(
                RentalReview(2, 5, "Le Prado est impeccable, très robuste pour circuler sur les routes de Port-Gentil.", "Marc Aubame", "10/06/2026"),
                RentalReview(2, 4, "Véhicule propre et confortable. Bon retour de caution sans accroc.", "Yannick Mba", "02/06/2026")
            ),
            3 to listOf(
                RentalReview(3, 5, "Appartement très moderne et bien climatisé. Équipements de cuisine haut de gamme.", "Inès Bongo", "22/06/2026")
            ),
            4 to listOf(
                RentalReview(4, 4, "Bon matériel professionnel, idéal pour de l'événementiel sur Akanda.", "David Ogoula", "18/06/2026")
            )
        )
        _reviews.value = initialReviews

        // Seed database immediately on launch
        viewModelScope.launch {
            repository.seedDatabase()
        }
    }

    fun addReview(rentalItemId: Int, rating: Int, comment: String) {
        val current = _reviews.value.toMutableMap()
        val list = current[rentalItemId]?.toMutableList() ?: mutableListOf()
        val authorName = "Visiteur Gabonais"
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())
        list.add(0, RentalReview(rentalItemId, rating, comment, authorName, currentDate))
        current[rentalItemId] = list
        _reviews.value = current
    }

    // Exposing Reactive Flows
    val rawRentalItems: StateFlow<List<RentalItem>> = repository.allRentalItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedItems: StateFlow<List<RentalItem>> = repository.bookmarkedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered lists of rentals for search and categories
    val filteredRentalItems: StateFlow<List<RentalItem>> = combine(
        rawRentalItems,
        searchQuery,
        selectedCategory,
        selectedCity,
        selectedMinPrice,
        selectedMaxPrice
    ) { items, query, category, city, minPrice, maxPrice ->
        items.filter { item ->
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.neighborhood.contains(query, ignoreCase = true)
            
            val matchesCategory = category == "Tous" || item.category.equals(category, ignoreCase = true)
            val matchesCity = city == "Tous" || item.city.equals(city, ignoreCase = true)
            val matchesPrice = item.pricePerDay >= minPrice && (maxPrice == 0 || item.pricePerDay <= maxPrice)

            matchesQuery && matchesCategory && matchesCity && matchesPrice
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat streams
    private val _activeChatRentalId = MutableStateFlow<Int?>(null)
    val activeChatMessages: StateFlow<List<ChatMessage>> = _activeChatRentalId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getChatMessagesForRental(id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun nextOnboarding() {
        val current = _onboardingStep.value
        if (current < 3) {
            _onboardingStep.value = current + 1
        } else {
            _onboardingStep.value = 4 // Completed, enter dashboard
        }
    }

    fun skipOnboarding() {
        _onboardingStep.value = 4 // Direct to dashboard
    }

    fun restartOnboarding() {
        _onboardingStep.value = 0
        _currentScreen.value = "home"
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectItem(item: RentalItem) {
        _selectedItem.value = item
    }

    fun toggleBookmark(item: RentalItem) {
        viewModelScope.launch {
            repository.updateBookmarkStatus(item.id, !item.isBookmarked)
            // If the currently selected item is this one, update the selection state to reflect bookmark change
            if (_selectedItem.value?.id == item.id) {
                _selectedItem.value = _selectedItem.value?.copy(isBookmarked = !item.isBookmarked)
            }
        }
    }

    // Interactive booking with Airtel/Moov money simulation
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

            // Dynamic interactive change: switch state to AwaitingPin instead of auto-completing
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

            // Creating actual Booking records in Db
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

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    // Simulated Chat Messaging
    fun openChatFor(item: RentalItem) {
        _activeChatRentalId.value = item.id
        viewModelScope.launch {
            // Seed a starter reply if empty
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
            // Insert user message
            repository.insertChatMessage(
                ChatMessage(
                    rentalItemId = rentalId,
                    sender = "User",
                    messageText = messageText
                )
            )

            // Auto simulated owner response
            delay(1500)
            val responseText = when {
                messageText.contains("disponible", ignoreCase = true) || messageText.contains("dispo", ignoreCase = true) -> {
                    "Absolument! L'offre est entièrement disponible. Vous pouvez effectuer la réservation directement sur LocAll avec Airtel Money ou Moov Money pour bloquer les dates!"
                }
                messageText.contains("prix", ignoreCase = true) || messageText.contains("tarif", ignoreCase = true) || messageText.contains("reduction", ignoreCase = true) -> {
                    "Le tarif est fixé à la journée. Si vous louez pour plus d'une semaine, je peux vous faire un geste commercial. N'hésitez pas à lancer la réservation pour en discuter."
                }
                messageText.contains("visite", ignoreCase = true) || messageText.contains("voir", ignoreCase = true) -> {
                    "Bien sûr, la visite est tout à fait possible à ${viewModelScope.run { rawRentalItems.value.find { it.id == rentalId }?.neighborhood ?: "Libreville" }}. Dites-moi quand vous seriez disponible !"
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

    // Add listing
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
                // Fallback stock pictures depending on category
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
        }
    }
}

sealed interface PaymentState {
    object Idle : PaymentState
    data class Processing(val status: String) : PaymentState
    data class AwaitingPin(val rentalItem: RentalItem, val days: Int, val paymentMethod: String, val phoneInput: String) : PaymentState
    data class Success(val booking: Booking) : PaymentState
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
