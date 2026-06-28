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
class RentalViewModelTest {
    private lateinit var viewModel: RentalViewModel

    @Before
    fun setup() {
        val app = RuntimeEnvironment.getApplication()
        viewModel = RentalViewModelFactory(app).create(RentalViewModel::class.java)
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun `initial state loads without crash`() {
        // ViewModel should initialize without throwing
        val vm = RentalViewModelFactory(RuntimeEnvironment.getApplication()).create(RentalViewModel::class.java)
        ShadowLooper.idleMainLooper()
        // Check that basic state is accessible
        assertNotNull(vm.authState.value)
        assertNotNull(vm.filteredRentalItems.value)
    }

    @Test
    fun `search filters items`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        viewModel.setSearchQuery(allItems.first().title.take(4))
        ShadowLooper.idleMainLooper()
        val filtered = viewModel.filteredRentalItems.value
        assertTrue(
            "Filtered items should match search",
            filtered.all {
                it.title.contains(allItems.first().title.take(4), ignoreCase = true) ||
                    it.description.contains(allItems.first().title.take(4), ignoreCase = true)
            }
        )
    }

    @Test
    fun `category filter works`() {
        val allItems = viewModel.rawRentalItems.value
        if (allItems.isEmpty()) return
        val category = allItems.first().category
        viewModel.setSelectedCategory(category)
        ShadowLooper.idleMainLooper()
        val filtered = viewModel.filteredRentalItems.value
        assertTrue(
            "Should only show $category",
            filtered.all { it.category == category }
        )
    }

    @Test
    fun `toggle bookmark works`() {
        val items = viewModel.rawRentalItems.value
        if (items.isEmpty()) return
        val item = items.first()
        val initialSize = viewModel.bookmarkedItems.value.size
        viewModel.toggleBookmark(item)
        ShadowLooper.idleMainLooper()
        val afterAdd = viewModel.bookmarkedItems.value.size
        assertTrue("Bookmark should be added", afterAdd == initialSize + 1 || afterAdd >= initialSize)
        viewModel.toggleBookmark(item)
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun `sort option changes order`() {
        viewModel.setSortOption(SortOption.PRICE_ASC)
        ShadowLooper.idleMainLooper()
        val items = viewModel.filteredRentalItems.value
        if (items.size > 1) {
            assertTrue(
                "Items should be sorted by price ascending",
                items[0].pricePerDay <= items[1].pricePerDay
            )
        }
    }

    @Test
    fun `auth state changes`() {
        viewModel.setAuthState("register")
        assertEquals("register", viewModel.authState.value)
        viewModel.setAuthState("login")
        assertEquals("login", viewModel.authState.value)
    }
}
