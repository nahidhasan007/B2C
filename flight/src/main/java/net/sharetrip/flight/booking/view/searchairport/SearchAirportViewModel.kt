package net.sharetrip.flight.booking.view.searchairport

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import kotlinx.coroutines.launch
import net.sharetrip.flight.booking.model.ApiCallingKey
import net.sharetrip.flight.booking.view.searchairport.data.Airport

class SearchAirportViewModel(private val searchAirportRepo: SearchAirportRepo) :
    BaseOperationalViewModel() {
    val airportList = MutableLiveData<List<Airport>>()

    init {
        fetchInitData()
    }

    private fun fetchInitData() {
        viewModelScope.launch {
            searchAirportRepo.getAirports()
            fetchTopAirportListWithLocal()
        }
    }

    fun fetchTopAirportListWithLocal() {
        executeSuspendedCodeBlock(ApiCallingKey.FetchAirport.name) {
            searchAirportRepo.getAirports("top")
        }
    }

    fun getAirportListByName(searchKey : String) {
        executeSuspendedCodeBlock(ApiCallingKey.FetchAirport.name) {
            searchAirportRepo.getAirports(searchKey)
        }
    }

    fun handleSelectedItem(airport: Airport) {
        val airportData = Airport(name = airport.name, city = airport.city, iata = airport.iata)
        try {
            insert(airportData)
        } catch (_: Exception) {
        }
    }

    private fun insert(airport: Airport) = insertAirport(airport)

    private fun insertAirport(airport: Airport) {
        searchAirportRepo.insert(airport)
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        if (operationTag == ApiCallingKey.FetchAirport.name) {
            airportList.value = (data.body as RestResponse<*>).response as List<Airport>
        }
    }
}
