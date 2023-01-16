package net.sharetrip.signup.view.login.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.signup.network.SingUpApiService

class SocialLoginVMFactory(
    val apiService: SingUpApiService,
    val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SocialLoginViewModel::class.java))
            return SocialLoginViewModel(apiService, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}