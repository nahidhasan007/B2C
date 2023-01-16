package net.sharetrip.holiday.booking.view.main

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.event.Event
import net.sharetrip.shared.utils.DateUtil
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.booking.model.Destination

class HolidayHeaderViewModel(private val repository: HolidayMainRepository) : BaseViewModel() {

    private val destinationList = ArrayList<Destination>()

    private val _navigateToHolidayList = MutableLiveData<Pair<List<String>, Boolean>>()
    val navigateToHolidayList: LiveData<Pair<List<String>, Boolean>>
        get() = _navigateToHolidayList

    private val _navigateToSearchedHolidayList = MutableLiveData<Event<Boolean>>()
    val navigateToSearchedHolidayList: LiveData<Event<Boolean>>
        get() = _navigateToSearchedHolidayList

    private val _navigateToTravelData = MutableLiveData<Event<Boolean>>()
    val navigateToTravelDate: LiveData<Event<Boolean>>
        get() = _navigateToTravelData

    private val _navigateToCitySearch = MutableLiveData<Pair<Boolean, Int>>()
    val navigateToCitySearch: LiveData<Pair<Boolean, Int>>
        get() = _navigateToCitySearch

    var cityPosition = 0
    val liveCities = repository.liveCities
    val showToast = repository.showToastMessage
    val liveDestination = MutableLiveData<ArrayList<Destination>>()
    val isVisible = ObservableBoolean(true)
    var dateOfTravelling = ObservableField<String>()
    var cityCodeList: String? = null
    var cityNameList: String? = null

    init {
        val entry = DateUtil.parseDisplayDateFormatFromApiDateFormat(DateUtil.getApiDateFormat(3))
        dateOfTravelling.set(entry)
        fetchPopularTourCity()
    }

    fun resetNavigationFlags() {
        _navigateToHolidayList.value = Pair(listOf(), false)
        _navigateToCitySearch.value = Pair(false, cityPosition)
    }

    private fun fetchPopularTourCity() {
        viewModelScope.launch {
            repository.getPopularCityForHoliday()
        }
    }

    fun initDestinationAdapter() {
        if (destinationList.isEmpty()) {
            destinationList.add(Destination(cityCode = "1", cityName = "Select a city"))
            liveDestination.value = destinationList
        }
    }

    fun navigateToCityHolidayList(position: Int) {
        liveCities.value?.let {
            val cityCode = it[position].code
            val cityName = it[position].name
            val values = Pair(listOf(cityCode, cityName), true)
            _navigateToHolidayList.value = values
        }
    }

    fun onClickSearchButton() {
        cityNameList = destinationList.filter { it.cityCode != "1" }
            .map { it.cityName }
            .distinct().toString()
            .replace("[", "")
            .replace("]", "")

        cityCodeList = destinationList.filter { it.cityCode != "1" }
            .map { "\"" + it.cityCode + "\"" }
            .distinct().toString()
            .replace("[", "")
            .replace("]", "")

        if (cityCodeList!!.isNotEmpty() && cityNameList!!.isNotEmpty()) {
            _navigateToSearchedHolidayList.value = Event(true)
        } else {
            showToast.value = Event("Select City")
        }
    }

    fun onAddDestination() {
        if (destinationList.size < 4) {
            destinationList.add(Destination(cityCode = "1", cityName = "Select a city"))
            liveDestination.value = destinationList
        }
        isVisible.set(destinationList.size != 4)
    }

    fun onUpdateDestinationName(cityCode: String, cityName: String, position: Int) {
        if (position in 0 until destinationList.size) {
            destinationList[position].cityName = cityName
            destinationList[position].cityCode = cityCode
            liveDestination.value = destinationList
        }
    }

    fun onUpdateTravelDate(travelDate: Long) {
        val entry = DateUtil.parseDisplayDateFormatFromMilliSecond(travelDate)
        dateOfTravelling.set(entry)
    }

    fun onRemovedDestinationItem(position: Int) {
        destinationList.removeAt(position)
        liveDestination.value = destinationList
        isVisible.set(destinationList.size != 4)
    }

    fun onDestinationFieldClicked(position: Int) {
        cityPosition = position
        _navigateToCitySearch.value = Pair(true, position)
    }

    fun onClickTravelDate() {
        _navigateToTravelData.value = Event(true)
    }
}
