package com.example.data.repository

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.ContextWrapper
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.RentalItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SeedDataTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: RentalRepository
    private lateinit var application: Application

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RentalRepository(db.rentalDao())
    }

    @After
    fun teardown() {
        db.close()
    }

    private suspend fun seed() = repository.seedDatabase(application)

    @Test
    fun seedPopulatesAllSections() = runTest {
        seed()

        assertEquals(5, repository.allRentalItems.first().size)
        assertEquals(4, repository.getAllChatMessages().first().size)
        assertEquals(3, repository.notifications.first().size)
        assertEquals(2, repository.getReviewsForItem(1).first().size)
        assertEquals(1, repository.getReviewsForItem(2).first().size)
        assertEquals(1, repository.getReviewsForItem(5).first().size)
    }

    @Test
    fun seedIsIdempotent() = runTest {
        seed()
        seed()
        seed()

        assertEquals(5, repository.allRentalItems.first().size)
        assertEquals(3, repository.notifications.first().size)
        assertEquals(4, repository.getAllChatMessages().first().size)
    }

    @Test
    fun seedMapsFirstItemFields() = runTest {
        seed()

        val items = repository.allRentalItems.first()
        val villa = items.firstOrNull { it.pricePerDay == 150000 }
        assertNotNull("seeded villa with price 150000 must exist", villa)
        assertEquals("Villa de Luxe meublée - La Sablière", villa!!.title)
        assertEquals("Immobilier", villa.category)
        assertEquals("Libreville", villa.city)
        assertEquals("La Sablière", villa.neighborhood)
        assertEquals("Kofi Mensah", villa.ownerName)
        assertEquals(4.9f, villa.ownerRating, 0.001f)
        assertTrue("seeded item must be verified", villa.isVerified)
        assertNotNull(villa.imageUrl)
        assertTrue("all seeded items must carry a price", items.all { it.pricePerDay > 0 })
        assertTrue("all seeded items must carry a city", items.all { it.city.isNotBlank() })
    }

    @Test
    fun seedAssetIsStructurallyValid() {
        val root = Json.parseToJsonElement(readSeedAsset()).jsonObject

        assertEquals(
            setOf("rentalItems", "chatMessages", "notifications", "reviews"),
            root.keys
        )
        assertEquals(5, root.getValue("rentalItems").jsonArray.size)
        assertEquals(4, root.getValue("chatMessages").jsonArray.size)
        assertEquals(3, root.getValue("notifications").jsonArray.size)
        assertEquals(4, root.getValue("reviews").jsonArray.size)

        root.getValue("rentalItems").jsonArray.forEach { element ->
            val item = element.jsonObject
            for (key in listOf(
                "title", "description", "category", "pricePerDay", "city",
                "neighborhood", "ownerName", "ownerPhone", "ownerRating", "isVerified"
            )) {
                assertTrue("rentalItems entry is missing '$key'", item.containsKey(key))
            }
            assertTrue(
                "pricePerDay must be numeric",
                item.getValue("pricePerDay").jsonPrimitive.content.toIntOrNull() != null
            )
            assertTrue(
                "ownerRating must be numeric",
                item.getValue("ownerRating").jsonPrimitive.content.toFloatOrNull() != null
            )
        }

        root.getValue("reviews").jsonArray.forEach { element ->
            val review = element.jsonObject
            for (key in listOf("rentalItemId", "rating", "comment", "author", "date")) {
                assertTrue("reviews entry is missing '$key'", review.containsKey(key))
            }
        }
    }

    @Test
    fun missingSeedAssetDoesNotThrow() = runTest {
        val broken = object : ContextWrapper(application) {
            override fun getAssets(): AssetManager = throw IOException("seed asset unavailable")
        }

        repository.seedDatabase(broken)

        assertTrue(repository.allRentalItems.first().isEmpty())
        assertTrue(repository.notifications.first().isEmpty())
    }
    private fun readSeedAsset(): String =
        application.assets.open("seed/seed_data.json").bufferedReader().use { it.readText() }
}
