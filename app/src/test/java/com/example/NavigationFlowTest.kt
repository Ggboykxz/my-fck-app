package com.example

import android.app.Application
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.RentalRepository
import com.example.ui.screens.MainDashboardViewNavHost
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RentalViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLog
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class NavigationFlowTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var viewModel: RentalViewModel

    @Before
    fun setup() {
        ShadowLog.stream = System.out
        app = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        viewModel = RentalViewModel(app, RentalRepository(db.rentalDao()))
        composeTestRule.mainClock.advanceTimeBy(3000)
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun pump(timeoutMs: Long, condition: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            composeTestRule.waitForIdle()
            ShadowLooper.idleMainLooper(400L, TimeUnit.MILLISECONDS)
            Thread.sleep(25)
            composeTestRule.mainClock.advanceTimeBy(400)
            if (condition()) break
        }
        composeTestRule.waitForIdle()
    }

    private fun settle() = pump(1500) { false }

    private fun firstCardTag(): String? =
        (1..12).flatMap { listOf("rental_card_$it", "rental_card_compact_$it") }
            .firstOrNull { composeTestRule.onAllNodesWithTag(it).fetchSemanticsNodes().isNotEmpty() }

    private fun dumpTree() {
        composeTestRule.onRoot().printToLog("NAV_TREE")
    }

    @Test
    fun `home screen renders with shared element cards`() {
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardViewNavHost(viewModel = viewModel) }
        }
        pump(8000) { firstCardTag() != null }

        val tag = firstCardTag()
        if (tag == null) dumpTree()
        assertNotNull("a rental card should be rendered on home", tag)
        composeTestRule.onNodeWithTag(tag!!).assertExists()
    }

    @Test
    fun `open details then return to home via bottom bar`() {
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardViewNavHost(viewModel = viewModel) }
        }
        pump(8000) { firstCardTag() != null }

        val tag = firstCardTag()
        if (tag == null) dumpTree()
        assertNotNull("a rental card should be rendered on home", tag)
        composeTestRule.onNodeWithTag(tag!!).performClick()
        settle()

        composeTestRule.onNode(hasText("Publier") and hasClickAction()).performClick()
        pump(6000) {
            composeTestRule.onAllNodesWithTag("post_title_input").fetchSemanticsNodes().isNotEmpty()
        }
        val onPost = composeTestRule.onAllNodesWithTag("post_title_input").fetchSemanticsNodes().isNotEmpty()
        if (!onPost) dumpTree()
        assertTrue(
            "bottom bar should leave details (currentScreen=${viewModel.currentScreen.value})", onPost
        )

        composeTestRule.onNode(hasText("Explorer") and hasClickAction()).performClick()
        pump(8000) { firstCardTag() != null }

        val back = firstCardTag()
        if (back == null) dumpTree()
        assertNotNull("home should be restored via the bottom bar", back)
    }

    @Test
    fun `navigateTo home from every secondary screen`() {
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardViewNavHost(viewModel = viewModel) }
        }
        settle()

        for (screen in listOf(
            "details", "bookmarks", "bookings", "messages", "post_listing",
            "profile", "map_explorer", "search_intelligence"
        )) {
            viewModel.navigateTo(screen)
            settle()
            viewModel.navigateTo("home")
            settle()
        }
    }

    @Test
    fun `bottom bar home tab is reachable`() {
        composeTestRule.setContent {
            MyApplicationTheme { MainDashboardViewNavHost(viewModel = viewModel) }
        }
        settle()

        val explorerTabs = composeTestRule.onAllNodes(hasText("Explorer") and hasClickAction())
        val found = explorerTabs.fetchSemanticsNodes().isNotEmpty()
        if (!found) dumpTree()
        assertTrue("bottom bar should render on home", found)
        composeTestRule.onNode(hasText("Explorer") and hasClickAction()).performClick()
        settle()
    }
}
