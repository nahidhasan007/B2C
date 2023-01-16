package net.sharetrip.tour.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object TourDataManager {

    val tourBookingAPIService = ServiceGenerator.createService(TourBookingAPIService::class.java)
    val tourHistoryAPIService = ServiceGenerator.createService(TourHistoryAPIService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
