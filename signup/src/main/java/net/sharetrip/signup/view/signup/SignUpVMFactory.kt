package net.sharetrip.signup.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.signup.network.SingUpApiService

class SignUpVMFactory(val apiService: SingUpApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java))
            return SignUpViewModel(apiService) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}