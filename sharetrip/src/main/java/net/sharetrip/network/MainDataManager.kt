package net.sharetrip.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object MainDataManager {
    val mainApiService = ServiceGenerator.createService(MainApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
