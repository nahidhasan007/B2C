package net.sharetrip.holiday.booking.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.booking.model.HolidayCity

class HolidayCitySearchViewModel(
    val rootPosition: Int,
    private val repository: HolidayCitySearchRepository
) : BaseViewModel() {

    val isDataLoading : LiveData<Boolean> = repository.isDataLoading
    val cities: LiveData<List<HolidayCity>> = repository.cities

    init {
        fetchPopularCityList()
    }

     private fun fetchPopularCityList() {
        viewModelScope.launch {
            repository.getPopularCityList()
        }
    }

     fun fetchTourCityList(cityKey: String) {
        viewModelScope.launch {
            repository.getSearchedCityListForHoliday(cityKey)
        }
    }
}
