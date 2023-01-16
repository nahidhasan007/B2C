package net.sharetrip.signup.network

import android.content.Context
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.ServiceGenerator

object SignUpDataManager {

    val signupApiService = ServiceGenerator.createService(SingUpApiService::class.java)

    fun signUpSharedPref(context: Context) = SharedPrefsHelper(context)

}