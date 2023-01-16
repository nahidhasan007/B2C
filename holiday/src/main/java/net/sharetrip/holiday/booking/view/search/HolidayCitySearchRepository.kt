package net.sharetrip.holiday.booking.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.holiday.booking.model.HolidayCity
import net.sharetrip.holiday.network.HolidayBookingApiService

class HolidayCitySearchRepository(private val apiService: HolidayBookingApiService) {

    private val _cities = MutableLiveData<List<HolidayCity>>()
    val cities: LiveData<List<HolidayCity>>
    get() = _cities

    private val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean>
    get() = _isDataLoading

    suspend fun getPopularCityList(){
        _isDataLoading.value = true
        try {
            val data= apiService.fetchPopularCityForHoliday()
            _cities.value = data.response!!
        }catch (e: Exception){
            e.message
        }
        _isDataLoading.value = false
    }

    suspend fun getSearchedCityListForHoliday(cityKey: String){
        _isDataLoading.value = true
        try {
            val data = apiService.searchCityListForHoliday(cityKey)
            _cities.value = data.response!!
        } catch (e: Exception) {
            e.message
        }
        _isDataLoading.value = false
    }
}