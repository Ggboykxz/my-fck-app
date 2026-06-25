<div align="center">
<img width="1200" height="475" alt="LocAll Banner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
<h1>LocAll</h1>
<p><strong>Louez tout, partout au Gabon</strong></p>
<p>Application mobile de location entre particuliers — prototype fonctionnel (51 écrans)</p>
</div>

---

## Aperçu

LocAll est une application Android de marketplace de location (véhicules, équipements, biens) ciblant le marché gabonais. Elle supporte les paiements via Airtel Money et Moov Money, avec une interface entièrement en français.

**Stack technique :**
- Kotlin + Jetpack Compose (Material 3)
- Room Database (SQLite local)
- MVVM Architecture
- Coil pour le chargement d'images
- Coroutines + Flow

---

## Captures d'écran — 51 écrans

### 1. Onboarding (4 écrans)

| 01 — Splash | 02 — Bienvenue | 03 — Paiements | 04 — Confiance |
|:-----------:|:--------------:|:--------------:|:--------------:|
| ![Splash](docs/screenshots/01_splash.png) | ![Welcome](docs/screenshots/02_welcome.png) | ![Payments](docs/screenshots/03_payments_onboarding.png) | ![Trust](docs/screenshots/04_trust_onboarding.png) |

---

### 2. Authentification (12 écrans)

| 05 — Connexion | 06 — Inscription | 07 — Mot de passe oublié |
|:--------------:|:----------------:|:------------------------:|
| ![Login](docs/screenshots/05_login.png) | ![Register](docs/screenshots/06_register.png) | ![Forgot](docs/screenshots/07_forgot_password.png) |

| 08 — Code OTP | 09 — Nouveau mot de passe | 10 — Chargement connexion |
|:-------------:|:------------------------:|:-------------------------:|
| ![OTP](docs/screenshots/08_otp.png) | ![New Password](docs/screenshots/09_new_password.png) | ![Loading Login](docs/screenshots/10_loading_login.png) |

| 11 — Succès connexion | 12 — Chargement inscription | 13 — Succès inscription |
|:---------------------:|:---------------------------:|:-----------------------:|
| ![Login Success](docs/screenshots/11_login_success.png) | ![Loading Register](docs/screenshots/12_loading_register.png) | ![Register Success](docs/screenshots/13_register_success.png) |

| 14 — Compléter profil | 15 — Succès profil | 16 — Mot de passe réinitialisé |
|:---------------------:|:------------------:|:-----------------------------:|
| ![Complete Profile](docs/screenshots/14_complete_profile.png) | ![Profile Success](docs/screenshots/15_profile_success.png) | ![Password Reset](docs/screenshots/16_password_reset_success.png) |

---

### 3. Application principale (7 écrans)

| 17 — Exploration | 18 — Détail annonce | 19 — Favoris |
|:----------------:|:-------------------:|:------------:|
| ![Explore](docs/screenshots/17_explore.png) | ![Details](docs/screenshots/18_item_details.png) | ![Bookmarks](docs/screenshots/19_bookmarks.png) |

| 20 — Réservations | 21 — Messages | 22 — Publier annonce |
|:-----------------:|:-------------:|:--------------------:|
| ![Bookings](docs/screenshots/20_bookings.png) | ![Messages](docs/screenshots/21_messages.png) | ![Post Listing](docs/screenshots/22_post_listing.png) |

| 23 — Chat |
|:---------:|
| ![Chat](docs/screenshots/23_chat.png) |

---

### 4. Profil & Paramètres (14 écrans)

| 24 — Profil | 25 — Éditer profil | 26 — Vérification identité |
|:-----------:|:------------------:|:--------------------------:|
| ![Profile](docs/screenshots/24_profile.png) | ![Edit Profile](docs/screenshots/25_edit_profile.png) | ![Identity](docs/screenshots/26_identity_verification.png) |

| 27 — Langue | 28 — Sécurité | 29 — Notifications |
|:-----------:|:-------------:|:------------------:|
| ![Language](docs/screenshots/27_language.png) | ![Security](docs/screenshots/28_security.png) | ![Notifications](docs/screenshots/29_notifications.png) |

| 30 — Aide & Support | 31 — Moyens de paiement | 32 — Litiges |
|:-------------------:|:-----------------------:|:------------:|
| ![Help](docs/screenshots/30_help.png) | ![Payment Methods](docs/screenshots/31_payment_methods.png) | ![Disputes](docs/screenshots/32_disputes.png) |

| 33 — Médiation | 34 — Réservations locataire | 35 — Signaler dommage |
|:--------------:|:---------------------------:|:---------------------:|
| ![Mediation](docs/screenshots/33_mediation.png) | ![Tenant Bookings](docs/screenshots/34_tenant_bookings.png) | ![Damage Report](docs/screenshots/35_damage_report.png) |

| 36 — Avis locataire | 37 — À propos |
|:--------------------:|:-------------:|
| ![Tenant Review](docs/screenshots/36_tenant_review.png) | ![About](docs/screenshots/37_about.png) |

---

### 5. Espace propriétaire (6 écrans)

| 38 — Tableau de bord | 39 — Historique revenus | 40 — Portefeuille |
|:--------------------:|:----------------------:|:-----------------:|
| ![Owner Dashboard](docs/screenshots/38_owner_dashboard.png) | ![Earnings](docs/screenshots/39_earnings.png) | ![Wallet](docs/screenshots/40_wallet.png) |

| 41 — Mes annonces | 42 — Calendrier | 43 — Réservations reçues |
|:-----------------:|:---------------:|:------------------------:|
| ![Owner Listings](docs/screenshots/41_owner_listings.png) | ![Calendar](docs/screenshots/42_calendar.png) | ![Received Bookings](docs/screenshots/43_received_bookings.png) |

---

### 6. États spéciaux (2 écrans)

| 44 — Paiement en cours | 45 — Skeleton loading |
|:----------------------:|:---------------------:|
| ![Payment Processing](docs/screenshots/44_payment_processing.png) | ![Skeleton](docs/screenshots/45_skeleton_loading.png) |

---

## Fonctionnalités

### Authentification
- Connexion / Inscription avec validation de formulaires
- Mot de passe oublié avec OTP (timer + renvoi)
- Indicateur de force du mot de passe
- Acceptation des conditions générales

### Exploration & Recherche
- Grille d'annonces avec skeleton loading
- Pull-to-refresh
- Recherche par texte (quartier, description)
- Filtres par catégorie, ville, prix max
- Tri (prix croissant/décroissant, récent, note)
- Tags populaires cliquables

### Détail d'une annonce
- Galerie d'images (hero banner)
- Récapitulatif de réservation (prix + commission 5%)
- Annonces similaires
- Bouton de partage (intent Android)
- Géolocalisation du bien
- Fiche du propriétaire (note, téléphone masqué)

### Réservation
- Dialog interactif de réservation
- Sélection du nombre de jours
- Choix du mode de paiement (Airtel Money / Moov Money)
- Saisie du numéro de téléphone
- Confirmation PIN
- Annulation avec confirmation

### Messagerie
- Liste des conversations
- Bulles de messages (utilisateur / propriétaire)
- Indicateur de saisie ("écrit...")
- Badge de notifications non lues

### Profil utilisateur
- Édition du profil (nom, téléphone)
- Vérification d'identité
- Historique des réservations
- Litiges & médiation
- Notifications
- Portefeuille & retraits
- Aide & support
- Moyens de paiement (Airtel Money / Moov Money)
- Sécurité & langue
- À propos

### Espace propriétaire
- Tableau de bord (annonces, revenus, portefeuille)
- Gestion des annonces (actives, en révision, suspendues)
- Édition / Suppression d'annonces
- Calendrier de disponibilités
- Réservations reçues (accepter / refuser avec confirmation)
- Signalement de dommages
- Avis locataire

### Fonctionnalités additionnelles
- Recherche avancée avec filtres (catégorie, ville, prix)
- Paramètres généraux (notifications, thème, géolocalisation)
- Système d'invitation d'amis (5 000 F CFA par parrainage)
- Formulaire d'avis avec notation par étoiles
- Détail de réservation avec annulation
- Historique des paiements avec résumé
- Badges sur les annonces (Nouveau, Populaire)

### Design & UX
- Thème dark/light
- Numéros de téléphone masqués
- Skeleton loading
- Empty states réutilisables
- ConfirmDialog pour actions destructives
- Badges de notification
- Accessibilité (contentDescription)

---

## Installation

### Prérequis
- [Android Studio](https://developer.android.com/studio) (Ladybug ou plus récent)
- JDK 17+
- Android SDK 36

### Étapes

1. Cloner le dépôt :
```bash
git clone https://github.com/Ggboykxz/my-fck-app.git
cd my-fck-app
```

2. Ouvrir le projet dans Android Studio

3. Compiler et installer sur un émulateur ou device physique :
```bash
./gradlew assembleDebug
```

L'APK sera généré dans `app/build/outputs/apk/debug/`.

---

## Structure du projet

```
app/src/main/java/com/example/
├── data/
│   ├── local/          # Room DB, DAO
│   ├── model/          # Entités (RentalItem, Booking, ChatMessage, UserProfile...)
│   └── repository/     # RentalRepository
├── ui/
│   ├── components/     # Composants réutilisables (SkeletonCard, EmptyState, etc.)
│   ├── screens/        # Écrans (Auth, Dashboard, Profile, Onboarding)
│   ├── theme/          # Couleurs, typographie, thème
│   └── viewmodel/      # RentalViewModel (état + logique)
└── MainActivity.kt
```

---

## État du projet

> **Prototype fonctionnel** — toutes les données sont simulées (Room DB locale, pas de backend).

### 51 écrans implémentés
- 4 écrans d'onboarding
- 12 écrans d'authentification
- 7 écrans du dashboard principal
- 14 écrans de profil & paramètres
- 6 écrans d'espace propriétaire
- 6 écrans additionnels (recherche avancée, paramètres, invitation, avis, détail réservation, historique paiements)
- 2 états spéciaux (paiement, skeleton)

## Licence

Projet privé — prototype de démonstration.
