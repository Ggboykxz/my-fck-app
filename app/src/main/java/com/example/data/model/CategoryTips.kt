package com.example.data.model

data class CategoryTip(
    val category: String,
    val tips: List<String>,
    val priceRange: String,
    val commonIssues: List<String>,
    val negotiationAdvice: String
)

object CategoryTipsData {
    val tips = listOf(
        CategoryTip("Appartement", listOf("Vérifiez la plomberie", "Demandez les quittances d'électricité", "Visitez le quartier la nuit"), "15 000 - 80 000 FCFA/jour", listOf("Fuites d'eau", "Coupures électricité"), "Négociez 10-15% pour location longue durée"),
        CategoryTip("Maison", listOf("Vérifiez le toit", "Inspectez le jardin", "Demandez le plan cadastral"), "20 000 - 150 000 FCFA/jour", listOf("Infiltrations", "Problèmes d'égouts"), "Proposez un bail de 2+ ans pour réduire le prix"),
        CategoryTip("Chambre", listOf("Vérifiez la sécurité", "Demandez les règles de la colocation", "Inspectez les sanitaires communs"), "3 000 - 15 000 FCFA/jour", listOf("Manque de vie privée", "Charges partagées"), "Proposez de payer d'avance pour réduire"),
        CategoryTip("Bureau", listOf("Vérifiez la connectivité internet", "Inspectez l'espace de stationnement", "Demandez les charges"), "10 000 - 100 000 FCFA/jour", listOf("Internet lent", "Climatisation défaillante"), "Négociez les charges incluses"),
        CategoryTip("Commerce", listOf("Vérifiez l'emplacement", "Inspectez la vitrine", "Demandez le trafic piéton"), "15 000 - 200 000 FCFA/jour", listOf("Faible fréquentation", "Problèmes de voisinage"), "Proposez un bail commercial longue durée"),
        CategoryTip("Terrain", listOf("Vérifiez le titre foncier", "Inspectez l'accès routier", "Demandez le PLU"), "5 000 - 50 000 FCFA/jour", listOf("Litiges fonciers", "Accès difficile"), "Proposez un paiement en plusieurs fois"),
        CategoryTip("Studio", listOf("Vérifiez la superficie", "Inspectez les équipements", "Demandez si meublé"), "8 000 - 30 000 FCFA/jour", listOf("Espace limité", "Isolation phonique"), "Négociez le meublement inclus"),
        CategoryTip("Villa", listOf("Vérifiez la sécurité du quartier", "Inspectez la piscine", "Demandez les charges de gardiennage"), "50 000 - 300 000 FCFA/jour", listOf("Coûts d'entretien élevés", "Gardiennage"), "Proposez un bail de 3+ ans")
    )
}
