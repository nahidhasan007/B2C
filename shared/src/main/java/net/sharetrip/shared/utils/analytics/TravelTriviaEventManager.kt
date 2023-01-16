package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class TravelTriviaEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun clickDailyQuiz() {
        analyticsManager.track(clickDailyQuiz)
    }

    fun clickStartQuiz() {
        analyticsManager.track(clickStartQuiz)
    }

    fun clickDailyQuizLeaderBoard() {
        analyticsManager.track(clickDailyQuizLeaderBoard)
    }

    fun clickDailyQuizTermsCondition() {
        analyticsManager.track(clickDailyQuizTermsCondition)
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

        fun build(): TravelTriviaEventManager {
            val analyticsManager = DataAnalyticsManager(firebaseAnalytics)
            return TravelTriviaEventManager(analyticsManager)
        }
    }

    companion object {
        private const val clickDailyQuiz = "Click_Daily_Quiz"
        private const val clickStartQuiz = "Click_Start_Quiz"
        private const val clickDailyQuizLeaderBoard = "Click_DailyQuiz_LeaderBoard"
        private const val clickDailyQuizTermsCondition = "Click_DailyQuiz_Terms&Condition"
    }
}
