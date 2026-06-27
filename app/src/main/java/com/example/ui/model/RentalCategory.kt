package com.example.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class RentalCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    IMMOBILIER("Immobilier", Icons.Rounded.Home, Color(0xFF2196F3)),
    VEHICULES("Véhicules", Icons.Rounded.DirectionsCar, Color(0xFFFF9800)),
    EQUIPEMENTS("Équipements", Icons.Rounded.Build, Color(0xFF9C27B0)),
    EVENEMENTIEL("Événementiel", Icons.Rounded.Celebration, Color(0xFFE91E63)),
    MODE_BEAUTE("Mode & Beauté", Icons.Rounded.Checkroom, Color(0xFF00BCD4)),
    SERVICES("Services", Icons.Rounded.MiscellaneousServices, Color(0xFFFF5722)),
    ESPACES("Espaces", Icons.Rounded.Business, Color(0xFF607D8B)),
    MATERIEL_PRO("Matériel Pro", Icons.Rounded.Engineering, Color(0xFF795548)),
    MARINE_FLUVIAL("Marine & Fluvial", Icons.Rounded.Sailing, Color(0xFF009688)),
    SPORT_LOISIRS("Sport & Loisirs", Icons.Rounded.Sports, Color(0xFF4CAF50));

    companion object {
        fun fromString(value: String): RentalCategory {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: IMMOBILIER
        }

        val allDisplayNames: List<String> = entries.map { it.displayName }
    }
}
