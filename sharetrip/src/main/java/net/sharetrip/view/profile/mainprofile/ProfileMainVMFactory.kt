package net.sharetrip.view.profile.mainprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class ProfileMainVMFactory(val sharedPrefsHelper: SharedPrefsHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileMainViewModel::class.java))
            return ProfileMainViewModel(sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}