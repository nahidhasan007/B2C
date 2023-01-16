package net.sharetrip.shared.utils.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class DataAnalyticsManager(private var firebaseAnalytics: FirebaseAnalytics? = null) {

    fun track(eventName: String, properties: Map<String, String>? = null) {

        //mixPanelLogEvent(eventName, properties)

       // fireBaseLogEvent(eventName, properties)
    }

    /*private fun mixPanelLogEvent(eventName: String, properties: Map<String, String>? = null) {
        if (properties == null) {
            mixPanelAPI?.track(eventName)
        } else {
            val jsonObject = JSONObject()
            for ((key, value) in properties) {
                jsonObject.put(key, value)
            }
            mixPanelAPI?.track(eventName, jsonObject)
        }
    }*/

    private fun fireBaseLogEvent(eventName: String, properties: Map<String, String>? = null) {
        val bundle = Bundle()
        if (properties != null)
            for ((key, value) in properties) {
                bundle.putString(key, value)
            }
        firebaseAnalytics?.logEvent(eventName, bundle)
    }
}
