package net.sharetrip.signup.view.forget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.signup.network.SingUpApiService

class ForgetPasswordVMFactory(val apiService: SingUpApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java))
            return ForgetPasswordViewModel(apiService) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}