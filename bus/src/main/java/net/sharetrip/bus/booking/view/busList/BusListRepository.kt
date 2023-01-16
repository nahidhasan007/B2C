package net.sharetrip.bus.booking.view.busList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.GenericResponse
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.network.model.Status
import net.sharetrip.bus.booking.model.BusSearch
import net.sharetrip.bus.booking.model.CoachList
import net.sharetrip.bus.network.BusBookingApiService

class BusListRepository(private val apiService: BusBookingApiService) {

    private val _coaches = MutableLiveData<CoachList?>()
    val coaches: MutableLiveData<CoachList?>
        get() = _coaches


    val isDataLoading = MutableLiveData<Boolean>()

    private val _dataLoadingInit = MutableLiveData<Boolean>()
    val dataLoadingInit: LiveData<Boolean>
        get() = _dataLoadingInit

    private val _apiStatus = MutableLiveData<Status>()
    val apiStatus: LiveData<Status>
        get() = _apiStatus

    suspend fun getBusCoachList(busSearch: BusSearch) : GenericResponse<RestResponse<CoachList>> {
        isDataLoading.value = true
//        var data: RestResponse<CoachList>? = null
//        try {
//            data = apiService.getBus(busSearch)
//
//            if (data.response.coaches.isNotEmpty()) {
//                _coaches.value = data.response
//                _dataLoadingInit.value = true
//            } else
//                _dataLoadingInit.value = false
//
//            _apiStatus.value = Status.SUCCESS
//            _isDataLoading.value = false
//
//            return data
//        } catch (e: Exception) {
//            e.printStackTrace()
//            _dataLoadingInit.value = false
//            _isDataLoading.value = false
//            _apiStatus.value = Status.FAILED
//        }
//
//        return data
        return apiService.getBus(busSearch)
    }

}
