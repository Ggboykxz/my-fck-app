package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.Booking
import com.example.data.model.RentalItem
import com.example.data.repository.RentalRepository
import com.example.ui.viewmodel.PaymentState
import com.example.ui.viewmodel.RentalViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
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
class RentalViewModelPaymentFlowTest {

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: RentalRepository
    private lateinit var viewModel: RentalViewModel

    private val rentalItem = RentalItem(
        id = 0,
        title = "Villa Test Paiement",
        description = "Villa de test pour le parcours de paiement",
        category = "Immobilier",
        pricePerDay = 150000,
        city = "Libreville",
        neighborhood = "La Sablière",
        ownerName = "Kofi Mensah",
        ownerPhone = "077894512",
        ownerRating = 4.9f
    )

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

    /**
     * Advances the Robolectric main looper (running every coroutine continuation and
     * [androidx.lifecycle.viewModelScope] [kotlinx.coroutines.delay]) while giving Room's
     * background query executor real time to resume the suspend calls.
     */
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

    private fun itemWithTitle(title: String): RentalItem = rentalItem.copy(id = 11, title = title)

    @Test
    fun `initiate booking walks to awaiting pin`() {
        val item = itemWithTitle("Villa Airtel")
        viewModel.initiateBooking(item, days = 3, paymentMethod = "Airtel Money", phoneInput = "070000000")
        pump(4500)
        awaitUntil { viewModel.paymentState.value is PaymentState.AwaitingPin }

        val state = viewModel.paymentState.value
        assertTrue("expected AwaitingPin but was $state", state is PaymentState.AwaitingPin)
        state as PaymentState.AwaitingPin
        assertEquals(item, state.rentalItem)
        assertEquals(3, state.days)
        assertEquals("Airtel Money", state.paymentMethod)
        assertEquals("070000000", state.phoneInput)
    }

    @Test
    fun `initiate booking keeps state idle until reset is called`() {
        viewModel.resetPaymentState()
        assertEquals(PaymentState.Idle, viewModel.paymentState.value)

        viewModel.initiateBooking(rentalItem, 1, "Moov Money", "060000000")
        pump(4500)
        viewModel.resetPaymentState()

        assertEquals(PaymentState.Idle, viewModel.paymentState.value)
    }

    @Test
    fun `confirm booking stores booking and receipt then reports success`() {
        val item = itemWithTitle("Villa Moov")
        val days = 3

        viewModel.confirmBookingPayment(
            rentalItem = item,
            days = days,
            paymentMethod = "Moov Money",
            phoneInput = "060000000",
            pinCode = "1234"
        )
        pump(4500)
        awaitUntil { viewModel.paymentState.value is PaymentState.Success }

        val state = viewModel.paymentState.value
        assertTrue("expected Success but was $state", state is PaymentState.Success)
        val reported = (state as PaymentState.Success).booking
        assertEquals("Villa Moov", reported.rentalItemTitle)
        assertEquals(days, reported.days)
        assertEquals(150000 * days, reported.totalPrice)
        assertEquals("Payé", reported.status)
        assertEquals("Moov Money", reported.paymentMethod)

        val bookings = runBlocking { repository.allBookings.first() }
        assertEquals(1, bookings.size)
        val stored: Booking = bookings[0]
        assertEquals("Villa Moov", stored.rentalItemTitle)
        assertEquals(days, stored.days)
        assertEquals(150000 * days, stored.totalPrice)
        assertEquals("Payé", stored.status)

        val receipts = runBlocking { repository.getAllPaymentReceipts().first() }
        assertEquals(1, receipts.size)
        val receipt = receipts[0]
        assertEquals(150000 * days, receipt.amount)
        assertEquals("Moov Money", receipt.paymentMethod)
        assertEquals("Kofi Mensah", receipt.payeeName)
        assertTrue("receipt number must be generated", receipt.receiptNumber.startsWith("LOC-"))
    }

    @Test
    fun `payment state transitions start from idle`() {
        assertEquals(PaymentState.Idle, viewModel.paymentState.value)

        viewModel.initiateBooking(rentalItem, 2, "Airtel Money", "070000000")
        pump(600)
        val duringProcessing = viewModel.paymentState.value
        assertTrue(
            "expected a Processing state during payment but was $duringProcessing",
            duringProcessing is PaymentState.Processing
        )
        assertTrue(
            "processing status must mention the item",
            (duringProcessing as PaymentState.Processing).status.contains(rentalItem.title)
        )
    }

    @Test
    fun `cancel booking updates status and shows localized snackbar`() {
        val item = itemWithTitle("Villa Annulée")
        viewModel.confirmBookingPayment(item, 1, "Airtel Money", "070000000", "1234")
        pump(4500)
        awaitUntil { viewModel.paymentState.value is PaymentState.Success }

        val booking = runBlocking { repository.allBookings.first().single() }
        viewModel.dismissSnackbar()

        val cancelledLabel = app.getString(R.string.booking_cancelled)
        viewModel.cancelBooking(booking.id, "Plus besoin")
        awaitUntil { viewModel.snackbarMessage.value == cancelledLabel }

        val updated = runBlocking { repository.allBookings.first().single() }
        assertEquals("Annulé", updated.status)
        assertEquals("Plus besoin", updated.cancelReason)
        assertEquals(cancelledLabel, viewModel.snackbarMessage.value)
    }
}
