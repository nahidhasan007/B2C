package net.sharetrip.flight.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val flightApiService = ServiceGenerator.createService(FlightBookingApiService::class.java)
    val flightHistoryApiService = ServiceGenerator.createService(FlightHistoryApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
