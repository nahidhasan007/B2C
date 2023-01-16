package net.sharetrip.visa.history.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import java.lang.IllegalArgumentException

class VisaHistoryDetailViewModelFactory(
    private val bookingCode: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: VisaHistoryDetailRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaHistoryDetailsViewModel::class.java))
            return VisaHistoryDetailsViewModel(bookingCode, sharedPrefsHelper, repository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}