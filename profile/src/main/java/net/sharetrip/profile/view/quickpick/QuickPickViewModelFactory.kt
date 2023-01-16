package net.sharetrip.profile.view.quickpick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class QuickPickViewModelFactory(
    private val repository: QuickPickRepository,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuickPickViewModel::class.java))
            return QuickPickViewModel(repository, sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
