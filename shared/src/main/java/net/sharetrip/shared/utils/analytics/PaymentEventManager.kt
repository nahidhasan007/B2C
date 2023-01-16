package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class PaymentEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun paymentCompleteHoliday() {
        analyticsManager.track(paymentCompleteHoliday)
    }

    fun paymentFailedHoliday() {
        analyticsManager.track(paymentFailedHoliday)
    }

    fun paymentCompleteFlight() {
        analyticsManager.track(paymentCompleteFlight)
    }

    fun paymentFailedFlight() {
        analyticsManager.track(paymentFailedFlight)
    }

    fun paymentCompleteVisa() {
        analyticsManager.track(paymentCompleteVisa)
    }

    fun paymentFailedVisa() {
        analyticsManager.track(paymentFailedVisa)
    }

    fun paymentCompleteHotel() {
        analyticsManager.track(paymentCompleteHotel)
    }

    fun paymentFailedHotel() {
        analyticsManager.track(paymentFailedHotel)
    }

    class Builder {
        private var firebaseAnalytics: FirebaseAnalytics? = null

        fun setFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Builder {
            this.firebaseAnalytics = firebaseAnalytics
            return this
        }

        fun build(): PaymentEventManager {
            val analysisManager = DataAnalyticsManager(firebaseAnalytics)
            return PaymentEventManager(analysisManager)
        }
    }

    companion object {
        private const val paymentCompleteHoliday = "Payment_Complete_Holiday"
        private const val paymentFailedHoliday = "Payment_Failed_Holiday"
        private const val paymentCompleteFlight = "Payment_Complete_Flight"
        private const val paymentFailedFlight = "Payment_Failed_Flight"
        private const val paymentCompleteVisa = "Payment_Complete_Visa"
        private const val paymentFailedVisa = "Payment_Failed_Visa"
        private const val paymentCompleteHotel = "Payment_Complete_Hotel"
        private const val paymentFailedHotel = "Payment_Failed_Hotel"
    }
}