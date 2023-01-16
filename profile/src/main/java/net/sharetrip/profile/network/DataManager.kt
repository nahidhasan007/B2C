package net.sharetrip.profile.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object DataManager {
    val profileApiService =
        ServiceGenerator.createService(ProfileApiService::class.java)

    fun getSharedPref(context: Context): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }
}
