package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class ProfileEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun clickEditProfile() {
        analyticsManager.track(clickEditProfile)
    }

    fun openFavGuestList() {
        analyticsManager.track(openFavGuestList)
    }

    fun clickReferEarn() {
        analyticsManager.track(clickReferEarn)
    }

    fun clickProfileMoreInfo() {
        analyticsManager.track(ClickProfileMoreInfo)
    }

    fun uploadPhotoInProfile() {
        analyticsManager.track(uploadPhotoInProfile)
    }

    fun uploadPassportInProfile() {
        analyticsManager.track(uploadPassportInProfile)
    }

    fun clickOnFlightTracker() {
        analyticsManager.track(clickOnFlightTracker)
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

        fun build(): ProfileEventManager {
            val analyticsManager = DataAnalyticsManager(firebaseAnalytics)
            return ProfileEventManager(analyticsManager)
        }
    }

    companion object {
        private const val clickEditProfile = "Click_Edit_Profile"
        private const val openFavGuestList = "Open_Fav_Guest_List"
        private const val clickReferEarn = "Click_Refer_Earn"
        private const val ClickProfileMoreInfo = "Click_Profile_More_Info"
        private const val uploadPassportInProfile = "Upload_Passport_In_Profile"
        private const val uploadPhotoInProfile = "Upload_Photo_In_Profile"
        private const val uploadVisaInProfile = "Upload_Visa_In_Profile"

        private const val clickOnFlightTracker = "Click_On_Flight_Tracker"
    }
}
