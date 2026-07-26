<div align="center">
<h1>LocAll</h1>
<p><strong>Louez tout, partout au Gabon</strong></p>
<p>Application mobile de location entre particuliers — prototype fonctionnel (v1.8.0)</p>
</div>

---

## Apercu

LocAll est une application Android de marketplace de location (vehicules, equipements, biens) ciblant le marche gabonais. Elle supporte les paiements via Airtel Money et Moov Money, avec une interface entierement en francais.

**Stack technique :**
- Kotlin + Jetpack Compose (Material 3)
- Room Database (SQLite local)
- MVVM Architecture + Manual DI (AppContainer)
- Coil 2.7.0 pour le chargement d'images
- Coroutines + Flow + EventBus
- Navigation Compose 2.9.0
- CameraX + Media3
- Roborazzi + JUnit4 (tests)
- CI/CD GitHub Actions

---

## Captures d'ecran — 51 ecrans verifies

### 1. Onboarding (4 ecrans)

| 01 — Splash | 02 — Bienvenue | 03 — Paiements | 04 — Confiance |
|:-----------:|:--------------:|:--------------:|:--------------:|
| ![Splash](docs/screenshots/01_splash.png) | ![Welcome](docs/screenshots/02_welcome.png) | ![Payments](docs/screenshots/03_payments_onboarding.png) | ![Trust](docs/screenshots/04_trust_onboarding.png) |

---

### 2. Authentification (12 ecrans)

| 05 — Connexion | 06 — Inscription | 07 — Mot de passe oublie |
|:--------------:|:----------------:|:------------------------:|
| ![Login](docs/screenshots/05_login.png) | ![Register](docs/screenshots/06_register.png) | ![Forgot](docs/screenshots/07_forgot_password.png) |

| 08 — Code OTP | 09 — Nouveau mot de passe | 10 — Chargement connexion |
|:-------------:|:------------------------:|:-------------------------:|
| ![OTP](docs/screenshots/08_otp.png) | ![New Password](docs/screenshots/09_new_password.png) | ![Loading Login](docs/screenshots/10_loading_login.png) |

| 11 — Succes connexion | 12 — Chargement inscription | 13 — Succes inscription |
|:---------------------:|:---------------------------:|:-----------------------:|
| ![Login Success](docs/screenshots/11_login_success.png) | ![Loading Register](docs/screenshots/12_loading_register.png) | ![Register Success](docs/screenshots/13_register_success.png) |

| 14 — Completer profil | 15 — Succes profil | 16 — Mot de passe reinitialise |
|:---------------------:|:------------------:|:-----------------------------:|
| ![Complete Profile](docs/screenshots/14_complete_profile.png) | ![Profile Success](docs/screenshots/15_profile_success.png) | ![Password Reset](docs/screenshots/16_password_reset_success.png) |

---

### 3. Application principale (7 ecrans)

| 17 — Exploration | 18 — Detail annonce | 19 — Favoris |
|:----------------:|:-------------------:|:------------:|
| ![Explore](docs/screenshots/17_explore.png) | ![Details](docs/screenshots/18_item_details.png) | ![Bookmarks](docs/screenshots/19_bookmarks.png) |

| 20 — Reservations | 21 — Messages | 22 — Publier annonce |
|:-----------------:|:-------------:|:--------------------:|
| ![Bookings](docs/screenshots/20_bookings.png) | ![Messages](docs/screenshots/21_messages.png) | ![Post Listing](docs/screenshots/22_post_listing.png) |

| 23 — Chat |
|:---------:|
| ![Chat](docs/screenshots/23_chat.png) |

---

### 4. Profil & Parametres (14 ecrans)

| 24 — Profil | 25 — Editer profil | 26 — Verification identite |
|:-----------:|:------------------:|:--------------------------:|
| ![Profile](docs/screenshots/24_profile.png) | ![Edit Profile](docs/screenshots/25_edit_profile.png) | ![Identity](docs/screenshots/26_identity_verification.png) |

| 27 — Langue | 28 — Securite | 29 — Notifications |
|:-----------:|:-------------:|:------------------:|
| ![Language](docs/screenshots/27_language.png) | ![Security](docs/screenshots/28_security.png) | ![Notifications](docs/screenshots/29_notifications.png) |

| 30 — Aide & Support | 31 — Moyens de paiement | 32 — Litiges |
|:-------------------:|:-----------------------:|:------------:|
| ![Help](docs/screenshots/30_help.png) | ![Payment Methods](docs/screenshots/31_payment_methods.png) | ![Disputes](docs/screenshots/32_disputes.png) |

| 33 — Mediation | 34 — Reservations locataire | 35 — Signaler dommage |
|:--------------:|:---------------------------:|:---------------------:|
| ![Mediation](docs/screenshots/33_mediation.png) | ![Tenant Bookings](docs/screenshots/34_tenant_bookings.png) | ![Damage Report](docs/screenshots/35_damage_report.png) |

| 36 — Avis locataire | 37 — A propos |
|:--------------------:|:-------------:|
| ![Tenant Review](docs/screenshots/36_tenant_review.png) | ![About](docs/screenshots/37_about.png) |

---

### 5. Espace proprietaire (6 ecrans)

| 38 — Tableau de bord | 39 — Historique revenus | 40 — Portefeuille |
|:--------------------:|:----------------------:|:-----------------:|
| ![Owner Dashboard](docs/screenshots/38_owner_dashboard.png) | ![Earnings](docs/screenshots/39_earnings.png) | ![Wallet](docs/screenshots/40_wallet.png) |

| 41 — Mes annonces | 42 — Calendrier | 43 — Reservations recues |
|:-----------------:|:---------------:|:------------------------:|
| ![Owner Listings](docs/screenshots/41_owner_listings.png) | ![Calendar](docs/screenshots/42_calendar.png) | ![Received Bookings](docs/screenshots/43_received_bookings.png) |

---

### 6. Etats speciaux (2 ecrans)

| 44 — Paiement en cours | 45 — Skeleton loading |
|:----------------------:|:---------------------:|
| ![Payment Processing](docs/screenshots/44_payment_processing.png) | ![Skeleton](docs/screenshots/45_skeleton_loading.png) |

---

### 7. Ecrans supplementaires (6 ecrans)

| 46 — Recherche avancee | 47 — Parametres | 48 — Inviter un ami |
|:----------------------:|:---------------:|:-------------------:|
| ![Advanced Search](docs/screenshots/46_advanced_search.png) | ![Settings](docs/screenshots/47_settings.png) | ![Invite Friend](docs/screenshots/48_invite_friend.png) |

| 49 — Donner un avis | 50 — Detail reservation | 51 — Historique paiements |
|:-------------------:|:----------------------:|:-------------------------:|
| ![Rating](docs/screenshots/49_rating.png) | ![Reservation Detail](docs/screenshots/50_reservation_detail.png) | ![Payment History](docs/screenshots/51_payment_history.png) |

---

## Fonctionnalites

### Authentification
- Connexion / Inscription avec validation de formulaires
- Mot de passe oublie avec OTP (timer + renvoi)
- Indicateur de force du mot de passe
- Acceptation des conditions generales

### Exploration & Recherche
- Grille d'annonces avec skeleton loading et 68 annonces pre-chargees
- Pull-to-refresh sur tous les listes
- Recherche floue avec autocompletion et suggestions
- Recherche intelligente avec analytics et tendances
- Filtres par categorie (Immobilier, Vehicules, Materiel, evenements), ville, prix max
- Tri (prix croissant/decroissant, recent, note)
- Tags populaires cliquables
- Recherche vocale (simulee)
- Listes sauvegardee avec alertes

### Detail d'une annonce
- Carrousel d'images (HorizontalPager)
- Comparaison de prix avec la moyenne du marche
- Fiche contact proprietaire (WhatsApp, Telephone, SMS, Message)
- Annonces similaires
- Bouton de partage avec QR code
- Signalement d'annonce
- Geolocalisation du bien

### Reservation (4 etapes)
- Calendrier interactif avec disponibilites
- Confirmation avec recu PDF
- Paiement via Airtel Money / Moov Money (sous sequestre)
- Annulation avec confirmation et remboursement
- Avis et evaluation apres reservation

### Messagerie temps reel
- Liste des conversations avec recherche
- Bulles de messages avec progression (envoye/lu/recu)
- Indicateur de saisie
- Badge de notifications non lues
- Reactions emoji
- Messages image
- Menu contextuel (supprimer, archiver, bloquer)

### Profil utilisateur
- Edition du profil (nom, telephone)
- Verification d'identite 4 niveaux (CNI, Selfie 3D, selfie bio, video)
- Historique des reservations
- Litiges & mediation avec votes
- Notifications parametrables
- Portefeuille avec transactions et retraits
- Aide & support avec chatbot Kassa
- Moyens de paiement (Airtel Money / Moov Money)
- Securite & langue (FR/EN/Gabonese)
- A propos avec easter egg

### Espace proprietaire
- Tableau de bord avec graphique de revenus
- Gestion des annonces (actives, en revision, suspendues)
- Edition / Suppression d'annonces
- Calendrier de disponibilites interactif
- Reservations recues (accepter / refuser)
- Analytics owner (vues, conversions, revenus)
- Insights du marche

### Fonctionnalites additionnelles
- **Gamification** : Badges, achievements, programme de parrainage (5 000 F CFA)
- **Offres flash** avec countdown timer
- **Loyalite** avec points et niveaux
- **Portefeuille** avec top-up, paiement, retrait, transactions
- **Codes promo** (LOCALL20, BIENVENUE, AMIS10)
- **Assurance** dommages et depots numeriques
- **Litiges communautaires** avec votes
- **Avis de quartier** (16 quartiers guides)
- **Recherche avancee** avec filtres
- **Parametres generaux** (notifications, theme, geolocalisation)
- **Mode hors ligne** avec banner et retry queue
- **Badges** sur les annonces (Nouveau, Populaire, Spotlight)

### Design & UX
- Theme dark avec palette BrandNavy/PrimaryGreen
- Transitions fluides (fade-in staggered, scale animations)
- Skeleton loading sur tous les listes
- Empty states reutilisables
- ConfirmDialog pour actions destructives
- Badges de notification
- Accessibilite (contentDescription sur 50+ icones, WCAG AA contrast)
- Touch targets minimum 48dp
- Typographie Material 3 complete

---

## Installation

### Pre requis
- [Android Studio](https://developer.android.com/studio) (Ladybug ou plus recent)
- JDK 21+
- Android SDK 36

### Etapes

1. Cloner le depot :
```bash
git clone https://github.com/Ggboykxz/my-fck-app.git
cd my-fck-app
```

2. Ouvrir le projet dans Android Studio

3. Compiler et installer sur un emulateur ou device physique :
```bash
./gradlew assembleDebug
```

L'APK sera genere dans `app/build/outputs/apk/debug/`.

### Build Release
```bash
export JAVA_HOME=/path/to/jdk-21
export ANDROID_HOME=$HOME/android-sdk
export KEYSTORE_PATH=/path/to/my-upload-key.jks
export STORE_PASSWORD=localdemo123
export KEY_ALIAS=upload
export KEY_PASSWORD=localdemo123
./gradlew assembleRelease
```

---

## Structure du projet

```
app/src/main/java/com/example/
├── data/
│   ├── local/          # Room DB (v8), DAO (80+ methodes)
│   ├── model/          # 20+ entites (RentalItem, Booking, Wallet, etc.)
│   └── repository/     # RentalRepository (68 annonces seed)
├── ui/
│   ├── components/     # 40+ composants (AppCard, ValidatedTextField, etc.)
│   ├── navigation/     # 4 NavHosts (Root, Auth, Dashboard, Profile)
│   ├── screens/        # 45+ ecrans (auth/, dashboard/, profile/)
│   ├── theme/          # Couleurs, typographie, Material 3
│   └── viewmodel/      # RentalViewModel + UIEvent bus
├── connectivity/       # ConnectivityMonitor (online/metered)
├── di/                 # AppContainer (manual DI)
├── notifications/      # NotificationHelper (4 canaux)
├── preferences/        # UserPreferences
├── security/           # SecureStorage (EncryptedSharedPreferences)
├── util/               # LocationUtils, FilePicker
└── worker/             # SyncWorker (background)
```

---

## Tests

- **51 tests Roborazzi** : captures d'ecrans automotisees de tous les ecrans
- **8 tests ViewModel** : recherche, bookmarks, filtres, litiges
- **1 test Robolectric** : validation du contexte
- **CI/CD** : GitHub Actions (JDK 21, build debug, tests, upload artifacts)

```bash
# Executer tous les tests
./gradlew testDebugUnitTest

# Executer uniquement les screenshots
./gradlew testDebugUnitTest --tests "com.example.AllScreensScreenshotTest"
```

---

## Etat du projet

> **Prototype fonctionnel v1.8.0** — toutes les donnees sont simulees (Room DB locale, pas de backend).

### 51 ecrans verifies avec captures
- 4 ecrans d'onboarding
- 12 ecrans d'authentification
- 7 ecrans du dashboard principal
- 14 ecrans de profil & parametres
- 6 ecrans d'espace proprietaire
- 2 etats speciaux
- 6 ecrans supplementaires (recherche, parametres, parrainage, avis, detail, historique)

### Donnees pre-chargees
- **68 annonces** dans 8 categories et 5 villes
- **20 utilisateurs** profiles
- **28+ avis** et evaluations
- **16 guides de quartier**
- **6 promotions saisonnieres**
- **4 canaux de notification**

## Licence

Projet prive — prototype de demonstration.
