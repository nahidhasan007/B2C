package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class FlightEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun searchOneWayFlight() {
        analyticsManager.track(searchOneWayFlight)
    }

    fun searchReturnFlight() {
        analyticsManager.track(searchReturnFlight)
    }

    fun searchMultiCity() {
        analyticsManager.track(searchMultiCity)
    }

    fun clickOnBestDeals() {
        analyticsManager.track(clickOnBestDeals)
    }

    fun clickOnPreferredAirline() {
        analyticsManager.track(clickOnPreferredAirline)
    }

    fun clickOnFlightFilter() {
        analyticsManager.track(clickOnFlightFilter)
    }

    fun clickOnFlightSort() {
        analyticsManager.track(clickOnFlightSort)
    }

    fun selectBusinessClass() {
        analyticsManager.track(selectBusinessClass)
    }

    fun clickOnFlightDetails() {
        analyticsManager.track(clickOnFlightDetails)
    }

    fun clickOnAirFareRules() {
        analyticsManager.track(clickOnAirFareRules)
    }

    fun clickOnFlightBaggage() {
        analyticsManager.track(clickOnFlightBaggage)
    }

    fun clickOnTicketRefundPolicy() {
        analyticsManager.track(clickOnTicketRefundPolicy)
    }

    fun clickOnRedeemTripCoinInFlight() {
        analyticsManager.track(clickOnRedeemTC_InFlight)
    }

    fun clickOnBookNow() {
        analyticsManager.track(clickOnBookNow)
    }

    fun selectPassengerFromQuickPick() {
        analyticsManager.track(selectPassengerFromQuickPick)
    }

    fun uploadPassportCopy() {
        analyticsManager.track(uploadPassportCopy)
    }

    fun uploadVisaCopy() {
        analyticsManager.track(uploadVisaCopy)
    }

    fun flightHistory() {
        analyticsManager.track(flightHistory)
    }

    fun flightCancellationPolicy() {
        analyticsManager.track(F_Cancellation_Policy)
    }

    fun initialCheckoutFlight() {
        analyticsManager.track(initialCheckoutFlight)
    }

    fun clickRefundableB2c() {
        analyticsManager.track(clickRefundableB2c)
    }

    class Builder {
        //private var mixPanelAPI: MixpanelAPI? = null
        private var firebaseAnalytics: FirebaseAnalytics? = null

        /*fun setMixPanelAPI(mixPanelAPI: MixpanelAPI): Builder {
            this.mixPanelAPI = mixPanelAPI
            return this
        }*/
        fun setFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Builder {
            this.firebaseAnalytics = firebaseAnalytics
            return this
        }

        fun build(): FlightEventManager {
            val analyticsManager = DataAnalyticsManager(firebaseAnalytics)
            return FlightEventManager(analyticsManager)
        }
    }

    companion object {
        private const val searchOneWayFlight = "Search_OneWay_Flight"
        private const val searchReturnFlight = "Search_Return_Flight"
        private const val searchMultiCity = "Search_MultiCity_Flight"
        private const val clickOnBestDeals = "Click_On_Flight_Best_Deals"
        private const val clickOnPreferredAirline = "Click_On_Preferred_Airline"
        private const val clickOnFlightFilter = "Click_On_Flight_Filter"
        private const val clickOnFlightSort = "Click_On_Flight_Sort"
        private const val selectBusinessClass = "Select_Business_Class"
        private const val clickOnFlightDetails = "Click_On_Flight_Details"

        private const val clickOnAirFareRules = "Click_On_Air_Fare_Rules"
        private const val clickOnFlightBaggage = "Click_On_Flight_Baggage"

        private const val clickOnTicketRefundPolicy = "Click_On_Flight_Ticket_Refund_Policy"
        private const val clickOnRedeemTC_InFlight = "Click_On_Redeem_TC_In_Flight"
        private const val clickOnBookNow = "Click_On_Flight_BookNow"
        private const val selectPassengerFromQuickPick = "Select_Passenger_from_QuickPick_In_Flight"
        private const val uploadPassportCopy = "Upload_Passport_Copy_In_Flight"
        private const val uploadVisaCopy = "Upload_Visa_Copy_In_Flight"
        private const val flightHistory = "Flight_History"
        private const val F_Cancellation_Policy = "Flight_Cancellation_Policy"
        private const val initialCheckoutFlight = "Initial_Checkout_Flight"
        private const val clickRefundableB2c = "Click_Refundable_B2c"
    }
}
