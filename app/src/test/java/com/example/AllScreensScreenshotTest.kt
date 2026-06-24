package com.example

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.RentalRepository
import com.example.ui.screens.AuthNavigator
import com.example.ui.screens.MainDashboardView
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RentalViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
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

  @Test
  fun screenshot_auth_login() {
    viewModel.setAuthState("login")
    composeTestRule.setContent {
      MyApplicationTheme {
        AuthNavigator(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/01_auth_login.png")
  }

  @Test
  fun screenshot_auth_register() {
    viewModel.setAuthState("register")
    composeTestRule.setContent {
      MyApplicationTheme {
        AuthNavigator(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/02_auth_register.png")
  }

  @Test
  fun screenshot_auth_forgot_password() {
    viewModel.setAuthState("forgot_password")
    composeTestRule.setContent {
      MyApplicationTheme {
        AuthNavigator(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/03_auth_forgot_password.png")
  }

  @Test
  fun screenshot_auth_otp() {
    viewModel.setAuthState("otp")
    composeTestRule.setContent {
      MyApplicationTheme {
        AuthNavigator(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/04_auth_otp.png")
  }

  @Test
  fun screenshot_auth_new_password() {
    viewModel.setAuthState("new_password")
    composeTestRule.setContent {
      MyApplicationTheme {
        AuthNavigator(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/05_auth_new_password.png")
  }

  @Test
  fun screenshot_main_dashboard() {
    viewModel.setLoggedIn(true)
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardView(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(3000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/06_main_explore.png")
  }

  @Test
  fun screenshot_bookmarks() {
    viewModel.setLoggedIn(true)
    viewModel.navigateTo("bookmarks")
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardView(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/07_bookmarks.png")
  }

  @Test
  fun screenshot_bookings() {
    viewModel.setLoggedIn(true)
    viewModel.navigateTo("bookings")
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardView(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/08_bookings.png")
  }

  @Test
  fun screenshot_messages() {
    viewModel.setLoggedIn(true)
    viewModel.navigateTo("messages")
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardView(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/09_messages.png")
  }

  @Test
  fun screenshot_post_listing() {
    viewModel.setLoggedIn(true)
    viewModel.navigateTo("post_listing")
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardView(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/10_post_listing.png")
  }

  @Test
  fun screenshot_profile() {
    viewModel.setLoggedIn(true)
    viewModel.navigateTo("profile")
    composeTestRule.setContent {
      MyApplicationTheme {
        MainDashboardView(viewModel = viewModel)
      }
    }
    composeTestRule.mainClock.advanceTimeBy(2000)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/11_profile.png")
  }
}
