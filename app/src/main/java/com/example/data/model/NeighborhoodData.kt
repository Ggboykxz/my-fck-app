package com.example.data.model

data class NeighborhoodGuide(
    val name: String,
    val city: String,
    val safetyScore: Int,
    val noiseScore: Int,
    val accessibilityScore: Int,
    val averageRent: Int,
    val description: String,
    val highlights: List<String>,
    val warnings: List<String>
)

object NeighborhoodData {
    val guides = listOf(
        NeighborhoodGuide(
            name = "Nouveau Gabon",
            city = "Libreville",
            safetyScore = 8,
            noiseScore = 6,
            accessibilityScore = 9,
            averageRent = 250000,
            description = "Quartier moderne et bien desservi, idéal pour les familles",
            highlights = listOf("Proche du centre commercial", "Écoles internationales", "Transports en commun"),
            warnings = listOf("Trafic aux heures de pointe")
        ),
        NeighborhoodGuide(
            name = "Louis",
            city = "Libreville",
            safetyScore = 7,
            noiseScore = 7,
            accessibilityScore = 8,
            averageRent = 200000,
            description = "Quartier résidentiel calme avec bonnes infrastructures",
            highlights = listOf("Marché local", "Parc", "Cliniques"),
            warnings = listOf("Coupures d'électricité fréquentes")
        ),
        NeighborhoodGuide(
            name = "Batterie IV",
            city = "Libreville",
            safetyScore = 9,
            noiseScore = 8,
            accessibilityScore = 7,
            averageRent = 300000,
            description = "Quartier huppé de Libreville, très sécurisé",
            highlights = listOf("Ambassades", "Hôtels 5 étoiles", "Restaurants"),
            warnings = listOf("Prix élevés")
        ),
        NeighborhoodGuide(
            name = "Owendo",
            city = "Libreville",
            safetyScore = 6,
            noiseScore = 5,
            accessibilityScore = 7,
            averageRent = 150000,
            description = "Zone portuaire en développement, prix accessibles",
            highlights = listOf("Port", "Gare routière", "Marché B"),
            warnings = listOf("Bruit du port la nuit")
        ),
        NeighborhoodGuide(
            name = "Sainte-Marie",
            city = "Libreville",
            safetyScore = 7,
            noiseScore = 6,
            accessibilityScore = 8,
            averageRent = 180000,
            description = "Quartier historique avec caractère",
            highlights = listOf("Cathédrale", "Centre-ville", "Bureaux"),
            warnings = listOf("Stationnement difficile")
        ),
        NeighborhoodGuide(
            name = "Tchimbélé",
            city = "Port-Gentil",
            safetyScore = 7,
            noiseScore = 7,
            accessibilityScore = 6,
            averageRent = 200000,
            description = "Quartier résidentiel de Port-Gentil",
            highlights = listOf("Plage", "Restaurant de poisson", "Calme"),
            warnings = listOf("Éloigné du centre")
        ),
        NeighborhoodGuide(
            name = "Sibang",
            city = "Libreville",
            safetyScore = 8,
            noiseScore = 8,
            accessibilityScore = 7,
            averageRent = 175000,
            description = "Quartier calme et résidentiel, proche des écoles",
            highlights = listOf("Écoles primaires", "Marché hebdomadaire", "Espace vert"),
            warnings = listOf("Peu de commerces la nuit")
        ),
        NeighborhoodGuide(
            name = "Akanda",
            city = "Libreville",
            safetyScore = 7,
            noiseScore = 7,
            accessibilityScore = 6,
            averageRent = 160000,
            description = "Quartier en expansion avec vue sur la baie",
            highlights = listOf("Vue mer", "Marché d'Akanda", "Proche aéroport"),
            warnings = listOf("Inondations possibles en saison des pluies")
        ),
        NeighborhoodGuide(
            name = "Nzeng-Ayong",
            city = "Libreville",
            safetyScore = 6,
            noiseScore = 6,
            accessibilityScore = 7,
            averageRent = 130000,
            description = "Quartier populaire avecforte densité",
            highlights = listOf("Prix abordables", "Transports faciles", "Marché vivant"),
            warnings = listOf("Insécurité nocturne")
        ),
        NeighborhoodGuide(
            name = "Glass",
            city = "Libreville",
            safetyScore = 7,
            noiseScore = 5,
            accessibilityScore = 9,
            averageRent = 190000,
            description = "Quartier d'affaires historique au centre-ville",
            highlights = listOf("Centre-ville", "Bureaux", "Banques"),
            warnings = listOf("Bruit permanent")
        ),
        NeighborhoodGuide(
            name = "Centre-Ville",
            city = "Port-Gentil",
            safetyScore = 6,
            noiseScore = 5,
            accessibilityScore = 8,
            averageRent = 220000,
            description = "Coeur économique de Port-Gentil",
            highlights = listOf("Commerces", "Port", "Restaurants"),
            warnings = listOf("Trafic dense")
        ),
        NeighborhoodGuide(
            name = "Diéla",
            city = "Franceville",
            safetyScore = 7,
            noiseScore = 8,
            accessibilityScore = 6,
            averageRent = 120000,
            description = "Quartier résidentiel calme de Franceville",
            highlights = listOf("Écoles", "Stade", "Marché"),
            warnings = listOf("Peu de vie nocturne")
        ),
        NeighborhoodGuide(
            name = "Mouila Centre",
            city = "Mouila",
            safetyScore = 7,
            noiseScore = 7,
            accessibilityScore = 5,
            averageRent = 100000,
            description = "Centre-ville de Mouila, petit commerce local",
            highlights = listOf("Marché central", "Église", "Pharmacie"),
            warnings = listOf("Éloigné de Libreville")
        ),
        NeighborhoodGuide(
            name = "Lambaréné Centre",
            city = "Lambaréné",
            safetyScore = 8,
            noiseScore = 9,
            accessibilityScore = 5,
            averageRent = 90000,
            description = "Ville calme au bord de l'Ogooué, ambiance rurale",
            highlights = listOf("Hôpital Albert Schweitzer", "Fleuve Ogooué", "Nature"),
            warnings = listOf("Infrastructures limitées")
        ),
        NeighborhoodGuide(
            name = "Nkembo",
            city = "Libreville",
            safetyScore = 8,
            noiseScore = 7,
            accessibilityScore = 8,
            averageRent = 280000,
            description = "Quartier résidentiel haut de gamme avec vue estuaire",
            highlights = listOf("Vue panoramique", "Restaurants", "Villas"),
            warnings = listOf("Prix élevés")
        ),
        NeighborhoodGuide(
            name = "Oloumi",
            city = "Libreville",
            safetyScore = 6,
            noiseScore = 5,
            accessibilityScore = 7,
            averageRent = 140000,
            description = "Zone industrielle en reconversion résidentielle",
            highlights = listOf("Prix bas", "Proche port", "Artisanat"),
            warnings = listOf("Bruit industriel")
        ),
        NeighborhoodGuide(
            name = "Oyem Centre",
            city = "Oyem",
            safetyScore = 7,
            noiseScore = 8,
            accessibilityScore = 4,
            averageRent = 80000,
            description = "Chef-lieu de la Woleu-Ntem, villefrontalière",
            highlights = listOf("Marché transfrontalier", "Forêts", "Artisanat"),
            warnings = listOf("Éloigné, routes difficiles")
        )
    )
}
