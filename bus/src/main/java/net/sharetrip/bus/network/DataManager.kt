package net.sharetrip.bus.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val busBookingApiService = ServiceGenerator.createService(BusBookingApiService::class.java)
    val busHistoryApiService = ServiceGenerator.createService(BusHistoryApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
