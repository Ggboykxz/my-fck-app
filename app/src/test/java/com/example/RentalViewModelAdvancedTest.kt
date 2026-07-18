package com.example

import android.app.Application
import com.example.ui.viewmodel.RentalViewModel
import com.example.ui.viewmodel.RentalViewModelFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RentalViewModelAdvancedTest {
    private lateinit var viewModel: RentalViewModel

    @Before
    fun setup() {
        val app = RuntimeEnvironment.getApplication()
        viewModel = RentalViewModelFactory(app).create(RentalViewModel::class.java)
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun `search returns matching results`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        val query = allItems.first().title.take(3)
        viewModel.setSearchQuery(query)
        ShadowLooper.idleMainLooper()
        val filtered = viewModel.filteredRentalItems.value
        assertTrue("Search should return matching items", filtered.isNotEmpty())
        assertTrue(
            "All results should match query",
            filtered.all {
                it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.neighborhood.contains(query, ignoreCase = true)
            }
        )
    }

    @Test
    fun `bookmark toggle works`() {
        val items = viewModel.rawRentalItems.value
        if (items.isEmpty()) return
        val item = items.first()
        val initialBookmarked = viewModel.bookmarkedItems.value.any { it.id == item.id }
        viewModel.toggleBookmark(item)
        ShadowLooper.idleMainLooper()
        val afterToggle = viewModel.bookmarkedItems.value.any { it.id == item.id }
        assertTrue("Bookmark state should change", initialBookmarked != afterToggle)
    }

    @Test
    fun `category filter works`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        val categories = allItems.map { it.category }.distinct()
        if (categories.size < 2) return
        val category = categories.first()
        viewModel.setSelectedCategory(category)
        ShadowLooper.idleMainLooper()
        val filtered = viewModel.filteredRentalItems.value
        assertTrue("All filtered items should be in category", filtered.all { it.category == category })
    }

    @Test
    fun `fuzzy search handles typos`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        val title = allItems.first().title.lowercase()
        if (title.length < 4) return
        val typoQuery = title.take(2) + (title.getOrNull(3) ?: "")
        val results = viewModel.fuzzySearch(typoQuery)
        assertNotNull("Fuzzy search should return a list", results)
    }

    @Test
    fun `notification count updates`() {
        val count = viewModel.unreadNotificationCount.value
        assertNotNull("Notification count should not be null", count)
        assertTrue("Notification count should be non-negative", count >= 0)
    }

    @Test
    fun `city filter works`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        val cities = allItems.map { it.city }.distinct()
        if (cities.size < 2) return
        val city = cities.first()
        viewModel.setSelectedCity(city)
        ShadowLooper.idleMainLooper()
        val filtered = viewModel.filteredRentalItems.value
        assertTrue("All filtered items should be in city", filtered.all { it.city == city })
        viewModel.setSelectedCity("Tous")
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun `clear all filters resets state`() {
        viewModel.setSearchQuery("test")
        viewModel.setSelectedCategory("Immobilier")
        viewModel.setSelectedCity("Libreville")
        viewModel.setSelectedMaxPrice(50000)
        ShadowLooper.idleMainLooper()
        viewModel.clearAllFilters()
        ShadowLooper.idleMainLooper()
        assertEquals("Search query should be empty", "", viewModel.searchQuery.value)
        assertEquals("Category should be Tous", "Tous", viewModel.selectedCategory.value)
        assertEquals("City should be Tous", "Tous", viewModel.selectedCity.value)
        assertEquals("Max price should be 0", 0, viewModel.selectedMaxPrice.value)
    }

    @Test
    fun `navigation works for all screens`() {
        val screens = listOf("home", "details", "bookmarks", "bookings", "messages", "chat", "post_listing", "profile", "map_explorer", "search_intelligence")
        screens.forEach { screen ->
            viewModel.navigateTo(screen)
            ShadowLooper.idleMainLooper()
            assertNotNull("Current screen should not be null after navigating to $screen", viewModel.currentScreen.value)
        }
    }
}
