package net.sharetrip.view.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class NotificationVMFactory(
    val sharedPrefsHelper: SharedPrefsHelper,
    private val repo: NotificationRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java))
            return NotificationViewModel(sharedPrefsHelper, repo) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
