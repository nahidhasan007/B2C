package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsProvider {

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun getFirebaseAnalytics(): FirebaseAnalytics {
        if (firebaseAnalytics == null) firebaseAnalytics = Firebase.analytics
        return firebaseAnalytics!!
    }

    fun blogEventManager(firebaseAnalytics: FirebaseAnalytics) =
        BlogEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun busEventManager(firebaseAnalytics: FirebaseAnalytics) =
        BusEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun eventManager(firebaseAnalytics: FirebaseAnalytics) =
        EventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun flightEventManager(firebaseAnalytics: FirebaseAnalytics) =
        FlightEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun gamificationEventManager(firebaseAnalytics: FirebaseAnalytics) =
        GamificationEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun homePageEventManager(firebaseAnalytics: FirebaseAnalytics) =
        HomePageEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun hotelEventManager(firebaseAnalytics: FirebaseAnalytics) =
        HotelEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun loyaltyEventManager(firebaseAnalytics: FirebaseAnalytics) = LoyaltyEventManager.Builder()
        .setFirebaseAnalytics(firebaseAnalytics).build()

    fun paymentEventManager(firebaseAnalytics: FirebaseAnalytics) =
        PaymentEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()

    fun profileEventManager(firebaseAnalytics: FirebaseAnalytics) = ProfileEventManager.Builder()
        .setFirebaseAnalytics(firebaseAnalytics).build()

    fun travelTriviaEventManager(firebaseAnalytics: FirebaseAnalytics) =
        TravelTriviaEventManager.Builder().setFirebaseAnalytics(firebaseAnalytics).build()
}