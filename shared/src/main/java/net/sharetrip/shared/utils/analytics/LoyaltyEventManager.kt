package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class LoyaltyEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun clickOnLoyaltyTermsAndCondition() {
        analyticsManager.track(clickOnLoyaltyTermAndCondition)
    }

    fun clickOnGold() {
        analyticsManager.track(clickOnGold)
    }

    fun clickOnPlatinum() {
        analyticsManager.track(clickOnPlatinum)
    }

    fun clickOnLoyaltyLeaderBoard() {
        analyticsManager.track(clickOnLoyaltyLeaderBoard)
    }

    fun scrollUserTripCoinHistory() {
        analyticsManager.track(scrollUserTripCoinHistory)
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

        fun build(): LoyaltyEventManager {
            val analysisManager = DataAnalyticsManager(firebaseAnalytics)
            return LoyaltyEventManager(analysisManager)
        }
    }

    companion object {
        private const val clickOnLoyaltyTermAndCondition = "Click_On_Loyalty_T&C"
        private const val clickOnGold = "Click_On_Gold"
        private const val clickOnPlatinum = "Click_On_Platinum"
        private const val clickOnLoyaltyLeaderBoard = "Click_On_Loyalty_LeaderBoard"
        private const val scrollUserTripCoinHistory = "Scroll_User_TC_History"
    }
}