package net.sharetrip.flight.booking.view.nationality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class NationalityViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NationalityViewModel::class.java))
            return NationalityViewModel(sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
