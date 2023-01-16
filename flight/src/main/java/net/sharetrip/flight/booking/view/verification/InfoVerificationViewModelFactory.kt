package net.sharetrip.flight.booking.view.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.flight.booking.model.ItemTraveler

class InfoVerificationViewModelFactory(val travellers: MutableList<ItemTraveler>,val sharedPrefsHelper: SharedPrefsHelper) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoVerificationViewModel::class.java))
            return InfoVerificationViewModel(travellers,sharedPrefsHelper) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
