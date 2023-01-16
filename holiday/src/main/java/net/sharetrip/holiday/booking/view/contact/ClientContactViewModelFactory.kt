package net.sharetrip.holiday.booking.view.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.holiday.booking.model.HolidayParam

class ClientContactViewModelFactory(
    private val holidayParam: HolidayParam,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val clientContactRepository: ClientContactRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientContactViewModel::class.java))
            return ClientContactViewModel(holidayParam, sharedPrefsHelper, clientContactRepository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}