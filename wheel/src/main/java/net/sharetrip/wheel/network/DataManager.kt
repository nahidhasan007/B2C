package net.sharetrip.wheel.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val wheelApiService = ServiceGenerator.createService(WheelApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper{
        return SharedPrefsHelper(context)
    }
}