package net.sharetrip.visa.history.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class VisaHistoryListViewModelFactory(
    private val repository: VisaHistoryListRepo,
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaHistoryListViewModel::class.java))
            return VisaHistoryListViewModel(repository, sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
