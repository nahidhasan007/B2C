package net.sharetrip.view.blog.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.network.MainApiService

class SearchBlogVMFactory(
    val apiService: MainApiService,
    val slug: String?,
    val sharedPrefsHelper: SharedPrefsHelper
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchBlogViewModel::class.java))
            return SearchBlogViewModel(apiService, slug, sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}