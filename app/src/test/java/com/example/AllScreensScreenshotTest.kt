package com.example

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.Booking
import com.example.data.model.RentalItem
import com.example.data.repository.RentalRepository
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RentalViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class AllScreensScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var viewModel: RentalViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = RentalViewModel(app)
    }

    private fun screenshot(name: String) {
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/$name.png")
    }

    // ==================== ONBOARDING (4) ====================

    @Test
    fun screenshot_01_splash() {
        viewModel.restartOnboarding()
        composeTestRule.setContent {
            MyApplicationTheme { OnboardingNavigator(viewModel = viewModel, onFinished = {}) }
        }
        screenshot("01_splash")
    }

    @Test
    fun screenshot_02_welcome() {
        viewModel.restartOnboarding()
        viewModel.nextOnboarding()
        composeTestRule.setContent {
            MyApplicationTheme { OnboardingNavigator(viewModel = viewModel, onFinished = {}) }
        }
        screenshot("02_welcome")
    }

    @Test
    fun screenshot_03_payments_onboarding() {
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    PaymentsOnboardingScreen(onNext = {}, onSkip = {})
                }
            }
        }
        screenshot("03_payments_onboarding")
    }

    @Test
    fun screenshot_04_trust_onboarding() {
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    TrustOnboardingScreen(onStart = {})
                }
            }
        }
        screenshot("04_trust_onboarding")
    }

    // ==================== AUTH (12) ====================

    @Test
    fun screenshot_05_login() {
        viewModel.setAuthState("login")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("05_login")
    }

    @Test
    fun screenshot_06_register() {
        viewModel.setAuthState("register")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("06_register")
    }

    @Test
    fun screenshot_07_forgot_password() {
        viewModel.setAuthState("forgot_password")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("07_forgot_password")
    }

    @Test
    fun screenshot_08_otp() {
        viewModel.setAuthState("otp")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("08_otp")
    }

    @Test
    fun screenshot_09_new_password() {
        viewModel.setAuthState("new_password")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("09_new_password")
    }

    @Test
    fun screenshot_10_loading_login() {
        viewModel.setAuthState("loading_login")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("10_loading_login")
    }

    @Test
    fun screenshot_11_login_success() {
        viewModel.setAuthState("login_success")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("11_login_success")
    }

    @Test
    fun screenshot_12_loading_register() {
        viewModel.setAuthState("loading_register")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("12_loading_register")
    }

    @Test
    fun screenshot_13_register_success() {
        viewModel.setAuthState("register_success")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("13_register_success")
    }

    @Test
    fun screenshot_14_complete_profile() {
        viewModel.setAuthState("complete_profile")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("14_complete_profile")
    }

    @Test
    fun screenshot_15_profile_success() {
        viewModel.setAuthState("profile_success")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("15_profile_success")
    }

    @Test
    fun screenshot_16_password_reset_success() {
        viewModel.setAuthState("password_reset_success")
        composeTestRule.setContent {
            MyApplicationTheme { AuthNavigator(viewModel = viewModel) }
        }
        screenshot("16_password_reset_success")
    }

    // ==================== DASHBOARD (7) ====================

    @Test
    fun screenshot_17_explore() {
        viewModel.setLoggedIn(true)
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("17_explore")
    }

    @Test
    fun screenshot_18_item_details() {
        viewModel.setLoggedIn(true)
        viewModel.navigateTo("details")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("18_item_details")
    }

    @Test
    fun screenshot_19_bookmarks() {
        viewModel.setLoggedIn(true)
        viewModel.navigateTo("bookmarks")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("19_bookmarks")
    }

    @Test
    fun screenshot_20_bookings() {
        viewModel.setLoggedIn(true)
        viewModel.navigateTo("bookings")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("20_bookings")
    }

    @Test
    fun screenshot_21_messages() {
        viewModel.setLoggedIn(true)
        viewModel.navigateTo("messages")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("21_messages")
    }

    @Test
    fun screenshot_22_post_listing() {
        viewModel.setLoggedIn(true)
        viewModel.navigateTo("post_listing")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("22_post_listing")
    }

    @Test
    fun screenshot_23_chat() {
        viewModel.setLoggedIn(true)
        val items = viewModel.rawRentalItems.value
        if (items.isNotEmpty()) {
            viewModel.selectItem(items.first())
            viewModel.openChatFor(items.first())
        }
        viewModel.navigateTo("chat")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("23_chat")
    }

    // ==================== PROFILE MAIN + SUB-SCREENS (18) ====================

    @Test
    fun screenshot_24_profile() {
        viewModel.setLoggedIn(true)
        viewModel.navigateTo("profile")
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        screenshot("24_profile")
    }

    @Test
    fun screenshot_25_edit_profile() {
        composeTestRule.setContent {
            MyApplicationTheme {
                EditProfileScreen(viewModel = viewModel, onBack = {}, onSave = {})
            }
        }
        screenshot("25_edit_profile")
    }

    @Test
    fun screenshot_26_identity_verification() {
        composeTestRule.setContent {
            MyApplicationTheme {
                IdentityVerificationScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("26_identity_verification")
    }

    @Test
    fun screenshot_27_language() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LanguageSelectionScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("27_language")
    }

    @Test
    fun screenshot_28_security() {
        composeTestRule.setContent {
            MyApplicationTheme {
                SecuritySettingsScreen(onBack = {})
            }
        }
        screenshot("28_security")
    }

    @Test
    fun screenshot_29_notifications() {
        composeTestRule.setContent {
            MyApplicationTheme {
                NotificationsScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("29_notifications")
    }

    @Test
    fun screenshot_30_help() {
        composeTestRule.setContent {
            MyApplicationTheme {
                HelpAndSupportScreen(onBack = {})
            }
        }
        screenshot("30_help")
    }

    @Test
    fun screenshot_31_payment_methods() {
        composeTestRule.setContent {
            MyApplicationTheme {
                PaymentMethodsScreen(onBack = {})
            }
        }
        screenshot("31_payment_methods")
    }

    @Test
    fun screenshot_32_disputes() {
        viewModel.setOwnerMode(false)
        composeTestRule.setContent {
            MyApplicationTheme {
                DisputesHistoryScreen(viewModel = viewModel, onBack = {}, onSelectDispute = {})
            }
        }
        screenshot("32_disputes")
    }

    @Test
    fun screenshot_33_mediation() {
        composeTestRule.setContent {
            MyApplicationTheme {
                MediationDetailsScreen(viewModel = viewModel, disputeId = "#LIT-8492", onBack = {})
            }
        }
        screenshot("33_mediation")
    }

    @Test
    fun screenshot_34_tenant_bookings() {
        viewModel.setOwnerMode(false)
        composeTestRule.setContent {
            MyApplicationTheme {
                TenantBookingsScreen(viewModel = viewModel, onBack = {}, onReportDamage = {})
            }
        }
        screenshot("34_tenant_bookings")
    }

    @Test
    fun screenshot_35_damage_report() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DamageReportingScreen(reservation = null, onBack = {}, onSubmitted = {})
            }
        }
        screenshot("35_damage_report")
    }

    @Test
    fun screenshot_36_tenant_review() {
        composeTestRule.setContent {
            MyApplicationTheme {
                TenantReviewScreen(reservation = null, onBack = {}, onSubmitted = {})
            }
        }
        screenshot("36_tenant_review")
    }

    @Test
    fun screenshot_37_about() {
        composeTestRule.setContent {
            MyApplicationTheme {
                AboutScreen(onBack = {})
            }
        }
        screenshot("37_about")
    }

    // ==================== OWNER MODE (12) ====================

    @Test
    fun screenshot_38_owner_dashboard() {
        viewModel.setOwnerMode(true)
        composeTestRule.setContent {
            MyApplicationTheme {
                OwnerDashboardScreen(viewModel = viewModel, onBack = {}, onNavigate = {})
            }
        }
        screenshot("38_owner_dashboard")
    }

    @Test
    fun screenshot_39_earnings() {
        composeTestRule.setContent {
            MyApplicationTheme {
                EarningsHistoryScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("39_earnings")
    }

    @Test
    fun screenshot_40_wallet() {
        composeTestRule.setContent {
            MyApplicationTheme {
                WalletAndWithdrawalScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("40_wallet")
    }

    @Test
    fun screenshot_41_owner_listings() {
        viewModel.setOwnerMode(true)
        composeTestRule.setContent {
            MyApplicationTheme {
                OwnerListingsScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("41_owner_listings")
    }

    @Test
    fun screenshot_42_calendar() {
        composeTestRule.setContent {
            MyApplicationTheme {
                AvailabilityCalendarScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("42_calendar")
    }

    @Test
    fun screenshot_43_received_bookings() {
        viewModel.setOwnerMode(true)
        composeTestRule.setContent {
            MyApplicationTheme {
                ReceivedBookingsScreen(
                    viewModel = viewModel,
                    onBack = {},
                    onReportDamage = {},
                    onReviewTenant = {}
                )
            }
        }
        screenshot("43_received_bookings")
    }

    // ==================== PAYMENT FLOW (2) ====================

    @Test
    fun screenshot_44_payment_processing() {
        viewModel.setLoggedIn(true)
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardView(viewModel = viewModel) }
        }
        composeTestRule.mainClock.advanceTimeBy(3000)
        composeTestRule.waitForIdle()
        screenshot("44_payment_processing")
    }

    @Test
    fun screenshot_45_skeleton_loading() {
        composeTestRule.setContent {
            MyApplicationTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    repeat(3) { i ->
                        com.example.ui.components.SkeletonCard()
                    }
                }
            }
        }
        screenshot("45_skeleton_loading")
    }

    // ==================== NEW SCREENS (6) ====================

    @Test
    fun screenshot_46_advanced_search() {
        composeTestRule.setContent {
            MyApplicationTheme {
                AdvancedSearchScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("46_advanced_search")
    }

    @Test
    fun screenshot_47_settings() {
        composeTestRule.setContent {
            MyApplicationTheme {
                SettingsScreen(onBack = {})
            }
        }
        screenshot("47_settings")
    }

    @Test
    fun screenshot_48_invite_friend() {
        composeTestRule.setContent {
            MyApplicationTheme {
                InviteFriendScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("48_invite_friend")
    }

    @Test
    fun screenshot_49_rating() {
        composeTestRule.setContent {
            MyApplicationTheme {
                RatingScreen(rentalItemTitle = "Villa de Luxe - La Sablière", onBack = {}, onSubmitted = {})
            }
        }
        screenshot("49_rating")
    }

    @Test
    fun screenshot_50_reservation_detail() {
        val mockBooking = Booking(
            id = 1,
            rentalItemId = 5,
            rentalItemTitle = "Toyota Prado VXR 2023",
            rentalItemCategory = "Véhicules",
            pricePerDay = 95000,
            days = 3,
            totalPrice = 285000,
            status = "Confirmé",
            paymentMethod = "Airtel Money",
            paymentPhone = "+241 07 12 34 56",
            bookingTimestamp = System.currentTimeMillis()
        )
        composeTestRule.setContent {
            MyApplicationTheme {
                ReservationDetailScreen(booking = mockBooking, onBack = {}, onCancel = {})
            }
        }
        screenshot("50_reservation_detail")
    }

    @Test
    fun screenshot_51_payment_history() {
        composeTestRule.setContent {
            MyApplicationTheme {
                PaymentHistoryScreen(viewModel = viewModel, onBack = {})
            }
        }
        screenshot("51_payment_history")
    }
}
