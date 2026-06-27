package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SupportMessage(
    val sender: String, // "Bot" or "User"
    val text: String,
    val time: String
)

@Composable
fun HelpAndSupportScreen(
    onBack: () -> Unit
) {
    val faqs = listOf(
        Pair("Comment signaler un problème ?", "Si vous rencontrez un dysfonctionnement avec la location, un bouton 'Signaler' vous permet d'ouvrir un litige avec des preuves photos sous 24h."),
        Pair("Fonctionnement de Mobile Money", "Les paiements s'effectuent par validation USSD (sms de push direct de Airtel Money ou Moov Money). Les fonds restent sous séquestre jusqu'à la remise du bien."),
        Pair("Garantie de Protection LocAll", "LocAll Gabon couvre les sinistres et dégradations matérielles jusqu'à hauteur de 5,000,000 F CFA grâce à nos partenaires de réassurance basés à Libreville."),
        Pair("Conditions d'annulation", "L'annulation est gratuite jusqu'à 24h avant le début planifié de la remise des clés. Passé ce délai, des frais de dédommagement de 35% s'appliquent.")
    )

    var showChatbot by remember { mutableStateOf(false) }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                SupportMessage(
                    sender = "Bot",
                    text = "Bonjour ! Je suis Kassa, votre conseiller virtuel LocAll Gabon 🇬🇦. Comment puis-je vous aider dans vos locations de matériel aujourd'hui ?",
                    time = "À l'instant"
                )
            )
        )
    }
    var chatbotInput by remember { mutableStateOf("") }
    var isBotTyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val quickQuestions = listOf(
        "Délai versement ?",
        "Comment marche la caution ?",
        "Est-ce sécurisé ?",
        "Parler à un humain"
    )

    fun handleSend(messageText: String) {
        if (messageText.isBlank()) return
        
        // Add User Message
        val newMsg = SupportMessage(sender = "User", text = messageText, time = "Maintenant")
        chatMessages = chatMessages + newMsg
        chatbotInput = ""
        isBotTyping = true

        coroutineScope.launch {
            delay(1500)
            isBotTyping = false
            
            val responseText = when {
                messageText.contains("versement", ignoreCase = true) || messageText.contains("retrait", ignoreCase = true) || messageText.contains("payout", ignoreCase = true) -> {
                    "Pour les propriétaires, le transfert vers votre compte Airtel Money ou Moov Money s'effectue sous 2 à 4 heures ouvrées dès la validation de la remise des clés par le locataire."
                }
                messageText.contains("caution", ignoreCase = true) || messageText.contains("séquestre", ignoreCase = true) || messageText.contains("garantie", ignoreCase = true) -> {
                    "Le dépôt de garantie (caution) est bloqué en toute sécurité par LocAll Gabon. Il n'est reversé au propriétaire qu'en cas de dommage avéré constaté dans les 24h suivant le retour."
                }
                messageText.contains("sécur", ignoreCase = true) || messageText.contains("fiable", ignoreCase = true) -> {
                    "Absolument ! Tous nos utilisateurs passent par une vérification biométrique instantanée (CNI et Selfie 3D). De plus, nos baux de location sont conformes au droit OHADA en vigueur au Gabon."
                }
                messageText.contains("humain", ignoreCase = true) || messageText.contains("agent", ignoreCase = true) || messageText.contains("téléphone", ignoreCase = true) -> {
                    "Je viens de notifier un de nos conseillers humains de notre bureau de Libreville (Alibandeng). Un agent va prendre le relais ici. Vous pouvez aussi nous appeler direct au +241 07 12 34 56."
                }
                else -> {
                    "C'est bien noté ! Pour toute question spécifique à une annonce, vous pouvez lancer un chat direct avec le propriétaire depuis les détails de l'annonce d'intérêt."
                }
            }
            chatMessages = chatMessages + SupportMessage(sender = "Bot", text = responseText, time = "Maintenant")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (showChatbot) showChatbot = false else onBack()
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (showChatbot) "Discuter avec l'Assistant LocAll" else "Centre d'Aide & FAQ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!showChatbot) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Questions Fréquentes (FAQ)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                faqs.forEach { f ->
                    var expanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { expanded = !expanded },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(f.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = null,
                                    tint = PrimaryGreen
                                )
                            }
                            if (expanded) {
                                Text(f.second, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.SupportAgent, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                        }

                        Text("Toujours besoin de réponses ?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Notre assistant client virtuel répond en direct à toutes vos interrogations techniques ou juridiques.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = { showChatbot = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen, contentColor = BrandNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Rounded.Forum, contentDescription = null)
                                Text("Lancer le Chatbot en direct", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // HIGH-FIDELITY INTERACTIVE SUPPORT CHAT WINDOW
            Column(modifier = Modifier.weight(1f)) {
                // Chats listing flow
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(chatMessages) { msg ->
                            val isBot = msg.sender == "Bot"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isBot) 4.dp else 16.dp,
                                                bottomEnd = if (isBot) 16.dp else 4.dp
                                            )
                                        )
                                        .background(
                                            if (isBot) Color(0xFF162133) else PrimaryGreen
                                        )
                                        .padding(14.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = msg.text,
                                            color = if (isBot) Color.White else BrandNavy,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = msg.time,
                                                color = if (isBot) Color.White.copy(alpha = 0.4f) else BrandNavy.copy(alpha = 0.6f),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isBotTyping) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                color = PrimaryGreen,
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Text("Kassa écrit...", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Inquiry Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickQuestions) { q ->
                        LocalInquiryChip(text = q, onClick = { handleSend(q) })
                    }
                }

                // Chat text input box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = chatbotInput,
                        onValueChange = { chatbotInput = it },
                        placeholder = { Text("Écrire à Kassa...", color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("support_chat_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f)
                        ),
                        singleLine = true
                    )

                    IconButton(
                        onClick = { handleSend(chatbotInput) },
                        enabled = chatbotInput.isNotBlank(),
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                if (chatbotInput.isNotBlank()) PrimaryGreen else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Envoyer",
                            tint = if (chatbotInput.isNotBlank()) BrandNavy else Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LocalInquiryChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(34.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------- ABOUT SCREEN ----------------

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    var showCguDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    if (showCguDialog) {
        Dialog(onDismissRequest = { showCguDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Conditions Générales d'Utilisation", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Dernière mise à jour : Février 2026", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "Les présentes Conditions Générales d'Utilisation (CGU) régissent l'accès et l'utilisation de l'application LocAll, éditée par LocAll Gabon SARL, dont le siège social est situé à Libreville, Gabon.\n\n" +
                        "En utilisant LocAll, vous acceptez sans réserve les dispositions des présentes CGU. LocAll met en relation des particuliers pour la location de biens entre eux sur le territoire gabonais.\n\n" +
                        "1. Objet : LocAll est une plateforme de mise en relation entre locataires et propriétaires. LocAll n'est pas partie au contrat de location conclu entre les utilisateurs.\n\n" +
                        "2. Inscription : L'inscription est ouverte à toute personne physique majeure résidant au Gabon. L'identification par CNI et selfie biométrique est obligatoire.\n\n" +
                        "3. Obligations : Les utilisateurs s'engagent à fournir des informations exactes et à respecter les biens loués. Tout manquement pourra entraîner la suspension du compte.\n\n" +
                        "4. Paiements : Les transactions s'effectuent via Mobile Money (Airtel Money, Moov Money) sous séquestre sécurisé LocAll.",
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Button(onClick = { showCguDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy), modifier = Modifier.fillMaxWidth()) {
                        Text("Fermer", color = Color.White)
                    }
                }
            }
        }
    }

    if (showPrivacyDialog) {
        Dialog(onDismissRequest = { showPrivacyDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Politique de Confidentialité", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("Dernière mise à jour : Février 2026", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "LocAll Gabon SARL attache une importance particulière à la protection de vos données personnelles. Cette politique de confidentialité décrit comment nous collectons, utilisons et protégeons vos informations.\n\n" +
                        "1. Données collectées : Nom, numéro de téléphone, photographie de profil, pièce d'identité (CNI/Passeport), selfie biométrique, données de géolocalisation, historique de transactions.\n\n" +
                        "2. Finalités : Vérification d'identité, mise en relation entre utilisateurs, traitement des paiements, prévention de la fraude, amélioration du service.\n\n" +
                        "3. Partage : Vos données ne sont jamais vendues à des tiers. Elles peuvent être partagées avec nos partenaires de paiement (Airtel Money, Moov Money) et d'assurance (GabAsur) uniquement dans le cadre des transactions.\n\n" +
                        "4. Sécurité : Toutes les données sont chiffrées (AES-256) et stockées sur des serveurs sécurisés conformes aux normes ISO 27001.\n\n" +
                        "5. Droits : Conformément à la loi gabonaise sur la protection des données, vous disposez d'un droit d'accès, de rectification et de suppression de vos données.",
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy), modifier = Modifier.fillMaxWidth()) {
                        Text("Fermer", color = Color.White)
                    }
                }
            }
        }
    }

    if (showLicensesDialog) {
        Dialog(onDismissRequest = { showLicensesDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Licences Open-Source", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                    Text("LocAll utilise les bibliothèques open-source suivantes :", fontSize = 12.sp, color = Color.Gray)

                    val licenses = listOf(
                        Triple("Jetpack Compose", "Apache License 2.0", "Google"),
                        Triple("Kotlin", "Apache License 2.0", "JetBrains"),
                        Triple("Room Database", "Apache License 2.0", "Google"),
                        Triple("Coil", "Apache License 2.0", "Coil Contributors"),
                        Triple("Material Icons Extended", "Apache License 2.0", "Google"),
                        Triple("Navigation Compose", "Apache License 2.0", "Google"),
                        Triple("ViewModel Compose", "Apache License 2.0", "Google"),
                        Triple("Coroutines", "Apache License 2.0", "JetBrains")
                    )

                    licenses.forEach { (name, license, author) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavy)
                                Text("$license · $author", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    Button(onClick = { showLicensesDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandNavy), modifier = Modifier.fillMaxWidth()) {
                        Text("Fermer", color = Color.White)
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
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
                Text("À propos", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Logo Section
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PrimaryGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "LocAll Logo",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "LocAll",
                color = PrimaryGreen,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "v1.0.0 (Prototype)",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Description", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "LocAll est une application de location entre particuliers conçue pour le marché gabonais. Louez tout, partout au Gabon.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Credits
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Crédits", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Développé avec ❤️ au Gabon",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Text(
                        "Technologies: Kotlin, Jetpack Compose, Room DB",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legal Links
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCguDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Gavel, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text("Conditions Générales d'Utilisation", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Shield, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text("Politique de Confidentialité", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLicensesDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Rounded.Code, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                            Text("Licences open-source", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Color.White.copy(alpha = 0.3f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Social / Contact
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162133)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Suivez-nous", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Instagram
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.CameraAlt, contentDescription = "Instagram", tint = Color(0xFFE1306C), modifier = Modifier.size(22.dp))
                            }
                            Text("Instagram", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }

                        // Twitter / X
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Tag, contentDescription = "Twitter", tint = Color(0xFF1DA1F2), modifier = Modifier.size(22.dp))
                            }
                            Text("Twitter", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }

                        // Facebook
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1A1A2E).copy(alpha = 0.8f))
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Public, contentDescription = "Facebook", tint = Color(0xFF1877F2), modifier = Modifier.size(22.dp))
                            }
                            Text("Facebook", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Copyright
            Text(
                "© 2026 LocAll. Tous droits réservés.",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp)
            )
        }
    }
}
