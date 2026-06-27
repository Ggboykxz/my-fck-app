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
        }
    }
}
