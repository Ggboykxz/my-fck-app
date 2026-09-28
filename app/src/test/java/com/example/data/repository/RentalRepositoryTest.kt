package com.example.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RentalRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: RentalRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = RentalRepository(db.rentalDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun repository_exposesRentalItemsFlow() = runTest {
        val items = repository.allRentalItems.first()
        assertNotNull(items)
    }

    @Test
    fun repository_exposesBookmarksFlow() = runTest {
        val items = repository.bookmarkedItems.first()
        assertNotNull(items)
    }

    @Test
    fun repository_exposesBookingsFlow() = runTest {
        val bookings = repository.allBookings.first()
        assertNotNull(bookings)
    }

    @Test
    fun repository_getUserProfileOnce_returnsNullWhenEmpty() = runTest {
        val profile = repository.getUserProfileOnce()
        assertNull(profile)
    }

    @Test
    fun repository_insertAndDeleteRentalItem() = runTest {
        val item = RentalItem(
            title = "Repo Test",
            description = "Desc",
            category = "Immobilier",
            pricePerDay = 25000,
            city = "Franceville",
            neighborhood = "Centre",
            ownerName = "Test",
            ownerPhone = "01020304",
            ownerRating = 4.2f
        )
        repository.insertRentalItem(item)
        val all = repository.allRentalItems.first()
        assertTrue(all.any { it.title == "Repo Test" })

        val inserted = all.first { it.title == "Repo Test" }
        repository.deleteRentalItem(inserted.id)
        val afterDelete = repository.allRentalItems.first()
        assertTrue(afterDelete.none { it.title == "Repo Test" })
    }

    @Test
    fun repository_updateBookingStatus() = runTest {
        val booking = Booking(
            rentalItemId = 1,
            rentalItemTitle = "Test Booking",
            rentalItemCategory = "Immobilier",
            pricePerDay = 50000,
            days = 2,
            totalPrice = 100000,
            paymentMethod = "Airtel Money",
            paymentPhone = "01020304",
            status = "Payé"
        )
        repository.insertBooking(booking)
        val all = repository.allBookings.first()
        val id = all.first().id

        repository.updateBookingStatus(id, "Annulé", "Test reason")
        val updated = repository.allBookings.first()
        assertEquals("Annulé", updated.first { it.id == id }.status)
    }
}
