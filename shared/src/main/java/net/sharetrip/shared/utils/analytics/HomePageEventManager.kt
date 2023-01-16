package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class HomePageEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun clickOnFlight() {
        analyticsManager.track(clickOnFlight)
    }

    fun clickOnBus() {
        analyticsManager.track(clickOnBus)
    }

    fun clickOnHotel() {
        analyticsManager.track(clickOnHotel)
    }

    fun clickOnHoliday() {
        analyticsManager.track(clickOnHoliday)
    }

    fun openTreasureBox() {
        analyticsManager.track(openTreasureBox)
    }

    fun clickOnSpin2Win() {
        analyticsManager.track(clickSpin2Win)
    }

    fun selectHolidayPCityFromHomeScreen() {
        analyticsManager.track(selectHolidayPCityFromHomeScreen)
    }

    fun selectHolidayFromHomeScreen() {
        analyticsManager.track(selectHolidayFromHomeScreen)
    }

    fun clickOnHomeTab() {
        analyticsManager.track(clickOnHomeTab)
    }

    fun clickOnBookingTab() {
        analyticsManager.track(clickOnBookingTab)
    }

    fun clickOnNotificationTab() {
        analyticsManager.track(clickOnNotificationTab)
    }

    fun clickOnLoyaltyTab() {
        analyticsManager.track(clickOnLoyaltyTab)
    }

    fun clickOnProfileTab() {
        analyticsManager.track(clickOnProfileTab)
    }

    fun clickOnBlogTab() {
        analyticsManager.track(clickOnBlogTab)
    }

    fun clickOnToolbarProfileLink() {
        analyticsManager.track(clickOnToolbarProfileLink)
    }

    fun clickOnOpenNotification() {
        analyticsManager.track(clickOnOpenNotification)
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

        fun build(): HomePageEventManager {
            val analysisManager = DataAnalyticsManager(firebaseAnalytics)
            return HomePageEventManager(analysisManager)
        }
    }

    companion object {
        private const val openTreasureBox = "Open_Treasure_Box"
        private const val clickSpin2Win = "Click_Spin2Win"
        private const val clickOnFlight = "Click_On_Flights"
        private const val clickOnHotel = "Click_On_Hotels"
        private const val clickOnBus = "Click_On_Bus"
        private const val clickOnHoliday = "Click_On_Holidays"
        private const val selectHolidayPCityFromHomeScreen = "Select_HPCity_In_HS"
        private const val selectHolidayFromHomeScreen = "Select_Holiday_In_HS"
        private const val clickOnHomeTab = "Click_On_Home_Tab"
        private const val clickOnBookingTab = "Click_On_Booking_Tab"
        private const val clickOnNotificationTab = "Click_On_Notification_Tab"
        private const val clickOnLoyaltyTab = "Click_On_Loyalty_Tab"
        private const val clickOnProfileTab = "Click_On_Profile_Tab"
        private const val clickOnBlogTab = "Click_On_Blog"
        private const val clickOnToolbarProfileLink = "Click_On_Toolbar_Profile_Link"
        private const val clickOnOpenNotification = "Click_On_Open_Notification"
    }
}
