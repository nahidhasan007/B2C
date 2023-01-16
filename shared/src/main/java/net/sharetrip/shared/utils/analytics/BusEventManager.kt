package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class BusEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun selectPassengerFromQuickPick() {
        analyticsManager.track(selectBusPassengerFromQuickPick)
    }

    class Builder {
        private var firebaseAnalytics: FirebaseAnalytics? = null

        fun setFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Builder {
            this.firebaseAnalytics = firebaseAnalytics
            return this
        }

        fun build(): BusEventManager {
            val analyticsManager = DataAnalyticsManager(firebaseAnalytics)
            return BusEventManager(analyticsManager)
        }
    }
    companion object{

        private const val selectBusPassengerFromQuickPick = "Select_Passenger_from_QuickPick_In_Bus"

    }
}