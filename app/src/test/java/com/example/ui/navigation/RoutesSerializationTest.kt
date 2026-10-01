package com.example.ui.navigation

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutesSerializationTest {

    private val json = Json

    private fun <T> assertRoundTrip(serializer: KSerializer<T>, value: T) {
        val encoded = json.encodeToString(serializer, value)
        val decoded = json.decodeFromString(serializer, encoded)
        assertEquals("round trip failed for $value (encoded as $encoded)", value, decoded)
    }

    @Test
    fun rootNavigationRoutesRoundTrip() {
        assertRoundTrip(RouteHome.serializer(), RouteHome)
        assertRoundTrip(RouteBookings.serializer(), RouteBookings)
        assertRoundTrip(RoutePostListing.serializer(), RoutePostListing)
        assertRoundTrip(RouteBookmarks.serializer(), RouteBookmarks)
        assertRoundTrip(RouteMessages.serializer(), RouteMessages)
        assertRoundTrip(RouteProfile.serializer(), RouteProfile)
        assertRoundTrip(RouteChat.serializer(), RouteChat)
        assertRoundTrip(RouteMapExplorer.serializer(), RouteMapExplorer)
    }

    @Test
    fun onboardingAndAuthRoutesRoundTrip() {
        assertRoundTrip(RouteOnboardingSplash.serializer(), RouteOnboardingSplash)
        assertRoundTrip(RouteOnboardingWelcome.serializer(), RouteOnboardingWelcome)
        assertRoundTrip(RouteAuthLogin.serializer(), RouteAuthLogin)
        assertRoundTrip(RouteAuthRegister.serializer(), RouteAuthRegister)
        assertRoundTrip(RouteAuthOtp.serializer(), RouteAuthOtp)
        assertRoundTrip(RouteAuthNewPassword.serializer(), RouteAuthNewPassword)
    }

    @Test
    fun profileAndFeatureRoutesRoundTrip() {
        assertRoundTrip(RouteProfileSettings.serializer(), RouteProfileSettings)
        assertRoundTrip(RouteProfileWallet.serializer(), RouteProfileWallet)
        assertRoundTrip(RouteSearchIntelligence.serializer(), RouteSearchIntelligence)
        assertRoundTrip(RouteOwnerAnalytics.serializer(), RouteOwnerAnalytics)
        assertRoundTrip(RouteWallet.serializer(), RouteWallet)
        assertRoundTrip(RoutePaymentReceipts.serializer(), RoutePaymentReceipts)
    }

    @Test
    fun routeDetailsRoundTripsWithArgument() {
        val original = RouteDetails(itemId = 7)
        val encoded = json.encodeToString(RouteDetails.serializer(), original)

        assertTrue("argument must be encoded: $encoded", encoded.contains("7"))
        assertEquals(original, json.decodeFromString(RouteDetails.serializer(), encoded))
        assertEquals(7, assertRoundTripAndGet(RouteDetails.serializer(), original).itemId)
    }

    @Test
    fun routeDetailsKeepsDefaultArgument() {
        val decoded = json.decodeFromString(RouteDetails.serializer(), "{}")
        assertEquals(RouteDetails(), decoded)
        assertEquals(0, decoded.itemId)
    }

    @Test
    fun parameterisedRoutesRoundTrip() {
        assertRoundTrip(RouteUserProfile.serializer(), RouteUserProfile(userId = 42))
        assertRoundTrip(RouteSplitPayment.serializer(), RouteSplitPayment(bookingId = 13))
        assertRoundTrip(RouteMedia.serializer(), RouteMedia(listingId = 9))
    }

    @Test
    fun detailsWithDifferentArgumentsProduceDifferentPayloads() {
        val five = json.encodeToString(RouteDetails.serializer(), RouteDetails(itemId = 5))
        val nine = json.encodeToString(RouteDetails.serializer(), RouteDetails(itemId = 9))
        assertNotEquals(five, nine)
    }

    private fun <T> assertRoundTripAndGet(serializer: KSerializer<T>, value: T): T =
        json.decodeFromString(serializer, json.encodeToString(serializer, value))
}
