package net.sharetrip.holiday.booking.view.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.holiday.booking.model.HolidayContact
import net.sharetrip.holiday.network.HolidayBookingApiService

class ClientContactRepository(private val apiService: HolidayBookingApiService) {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
    get() = _dataLoading

    private val _primaryContact = MutableLiveData<HolidayContact>()
    val primaryContact:LiveData<HolidayContact>
    get() = _primaryContact

    suspend fun getHolidayContactFromRemoteSource(token: String){
        _dataLoading.value = true
        try {
            val data  = apiService.getHolidayContactResponse(token)
            _primaryContact.value = data.response!!

        } catch (e: Exception) {
            e.message
        }
        _dataLoading.value = false
    }
}