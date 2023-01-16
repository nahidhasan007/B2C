package net.sharetrip.tour.history.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import java.lang.IllegalArgumentException

class TourHistoryListVMF(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repo: TourHistoryListRepo
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourHistoryListViewModel::class.java))
            return TourHistoryListViewModel(sharedPrefsHelper, repo) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
