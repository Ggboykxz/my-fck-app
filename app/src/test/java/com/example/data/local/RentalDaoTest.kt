package com.example.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RentalDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: RentalDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.rentalDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveRentalItem() = runTest {
        val item = RentalItem(
            title = "Test Villa",
            description = "Desc",
            category = "Immobilier",
            pricePerDay = 50000,
            city = "Libreville",
            neighborhood = "Centre",
            ownerName = "Test Owner",
            ownerPhone = "01020304",
            ownerRating = 4.5f,
            imageUrl = "https://example.com/img.jpg",
            isVerified = true
        )
        dao.insertRentalItem(item)
        val all = dao.getAllRentalItems().first()
        assertEquals(1, all.size)
        assertEquals("Test Villa", all[0].title)
    }

    @Test
    fun bookmarkToggleWorks() = runTest {
        val item = RentalItem(
            title = "Bookmark Test",
            description = "Desc",
            category = "Immobilier",
            pricePerDay = 30000,
            city = "Port-Gentil",
            neighborhood = "Centre",
            ownerName = "Owner",
            ownerPhone = "01020304",
            ownerRating = 4.0f
        )
        dao.insertRentalItem(item)
        val items = dao.getAllRentalItems().first()
        val id = items.first().id

        dao.updateBookmarkStatus(id, true)
        val bookmarked = dao.getBookmarkedItems().first()
        assertEquals(1, bookmarked.size)

        dao.updateBookmarkStatus(id, false)
        val notBookmarked = dao.getBookmarkedItems().first()
        assertEquals(0, notBookmarked.size)
    }

    @Test
    fun searchHistoryIsInsertedAndCleared() = runTest {
        dao.insertSearchHistory(SearchHistoryEntry(query = "villa"))
        val history = dao.getSearchHistory().first()
        assertEquals(1, history.size)
        assertEquals("villa", history[0].query)

        dao.clearSearchHistory()
        val cleared = dao.getSearchHistory().first()
        assertTrue(cleared.isEmpty())
    }

    @Test
    fun notificationsReadCountWorks() = runTest {
        dao.insertNotification(NotificationEntity(type = "booking", title = "T", message = "M", time = "now"))
        dao.insertNotification(NotificationEntity(type = "message", title = "T2", message = "M2", time = "now2"))
        dao.markNotificationRead(1)

        val count = dao.getUnreadNotificationCount().first()
        assertEquals(1, count)
    }

    @Test
    fun insertAndRetrieveBooking() = runTest {
        val booking = Booking(
            rentalItemId = 1,
            rentalItemTitle = "Villa Test",
            rentalItemCategory = "Immobilier",
            pricePerDay = 50000,
            days = 3,
            totalPrice = 150000,
            paymentMethod = "Airtel Money",
            paymentPhone = "01020304",
            status = "Payé"
        )
        dao.insertBooking(booking)
        val all = dao.getAllBookings().first()
        assertEquals(1, all.size)
        assertEquals("Villa Test", all[0].rentalItemTitle)
    }

    @Test
    fun disputeInsertAndRetrieve() = runTest {
        val dispute = DisputeEntity(
            title = "Test Dispute",
            status = "open",
            date = "2026-08-01",
            type = "damage"
        )
        dao.insertDispute(dispute)
        val all = dao.getAllDisputes().first()
        assertEquals(1, all.size)
        assertEquals("Test Dispute", all[0].title)
    }

    @Test
    fun reviewInsertAndRetrieve() = runTest {
        val review = ReviewEntity(
            rentalItemId = 1,
            rating = 5,
            comment = "Super!",
            author = "Marie",
            date = "01/08/2026"
        )
        dao.insertReview(review)
        val reviews = dao.getReviewsForItem(1).first()
        assertEquals(1, reviews.size)
        assertEquals("Super!", reviews[0].comment)
    }

    @Test
    fun userProfileUpsertWorks() = runTest {
        val profile = UserProfile(
            id = 1,
            fullName = "Marie-Claire",
            phone = "+241 77 12 34 56",
            email = "marie@example.com",
            city = "Libreville"
        )
        dao.upsertUserProfile(profile)
        val retrieved = dao.getUserProfileOnce()
        assertNotNull(retrieved)
        assertEquals("Marie-Claire", retrieved!!.fullName)
    }

    @Test
    fun similarItemsExcludesCurrent() = runTest {
        dao.insertRentalItem(RentalItem(title = "A", description = "A", category = "Immobilier", pricePerDay = 1000, city = "L", neighborhood = "N", ownerName = "O", ownerPhone = "P", ownerRating = 4f))
        dao.insertRentalItem(RentalItem(title = "B", description = "B", category = "Immobilier", pricePerDay = 2000, city = "L", neighborhood = "N", ownerName = "O", ownerPhone = "P", ownerRating = 4f))
        dao.insertRentalItem(RentalItem(title = "C", description = "C", category = "Vehicules", pricePerDay = 3000, city = "L", neighborhood = "N", ownerName = "O", ownerPhone = "P", ownerRating = 4f))

        val all = dao.getAllRentalItems().first()
        val idA = all.first { it.title == "A" }.id
        val similar = dao.getSimilarItems(idA, "Immobilier").first()
        assertEquals(1, similar.size)
        assertTrue(similar.none { it.id == idA })
    }
}
