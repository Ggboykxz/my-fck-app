package com.example.ui.utils

import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WindowSizeClassTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun widthThresholdsAreClassifiedCorrectly() {
        assertEquals(WindowWidthSize.Compact, windowSizeClassOf(0, 700).width)
        assertEquals(WindowWidthSize.Compact, windowSizeClassOf(320, 700).width)
        assertEquals(WindowWidthSize.Compact, windowSizeClassOf(599, 700).width)
        assertEquals(WindowWidthSize.Medium, windowSizeClassOf(600, 700).width)
        assertEquals(WindowWidthSize.Medium, windowSizeClassOf(839, 700).width)
        assertEquals(WindowWidthSize.Expanded, windowSizeClassOf(840, 700).width)
        assertEquals(WindowWidthSize.Expanded, windowSizeClassOf(1280, 700).width)
    }

    @Test
    fun heightThresholdsAreClassifiedCorrectly() {
        assertEquals(WindowHeightSize.Compact, windowSizeClassOf(400, 0).height)
        assertEquals(WindowHeightSize.Compact, windowSizeClassOf(400, 399).height)
        assertEquals(WindowHeightSize.Medium, windowSizeClassOf(400, 400).height)
        assertEquals(WindowHeightSize.Medium, windowSizeClassOf(400, 699).height)
        assertEquals(WindowHeightSize.Expanded, windowSizeClassOf(400, 700).height)
        assertEquals(WindowHeightSize.Expanded, windowSizeClassOf(400, 1024).height)
    }

    @Test
    fun combinedClassificationCoversPhoneAndTabletShapes() {
        assertEquals(
            WindowSizeClass(WindowWidthSize.Compact, WindowHeightSize.Medium),
            windowSizeClassOf(360, 640)
        )
        assertEquals(
            WindowSizeClass(WindowWidthSize.Medium, WindowHeightSize.Expanded),
            windowSizeClassOf(700, 900)
        )
        assertEquals(
            WindowSizeClass(WindowWidthSize.Expanded, WindowHeightSize.Expanded),
            windowSizeClassOf(1280, 800)
        )
    }

    @Test
    fun currentWindowSizeClassReadsLocalConfiguration() {
        var result: WindowSizeClass? = null

        composeTestRule.setContent {
            val configuration = Configuration().apply {
                screenWidthDp = 900
                screenHeightDp = 800
            }
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                result = currentWindowSizeClass()
            }
        }
        composeTestRule.waitForIdle()

        assertNotNull(result)
        assertEquals(WindowWidthSize.Expanded, result!!.width)
        assertEquals(WindowHeightSize.Expanded, result!!.height)
    }
}
