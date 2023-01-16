package net.sharetrip.profile.view.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ContentViewModelFactory(
    private val faqNumber: Int,
    private val repository: ContentRepo
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java))
            return ContentViewModel(faqNumber, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
