package net.sharetrip.profile.view.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class NotificationDetailsVMFactory(private val sharedPref: SharedPrefsHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationDetailViewModel::class.java))
            return NotificationDetailViewModel(sharedPref) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
