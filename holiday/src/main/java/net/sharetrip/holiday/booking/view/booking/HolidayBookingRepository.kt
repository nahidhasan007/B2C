package net.sharetrip.holiday.booking.view.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.Status
import net.sharetrip.holiday.network.HolidayBookingApiService
import java.text.NumberFormat
import java.util.*

class HolidayBookingRepository(private val apiService: HolidayBookingApiService) {

    private val _tripCoin = MutableLiveData<String>()
    val tripCoin :LiveData<String>
    get() = _tripCoin

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
    get() = _apiStatus

    suspend fun getUserProfileFromRemoteSource(token: String){
            _apiStatus.value = Status.RUNNING
            try {
                val data = apiService.getUserInformation(token)
                _tripCoin.value = NumberFormat.getNumberInstance(Locale.US).format(data.response.totalPoints.toLong())
                _apiStatus.value = Status.SUCCESS
            } catch (e: Exception) {
                e.message
                _tripCoin.value = "0"
                _apiStatus.value = Status.FAILED
            }
    }
}