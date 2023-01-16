package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class GamificationEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun playSpin2Win() {
        analyticsManager.track(playSpin2Win)
    }

    fun clickSpin2WinRules() {
        analyticsManager.track(clickSpin2WinRules)
    }

    fun clickSpin2WinRewards() {
        analyticsManager.track(clickSpin2WinRewards)
    }

    fun clickSpin2WinLeaderBoard() {
        analyticsManager.track(clickSpin2WinLeaderBoard)
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

        fun build(): GamificationEventManager {
            val analysisManager = DataAnalyticsManager(firebaseAnalytics)
            return GamificationEventManager(analysisManager)
        }
    }

    companion object {
        private const val playSpin2Win = "Play_Spin2Win"
        private const val clickSpin2WinRules = "Click_Spin2Win_Rules"
        private const val clickSpin2WinRewards = "Click_Spin2Win_Rewards"
        private const val clickSpin2WinLeaderBoard = "Click_Spin2Win_LeaderBoard"
    }
}