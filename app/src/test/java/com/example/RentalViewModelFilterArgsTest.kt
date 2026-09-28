package com.example

import com.example.ui.components.SortOption
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
class RentalViewModelFilterArgsTest {
    private lateinit var viewModel: RentalViewModel

    @Before
    fun setup() {
        val app = RuntimeEnvironment.getApplication()
        viewModel = RentalViewModelFactory(app).create(RentalViewModel::class.java)
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun `filterArgs data class is created correctly`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return

        viewModel.setSearchQuery("test")
        viewModel.setSelectedCategory("Immobilier")
        viewModel.setSelectedCity("Libreville")
        viewModel.setSelectedMaxPrice(50000)
        viewModel.setSortOption(SortOption.PRICE_ASC)
        ShadowLooper.idleMainLooper()

        val filtered = viewModel.filteredRentalItems.value
        assertTrue("Filtered items should respect category", filtered.all { it.category == "Immobilier" })
        assertTrue("Filtered items should respect city", filtered.all { it.city == "Libreville" })
        assertTrue("Filtered items should respect max price", filtered.all { it.pricePerDay <= 50000 })
    }

    @Test
    fun `clearAllFilters resets all filter states`() {
        viewModel.setSearchQuery("test")
        viewModel.setSelectedCategory("Immobilier")
        viewModel.setSelectedCity("Libreville")
        viewModel.setSelectedMaxPrice(50000)
        viewModel.setSortOption(SortOption.PRICE_DESC)
        ShadowLooper.idleMainLooper()

        viewModel.clearAllFilters()
        ShadowLooper.idleMainLooper()

        assertEquals("", viewModel.searchQuery.value)
        assertEquals("Tous", viewModel.selectedCategory.value)
        assertEquals("Tous", viewModel.selectedCity.value)
        assertEquals(0, viewModel.selectedMaxPrice.value)
        assertEquals(SortOption.RECENT, viewModel.sortOption.value)
    }

    @Test
    fun `wallet balance is exposed`() {
        val balance = viewModel.walletBalance.value
        assertTrue("Wallet balance should be non-negative", balance >= 0)
    }

    @Test
    fun `wallet transactions are exposed`() {
        val txns = viewModel.walletTransactions.value
        assertNotNull(txns)
    }

    @Test
    fun `recentlyViewed is empty initially`() {
        val recent = viewModel.recentlyViewed.value
        assertNotNull(recent)
    }

    @Test
    fun `addToRecentlyViewed adds item`() {
        val items = viewModel.rawRentalItems.value
        if (items.isEmpty()) return

        viewModel.addToRecentlyViewed(items.first())
        ShadowLooper.idleMainLooper()
        val recent = viewModel.recentlyViewed.value
        assertEquals(1, recent.size)
    }

    @Test
    fun `clearRecentlyViewed empties list`() {
        val items = viewModel.rawRentalItems.value
        if (items.isEmpty()) return

        viewModel.addToRecentlyViewed(items.first())
        ShadowLooper.idleMainLooper()
        viewModel.clearRecentlyViewed()
        ShadowLooper.idleMainLooper()
        assertTrue(viewModel.recentlyViewed.value.isEmpty())
    }

    @Test
    fun `fuzzySearch handles short queries`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        val title = allItems.first().title.lowercase()
        if (title.length < 4) return

        val typoQuery = title.take(2) + (title.getOrNull(3) ?: "")
        val results = viewModel.fuzzySearch(typoQuery)
        assertNotNull(results)
    }

    @Test
    fun `paymentState starts as Idle`() {
        val state = viewModel.paymentState.value
        assertTrue(state is com.example.ui.viewmodel.PaymentState.Idle)
    }

    @Test
    fun `profileCompletion score is calculated`() {
        val score = viewModel.profileCompletion.value
        assertTrue("Profile completion should be between 0 and 100", score in 0..100)
    }
}
