package net.sharetrip.view.blog.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class BlogDashboardVMFactory(
    val apiService: MainApiService,
    val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogDashboardViewModel::class.java))
            return BlogDashboardViewModel(apiService, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}