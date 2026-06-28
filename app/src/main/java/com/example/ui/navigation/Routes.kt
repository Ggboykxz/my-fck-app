package com.example.ui.navigation

import kotlinx.serialization.Serializable

// ─── Onboarding (4 routes) ───
@Serializable data object RouteOnboardingSplash
@Serializable data object RouteOnboardingWelcome
@Serializable data object RouteOnboardingPayments
@Serializable data object RouteOnboardingTrust

// ─── Auth (12 routes) ───
@Serializable data object RouteAuthLogin
@Serializable data object RouteAuthRegister
@Serializable data object RouteAuthLoadingLogin
@Serializable data object RouteAuthLoginSuccess
@Serializable data object RouteAuthLoadingRegister
@Serializable data object RouteAuthRegisterSuccess
@Serializable data object RouteAuthCompleteProfile
@Serializable data object RouteAuthProfileSuccess
@Serializable data object RouteAuthForgotPassword
@Serializable data object RouteAuthOtp
@Serializable data object RouteAuthNewPassword
@Serializable data object RouteAuthPasswordResetSuccess

// ─── Dashboard — Top-level screens (8 routes) ───
@Serializable data object RouteHome
@Serializable data object RouteBookings
@Serializable data object RoutePostListing
@Serializable data object RouteBookmarks
@Serializable data object RouteMessages
@Serializable data object RouteProfile
@Serializable data class RouteDetails(val itemId: Int = 0)
@Serializable data object RouteChat

// ─── Profile sub-screens (35 routes) ───
@Serializable data object RouteProfileMain
@Serializable data object RouteProfileEdit
@Serializable data object RouteProfileDashboard
@Serializable data object RouteProfileEarnings
@Serializable data object RouteProfileWallet
@Serializable data object RouteProfileListings
@Serializable data object RouteProfileCalendar
@Serializable data object RouteProfileBookingsReceived
@Serializable data object RouteProfileIdentity
@Serializable data object RouteProfileDisputes
@Serializable data object RouteProfileMediation
@Serializable data object RouteProfileTenantBookings
@Serializable data object RouteProfileLanguage
@Serializable data object RouteProfileSecurity
@Serializable data object RouteProfileNotifications
@Serializable data object RouteProfileHelp
@Serializable data object RouteProfilePaymentMethods
@Serializable data object RouteProfileDamage
@Serializable data object RouteProfileReviewTenant
@Serializable data object RouteProfileAbout
@Serializable data object RouteProfileAdvancedSearch
@Serializable data object RouteProfileSettings
@Serializable data object RouteProfileInviteFriend
@Serializable data object RouteProfileRating
@Serializable data object RouteProfileReservationDetail
@Serializable data object RouteProfilePaymentHistory
@Serializable data object RouteProfileLeaderboard
@Serializable data object RouteProfileAchievements
@Serializable data object RouteProfileFlashOffers
@Serializable data object RouteProfileLoyaltyRedeem
@Serializable data object RouteProfileRewardsCoupons
@Serializable data object RouteProfileDispute
@Serializable data object RouteProfileInsurance
@Serializable data object RouteProfileDigitalDeposit
@Serializable data object RouteProfileRealtimeVerification
@Serializable data object RouteProfileInteractiveCalendar
