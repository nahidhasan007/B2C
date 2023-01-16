package net.sharetrip.view.blog.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class BlogDetailsVMFactory(
    val apiService: MainApiService,
    val slug: String?,
    val sharedPrefsHelper: SharedPrefsHelper
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogDetailsViewModel::class.java))
            return BlogDetailsViewModel(apiService, slug, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}