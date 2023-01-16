package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class BlogEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun handleBlogCategoryEvent(tag: String) {
        when (tag) {
            "Destinations" -> {
                analyticsManager.track(clickOnBlogDestination)
            }
            "Discover Bangladesh" -> {
                analyticsManager.track(clickOnBlogDiscoverBangladesh)
            }
            "Travel Inspiration" -> {
                analyticsManager.track(clickOnBlogTravelInspiration)
            }
            "Stories" -> {
                analyticsManager.track(clickOnBlogStories)
            }
            "Tips" -> {
                analyticsManager.track(clickOnBlogTips)
            }
        }
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

        fun build(): BlogEventManager {
            val analyticsManager = DataAnalyticsManager(firebaseAnalytics)
            return BlogEventManager(analyticsManager)
        }
    }

    companion object {
        private const val clickOnBlogDestination = "Click_On_Blog_Destination"
        private const val clickOnBlogDiscoverBangladesh = "Click_On_Blog_DiscoverBangladesh"
        private const val clickOnBlogTravelInspiration = "Click_On_Blog_TravelInspiration"
        private const val clickOnBlogStories = "Click_On_Blog_Stories"
        private const val clickOnBlogTips = "Click_On_Blog_ Tips"
    }
}