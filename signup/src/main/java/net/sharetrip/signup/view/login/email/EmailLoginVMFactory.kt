package net.sharetrip.signup.view.login.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.signup.network.SingUpApiService

class EmailLoginVMFactory(
    val apiService: SingUpApiService,
    val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmailLoginViewModel::class.java))
            return EmailLoginViewModel(apiService, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}