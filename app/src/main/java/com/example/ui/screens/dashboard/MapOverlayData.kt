package com.example.ui.screens.dashboard

import androidx.compose.ui.graphics.Color

data class MapMarker(
    val id: Int,
    val title: String,
    val lat: Double,
    val lng: Double,
    val category: String,
    val price: Int,
    val color: Color,
    val rating: Float = 4.0f
)

data class CityArea(
    val name: String,
    val centerLat: Double,
    val centerLng: Double,
    val radius: Double,
    val avgPrice: Int,
    val listingCount: Int,
    val demandLevel: String
)

object MapData {
    val cityAreas = listOf(
        CityArea("Nouveau Gabon", 0.3880, 9.4480, 1.5, 250000, 12, "Élevée"),
        CityArea("Louis", 0.3830, 9.4400, 1.2, 200000, 8, "Moyenne"),
        CityArea("Batterie IV", 0.3910, 9.4550, 0.8, 300000, 6, "Élevée"),
        CityArea("Owendo", 0.3700, 9.4700, 2.0, 150000, 10, "Moyenne"),
        CityArea("Sainte-Marie", 0.3850, 9.4450, 1.0, 180000, 7, "Moyenne"),
        CityArea("Akébé", 0.3950, 9.4350, 1.5, 170000, 5, "Faible"),
        CityArea("Montagne Sainte", 0.3780, 9.4600, 1.8, 160000, 9, "Moyenne"),
        CityArea("Château d'Eau", 0.3870, 9.4420, 0.7, 220000, 4, "Élevée")
    )

    val markers = listOf(
        MapMarker(1, "Appartement 3 pièces", 0.3880, 9.4480, "Appartement", 25000, Color(0xFF4CAF50)),
        MapMarker(2, "Studio meublé", 0.3850, 9.4450, "Studio", 15000, Color(0xFF2196F3)),
        MapMarker(3, "Maison familiale", 0.3830, 9.4400, "Maison", 35000, Color(0xFFFF9800)),
        MapMarker(4, "Bureau moderne", 0.3910, 9.4550, "Bureau", 45000, Color(0xFF9C27B0)),
        MapMarker(5, "Commerce centre-ville", 0.3860, 9.4500, "Commerce", 50000, Color(0xFFF44336)),
        MapMarker(6, "Terrain constructible", 0.3700, 9.4700, "Terrain", 80000, Color(0xFF795548)),
        MapMarker(7, "Parking aéroport", 0.3890, 9.4530, "Parking", 5000, Color(0xFF607D8B)),
        MapMarker(8, "Entrepôt Owendo", 0.3720, 9.4680, "Entrepôt", 100000, Color(0xFF455A64)),
        MapMarker(9, "Villa luxe", 0.3900, 9.4520, "Villa", 80000, Color(0xFFFFD700)),
        MapMarker(10, "Chambre colocation", 0.3840, 9.4430, "Chambre", 8000, Color(0xFF00BCD4))
    )
}

data class FavoriteLocation(
    val id: Int,
    val name: String,
    val city: String,
    val lat: Double,
    val lng: Double
)
