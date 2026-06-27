package com.example.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.*
import com.example.ui.viewmodel.RentalViewModel

@Composable
fun ProfileNavHost(
    navController: NavHostController,
    viewModel: RentalViewModel
) {
    val isOwnerMode by viewModel.isOwnerMode.collectAsState()
    val selectedDisputeId by viewModel.selectedDisputeId.collectAsState()
    val activeDamageSelection by viewModel.activeDamageSelection.collectAsState()
    val activeReviewSelection by viewModel.activeReviewSelection.collectAsState()

    Box(modifier = Modifier.statusBarsPadding()) {
    NavHost(
        navController = navController,
        startDestination = RouteProfileMain,
        enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
        exitTransition = { slideOutHorizontally(tween(300)) { -it } + fadeOut(tween(300)) },
        popEnterTransition = { slideInHorizontally(tween(300)) { -it } + fadeIn(tween(300)) },
        popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
    ) {
        composable<RouteProfileMain> {
            ProfileMainScreen(
                viewModel = viewModel,
                onNavigate = { dest -> navController.navigate(destToRoute(dest)) },
                onEditProfile = { navController.navigate(RouteProfileEdit::class.qualifiedName!!) }
            )
        }

        composable<RouteProfileEdit> {
            EditProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        // ─── Owner Dashboard sub-screens ───

        composable<RouteProfileDashboard> {
            OwnerDashboardScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigate = { dest -> navController.navigate(destToRoute(dest)) }
            )
        }

        composable<RouteProfileEarnings> {
            EarningsHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileWallet> {
            WalletAndWithdrawalScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileListings> {
            OwnerListingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileCalendar> {
            AvailabilityCalendarScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileBookingsReceived> {
            ReceivedBookingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onReportDamage = { res ->
                    viewModel.setDamageSelection(res)
                    navController.navigate(RouteProfileDamage::class.qualifiedName!!)
                },
                onReviewTenant = { res ->
                    viewModel.setReviewSelection(res)
                    navController.navigate(RouteProfileReviewTenant::class.qualifiedName!!)
                }
            )
        }

        composable<RouteProfileIdentity> {
            IdentityVerificationScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileDisputes> {
            DisputesHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSelectDispute = { id ->
                    viewModel.setSelectedDispute(id)
                    navController.navigate(RouteProfileMediation::class.qualifiedName!!)
                }
            )
        }

        composable<RouteProfileMediation> {
            MediationDetailsScreen(
                viewModel = viewModel,
                disputeId = selectedDisputeId ?: "#LIT-8492",
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileTenantBookings> {
            TenantBookingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onReportDamage = { res ->
                    viewModel.setDamageSelection(res)
                    navController.navigate(RouteProfileDamage::class.qualifiedName!!)
                }
            )
        }

        composable<RouteProfileLanguage> {
            LanguageSelectionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileSecurity> {
            SecuritySettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileNotifications> {
            NotificationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileHelp> {
            HelpAndSupportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfilePaymentMethods> {
            PaymentMethodsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileDamage> {
            DamageReportingScreen(
                reservation = activeDamageSelection,
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        composable<RouteProfileReviewTenant> {
            TenantReviewScreen(
                reservation = activeReviewSelection,
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        composable<RouteProfileAbout> {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileAdvancedSearch> {
            AdvancedSearchScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileSettings> {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileInviteFriend> {
            InviteFriendScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileRating> {
            RatingScreen(
                viewModel = viewModel,
                rentalItemTitle = viewModel.selectedItem.value?.title ?: "Annonce",
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        composable<RouteProfileReservationDetail> {
            val booking = viewModel.bookings.collectAsState().value.firstOrNull()
            if (booking != null) {
                ReservationDetailScreen(
                    booking = booking,
                    onBack = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
        }

        composable<RouteProfilePaymentHistory> {
            PaymentHistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileLeaderboard> {
            LeaderboardScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileAchievements> {
            AchievementsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileFlashOffers> {
            FlashOffersScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileLoyaltyRedeem> {
            LoyaltyRedeemScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileRewardsCoupons> {
            RewardsCouponsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileDispute> {
            DisputeScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileInsurance> {
            InsuranceScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileDigitalDeposit> {
            DigitalDepositScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileRealtimeVerification> {
            RealTimeVerificationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<RouteProfileInteractiveCalendar> {
            InteractiveCalendarScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
    }
}

private fun destToRoute(dest: String): String {
    return when (dest) {
        "dashboard" -> RouteProfileDashboard::class.qualifiedName!!
        "earnings" -> RouteProfileEarnings::class.qualifiedName!!
        "wallet" -> RouteProfileWallet::class.qualifiedName!!
        "listings" -> RouteProfileListings::class.qualifiedName!!
        "calendar" -> RouteProfileCalendar::class.qualifiedName!!
        "bookings_received" -> RouteProfileBookingsReceived::class.qualifiedName!!
        "identity" -> RouteProfileIdentity::class.qualifiedName!!
        "disputes" -> RouteProfileDisputes::class.qualifiedName!!
        "mediation" -> RouteProfileMediation::class.qualifiedName!!
        "tenant_bookings" -> RouteProfileTenantBookings::class.qualifiedName!!
        "language" -> RouteProfileLanguage::class.qualifiedName!!
        "security" -> RouteProfileSecurity::class.qualifiedName!!
        "notifications" -> RouteProfileNotifications::class.qualifiedName!!
        "help" -> RouteProfileHelp::class.qualifiedName!!
        "payment_methods" -> RouteProfilePaymentMethods::class.qualifiedName!!
        "damage" -> RouteProfileDamage::class.qualifiedName!!
        "review_tenant" -> RouteProfileReviewTenant::class.qualifiedName!!
        "edit_profile" -> RouteProfileEdit::class.qualifiedName!!
        "about" -> RouteProfileAbout::class.qualifiedName!!
        "advanced_search" -> RouteProfileAdvancedSearch::class.qualifiedName!!
        "settings" -> RouteProfileSettings::class.qualifiedName!!
        "invite_friend" -> RouteProfileInviteFriend::class.qualifiedName!!
        "rating" -> RouteProfileRating::class.qualifiedName!!
        "reservation_detail" -> RouteProfileReservationDetail::class.qualifiedName!!
        "payment_history" -> RouteProfilePaymentHistory::class.qualifiedName!!
        "leaderboard" -> RouteProfileLeaderboard::class.qualifiedName!!
        "achievements" -> RouteProfileAchievements::class.qualifiedName!!
        "flash_offers" -> RouteProfileFlashOffers::class.qualifiedName!!
        "loyalty_redeem" -> RouteProfileLoyaltyRedeem::class.qualifiedName!!
        "rewards_coupons" -> RouteProfileRewardsCoupons::class.qualifiedName!!
        "dispute" -> RouteProfileDispute::class.qualifiedName!!
        "insurance" -> RouteProfileInsurance::class.qualifiedName!!
        "digital_deposit" -> RouteProfileDigitalDeposit::class.qualifiedName!!
        "realtime_verification" -> RouteProfileRealtimeVerification::class.qualifiedName!!
        "interactive_calendar" -> RouteProfileInteractiveCalendar::class.qualifiedName!!
        else -> RouteProfileMain::class.qualifiedName!!
    }
}
