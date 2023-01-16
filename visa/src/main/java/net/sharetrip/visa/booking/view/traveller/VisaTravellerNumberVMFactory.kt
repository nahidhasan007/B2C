package net.sharetrip.visa.booking.view.traveller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VisaTravellerNumberVMFactory(
    private val numberOfAdult: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VisaTravellerNumberViewModel(
            numberOfAdult
        ) as T
    }
}