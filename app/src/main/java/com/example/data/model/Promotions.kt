package com.example.data.model

data class SeasonalPromotion(
    val id: Int,
    val title: String,
    val description: String,
    val discount: String,
    val validUntil: String,
    val category: String?,
    val icon: String
)

object PromotionsData {
    val promotions = listOf(
        SeasonalPromotion(1, "Rentrée Scolaire", "Logements proches des écoles à prix réduit", "-20%", "30 Sept 2026", null, "🎓"),
        SeasonalPromotion(2, "Noël en Famille", "Maisons spacieuses pour les fêtes", "-15%", "31 Déc 2026", "Maison", "🎄"),
        SeasonalPromotion(3, "Printemps des Affaires", "Bureaux et commerces en promo", "-25%", "30 Juin 2026", "Bureau", "💼"),
        SeasonalPromotion(4, "Été au Gabon", "Studios meublés pour vacanciers", "-10%", "31 Août 2026", "Studio", "☀️"),
        SeasonalPromotion(5, "Nouvel An LocAll", "Toutes catégories - première réservation", "-30%", "31 Jan 2026", null, "🎉"),
        SeasonalPromotion(6, "Promo Colocation", "Trouvez un colocataire et économisez", "Gratuit 1 mois", "Permanant", "Chambre", "🤝")
    )
}
