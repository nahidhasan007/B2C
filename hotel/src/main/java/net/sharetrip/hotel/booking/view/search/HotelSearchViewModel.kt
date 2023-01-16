package net.sharetrip.hotel.booking.view.search

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.ApiCallingKey
import net.sharetrip.hotel.booking.model.HotelNamesResponse
import net.sharetrip.hotel.booking.model.HotelProperty
import net.sharetrip.hotel.network.DataManager

class HotelSearchViewModel : BaseOperationalViewModel() {
    val properties = MutableLiveData<ArrayList<HotelProperty>>()
    val goBack = MutableLiveData<Event<Boolean>>()

    init {
        fetchFrequentCityList()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.HotelNames.name -> {
                dataLoading.set(false)
                properties.value =
                    ((data.body as RestResponse<*>).response as HotelNamesResponse).cityList as ArrayList<HotelProperty>?
            }

            ApiCallingKey.TransportCity.name -> {
                dataLoading.set(false)
                properties.value =
                    ((data.body as RestResponse<*>).response as HotelNamesResponse).getBothHotelCityNamesList() as ArrayList<HotelProperty>?
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            ApiCallingKey.HotelNames.name -> {
                dataLoading.set(false)
            }

            ApiCallingKey.TransportCity.name -> {
                dataLoading.set(false)
            }
        }
    }

    private fun fetchFrequentCityList() {
        dataLoading.set(true)
        executeSuspendedCodeBlock(ApiCallingKey.HotelNames.name) {
            DataManager.hotelApiService.frequentHotelPropertyResponse()
        }
    }

    internal var onQueryTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.length >= 3) {
                    fetchTransportCityList(newText)
                }
                return false
            }
        }

    private fun fetchTransportCityList(keyword: String) {
        dataLoading.set(true)
        executeSuspendedCodeBlock(ApiCallingKey.TransportCity.name) {
            DataManager.hotelApiService.getHotelPropertyResponse(keyword)
        }
    }
}
