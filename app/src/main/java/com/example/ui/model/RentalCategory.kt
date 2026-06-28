package com.example.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class RentalCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val specs: List<CategorySpec>
) {
    IMMOBILIER("Immobilier", Icons.Rounded.Home, Color(0xFF2196F3), listOf(
        CategorySpec("surface", "Surface (m²)", "120"),
        CategorySpec("chambres", "Chambres", "3"),
        CategorySpec("salles_de_bain", "Salles de bain", "2"),
        CategorySpec("etage", "Étage", "2"),
        CategorySpec("meuble", "Meublé", "Oui")
    )),
    VEHICULES("Véhicules", Icons.Rounded.DirectionsCar, Color(0xFFFF9800), listOf(
        CategorySpec("kilometrage", "Kilométrage", "45 000 km"),
        CategorySpec("carburant", "Carburant", "Essence"),
        CategorySpec("transmission", "Transmission", "Automatique"),
        CategorySpec("annee", "Année", "2022"),
        CategorySpec("places", "Places", "5")
    )),
    EQUIPEMENTS("Équipements", Icons.Rounded.Build, Color(0xFF9C27B0), listOf(
        CategorySpec("puissance", "Puissance", "2500W"),
        CategorySpec("etat", "État", "Bon état"),
        CategorySpec("marque", "Marque", "Bosch"),
        CategorySpec("periode", "Période min", "1 jour")
    )),
    EVENEMENTIEL("Événementiel", Icons.Rounded.Celebration, Color(0xFFE91E63), listOf(
        CategorySpec("date", "Date", "28/06/2026"),
        CategorySpec("invites", "Invités", "50"),
        CategorySpec("type_event", "Type", "Mariage"),
        CategorySpec("duree", "Durée", "1 journée")
    )),
    MODE_BEAUTE("Mode & Beauté", Icons.Rounded.Checkroom, Color(0xFF00BCD4), listOf(
        CategorySpec("taille", "Taille", "M"),
        CategorySpec("couleur", "Couleur", "Noir"),
        CategorySpec("marque", "Marque", "Zara"),
        CategorySpec("matiere", "Matière", "Coton")
    )),
    SERVICES("Services", Icons.Rounded.MiscellaneousServices, Color(0xFFFF5722), listOf(
        CategorySpec("duree", "Durée", "2 heures"),
        CategorySpec("competences", "Compétences", "Plomberie"),
        CategorySpec("zone", "Zone", "Libreville"),
        CategorySpec("tarif_horaire", "Tarif/heure", "15 000 F")
    )),
    ESPACES("Espaces", Icons.Rounded.Business, Color(0xFF607D8B), listOf(
        CategorySpec("surface", "Surface (m²)", "200"),
        CategorySpec("capacite", "Capacité", "30 personnes"),
        CategorySpec("equipements", "Équipements", "Son, Light"),
        CategorySpec("horaires", "Horaires", "8h-22h")
    )),
    MATERIEL_PRO("Matériel Pro", Icons.Rounded.Engineering, Color(0xFF795548), listOf(
        CategorySpec("puissance", "Puissance", "50 CV"),
        CategorySpec("heures", "Heures utilisation", "1200h"),
        CategorySpec("marque", "Marque", "Caterpillar"),
        CategorySpec("certificat", "Certificat", "Oui")
    )),
    MARINE_FLUVIAL("Marine & Fluvial", Icons.Rounded.Sailing, Color(0xFF009688), listOf(
        CategorySpec("longueur", "Longueur", "8m"),
        CategorySpec("type_bateau", "Type", "Ponton"),
        CategorySpec("motorisation", "Motorisation", "150 CV"),
        CategorySpec("places", "Places", "8")
    )),
    SPORT_LOISIRS("Sport & Loisirs", Icons.Rounded.Sports, Color(0xFF4CAF50), listOf(
        CategorySpec("type_sport", "Type", "Surf"),
        CategorySpec("niveau", "Niveau requis", "Débutant"),
        CategorySpec("age_min", "Âge minimum", "12 ans"),
        CategorySpec("duree", "Durée", "2 heures")
    ));

    companion object {
        fun fromString(value: String): RentalCategory {
            return entries.find { it.displayName.equals(value, ignoreCase = true) } ?: IMMOBILIER
        }

        val allDisplayNames: List<String> = entries.map { it.displayName }
    }
}

data class CategorySpec(
    val key: String,
    val label: String,
    val sampleValue: String
)
