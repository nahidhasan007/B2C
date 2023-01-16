package net.sharetrip.view.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class BookingVMFactory(val sharedPrefsHelper: SharedPrefsHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java))
            return BookingViewModel(sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}