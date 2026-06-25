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

    suspend fun insertChatMessage(message: ChatMessage) =
        rentalDao.insertChatMessage(message)

    suspend fun getLastOwnerMessage(itemId: Int): ChatMessage? =
        rentalDao.getLastOwnerMessage(itemId)

    suspend fun upsertUserProfile(profile: UserProfile) =
        rentalDao.upsertUserProfile(profile)

    suspend fun insertSearchHistory(entry: SearchHistoryEntry) =
        rentalDao.insertSearchHistory(entry)

    suspend fun clearSearchHistory() =
        rentalDao.clearSearchHistory()

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
                )
            )
            for (item in seedItems) {
                rentalDao.insertRentalItem(item)
            }

            // Seed initial chat conversations
            val seedMessages = listOf(
                ChatMessage(rentalItemId = 1, sender = "Kofi Mensah", messageText = "Bonjour ! La villa est disponible du 15 au 20 juillet. Souhaitez-vous réserver ?", timestamp = System.currentTimeMillis() - 3600000),
                ChatMessage(rentalItemId = 1, sender = "User", messageText = "Oui, elle correspond parfaitement à mes critères. Le WiFi est inclus ?", timestamp = System.currentTimeMillis() - 3500000),
                ChatMessage(rentalItemId = 1, sender = "Kofi Mensah", messageText = "Oui, fibre optique 100 Mo/s. La piscine est également chauffée.", timestamp = System.currentTimeMillis() - 3400000),
                ChatMessage(rentalItemId = 5, sender = "Mael Koumba", messageText = "Le Prado est disponible dès demain. Besoin d'un chauffeur ?", timestamp = System.currentTimeMillis() - 7200000),
                ChatMessage(rentalItemId = 5, sender = "User", messageText = "Non merci, je conduis moi-même. LeGPS est fonctionnel ?", timestamp = System.currentTimeMillis() - 7100000),
                ChatMessage(rentalItemId = 5, sender = "Mael Koumba", messageText = "Oui, tout est opérationnel. Je vous envoie les photos de l'état actuel.", timestamp = System.currentTimeMillis() - 7000000),
                ChatMessage(rentalItemId = 9, sender = "Avenir Evenementiel", messageText = "Pack sono complet disponible pour le 28 juin. Montage inclus ?", timestamp = System.currentTimeMillis() - 14400000),
                ChatMessage(rentalItemId = 9, sender = "User", messageText = "Oui, j'organise un mariage à Akanda. Combien pour 3 jours ?", timestamp = System.currentTimeMillis() - 14300000)
            )
            for (msg in seedMessages) {
                rentalDao.insertChatMessage(msg)
            }
        }
    }
}
