package net.sharetrip.holiday.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val holidayBookingApiService =
        ServiceGenerator.createService(HolidayBookingApiService::class.java)

    val holidayHistoryApiService =
        ServiceGenerator.createService(HolidayHistoryApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
