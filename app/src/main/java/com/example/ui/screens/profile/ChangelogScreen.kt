package com.example.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.ui.theme.*

data class ChangelogEntry(
    val version: String,
    val date: String,
    val changes: List<String>
)

private val changelog = listOf(
    ChangelogEntry("1.5.0", "18 Juillet 2026", listOf(
        "Gestion d'état avancée avec UI State",
        "Chat en temps réel avec statuts de lecture",
        "Carte interactive avec marqueurs colorés",
        "Portefeuille numérique et historique de paiements",
        "Personnalisation et préférences utilisateur",
        "Mode hors ligne et connectivité",
        "Accessibilité améliorée",
        "68 annonces dans toutes les catégories"
    )),
    ChangelogEntry("1.4.0", "17 Juillet 2026", listOf(
        "Mode hors ligne avec bannière de connectivité",
        "Gestion d'erreurs et snackbar global",
        "Pipeline d'images avec cache Coil",
        "Accessibilité: descriptions sur 50+ icônes",
        "68 annonces réalistes",
        "Notifications push simulées"
    )),
    ChangelogEntry("1.3.0", "17 Juillet 2026", listOf(
        "Architecture DI avec AppContainer",
        "Splash screen animé",
        "Onboarding 3 pages",
        "CameraX et lecteur vidéo",
        "Sécurité: EncryptedSharedPreferences",
        "CI/CD GitHub Actions"
    )),
    ChangelogEntry("1.2.0", "17 Juillet 2026", listOf(
        "Recherche intelligente avec fuzzy search",
        "Multi-langue: Français, Anglais, Gabonais",
        "Communauté: profils, avis de quartier",
        "Paiements: escrow, split, reçus",
        "Média: caméra, vidéo, modération",
        "Analytics: graphiques et insights"
    )),
    ChangelogEntry("1.1.0", "17 Juillet 2026", listOf(
        "Transitions d'éléments partagés",
        "Auth screens séparés (4 fichiers)",
        "Material3 Compose BOM 2025.01.01"
    )),
    ChangelogEntry("1.0.0", "16 Juillet 2026", listOf(
        "Première version de LocAll",
        "Marketplace de location immobilière",
        "Réservations et paiements",
        "Messagerie et notifications",
        "Avis et confiance"
    ))
)

@Composable
fun ChangelogScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Nouveautés", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        items(changelog) { entry ->
            ChangelogEntryCard(entry = entry)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ChangelogEntryCard(entry: ChangelogEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = PrimaryGreen,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "v${entry.version}",
                        color = BrandNavy,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Text(entry.date, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            entry.changes.forEach { change ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.6f))
                            .padding(top = 2.dp)
                    )
                    Text(
                        change,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
