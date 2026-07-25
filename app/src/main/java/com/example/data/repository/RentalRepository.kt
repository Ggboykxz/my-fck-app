package com.example.data.repository

import com.example.data.local.RentalDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RentalRepository(private val rentalDao: RentalDao) {

    val allRentalItems: Flow<List<RentalItem>> = rentalDao.getAllRentalItems()
    val bookmarkedItems: Flow<List<RentalItem>> = rentalDao.getBookmarkedItems()
    val allBookings: Flow<List<Booking>> = rentalDao.getAllBookings()
    val userProfile: Flow<UserProfile?> = rentalDao.getUserProfile()
    val searchHistory: Flow<List<SearchHistoryEntry>> = rentalDao.getSearchHistory()

    suspend fun getUserProfileOnce(): UserProfile? {
        return rentalDao.getUserProfileOnce()
    }

    fun getRentalItemsByCategory(category: String): Flow<List<RentalItem>> =
        rentalDao.getRentalItemsByCategory(category)

    suspend fun getRentalItemById(id: Int): RentalItem? =
        rentalDao.getRentalItemById(id)

    fun getSimilarItems(excludeId: Int, category: String): Flow<List<RentalItem>> =
        rentalDao.getSimilarItems(excludeId, category)

    suspend fun insertRentalItem(item: RentalItem) =
        rentalDao.insertRentalItem(item)

    suspend fun updateRentalItem(item: RentalItem) =
        rentalDao.updateRentalItem(item)

    suspend fun deleteRentalItem(id: Int) =
        rentalDao.deleteRentalItem(id)

    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean) =
        rentalDao.updateBookmarkStatus(id, isBookmarked)

    suspend fun insertBooking(booking: Booking) =
        rentalDao.insertBooking(booking)

    suspend fun getBookingById(id: Int): Booking? =
        rentalDao.getBookingById(id)

    suspend fun updateBookingStatus(id: Int, status: String, reason: String? = null) =
        rentalDao.updateBookingStatus(id, status, reason)

    fun getChatMessagesForRental(itemId: Int): Flow<List<ChatMessage>> =
        rentalDao.getChatMessagesForRental(itemId)

    fun getAllChatMessages(): Flow<List<ChatMessage>> =
        rentalDao.getAllChatMessages()

    suspend fun insertChatMessage(message: ChatMessage) =
        rentalDao.insertChatMessage(message)

    suspend fun updateMessageStatus(id: Int, status: String) =
        rentalDao.updateMessageStatus(id, status)

    suspend fun getLastOwnerMessage(itemId: Int): ChatMessage? =
        rentalDao.getLastOwnerMessage(itemId)

    suspend fun upsertUserProfile(profile: UserProfile) =
        rentalDao.upsertUserProfile(profile)

    suspend fun insertSearchHistory(entry: SearchHistoryEntry) =
        rentalDao.insertSearchHistory(entry)

    suspend fun clearSearchHistory() =
        rentalDao.clearSearchHistory()

    suspend fun deleteSearchHistoryEntry(query: String) =
        rentalDao.deleteSearchHistoryEntry(query)

    // Notifications
    val notifications: Flow<List<NotificationEntity>> = rentalDao.getAllNotifications()

    suspend fun insertNotification(notification: NotificationEntity) =
        rentalDao.insertNotification(notification)

    suspend fun markNotificationRead(id: Int) =
        rentalDao.markNotificationRead(id)

    suspend fun markAllNotificationsRead() =
        rentalDao.markAllNotificationsRead()

    suspend fun deleteNotification(id: Int) =
        rentalDao.deleteNotification(id)

    suspend fun clearAllNotifications() =
        rentalDao.clearAllNotifications()

    fun getUnreadNotificationCount(): Flow<Int> =
        rentalDao.getUnreadNotificationCount()

    // Disputes
    val disputes: Flow<List<DisputeEntity>> = rentalDao.getAllDisputes()

    suspend fun insertDispute(dispute: DisputeEntity) =
        rentalDao.insertDispute(dispute)

    // Earnings
    val earnings: Flow<List<EarningEntity>> = rentalDao.getAllEarnings()

    suspend fun insertEarning(earning: EarningEntity) =
        rentalDao.insertEarning(earning)

    // User profile
    suspend fun updateUserProfileFields(name: String, phone: String, email: String, dob: String, gender: String, profession: String, city: String) =
        rentalDao.updateUserProfileFields(name, phone, email, dob, gender, profession, city)

    suspend fun updateProfileImage(url: String) =
        rentalDao.updateProfileImage(url)

    // Account deletion
    suspend fun deleteAllUserData() {
        rentalDao.deleteUserProfile()
        rentalDao.deleteAllBookings()
        rentalDao.deleteAllChatMessages()
        rentalDao.deleteAllSearchHistory()
        rentalDao.deleteAllNotifications()
        rentalDao.deleteAllDisputes()
        rentalDao.deleteAllEarnings()
        rentalDao.deleteAllReviews()
        rentalDao.deleteAllPaymentHistory()
    }

    // Reviews
    fun getReviewsForItem(itemId: Int): Flow<List<ReviewEntity>> =
        rentalDao.getReviewsForItem(itemId)

    suspend fun insertReview(review: ReviewEntity) =
        rentalDao.insertReview(review)

    // Payment history
    val paymentHistory: Flow<List<PaymentHistoryEntity>> = rentalDao.getAllPaymentHistory()

    suspend fun insertPaymentHistory(payment: PaymentHistoryEntity) =
        rentalDao.insertPaymentHistory(payment)

    // Saved Searches
    val savedSearches: Flow<List<SavedSearch>> = rentalDao.getSavedSearches()

    fun searchSuggestions(prefix: String): Flow<List<SearchSuggestion>> =
        rentalDao.getSearchSuggestions(prefix)

    val trendingSearches: Flow<List<SearchSuggestion>> = rentalDao.getTrendingSearches()

    val voiceSearchHistory: Flow<List<VoiceSearchHistory>> = rentalDao.getVoiceSearchHistory()

    suspend fun saveSearch(query: String, category: String?, city: String?, minPrice: Int?, maxPrice: Int?) =
        rentalDao.insertSavedSearch(SavedSearch(query = query, category = category, city = city, minPrice = minPrice, maxPrice = maxPrice))

    suspend fun deleteSavedSearch(id: Int) = rentalDao.deleteSavedSearch(id)

    suspend fun toggleSearchAlert(id: Int, enabled: Boolean) = rentalDao.toggleSearchAlert(id, enabled)

    suspend fun logSearch(query: String) {
        val now = System.currentTimeMillis()
        val existing = rentalDao.getSearchSuggestions(query, 1).first()
        if (existing.isNotEmpty()) {
            rentalDao.incrementSearchSuggestion(query, now)
        } else {
            rentalDao.insertSearchSuggestionIfNotExists(query, now)
        }
    }

    suspend fun logVoiceSearch(spoken: String, interpreted: String) =
        rentalDao.insertVoiceSearch(VoiceSearchHistory(spokenText = spoken, interpretedQuery = interpreted))

    fun getSearchAnalytics(): Flow<List<SearchSuggestion>> = rentalDao.getSearchAnalytics()

    // Owner Analytics
    val ownerAnalytics: Flow<OwnerAnalytics?> = rentalDao.getOwnerAnalytics()

    suspend fun insertOwnerAnalytics(analytics: OwnerAnalytics) = rentalDao.insertOwnerAnalytics(analytics)

    // Market Insights
    val marketInsights: Flow<List<MarketInsight>> = rentalDao.getMarketInsights()

    suspend fun insertMarketInsight(insight: MarketInsight) = rentalDao.insertMarketInsight(insight)

    // Push Notification Settings
    val pushNotificationSettings: Flow<PushNotificationSetting?> = rentalDao.getPushNotificationSettings()

    suspend fun insertPushNotificationSettings(settings: PushNotificationSetting) = rentalDao.insertPushNotificationSettings(settings)

    // Referral Tracking
    val referralTracking: Flow<List<ReferralTracking>> = rentalDao.getReferralTracking()

    suspend fun insertReferralTracking(referral: ReferralTracking) = rentalDao.insertReferralTracking(referral)

    suspend fun toggleFollow(followerId: Int, followedId: Int) {
        val existing = rentalDao.getFollow(followerId, followedId)
        if (existing != null) rentalDao.unfollow(followerId, followedId)
        else rentalDao.insertUserFollow(UserFollow(followerId = followerId, followedId = followedId))
    }

    fun getFollowerCount(userId: Int): Flow<Int> = rentalDao.getFollowerCount(userId)
    fun getFollowingCount(userId: Int): Flow<Int> = rentalDao.getFollowingCount(userId)
    suspend fun isFollowing(followerId: Int, followedId: Int): UserFollow? = rentalDao.getFollow(followerId, followedId)

    fun getVerificationBadges(userId: Int): Flow<List<VerificationBadge>> = rentalDao.getVerificationBadges(userId)
    suspend fun insertVerificationBadge(badge: VerificationBadge) = rentalDao.insertVerificationBadge(badge)

    suspend fun insertCommunityDispute(dispute: CommunityDispute) = rentalDao.insertCommunityDispute(dispute)
    fun getAllCommunityDisputes(): Flow<List<CommunityDispute>> = rentalDao.getAllCommunityDisputes()
    fun getUserDisputes(userId: Int): Flow<List<CommunityDispute>> = rentalDao.getUserDisputes(userId)
    suspend fun voteDispute(id: Int, delta: Int) = rentalDao.voteDispute(id, delta)

    fun getNeighborhoodReviews(city: String): Flow<List<NeighborhoodReview>> = rentalDao.getNeighborhoodReviews(city)
    suspend fun insertNeighborhoodReview(review: NeighborhoodReview) = rentalDao.insertNeighborhoodReview(review)

    fun getAllEscrows(): Flow<List<BookingEscrow>> = rentalDao.getAllEscrows()
    fun getEscrowsByStatus(status: String): Flow<List<BookingEscrow>> = rentalDao.getEscrowsByStatus(status)
    suspend fun insertEscrow(escrow: BookingEscrow) = rentalDao.insertEscrow(escrow)
    suspend fun updateEscrowStatus(id: Int, status: String, releasedAt: Long? = null) = rentalDao.updateEscrowStatus(id, status, releasedAt)

    suspend fun insertSplitPayment(split: SplitPayment) = rentalDao.insertSplitPayment(split)
    fun getAllSplitPayments(): Flow<List<SplitPayment>> = rentalDao.getAllSplitPayments()
    fun getSplitPaymentForBooking(bookingId: Int): Flow<List<SplitPayment>> = rentalDao.getSplitPaymentForBooking(bookingId)

    suspend fun insertPaymentReminder(reminder: PaymentReminder) = rentalDao.insertPaymentReminder(reminder)
    fun getAllPaymentReminders(): Flow<List<PaymentReminder>> = rentalDao.getAllPaymentReminders()

    suspend fun insertPaymentReceipt(receipt: PaymentReceipt) = rentalDao.insertPaymentReceipt(receipt)
    fun getAllPaymentReceipts(): Flow<List<PaymentReceipt>> = rentalDao.getAllPaymentReceipts()
    fun getReceiptsForBooking(bookingId: Int): Flow<List<PaymentReceipt>> = rentalDao.getReceiptsForBooking(bookingId)

    suspend fun insertCalendarSync(sync: CalendarSync) = rentalDao.insertCalendarSync(sync)
    fun getAllCalendarSyncs(): Flow<List<CalendarSync>> = rentalDao.getAllCalendarSyncs()
    suspend fun updateCalendarSync(id: Int, synced: Boolean) = rentalDao.updateCalendarSync(id, synced)

    fun getMediaItemsForListing(listingId: Int): Flow<List<MediaItem>> =
        rentalDao.getMediaItemsForListing(listingId)

    suspend fun getMediaItemById(id: Int): MediaItem? =
        rentalDao.getMediaItemById(id)

    suspend fun insertMediaItem(mediaItem: MediaItem): Long =
        rentalDao.insertMediaItem(mediaItem)

    suspend fun updateMediaItem(mediaItem: MediaItem) =
        rentalDao.updateMediaItem(mediaItem)

    suspend fun deleteMediaItem(id: Int) =
        rentalDao.deleteMediaItem(id)

    suspend fun updateMediaModerationStatus(id: Int, status: String) =
        rentalDao.updateMediaModerationStatus(id, status)

    fun getMediaItemsByStatus(status: String): Flow<List<MediaItem>> =
        rentalDao.getMediaItemsByStatus(status)

    suspend fun upsertMediaUploadSettings(settings: MediaUploadSettings) =
        rentalDao.upsertMediaUploadSettings(settings)

    fun getMediaUploadSettings(): Flow<MediaUploadSettings?> =
        rentalDao.getMediaUploadSettings()

    suspend fun deleteMediaItemsForListing(listingId: Int) =
        rentalDao.deleteMediaItemsForListing(listingId)

    suspend fun updateDisputeEvidence(id: Int, evidence: List<String>) =
        rentalDao.updateDisputeEvidence(id, evidence)

    suspend fun insertInsuranceClaim(claim: InsuranceClaim) =
        rentalDao.insertInsuranceClaim(claim)

    val insuranceClaims: Flow<List<InsuranceClaim>> = rentalDao.getAllInsuranceClaims()

    suspend fun insertInsuranceSubscription(sub: InsuranceSubscription) =
        rentalDao.insertInsuranceSubscription(sub)

    val insuranceSubscription: Flow<InsuranceSubscription?> = rentalDao.getInsuranceSubscription()

    suspend fun updatePhoneVerified(verified: Boolean) =
        rentalDao.updatePhoneVerified(verified)

    suspend fun updateIdentityStatus(status: String) =
        rentalDao.updateIdentityStatus(status)

    suspend fun insertWalletTransaction(txn: WalletTransaction) =
        rentalDao.insertWalletTransaction(txn)

    suspend fun seedDatabase() {
        val currentItems = allRentalItems.first()
        if (currentItems.isEmpty()) {
            val seedItems = listOf(
                RentalItem(
                    title = "Villa de Luxe meublée - La Sablière",
                    description = "Splendide villa meublée avec piscine et 4 chambres située en bordure de plage à la Sablière (Libreville). Entièrement climatisée, avec groupe électrogène automatique, forage d'eau potable et gardiennage armé H24. Idéale pour vos séjours familiaux ou de haut standing au Gabon.",
                    category = "Immobilier",
                    pricePerDay = 150000,
                    city = "Libreville",
                    neighborhood = "La Sablière",
                    ownerName = "Kofi Mensah",
                    ownerPhone = "077894512",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Appartement Chic Vue Mer - Batterie IV",
                    description = "Magnifique T3 meublé moderne et ultra-sécurisé à Batterie IV (Libreville). Grande terrasse avec vue panoramique sur l'Estuaire, salon spacieux d'inspiration contemporaine, deux suites parentales avec dressing, cuisine américaine équipée complète, ascenseur et parking sous-terrain.",
                    category = "Immobilier",
                    pricePerDay = 75000,
                    city = "Libreville",
                    neighborhood = "Batterie IV",
                    ownerName = "Marie-Claire Nzamba",
                    ownerPhone = "066451298",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Studio Cosy Près de l'Aéroport",
                    description = "Studio haut de gamme tout équipé et climatisé à Charbonnages/Aéroport. Idéal pour voyageurs ou professionnels de passage au Gabon. Connexion internet fibre optique ultra rapide, bouquet Canal+ complet, femme de ménage quotidienne et surveillance continue.",
                    category = "Immobilier",
                    pricePerDay = 35000,
                    city = "Libreville",
                    neighborhood = "Charbonnages",
                    ownerName = "Arthur Obiang",
                    ownerPhone = "074521485",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Bungalow Tropical de Vacances - Port-Gentil",
                    description = "Magnifique bungalow les pieds dans l'eau à Port-Gentil (N'tchengué). Un véritable havre de paix idéal pour s'évader le week-end. Équipé de climatisation, grand barbecue extérieur, transats en teck et groupe électrogène de secours.",
                    category = "Immobilier",
                    pricePerDay = 50000,
                    city = "Port-Gentil",
                    neighborhood = "N'tchengué",
                    ownerName = "Kofi Mensah",
                    ownerPhone = "077894512",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Toyota Prado VXR V6 2023 avec Chauffeur",
                    description = "Toyota Land Cruiser Prado VIP tout confort, boîte automatique séquentielle, intérieur en cuir surpiqué noir, toit panoramique ouvrant, climatisation automatique multi-zone. Véhicule 4x4 robuste idéal pour vos délégations à Libreville ou voyages à l'intérieur du pays (Oyem, Lambaréné, Franceville). Loué obligatoirement avec chauffeur bilingue formé à la conduite préventive.",
                    category = "Véhicules",
                    pricePerDay = 95000,
                    city = "Libreville",
                    neighborhood = "Aéroport",
                    ownerName = "Mael Koumba",
                    ownerPhone = "077145263",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Ford Ranger FX4 Double Cabine 4x4",
                    description = "Pick-up tout-terrain ultra robuste. Parfait pour les chantiers ou les longs trajets ruraux au Gabon. Suspension renforcée, grand coffre arrière sécurisé avec rollbar, treuil d'origine en cas de besoin et climatisation tropicalisée.",
                    category = "Véhicules",
                    pricePerDay = 65000,
                    city = "Libreville",
                    neighborhood = "Charbonnages",
                    ownerName = "Mael Koumba",
                    ownerPhone = "077145263",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Hyundai Accent Boite Auto (Économique)",
                    description = "Citadine de ville extrêmement fiable et à très faible consommation de carburant. Climatisation d'origine glaciale, intérieur propre, boîte de vitesse automatique réactive. Excellente pour circuler facilement au cœur de Libreville.",
                    category = "Véhicules",
                    pricePerDay = 25000,
                    city = "Libreville",
                    neighborhood = "Nzeng-Ayong",
                    ownerName = "Désiré Nguema",
                    ownerPhone = "066369524",
                    ownerRating = 4.3f,
                    imageUrl = "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Groupe Électrogène Insonorisé Cummins 40 kVA",
                    description = "Système de génération d'électricité diesel professionnel insonorisé de 40 kVA. Idéal pour chantiers d'envergure, mariages à Akanda, ou alimentation de secours immobilière complète à Libreville. Livré sur site avec câble de raccordement, plein de carburant initial et mise en service par technicien qualifié.",
                    category = "Équipements",
                    pricePerDay = 45000,
                    city = "Libreville",
                    neighborhood = "Oloumi",
                    ownerName = "SODIPGabon",
                    ownerPhone = "011745480",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Pack Sono Concert Pro + Jeux de Lumière",
                    description = "Matériel haut de gamme complet pour vos soirées festives ou d'entreprise au Gabon (jusqu'à 400 personnes). Comprend quatre enceintes amplifiées Yamaha de 1200W, deux caissons de basse puissants, table de mixage numérique Behinger avec connectivité Bluetooth, deux micros sans fil professionnels Shure, pont d'éclairage LED avec jeux de lumières commandés, et livraison/installation raccordée dans le grand Libreville.",
                    category = "Équipements",
                    pricePerDay = 60000,
                    city = "Akanda",
                    neighborhood = "Angondjé",
                    ownerName = "Avenir Événementiel",
                    ownerPhone = "074558833",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Terrain constructible 500m² - Akanda",
                    description = "Terrain plat viabilisé (eau, électricité, gaz) idéal pour construire villa ou immeuble. Situé dans zone résidentielle calme à Akanda, près du marché. Titre foncier disponible.",
                    category = "Immobilier",
                    pricePerDay = 25000,
                    city = "Libreville",
                    neighborhood = "Akanda",
                    ownerName = "Patrick Ondimba",
                    ownerPhone = "066123456",
                    ownerRating = 4.4f,
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Mercedes-Benz Classe C 2024 - VIP",
                    description = "Berline premium allemande entièrement noire, intérieur cuir beige, toit ouvrant panoramique, système audio Harman Kardon. Chauffeur inclus pour vos événements professionnels ou mariages à Libreville.",
                    category = "Véhicules",
                    pricePerDay = 120000,
                    city = "Libreville",
                    neighborhood = "Batterie IV",
                    ownerName = "Luxury Cars Gabon",
                    ownerPhone = "077987654",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Caméra Sony A7IV + Gimbal - Vidéaste",
                    description = "Kit complet pour vidéastes professionnels : boîtier Sony A7IV, objectif 24-70mm f/2.8, gimbal DJI RS3, deux batteries NP-FZ100, carte 256go. Idéal pour mariages, clips vidéo ou couverture d'événements au Gabon.",
                    category = "Équipements",
                    pricePerDay = 35000,
                    city = "Libreville",
                    neighborhood = "Nzeng-Ayong",
                    ownerName = "Studio Créatif Gabon",
                    ownerPhone = "066789123",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Tente Événementielle 10x20m - Mariage",
                    description = "Grande tente blanche de réception pour mariages et événements professionnels. Comprend structure métallique, bâche imperméable, sol parquet modulable, guirlandes lumineuses LED et installation sur site à Libreville ou Akanda.",
                    category = "Équipements",
                    pricePerDay = 80000,
                    city = "Akanda",
                    neighborhood = "Angondjé",
                    ownerName = "Avenir Événementiel",
                    ownerPhone = "074558833",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Moto Honda Africa Twin 2023 - Aventure",
                    description = "Moto trail tout-terrain pour aventuriers. Parfaite pour explorer les pistes du Gabon intérieur (Lopé, Minvoul, Makokou). Casque intégral et gants inclus. Permis moto obligatoire.",
                    category = "Véhicules",
                    pricePerDay = 30000,
                    city = "Franceville",
                    neighborhood = "Centre",
                    ownerName = "Gabon Aventure",
                    ownerPhone = "066456789",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Piscine Gonflable Familiale + Tabourets",
                    description = "Kit détente complet : piscine gonflable 3m x 0.76m avec filtration intégrée, 4 transats pliables, parasol UV. Livraison gratuite à Libreville. Idéal pour jardins ou terrasses lors des fêtes de fin d'année.",
                    category = "Équipements",
                    pricePerDay = 15000,
                    city = "Libreville",
                    neighborhood = "Oloumi",
                    ownerName = "Loc Gabon",
                    ownerPhone = "077234567",
                    ownerRating = 4.2f,
                    imageUrl = "https://images.unsplash.com/photo-1575429198097-0414ec08e8cd?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Duplex Moderne Vue Estuaire - Nkembo",
                    description = "Superbe duplex moderne de 180m² avec vue imprenable sur l'estuaire du Komo. 3 chambres avec salles de bain attenantes, salon double hauteur, cuisine équipée haut de gamme, piscine privée et jardin tropical. Gardiennage 24h/24.",
                    category = "Immobilier",
                    pricePerDay = 200000,
                    city = "Libreville",
                    neighborhood = "Nkembo",
                    ownerName = "Premium Properties Gabon",
                    ownerPhone = "011789456",
                    ownerRating = 5.0f,
                    imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Bureau Commercial Meublé - Montagne Sainte",
                    description = "Espace de bureau professionnel de 120m² entièrement meublé au cœur de Montagne Sainte. Open space modulable, 2 salles de réunion, cuisine équipée, parking 10 voitures. Fibre optique et gardiennage inclus. Idéal pour startups et PME.",
                    category = "Immobilier",
                    pricePerDay = 45000,
                    city = "Libreville",
                    neighborhood = "Montagne Sainte",
                    ownerName = "Agence Bongo Immobilier",
                    ownerPhone = "066123456",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Terrain Viabilisé 800m2 - Owendo",
                    description = "Terrain plat viabilisé de 800m² situé dans le nouveau lotissement d'Owendo. Eau potable, électricité CEG, accès bitumé. Titre foncier régulier. Proche du port maritime et de la zone industrielle. Parfait pour construction résidentielle ou commerciale.",
                    category = "Immobilier",
                    pricePerDay = 20000,
                    city = "Libreville",
                    neighborhood = "Owendo",
                    ownerName = "Patrick Ondimba",
                    ownerPhone = "077987654",
                    ownerRating = 4.3f,
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Toyota Hilux Double Cabine 4x4 2024",
                    description = "Pick-up Toyota Hilux Double Cabine 2024, boîte automatique, moteur 2.8L turbodiesel. Climatisation, GPS, caméra de recul, barres de toit. Entretien régulier chez le concessionnaire Toyota. Idéal pour chantiers et déplacements professionnels.",
                    category = "Véhicules",
                    pricePerDay = 55000,
                    city = "Port-Gentil",
                    neighborhood = "S ogłoszeni",
                    ownerName = "Gabon Transport Services",
                    ownerPhone = "066543210",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1559416523-140ddc3d238c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Camion Benne 10 Tonnes - Mines",
                    description = "Camion benne de 10 tonnes pour transport de matériaux, minerai, sable et gravier. Moteur diesel robuste, benne hydraulique. Permis C nécessaire. Chauffeur inclus sur demande. Disponible pour missions minières à Franceville et Moanda.",
                    category = "Véhicules",
                    pricePerDay = 85000,
                    city = "Franceville",
                    neighborhood = "Zone Industrielle",
                    ownerName = "Mines Gabon Logistics",
                    ownerPhone = "066789012",
                    ownerRating = 4.1f,
                    imageUrl = "https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Troupeau de Chèvres Locales (10 têtes)",
                    description = "Lot de 10 chèvres locales en bonne santé, âgées de 1 à 3 ans. Race locale gabonaise, bien nourries et vaccinées. Parfaites pour élevage ou événements culturels (dot, mariage). Prix négociable pour reprise.",
                    category = "Équipements",
                    pricePerDay = 10000,
                    city = "Lambaréné",
                    neighborhood = "Centre Ville",
                    ownerName = "Agro-Pastoral Gabon",
                    ownerPhone = "077345678",
                    ownerRating = 4.0f,
                    imageUrl = "https://images.unsplash.com/photo-1516467508483-a7212febe31a?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Pack Sono DJ Pro + Light Show",
                    description = "Système sonorisation professionnelle : 2 enceintes JBL PRX 815W (1500W RMS), 2 subwoofers 18\", table de mixage Pioneer DJM-900NXS2, 2 platines CDJ-3000. Package éclairage : 8 moving heads, 12 LED pars, fumigène. Montage et technicien inclus.",
                    category = "Équipements",
                    pricePerDay = 40000,
                    city = "Libreville",
                    neighborhood = "Batterie IV",
                    ownerName = "Events Pro Gabon",
                    ownerPhone = "066456789",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Van Toyota Hiace 14 Places - Shuttle",
                    description = "Minibus Toyota Hiace 2023, 14 places assises climatisées, ceintures de sécurité, coffre à bagages. Idéal pour transferts aéroport, excursions touristiques, événements d'entreprise. Chauffeur expérimenté inclus. Assurance tous risques.",
                    category = "Véhicules",
                    pricePerDay = 60000,
                    city = "Libreville",
                    neighborhood = "Quartier Nord",
                    ownerName = "Transport Plus Gabon",
                    ownerPhone = "077654321",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Appartement F2 Meublé - Sibang",
                    description = "Appartement F2 meublé de qualité dans le quartier résidentiel de Sibang. 1 chambre avec dressing, salon moderne, cuisine équipée (réfrigérateur, four, machine à café), salle de bain carrelée. Balcon avec vue sur le jardin. Gardiennage et parking.",
                    category = "Immobilier",
                    pricePerDay = 30000,
                    city = "Libreville",
                    neighborhood = "Sibang",
                    ownerName = "Marie Nguema Properties",
                    ownerPhone = "066876543",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Moto Yamaha NMAX 155cc - Livraison",
                    description = "Scooter Yamaha NMAX 155cc 2024, boîte CVT automatique, système ABS, phare LED. Idéal pour livraisons et déplacements urbains. Casque et gilet inclus. Contrôle technique à jour. Consommation économique : 2L/100km.",
                    category = "Véhicules",
                    pricePerDay = 15000,
                    city = "Oyem",
                    neighborhood = "Centre",
                    ownerName = "Moto Express Oyem",
                    ownerPhone = "077123098",
                    ownerRating = 4.2f,
                    imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85f82e?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                // ─── Événementiel ───
                RentalItem(
                    title = "Pack Sono JBL PartyBox - Événement",
                    description = "Système sonore JBL PartyBox 1000 complet avec 2 enceintes, mixeur, micros sans fil et câbles. Parfaite pour mariages, anniversaires et soirées. Livraison et montage inclus à Libreville.",
                    category = "Événementiel",
                    pricePerDay = 75000,
                    city = "Libreville",
                    neighborhood = "Nzeng-Ayong",
                    ownerName = "DJ Afro Sound",
                    ownerPhone = "066112233",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Décoration Mariage Premium - Tout Compris",
                    description = "Service complet de décoration pour mariages et événements : fleurs, nappes, chaises, arcs, éclairage LED, tente blanche 10x20m. Équipe professionnelle avec 10 ans d'expérience au Gabon.",
                    category = "Événementiel",
                    pricePerDay = 250000,
                    city = "Libreville",
                    neighborhood = "Batterie IV",
                    ownerName = "Élégance Events Gabon",
                    ownerPhone = "077445566",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1519225421980-715cb0215aed?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                // ─── Mode & Beauté ───
                RentalItem(
                    title = "Robe Wax Premium - Cérémonie",
                    description = "Robe longue en tissu wax hollandais authentique, coupe évasée, broderies main. Taille unique (ajustable). Parfaite pour mariages, baptêmes et cérémonies officielles.",
                    category = "Mode & Beauté",
                    pricePerDay = 25000,
                    city = "Libreville",
                    neighborhood = "Marché du Nord",
                    ownerName = "Fashion Gabon Studio",
                    ownerPhone = "066778899",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Kit Coiffure Afro Complet - Barbecue",
                    description = "Kit de coiffure professionnel : défrisage, tresses, tissages, extensions. Produits bio inclus. Déplacement à domicile possible. Spécialiste cheveux crépus et Afro.",
                    category = "Mode & Beauté",
                    pricePerDay = 15000,
                    city = "Port-Gentil",
                    neighborhood = "Siane",
                    ownerName = "Beauté d'Afrique",
                    ownerPhone = "077112233",
                    ownerRating = 4.4f,
                    imageUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                // ─── Services ───
                RentalItem(
                    title = "Traiteur Africain Premium - 100 personnes",
                    description = "Service traiteur complet pour événements : poisson braisé, poulet DG, riz, sauce arachide, banane plantain. Livraison chaude avec personnel de service. Minimum 50 personnes.",
                    category = "Services",
                    pricePerDay = 180000,
                    city = "Libreville",
                    neighborhood = "Oloumi",
                    ownerName = "Saveurs du Gabon",
                    ownerPhone = "066334455",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Photographe Professionnel - Événements",
                    description = "Photographe spécialisé mariages et corporate. Matériel Canon Pro, drone DJI, retouche numérique incluse. Livraison des photos en 48h. Portfolio disponible.",
                    category = "Services",
                    pricePerDay = 120000,
                    city = "Libreville",
                    neighborhood = "Louis",
                    ownerName = "Studio Photo Gabon",
                    ownerPhone = "077667788",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1554048612-b6a482bc67e5?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                // ─── Espaces ───
                RentalItem(
                    title = "Salle de Conférence - 50 places",
                    description = "Salle de conférence moderne climatisée avec écran projeteur, tableau interactif, micros sans fil et wifi fibre. Idéale pour réunions d'entreprise, séminaires et formations.",
                    category = "Espaces",
                    pricePerDay = 150000,
                    city = "Libreville",
                    neighborhood = "Centre Ville",
                    ownerName = "Espace Coworking Libreville",
                    ownerPhone = "066556677",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Terrain Nu - Port-Gentil",
                    description = "Terrain clôturé de 500m² à Port-Gentil, accès route bitumée, eau et électricité disponibles. Idéal pour entrepôt, parking ou projet immobilier. Titre foncier disponible.",
                    category = "Espaces",
                    pricePerDay = 50000,
                    city = "Port-Gentil",
                    neighborhood = "Siano",
                    ownerName = "Immo Port-Gentil",
                    ownerPhone = "077889900",
                    ownerRating = 4.3f,
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                // ─── Matériel Pro ───
                RentalItem(
                    title = "Groupe Électrogène 50kVA - Chantier",
                    description = "Groupe électrogène diesel 50kVA, démarrage automatique, silencieux. Idéal pour chantiers, événements et sites isolés. Carburant à la charge du locataire. Livraison possible.",
                    category = "Matériel Pro",
                    pricePerDay = 45000,
                    city = "Libreville",
                    neighborhood = "Awendjé",
                    ownerName = "Location Pro Gabon",
                    ownerPhone = "066990011",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1504222490345-c075b6008014?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Caméra Vidéo Sony A7III - Professionnelle",
                    description = "Boîtier Sony A7III + objectif 24-70mm f/2.8 + trépied + carte 128Go. Parfait pour tournages vidéo, clips et documentaires. Support technique inclus.",
                    category = "Matériel Pro",
                    pricePerDay = 35000,
                    city = "Libreville",
                    neighborhood = "Glass",
                    ownerName = "TechRent Gabon",
                    ownerPhone = "077223344",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                // ─── Marine & Fluvial ───
                RentalItem(
                    title = "Pirogue Motorisée 7m - Pêche",
                    description = "Pirogue en bois verni de 7m, moteur Yamaha 15CV, réservoir 50L. Équipement de pêche de base inclus. Départ du port de Libreville. Capacité 4 personnes.",
                    category = "Marine & Fluvial",
                    pricePerDay = 40000,
                    city = "Libreville",
                    neighborhood = "Port Autonome",
                    ownerName = "Pêche & Loisirs Gabon",
                    ownerPhone = "066445566",
                    ownerRating = 4.4f,
                    imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Kayak Biplace - Estuaire du Komo",
                    description = "Kayak de mer biplace, stable et léger, pagaies et gilets de sauvetage inclus. Exploration de l'estuaire du Komo et mangroves. Guide local possible en option.",
                    category = "Marine & Fluvial",
                    pricePerDay = 20000,
                    city = "Libreville",
                    neighborhood = "Nouveau Gabon",
                    ownerName = "Aventure Aquatique",
                    ownerPhone = "077556677",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1440778303588-435521a205bc?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                // ─── Sport & Loisirs ───
                RentalItem(
                    title = "VTT Tout-Terrain - Forêt de Lopé",
                    description = "Vélo tout-terrain Trek Marlin 7, 29 pouces, suspension avant. Parfait pour sentiers de la Lopé et balades en forêt. Casque et gants inclus. Guide disponible.",
                    category = "Sport & Loisirs",
                    pricePerDay = 12000,
                    city = "Makokou",
                    neighborhood = "Centre",
                    ownerName = "Outdoor Gabon Adventures",
                    ownerPhone = "077001122",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1544191696-102dbdaeeaa0?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Matériel Plongée Complet - 2 plongeurs",
                    description = "Kit plongée complet : combinaison, détendeur, bloc 12L, poids. Certifié AOW minimum. Sites : Pointe Denis, Île Evans. Guide moniteur inclus.",
                    category = "Sport & Loisirs",
                    pricePerDay = 50000,
                    city = "Libreville",
                    neighborhood = "Pointe Denis",
                    ownerName = "Dive Gabon Club",
                    ownerPhone = "066889900",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1544551763-77932a6c692c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Bel appartement 3 pièces vue mer - Nouveau Gabon",
                    description = "Superbe appartement T3 avec vue panoramique sur la mer. Climatisation réversible, cuisine équipée, eau courante 24h, parking sécurisé. Quartier calme à 5 min du centre commercial.",
                    category = "Immobilier",
                    pricePerDay = 45000,
                    city = "Libreville",
                    neighborhood = "Nouveau Gabon",
                    ownerName = "Jean-Pierre Mbarga",
                    ownerPhone = "077123456",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Studio meublé centre-ville - Louis",
                    description = "Studio tout meublé au coeur de Louis. Idéal pour étudiants ou professionnels. Climatisation, wifi, eau chaude. Marché à 200m. Bail mensuel possible.",
                    category = "Immobilier",
                    pricePerDay = 18000,
                    city = "Libreville",
                    neighborhood = "Louis",
                    ownerName = "Marie-Claire Onguene",
                    ownerPhone = "066987654",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Maison familiale 4 chambres - Port-Gentil",
                    description = "Grande maison familiale avec 4 chambres, 2 salles de bain, jardin arboré et garage. Idéale pour famille nombreuse. Eau potable, électricité 24h, gardiennage.",
                    category = "Immobilier",
                    pricePerDay = 85000,
                    city = "Port-Gentil",
                    neighborhood = "Centre-Ville",
                    ownerName = "Patrice Ndong",
                    ownerPhone = "077456789",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Appartement F3 Owendo - Vue port",
                    description = "Appartement spacieux F3 dans le nouveau lotissement d'Owendo. Vue sur le port, terrasse, climatisation, cuisine américaine. Parking inclus, proche gare routière.",
                    category = "Immobilier",
                    pricePerDay = 35000,
                    city = "Libreville",
                    neighborhood = "Owendo",
                    ownerName = "Serge Obiang",
                    ownerPhone = "066555123",
                    ownerRating = 4.4f,
                    imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Studio moderne Batterie IV - Standing",
                    description = "Studio haut standing à Batterie IV. Mobilier design, climatisation, eau chaude continue, fibre optique. Résidence avec gardiennage H24 et parking souterrain.",
                    category = "Immobilier",
                    pricePerDay = 30000,
                    city = "Libreville",
                    neighborhood = "Batterie IV",
                    ownerName = "Aimée Mba",
                    ownerPhone = "077789123",
                    ownerRating = 4.8f,
                    imageUrl = "https://images.unsplash.com/photo-1560185893-a55cbc8c57e8?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Maison avec piscine - Franceville",
                    description = "Belle maison individuelle avec piscine privée à Franceville. 3 chambres, salon, cuisine équipée, jardin. Idéal pour vacances ou séjour prolongé. Climatisation.",
                    category = "Immobilier",
                    pricePerDay = 60000,
                    city = "Franceville",
                    neighborhood = "Diéla",
                    ownerName = "Grâce Libama",
                    ownerPhone = "066333456",
                    ownerRating = 4.3f,
                    imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Bureau commercial - Centre-ville Libreville",
                    description = "Espace commercial de 80m² au centre-ville. Vitrine sur rue, eau et électricité, comptoir aménagé. Idéal boutique, salon ou agence. Bail commercial disponible.",
                    category = "Immobilier",
                    pricePerDay = 50000,
                    city = "Libreville",
                    neighborhood = "Centre Ville",
                    ownerName = "Agence Bongo Immobilier",
                    ownerPhone = "066123456",
                    ownerRating = 4.6f,
                    imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Parking couvert - Aéroport LBV",
                    description = "Place de parking couverte et sécurisée près de l'aéroport Léon Mba. Caméra de surveillance, accès badge. Location journalière ou mensuelle. Idéal pour voyageurs.",
                    category = "Espaces",
                    pricePerDay = 5000,
                    city = "Libreville",
                    neighborhood = "Aéroport",
                    ownerName = "Parking Plus Libreville",
                    ownerPhone = "077888999",
                    ownerRating = 4.2f,
                    imageUrl = "https://images.unsplash.com/photo-1506521781263-d8422e82f27a?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Entrepôt industriel - Owendo",
                    description = "Grand entrepôt de 300m² à Owendo, zone industrielle. Grue auxiliaire, quai de chargement, eau et électricité. Accès poids lourd. Idéal stockage ou production.",
                    category = "Espaces",
                    pricePerDay = 75000,
                    city = "Libreville",
                    neighborhood = "Zone Industrielle",
                    ownerName = "Immo Industriel Gabon",
                    ownerPhone = "066222333",
                    ownerRating = 4.1f,
                    imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Terrain 1000m² - Lambaréné",
                    description = "Grand terrain plat à Lambaréné, proche du fleuve Ogooué. Viabilisé, accès route bitumée. Titre foncier régulier. Idéal pour projet résidentiel ou touristique.",
                    category = "Immobilier",
                    pricePerDay = 15000,
                    city = "Lambaréné",
                    neighborhood = "Centre Ville",
                    ownerName = "Terres du Gabon",
                    ownerPhone = "077666777",
                    ownerRating = 4.0f,
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Chambre colocation - Sibang",
                    description = "Chambre dans appartement partagé à Sibang. Cuisine commune, salle de bain partagée, wifi inclus. Quartier calme, proche transports. Colocataires étudiants et jeunes actifs.",
                    category = "Immobilier",
                    pricePerDay = 5000,
                    city = "Libreville",
                    neighborhood = "Sibang",
                    ownerName = "Coloc Gabon",
                    ownerPhone = "066444555",
                    ownerRating = 4.3f,
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Studio meublé - Oyem Centre",
                    description = "Studio tout équipé au centre d'Oyem. Climatisation, eau courante, électricité stable. Marché à 5 minutes. Idéal pour missionnaires ou professionnels de passage.",
                    category = "Immobilier",
                    pricePerDay = 12000,
                    city = "Oyem",
                    neighborhood = "Centre",
                    ownerName = "Résidences Oyem",
                    ownerPhone = "077111222",
                    ownerRating = 4.2f,
                    imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Villa moderne 5 chambres - Nkembo",
                    description = "Villa contemporaine avec piscine, jardin tropical et vue estuaire. 5 chambres, 3 salles de bain, salon double hauteur, cuisine pro, garage double. Sécurité 24h.",
                    category = "Immobilier",
                    pricePerDay = 250000,
                    city = "Libreville",
                    neighborhood = "Nkembo",
                    ownerName = "Premium Properties Gabon",
                    ownerPhone = "011789456",
                    ownerRating = 5.0f,
                    imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Boutique commerciale - Marché du Nord",
                    description = "Local commercial de 40m² face au marché du Nord. Forte fréquentation, eau et électricité, vitrine vitrée. Idéal épicerie, boutique vêtements ou phone center.",
                    category = "Immobilier",
                    pricePerDay = 20000,
                    city = "Libreville",
                    neighborhood = "Marché du Nord",
                    ownerName = "Commerces Libreville",
                    ownerPhone = "066999000",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Appartement T2 - Sainte-Marie",
                    description = "Appartement T2 rénové dans le quartier historique de Sainte-Marie. Parquet, moulures, balcon. Proche cathédrale et centre-ville. Eau et électricité CEG.",
                    category = "Immobilier",
                    pricePerDay = 28000,
                    city = "Libreville",
                    neighborhood = "Sainte-Marie",
                    ownerName = "Patrimoine Libreville",
                    ownerPhone = "077333444",
                    ownerRating = 4.4f,
                    imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Maison de ville - Mouila",
                    description = "Charmante maison de ville à Mouila. 2 chambres, jardin, terrasse ombragée. Ambiance rurale avec confort moderne. Idéal retraite ou vacances au calme.",
                    category = "Immobilier",
                    pricePerDay = 25000,
                    city = "Mouila",
                    neighborhood = "Centre",
                    ownerName = "Maisons du Gabon",
                    ownerPhone = "077555666",
                    ownerRating = 4.1f,
                    imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Chambre meublée colocation - Glass",
                    description = "Chambre meublée dans colocation moderne à Glass. Wi-Fi haut débit, climatisation, cuisine équipée commune. Bureaux et banques à proximité. Bail flexible.",
                    category = "Immobilier",
                    pricePerDay = 6000,
                    city = "Libreville",
                    neighborhood = "Glass",
                    ownerName = "Vie Coloc",
                    ownerPhone = "066888777",
                    ownerRating = 4.2f,
                    imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Terrain commercial 500m² - Owendo",
                    description = "Terrain clôturé zone commerciale Owendo. Accès port, route bitumée, electricité disponible. Idéal dépôt, atelier ou station-service. Titre foncier OK.",
                    category = "Immobilier",
                    pricePerDay = 30000,
                    city = "Libreville",
                    neighborhood = "Owendo",
                    ownerName = "Patrick Ondimba",
                    ownerPhone = "077987654",
                    ownerRating = 4.3f,
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Duplex luxe Vue Estuaire - Akanda",
                    description = "Duplex de luxe de 200m² avec vue imprenable sur la baie d'Akanda. 4 chambres, 3 salles de bain, piscine privée, jardin paysager. Domotique, gardiennage.",
                    category = "Immobilier",
                    pricePerDay = 180000,
                    city = "Libreville",
                    neighborhood = "Akanda",
                    ownerName = "Luxury Homes Gabon",
                    ownerPhone = "011666777",
                    ownerRating = 4.9f,
                    imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Bureau coworking - Nzeng-Ayong",
                    description = "Espace coworking partagé avec postes individuels, salles de réunion et café inclus. Fibre optique, climatisation. Abonnement journalier ou mensuel. Ambiance startup.",
                    category = "Espaces",
                    pricePerDay = 10000,
                    city = "Libreville",
                    neighborhood = "Nzeng-Ayong",
                    ownerName = "Hub Coworking Libreville",
                    ownerPhone = "066777888",
                    ownerRating = 4.7f,
                    imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Parking extérieur - Marché du Nord",
                    description = "Place de parking extérieure près du Marché du Nord. Surveillance par gardien. Idéal pour commerçants et clients. Location mensuelle disponible.",
                    category = "Espaces",
                    pricePerDay = 3000,
                    city = "Libreville",
                    neighborhood = "Marché du Nord",
                    ownerName = "Parking Plus Libreville",
                    ownerPhone = "077888999",
                    ownerRating = 4.0f,
                    imageUrl = "https://images.unsplash.com/photo-1506521781263-d8422e82f27a?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Entrepôt frigorifique - Owendo",
                    description = "Entrepôt frigorifique de 150m² à Owendo. Température contrôlée, idéal denrées alimentaires ou produits pharmaceutiques. Électricité garantie, alarme incendie.",
                    category = "Espaces",
                    pricePerDay = 90000,
                    city = "Libreville",
                    neighborhood = "Zone Industrielle",
                    ownerName = "Froid Logistics Gabon",
                    ownerPhone = "066111000",
                    ownerRating = 4.5f,
                    imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                ),
                RentalItem(
                    title = "Terrain agricole 2000m² - Oyem",
                    description = "Grand terrain agricole à Oyem, sol fertile, eau disponible pour irrigation. Accès route. Idéal cultures maraîchères ou élevage. Titre foncier disponible.",
                    category = "Immobilier",
                    pricePerDay = 10000,
                    city = "Oyem",
                    neighborhood = "Périurbain",
                    ownerName = "Agro-Fermes du Nord",
                    ownerPhone = "077222333",
                    ownerRating = 4.0f,
                    imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    isVerified = false
                ),
                RentalItem(
                    title = "Local atelier - Oloumi",
                    description = "Local atelier de 120m² à Oloumi. Grue, porte de garage, électricité三相. Idéal mécanique, soudure ou menuiserie. Bail commercial longue durée.",
                    category = "Espaces",
                    pricePerDay = 35000,
                    city = "Libreville",
                    neighborhood = "Oloumi",
                    ownerName = "Zones Industrielles SA",
                    ownerPhone = "066444333",
                    ownerRating = 4.2f,
                    imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80",
                    isVerified = true
                )
            )
            for (item in seedItems) {
                rentalDao.insertRentalItem(item)
            }

            // Seed initial chat conversations
            val now = System.currentTimeMillis()
            val seedMessages = listOf(
                // Conversation 1: Villa La Sablière - Availability check
                ChatMessage(rentalItemId = 1, sender = "Kofi Mensah", messageText = "Bonjour ! La villa est disponible du 15 au 20 juillet. Souhaitez-vous réserver ?", timestamp = now - 3600000),
                ChatMessage(rentalItemId = 1, sender = "User", messageText = "Oui, elle correspond parfaitement à mes critères. Le WiFi est inclus ?", timestamp = now - 3500000),
                ChatMessage(rentalItemId = 1, sender = "Kofi Mensah", messageText = "Oui, fibre optique 100 Mo/s. La piscine est également chauffée.", timestamp = now - 3400000),

                // Conversation 2: Prado - Price negotiation
                ChatMessage(rentalItemId = 5, sender = "Mael Koumba", messageText = "Le Prado est disponible dès demain. Besoin d'un chauffeur ?", timestamp = now - 7200000),
                ChatMessage(rentalItemId = 5, sender = "User", messageText = "Non merci, je conduis moi-même. Le GPS est fonctionnel ?", timestamp = now - 7100000),
                ChatMessage(rentalItemId = 5, sender = "Mael Koumba", messageText = "Oui, tout est opérationnel. Je vous envoie les photos de l'état actuel.", timestamp = now - 7000000),

                // Conversation 3: Pack Sono - Event booking
                ChatMessage(rentalItemId = 9, sender = "Avenir Evenementiel", messageText = "Pack sono complet disponible pour le 28 juin. Montage inclus ?", timestamp = now - 14400000),
                ChatMessage(rentalItemId = 9, sender = "User", messageText = "Oui, j'organise un mariage à Akanda. Combien pour 3 jours ?", timestamp = now - 14300000),

                // Conversation 4: Villa - Cancellation request
                ChatMessage(rentalItemId = 1, sender = "User", messageText = "Malheureusement je dois annuler ma réservation du 15 juillet. Est-ce possible ?", timestamp = now - 86400000),
                ChatMessage(rentalItemId = 1, sender = "Kofi Mensah", messageText = "Pas de souci, je vais procéder à l'annulation. Le remboursement sera effectué sous 48h.", timestamp = now - 86300000),
                ChatMessage(rentalItemId = 1, sender = "User", messageText = "Merci pour votre compréhension. Je reviendrai à une autre date.", timestamp = now - 86200000),

                // Conversation 5: Toyota Hilux - Damage report
                ChatMessage(rentalItemId = 5, sender = "User", messageText = "J'ai constaté une rayure sur le pare-chocs arrière au retour du véhicule.", timestamp = now - 259200000),
                ChatMessage(rentalItemId = 5, sender = "Mael Koumba", messageText = "Pouvez-vous m'envoyer une photo ? Nous allons évaluer les dommages.", timestamp = now - 259100000),
                ChatMessage(rentalItemId = 5, sender = "User", messageText = "Photo envoyée. C'est une rayure superficielle, pas de bombe de peinture.", timestamp = now - 259000000),
                ChatMessage(rentalItemId = 5, sender = "Mael Koumba", messageText = "Merci. Étant donné le caractère superficiel, je n'appliquerai pas de retenue sur la caution.", timestamp = now - 258900000),

                // Conversation 6: Appartement Batterie IV - Visit request
                ChatMessage(rentalItemId = 2, sender = "User", messageText = "Bonjour, est-il possible de visiter l'appartement ce week-end ?", timestamp = now - 432000000),
                ChatMessage(rentalItemId = 2, sender = "Marie-Claire Nzamba", messageText = "Bien sûr ! Samedi matin de 9h à 11h, c'est parfait pour vous ?", timestamp = now - 431900000),
                ChatMessage(rentalItemId = 2, sender = "User", messageText = "Parfait, je serai là à 9h30. Merci !", timestamp = now - 431800000),

                // Conversation 7: Bureau Montagne Sainte - Invoice request
                ChatMessage(rentalItemId = 16, sender = "User", messageText = "Pourriez-vous me fournir une facture pour la location du bureau ?", timestamp = now - 604800000),
                ChatMessage(rentalItemId = 16, sender = "Agence Bongo Immobilier", messageText = "Bien sûr, je vous envoie la facture PDF par email. Quel est votre adresse ?", timestamp = now - 604700000),

                // Conversation 8: Piscine Gonflable - Deposit return
                ChatMessage(rentalItemId = 10, sender = "User", messageText = "Bonjour, quand sera retournée ma caution de 20 000 F ?", timestamp = now - 518400000),
                ChatMessage(rentalItemId = 10, sender = "Loc Gabon", messageText = "La vérification est terminée, tout est en ordre. Le virement sera effectué aujourd'hui.", timestamp = now - 518300000),
                ChatMessage(rentalItemId = 10, sender = "User", messageText = "Parfait, merci pour la rapidité !", timestamp = now - 518200000),

                // Conversation 9: Terrain Owendo - Location question
                ChatMessage(rentalItemId = 17, sender = "User", messageText = "Le terrain est-il constructible ? Y a-t-il des restrictions ?", timestamp = now - 172800000),
                ChatMessage(rentalItemId = 17, sender = "Patrick Ondimba", messageText = "Oui, zone résidentielle mixte. Vous pouvez construire jusqu'à R+2. Le PLU est disponible.", timestamp = now - 172700000),

                // Conversation 10: Camion Benne - Availability
                ChatMessage(rentalItemId = 18, sender = "User", messageText = "Le camion est disponible pour une mission à Moanda la semaine prochaine ?", timestamp = now - 345600000),
                ChatMessage(rentalItemId = 18, sender = "Mines Gabon Logistics", messageText = "Oui, disponible du lundi au vendredi. Besoin d'un chauffeur ?", timestamp = now - 345500000),

                // Conversation 11: Van Hiace - Airport transfer
                ChatMessage(rentalItemId = 22, sender = "User", messageText = "Je réserve le van pour 12 personnes, transfert aéroport le 20 à 6h du matin.", timestamp = now - 777600000),
                ChatMessage(rentalItemId = 22, sender = "Transport Plus Gabon", messageText = "Confirmé ! Le chauffeur vous attendra en hall d'arrivée avec un panneau LocAll.", timestamp = now - 777500000),
                ChatMessage(rentalItemId = 22, sender = "User", messageText = "Parfait. Le prix inclut-il les péages ?", timestamp = now - 777400000),
                ChatMessage(rentalItemId = 22, sender = "Transport Plus Gabon", messageText = "Oui, tout est inclus dans le tarif de 60 000 F. Bon voyage !", timestamp = now - 777300000),

                // Conversation 12: Moto NMAX - Test ride
                ChatMessage(rentalItemId = 25, sender = "User", messageText = "Est-ce que je peux faire un essai avant la location longue durée ?", timestamp = now - 43200000),
                ChatMessage(rentalItemId = 25, sender = "Moto Express Oyem", messageText = "Bien sûr, venez essayer demain matin au magasin. C'est gratuit !", timestamp = now - 43100000),

                // Enriched messages: image and location types
                ChatMessage(rentalItemId = 1, sender = "Propriétaire", messageText = "[image] https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800", timestamp = now - 3600000),
                ChatMessage(rentalItemId = 1, sender = "Propriétaire", messageText = "[location] Villa La Sablière, Rue des Manguiers, Libreville", timestamp = now - 3500000),
                ChatMessage(rentalItemId = 2, sender = "Propriétaire", messageText = "[image] https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=800", timestamp = now - 7200000)
            )
            for (msg in seedMessages) {
                rentalDao.insertChatMessage(msg)
            }

            // Seed notifications
            val seedNotifications = listOf(
                NotificationEntity(id = 1, type = "reservation", title = "Réservation confirmée", message = "Votre réservation Villa La Sablière est confirmée", time = "Il y a 2 heures", isRead = false),
                NotificationEntity(id = 2, type = "message", title = "Nouveau message", message = "Kofi: La villa est disponible du 15 au 20", time = "Il y a 3 heures", isRead = false),
                NotificationEntity(id = 3, type = "payment", title = "Paiement reçu", message = "45 000 F CFA reçus via Airtel Money", time = "Il y a 5 heures", isRead = true),
                NotificationEntity(id = 4, type = "alert", title = "Rappel de retour", message = "Retour Toyota Hilux prévu demain à 18h", time = "Il y a 1 jour", isRead = false),
                NotificationEntity(id = 5, type = "system", title = "Vérification", message = "Votre identité a été vérifiée avec succès", time = "Il y a 2 jours", isRead = true),
                NotificationEntity(id = 6, type = "reservation", title = "Réservation annulée", message = "Réservation Pack Sono annulée par le locataire", time = "Il y a 3 jours", isRead = true),
                NotificationEntity(id = 7, type = "promotion", title = "Offre flash", message = "-30% sur les véhicules ce week-end !", time = "Il y a 3 jours", isRead = true),
                NotificationEntity(id = 8, type = "message", title = "Nouveau message", message = "Marie-Claire: L'appartement est libre samedi", time = "Il y a 4 jours", isRead = false),
                NotificationEntity(id = 9, type = "reservation", title = "Rappel de retour", message = "Retourner Pack Sono demain avant 18h", time = "Il y a 1 semaine", isRead = false),
                NotificationEntity(id = 10, type = "payment", title = "Point de fidélité", message = "+250 points pour dernière réservation", time = "Il y a 2 semaines", isRead = true),
                NotificationEntity(id = 11, type = "promotion", title = "Offre spéciale", message = "-15% sur Immobilier ce week-end", time = "Il y a 2 semaines", isRead = true),
                NotificationEntity(id = 12, type = "system", title = "Sécurité", message = "Nouveau mot de passe configuré", time = "Il y a 3 semaines", isRead = true),
                NotificationEntity(id = 13, type = "reservation", title = "Modification acceptée", message = "Changement de dates accepté", time = "Il y a 3 semaines", isRead = true),
                NotificationEntity(id = 14, type = "message", title = "Relance propriétaire", message = "Marie-Claire vous a envoyé un rappel", time = "Il y a 1 mois", isRead = false),
                NotificationEntity(id = 15, type = "payment", title = "Facture disponible", message = "Facture location Hilux en téléchargement", time = "Il y a 1 mois", isRead = true)
            )
            for (notification in seedNotifications) {
                rentalDao.insertNotification(notification)
            }

            // Seed disputes
            val seedDisputes = listOf(
                DisputeEntity(id = 1, title = "Dommage Toyota Hilux", status = "En cours", date = "20/06/2026", type = "Dommage", description = "Le pare-chocs avant a été endommagé lors de la location", claimAmount = 150000),
                DisputeEntity(id = 2, title = "Annulation tardive Pack Sono", status = "Résolu", date = "15/06/2026", type = "Annulation", description = "Annulation moins de 24h avant l'événement", claimAmount = 50000)
            )
            for (dispute in seedDisputes) {
                rentalDao.insertDispute(dispute)
            }

            // Seed earnings
            val seedEarnings = listOf(
                EarningEntity(id = 1, amount = 45000, date = "22/06/2026", source = "Location Toyota Hilux - 3 jours", status = "Versé"),
                EarningEntity(id = 2, amount = 120000, date = "18/06/2026", source = "Location Villa La Sablière - 7 jours", status = "Versé"),
                EarningEntity(id = 3, amount = 25000, date = "15/06/2026", source = "Location Pack Sono Concert - 1 jour", status = "En attente"),
                EarningEntity(id = 4, amount = 85000, date = "10/06/2026", source = "Location Prado Port-Gentil - 5 jours", status = "Versé")
            )
            for (earning in seedEarnings) {
                rentalDao.insertEarning(earning)
            }

            // Seed payment history
            val seedPayments = listOf(
                PaymentHistoryEntity(id = 1, amount = 45000, date = "22/06/2026", description = "Location Toyota Hilux - 3 jours", method = "Airtel Money"),
                PaymentHistoryEntity(id = 2, amount = 120000, date = "18/06/2026", description = "Location Villa La Sablière - 7 jours", method = "Moov Money"),
                PaymentHistoryEntity(id = 3, amount = 25000, date = "15/06/2026", description = "Location Pack Sono - 1 jour", method = "Carte Bancaire"),
                PaymentHistoryEntity(id = 4, amount = 85000, date = "10/06/2026", description = "Location Prado - 5 jours", method = "Airtel Money")
            )
            for (payment in seedPayments) {
                rentalDao.insertPaymentHistory(payment)
            }

            // Seed reviews
            val seedReviews = listOf(
                ReviewEntity(rentalItemId = 1, rating = 5, comment = "Superbe villa, très propre et spacieuse. La piscine est un vrai plus.", author = "Stéphane Koumba", date = "20/06/2026"),
                ReviewEntity(rentalItemId = 1, rating = 4, comment = "Belle vue sur la mer, accès facile. La climatisation fonctionne parfaitement.", author = "Patricia Ndong", date = "15/05/2026"),
                ReviewEntity(rentalItemId = 1, rating = 5, comment = "Séjour exceptionnel ! Le propriétaire est très arrangeant.", author = "Cécilia Mba", date = "01/04/2026"),
                ReviewEntity(rentalItemId = 2, rating = 5, comment = "Appartement moderne avec une vue imprenable sur l'estuaire.", author = "Rodrigue Mintsa", date = "18/06/2026"),
                ReviewEntity(rentalItemId = 2, rating = 4, comment = "Bon emplacement, parking pratique. Le quartier est calme.", author = "Sylvie Obiang", date = "10/05/2026"),
                ReviewEntity(rentalItemId = 2, rating = 3, comment = "L'appartement est bien mais le bruit de la route est gênant.", author = "Patrice Oyé", date = "20/04/2026"),
                ReviewEntity(rentalItemId = 3, rating = 5, comment = "Appartement très moderne et bien climatisé.", author = "Inès Bongo", date = "22/06/2026"),
                ReviewEntity(rentalItemId = 3, rating = 4, comment = "Studio cozy et bien équipé. Localisation pratique.", author = "Bernadette Nguéma", date = "15/05/2026"),
                ReviewEntity(rentalItemId = 5, rating = 5, comment = "Le Prado est impeccable, très robuste.", author = "Marc Aubame", date = "10/06/2026"),
                ReviewEntity(rentalItemId = 5, rating = 4, comment = "Véhicule propre et confortable. Bon retour de caution.", author = "Yannick Mba", date = "02/06/2026"),
                ReviewEntity(rentalItemId = 5, rating = 2, comment = "Le réservoir était à moitié vide à la récupération.", author = "Françoise Limbaka", date = "15/05/2026"),
                ReviewEntity(rentalItemId = 9, rating = 5, comment = "Pack sono exceptionnel pour notre mariage !", author = "Hélène Ovono", date = "25/06/2026"),
                ReviewEntity(rentalItemId = 9, rating = 4, comment = "Très bon matériel, le technicien était ponctuel.", author = "Aimée Mboumba", date = "10/06/2026"),
                ReviewEntity(rentalItemId = 16, rating = 5, comment = "Bureau spacieux et bien équipé.", author = "Fabrice Mikala", date = "20/06/2026"),
                ReviewEntity(rentalItemId = 16, rating = 4, comment = "Bon rapport qualité-prix pour un bureau meublé.", author = "Josiane Nkoghe", date = "10/06/2026"),
                ReviewEntity(rentalItemId = 14, rating = 5, comment = "Très bon appartement, propre et bien situé.", author = "Jean-Pierre Mbarga", date = "15/06/2026"),
                ReviewEntity(rentalItemId = 14, rating = 4, comment = "Propriétaire sérieux, je recommande.", author = "Claire Ondo", date = "05/06/2026"),
                ReviewEntity(rentalItemId = 15, rating = 5, comment = "Excellent rapport qualité-prix, je reviendrai.", author = "Thierry Mba", date = "20/05/2026"),
                ReviewEntity(rentalItemId = 15, rating = 4, comment = "L'emplacement est idéal, proche du marché.", author = "Sophie Nguéma", date = "10/05/2026"),
                ReviewEntity(rentalItemId = 16, rating = 3, comment = "Bon logement mais bruit du voisinage la nuit.", author = "Alain Obiang", date = "01/05/2026"),
                ReviewEntity(rentalItemId = 17, rating = 5, comment = "Maison spacieuse, parfaite pour nos vacances.", author = "Famille Essono", date = "25/06/2026"),
                ReviewEntity(rentalItemId = 17, rating = 4, comment = "Jardin bien entretenu, piscine superbe.", author = "Diane Mba", date = "15/06/2026"),
                ReviewEntity(rentalItemId = 18, rating = 5, comment = "Appartement moderne, tout est neuf.", author = "Marc Léonard", date = "10/06/2026"),
                ReviewEntity(rentalItemId = 19, rating = 4, comment = "Studio compact mais fonctionnel. Je recommande.", author = "Pauline Mvé", date = "28/05/2026"),
                ReviewEntity(rentalItemId = 20, rating = 5, comment = "Villa de rêve, la vue est incroyable !", author = "Hervé Mba", date = "22/06/2026"),
                ReviewEntity(rentalItemId = 21, rating = 4, comment = "Très bon emplacement, facile d'accès.", author = "Nadia Onguene", date = "18/06/2026"),
                ReviewEntity(rentalItemId = 22, rating = 5, comment = "Parking pratique et sécurisé, je suis rassuré.", author = "Stéphane Ndounda", date = "12/06/2026"),
                ReviewEntity(rentalItemId = 23, rating = 4, comment = "Entrepôt bien équipé, livraison facilitée.", author = "Groupe Total Gabon", date = "08/06/2026"),
                ReviewEntity(rentalItemId = 24, rating = 5, comment = "Terrain parfait pour notre projet immobilier.", author = "Immobilière du Gabon", date = "01/06/2026"),
                ReviewEntity(rentalItemId = 25, rating = 4, comment = "Colocation sympa, ambiance étudiante.", author = "Julien Minko", date = "20/05/2026"),
                ReviewEntity(rentalItemId = 26, rating = 5, comment = "Excellent studio pour mission longue durée.", author = "Entreprise Minière", date = "15/05/2026"),
                ReviewEntity(rentalItemId = 27, rating = 5, comment = "Villa exceptionnelle, tout est parfait.", author = "Ambassade de France", date = "10/06/2026"),
                ReviewEntity(rentalItemId = 28, rating = 4, comment = "Boutique idéale pour lancer mon commerce.", author = "Fatima Béatrice", date = "05/06/2026"),
                ReviewEntity(rentalItemId = 29, rating = 5, comment = "Appartement charmant avec beaucoup de caractère.", author = "Gaston Ondo", date = "01/06/2026"),
                ReviewEntity(rentalItemId = 30, rating = 4, comment = "Maison accueillante, parfaitement entretenue.", author = "Véronique Mba", date = "25/05/2026"),
                ReviewEntity(rentalItemId = 31, rating = 5, comment = "Colocation top, j'ai trouvé de bons amis.", author = "Kevin Ntoutoume", date = "20/05/2026"),
                ReviewEntity(rentalItemId = 32, rating = 4, comment = "Terrain bien situé, près de la route principale.", author = "Société Forestière", date = "15/05/2026"),
                ReviewEntity(rentalItemId = 33, rating = 5, comment = "Duplex de luxe, prestations haut de gamme.", author = "Direction Générale Mines", date = "10/05/2026"),
                ReviewEntity(rentalItemId = 34, rating = 4, comment = "Cowworking moderne et bien équipé.", author = "Startup Gabon Tech", date = "05/05/2026"),
                ReviewEntity(rentalItemId = 35, rating = 5, comment = "Parking pratique, pas de stress pour la voiture.", author = "Marie-Louise Mvé", date = "01/05/2026"),
                ReviewEntity(rentalItemId = 36, rating = 4, comment = "Entrepôt froid impeccable, conforme aux normes.", author = "Pharma Gabon", date = "28/04/2026")
            )
            for (review in seedReviews) {
                rentalDao.insertReview(review)
            }

            val seedMockUsers = listOf(
                MockUser(id = 1, name = "Jean-Pierre Mbarga", city = "Libreville", verified = true, trustScore = 92, listings = 5),
                MockUser(id = 2, name = "Marie-Claire Onguene", city = "Port-Gentil", verified = true, trustScore = 88, listings = 3),
                MockUser(id = 3, name = "Patrice Ndong", city = "Franceville", verified = false, trustScore = 65, listings = 1),
                MockUser(id = 4, name = "Serge Obiang", city = "Libreville", verified = true, trustScore = 90, listings = 4),
                MockUser(id = 5, name = "Aimée Mba", city = "Libreville", verified = true, trustScore = 95, listings = 6),
                MockUser(id = 6, name = "Grâce Libama", city = "Franceville", verified = true, trustScore = 82, listings = 2),
                MockUser(id = 7, name = "Thierry Mba", city = "Libreville", verified = false, trustScore = 71, listings = 1),
                MockUser(id = 8, name = "Sophie Nguéma", city = "Port-Gentil", verified = true, trustScore = 87, listings = 3),
                MockUser(id = 9, name = "Claire Ondo", city = "Libreville", verified = true, trustScore = 91, listings = 4),
                MockUser(id = 10, name = "Alain Obiang", city = "Oyem", verified = false, trustScore = 60, listings = 1),
                MockUser(id = 11, name = "Famille Essono", city = "Libreville", verified = true, trustScore = 94, listings = 7),
                MockUser(id = 12, name = "Diane Mba", city = "Lambaréné", verified = true, trustScore = 85, listings = 2),
                MockUser(id = 13, name = "Marc Léonard", city = "Libreville", verified = true, trustScore = 89, listings = 5),
                MockUser(id = 14, name = "Pauline Mvé", city = "Port-Gentil", verified = false, trustScore = 68, listings = 1),
                MockUser(id = 15, name = "Hervé Mba", city = "Libreville", verified = true, trustScore = 93, listings = 6),
                MockUser(id = 16, name = "Nadia Onguene", city = "Franceville", verified = true, trustScore = 86, listings = 3),
                MockUser(id = 17, name = "Stéphane Ndounda", city = "Libreville", verified = true, trustScore = 90, listings = 4),
                MockUser(id = 18, name = "Groupe Total Gabon", city = "Port-Gentil", verified = true, trustScore = 98, listings = 10),
                MockUser(id = 19, name = "Julien Minko", city = "Libreville", verified = false, trustScore = 72, listings = 2),
                MockUser(id = 20, name = "Fatima Béatrice", city = "Oyem", verified = true, trustScore = 80, listings = 2)
            )

            val enrichedMessages = listOf(
                ChatMessage(rentalItemId = 14, sender = "Jean-Pierre Mbarga", messageText = "Bonjour ! L'appartement est disponible dès le 1er juillet. Souhaitez-vous une visite ?", timestamp = now - 3600000),
                ChatMessage(rentalItemId = 14, sender = "User", messageText = "Oui, c'est possible samedi ?", timestamp = now - 3500000),
                ChatMessage(rentalItemId = 14, sender = "Jean-Pierre Mbarga", messageText = "Parfait, samedi à 10h. Je vous envoie l'adresse exacte.", timestamp = now - 3400000),
                ChatMessage(rentalItemId = 17, sender = "Marie-Claire Onguene", messageText = "Le studio est meublé, climatisé. Tarif mensuel de 150 000 F.", timestamp = now - 7200000),
                ChatMessage(rentalItemId = 17, sender = "User", messageText = "C'est négociable pour un bail de 6 mois ?", timestamp = now - 7100000),
                ChatMessage(rentalItemId = 17, sender = "Marie-Claire Onguene", messageText = "Je peux faire 140 000 F/mois pour 6 mois minimum.", timestamp = now - 7000000),
                ChatMessage(rentalItemId = 19, sender = "Patrice Ndong", messageText = "La maison est disponible pour les fêtes de fin d'année.", timestamp = now - 14400000),
                ChatMessage(rentalItemId = 19, sender = "User", messageText = "Combien pour 2 semaines en décembre ?", timestamp = now - 14300000),
                ChatMessage(rentalItemId = 19, sender = "Patrice Ndong", messageText = "2 semaines = 1 200 000 F CFA tout compris. La maison a 4 chambres.", timestamp = now - 14200000),
                ChatMessage(rentalItemId = 25, sender = "Aimée Mba", messageText = "Le studio standing est libre. Meublé design, climatisation réversible.", timestamp = now - 86400000),
                ChatMessage(rentalItemId = 25, sender = "User", messageText = "Super ! Y a-t-il un parking dans la résidence ?", timestamp = now - 86300000),
                ChatMessage(rentalItemId = 25, sender = "Aimée Mba", messageText = "Oui, parking souterrain avec place attribuée. Gardiennage 24h.", timestamp = now - 86200000),
                ChatMessage(rentalItemId = 21, sender = "Serge Obiang", messageText = "L'appartement F3 Owendo est disponible. Vue sur le port.", timestamp = now - 172800000),
                ChatMessage(rentalItemId = 21, sender = "User", messageText = "Le loyer est de combien par mois ?", timestamp = now - 172700000),
                ChatMessage(rentalItemId = 21, sender = "Serge Obiang", messageText = "350 000 F/mois charges comprises. Eau et électricité incluses.", timestamp = now - 172600000),
                ChatMessage(rentalItemId = 36, sender = "User", messageText = "L'entrepôt froid est-il conforme aux normes pharmaceutiques ?", timestamp = now - 259200000),
                ChatMessage(rentalItemId = 36, sender = "Froid Logistics Gabon", messageText = "Oui, certifié GDP. Température maintenue entre 2 et 8°C.", timestamp = now - 259100000),
                ChatMessage(rentalItemId = 36, sender = "User", messageText = "Parfait pour notre stockage de vaccins. Quel volume maximum ?", timestamp = now - 259000000),
                ChatMessage(rentalItemId = 36, sender = "Froid Logistics Gabon", messageText = "Capacité de 50 palettes. Alarme température et backup groupe électrogène.", timestamp = now - 258900000),
                ChatMessage(rentalItemId = 18, sender = "Grâce Libama", messageText = "La maison avec piscine est idéale pour les vacances.", timestamp = now - 345600000),
                ChatMessage(rentalItemId = 18, sender = "User", messageText = "La piscine est entretenue régulièrement ?", timestamp = now - 345500000),
                ChatMessage(rentalItemId = 18, sender = "Grâce Libama", messageText = "Oui, maintenance hebdomadaire comprise. Eau toujours propre.", timestamp = now - 345400000)
            )
            for (msg in enrichedMessages) {
                rentalDao.insertChatMessage(msg)
            }
        }
    }
}

data class MockUser(val id: Int, val name: String, val city: String, val verified: Boolean, val trustScore: Int, val listings: Int)
