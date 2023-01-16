package net.sharetrip.holiday.booking.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.shared.model.ResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sharetrip.holiday.booking.model.HolidayDetailResponse
import net.sharetrip.holiday.booking.model.HolidayOffer
import net.sharetrip.holiday.network.HolidayBookingApiService

class HolidayDetailsRepository(private val apiService: HolidayBookingApiService) {

    private var _holidayDetails = MutableLiveData<HolidayDetailResponse>()
    val holidayDetails :LiveData<HolidayDetailResponse>
    get() = _holidayDetails

    private var _dataLoading = MutableLiveData(false)
    val dataLoading:LiveData<Boolean>
    get() = _dataLoading

    private var _responseStatus = MutableLiveData<ResponseStatus>()
    val responseStatus :LiveData<ResponseStatus>
    get() = _responseStatus

    private var _holidayOffers = MutableLiveData<ArrayList<HolidayOffer>>()
    val holidayOffers : LiveData<ArrayList<HolidayOffer>>
    get() = _holidayOffers

    private var _imageLink = MutableLiveData<List<String>>()
    val imageLink:LiveData<List<String>>
    get() = _imageLink

    fun updateResponseStatus(status: ResponseStatus){
        _responseStatus.value = status
    }

    suspend fun getHolidayDetailsFromRemoteSource(productCode: String) {
        _dataLoading.value = true
        _responseStatus.value = ResponseStatus.PROGRESS
        withContext(Dispatchers.IO){
            try {
                val data = apiService.fetchHolidayDetailResponse(productCode)
                val holidayResponse: HolidayDetailResponse = data.response
                _holidayDetails.postValue(holidayResponse)
                _holidayOffers.postValue(holidayResponse.offers.filter {
                    it.child3To6 != 0 || it.child7To12 != 0 ||
                            it.doublePerPax != 0 || it.infant != 0 ||
                            it.quadPerPax != 0 || it.singlePerPax != 0 ||
                            it.triplePerPax != 0 || it.twinPerPax != 0
                } as ArrayList<HolidayOffer>)
                if (holidayResponse.images!!.isNotEmpty()) {
                    _imageLink.postValue(holidayResponse.images!!.map { it.srcLarge })
                }
                _responseStatus.postValue(ResponseStatus.SUCCESS)
            }
            catch (e: Exception) {
                e.message
                _responseStatus.postValue(ResponseStatus.SERVER_ERROR)
            }
        }
        _dataLoading.value = false
    }
}
