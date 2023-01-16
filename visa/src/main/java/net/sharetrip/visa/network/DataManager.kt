package net.sharetrip.visa.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val visaBookingApiService = ServiceGenerator.createService(VisaBookingApiService::class.java)
    val visaHistoryApiService = ServiceGenerator.createService(VisaHistoryApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
