package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class EventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun registrationSuccessB2c() {
        analyticsManager.track(registrationSuccessB2c)
    }

    fun registrationErrorB2c() {
        analyticsManager.track(registrationErrorB2c)
    }

    fun initialCheckoutVisa() {
        analyticsManager.track(initialCheckoutVisa)
    }

    fun initialCheckoutHoliday() {
        analyticsManager.track(initialCheckoutHoliday)
    }

    class Builder {
        private var firebaseAnalytics: FirebaseAnalytics? = null

        fun setFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Builder {
            this.firebaseAnalytics = firebaseAnalytics
            return this
        }

        fun build(): EventManager {
            val analysisManager = DataAnalyticsManager(firebaseAnalytics)
            return EventManager(analysisManager)
        }
    }

    companion object {
        private const val registrationSuccessB2c = "Registration_Success_B2c"
        private const val registrationErrorB2c = "Registration_Error_B2c"
        private const val initialCheckoutVisa = "Initial_Checkout_Visa"
        private const val initialCheckoutHoliday = "Initial_Checkout_Holiday"
    }
}