package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.RentalRepository
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RentalViewModelLocalizationTest {

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: RentalRepository
    private lateinit var viewModel: RentalViewModel

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RentalRepository(db.rentalDao())
        viewModel = RentalViewModel(app, repository)
        pump(2500)
        viewModel.dismissSnackbar()
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun pump(totalMillis: Long, stepMillis: Long = 400L) {
        var elapsed = 0L
        while (elapsed < totalMillis) {
            ShadowLooper.idleMainLooper(stepMillis, TimeUnit.MILLISECONDS)
            elapsed += stepMillis
            Thread.sleep(25L)
            ShadowLooper.idleMainLooper()
        }
    }

    /** Keeps draining the main looper until [condition] holds (or the deadline passes). */
    private fun awaitUntil(timeoutMillis: Long = 8000L, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (true) {
            ShadowLooper.idleMainLooper()
            if (condition()) return
            if (System.currentTimeMillis() >= deadline) return
            ShadowLooper.idleMainLooper(200L, TimeUnit.MILLISECONDS)
            Thread.sleep(25L)
        }
    }

    private fun assertSnackbarEquals(resId: Int) {
        val expected = app.getString(resId)
        awaitUntil { viewModel.snackbarMessage.value == expected }
        assertEquals(expected, viewModel.snackbarMessage.value)
    }

    @Test
    fun saveCurrentSearchAnnouncesSearchSaved() {
        viewModel.saveCurrentSearch()
        pump(1500)
        assertSnackbarEquals(R.string.search_saved)
    }

    @Test
    fun clearAllNotificationsAnnouncesNotificationsCleared() {
        viewModel.clearAllNotifications()
        pump(1500)
        assertSnackbarEquals(R.string.notifications_cleared)
    }

    @Test
    fun markAllNotificationsReadAnnouncesMarkedRead() {
        viewModel.markAllNotificationsRead()
        pump(1500)
        assertSnackbarEquals(R.string.notifications_marked_read)
    }

    @Test
    fun searchAlertTogglesBetweenEnabledAndDisabledLabels() {
        viewModel.saveCurrentSearch()
        awaitUntil { runBlocking { repository.savedSearches.first() }.isNotEmpty() }
        val savedSearchId = runBlocking { repository.savedSearches.first().single().id }
        viewModel.dismissSnackbar()

        viewModel.toggleSearchAlert(savedSearchId, enabled = true)
        assertSnackbarEquals(R.string.alerts_enabled)

        viewModel.dismissSnackbar()
        viewModel.toggleSearchAlert(savedSearchId, enabled = false)
        assertSnackbarEquals(R.string.alerts_disabled)
    }

    @Test
    fun errorOccurredAcceptsAFormatArgument() {
        val sentinel = "\u0000SENTINEL"
        val formatted = app.getString(R.string.error_occurred, sentinel)

        assertTrue("format argument is not applied: $formatted", formatted.contains(sentinel))
        assertEquals(formatted, app.getString(R.string.error_occurred, sentinel))
    }

    @Test
    fun repositoryFailureSurfacesLocalizedErrorOccurred() {
        viewModel.dismissSnackbar()
        db.openHelper.writableDatabase.execSQL("DROP TABLE saved_searches")

        viewModel.saveCurrentSearch()
        awaitUntil { viewModel.snackbarMessage.value != null }

        val message = viewModel.snackbarMessage.value
        assertNotNull("a failure must surface a snackbar", message)

        val sentinel = "\u0000SENTINEL"
        val expectedPrefix = app.getString(R.string.error_occurred, sentinel).substringBefore(sentinel)
        assertTrue(
            "expected message starting with '$expectedPrefix' but was '$message'",
            message!!.startsWith(expectedPrefix)
        )
        assertTrue("exception detail must be forwarded: $message", message.contains("saved_searches"))
    }
}
