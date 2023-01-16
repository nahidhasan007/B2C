package net.sharetrip.hotel.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val hotelApiService = ServiceGenerator.createService(HotelBookingApiService::class.java)
    val hotelHistoryService = ServiceGenerator.createService(HotelHistoryApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
