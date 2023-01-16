package net.sharetrip.bus.booking.view.busList

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.view.busList.filter.BusFilter
import net.sharetrip.bus.booking.view.seatselection.BusSeatSelectionFragment
import net.sharetrip.bus.booking.view.seatselection.BusSeatSelectionFragment.Companion.ARG_BUS_SELECTION_DATE
import net.sharetrip.bus.utils.SingleLiveEvent

class BusListViewModel(
    private val busSearch: BusSearch,
    private val repository: BusListRepository,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private val _navigateToFilter = MutableLiveData<Event<Boolean>>()
    val navigateToFilter: LiveData<Event<Boolean>>
        get() = _navigateToFilter

    val coaches = repository.coaches
    val departure = MutableLiveData<List<Departure>>()
    val numberOfBusses = ObservableInt()
    val isDataLoading = repository.isDataLoading
    val dataLoadingInit = repository.dataLoadingInit
    val sortingObserver = ObservableField(SortingType.CHEAPEST)
    val isShowSort = ObservableBoolean(false)
    val countBus: HashMap<String, Int> = HashMap()
    val busNameList = MutableLiveData<List<CompanyName>>()
    val apiStatus = repository.apiStatus
    val navigateToSeatSelection = SingleLiveEvent<Bundle>()
    var errorMassage = MutableLiveData<String>()
    var busFilter: BusFilter? = null
    var busFilterData = FilterData()
    var departureMainList = listOf<Departure>()
    var departureMainListSorted = listOf<Departure>()
    var isNoDataFound = ObservableBoolean(false)
    var tripCoinValue = 0
    var searchIdAndTripCoin = SearchIdAndTripCoin("0", "")

    init {
        searchBus()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        if (operationTag == "BUS_LIST" && (data.body as RestResponse<CoachList>).response.coaches != null) {
            val dataList = (data.body as RestResponse<CoachList>).response
            if (dataList.tripCoin != null)
                tripCoinValue = dataList.tripCoin
            busFilter = BusFilter(dataList.coaches)
            countBusNumber()
            departure.value = busFilter?.getFilteredList(busFilterData)
            numberOfBusses.set(dataList.numberOfBuses)
            busFilterData = FilterData(numberOfBuses = numberOfBusses.get())
            searchIdAndTripCoin.searchId = dataList.searchId
            searchIdAndTripCoin.redeemableTripCoin =
                if (dataList.tripCoin != null) dataList.tripCoin.toString() else "0"
        }

        repository.isDataLoading.value = false
    }

    fun onSortingBtnClick(view: View) {
        if (view is AppCompatTextView) {
            when (view.id) {
                R.id.text_view_cheapest -> {
                    sortingObserver.set(SortingType.CHEAPEST)
                    busFilterData.sortingType = SortingType.CHEAPEST
                    departure.value = busFilter?.getFilteredList(busFilterData)
                    if (departure.value?.isNotEmpty() == true)
                        departure.value?.size?.let { numberOfBusses.set(it) }
                    else
                        numberOfBusses.set(0)
                    busFilterData.numberOfBuses = numberOfBusses.get()
                }

                R.id.text_view_fastest -> {
                    sortingObserver.set(SortingType.FASTEST)
                    busFilterData.sortingType = SortingType.FASTEST
                    departure.value = busFilter?.getFilteredList(busFilterData)
                    if (departure.value?.isNotEmpty() == true)
                        departure.value?.size?.let { numberOfBusses.set(it) }
                    else
                        numberOfBusses.set(0)
                    busFilterData.numberOfBuses = numberOfBusses.get()
                }

                R.id.text_view_earliest -> {
                    sortingObserver.set(SortingType.EARLIEST)
                    busFilterData.sortingType = SortingType.EARLIEST
                    departure.value = busFilter?.getFilteredList(busFilterData)
                    if (departure.value?.isNotEmpty() == true)
                        departure.value?.size?.let { numberOfBusses.set(it) }
                    else
                        numberOfBusses.set(0)
                    busFilterData.numberOfBuses = numberOfBusses.get()
                }

                R.id.text_view_sort -> isShowSort.set(!isShowSort.get())
            }
        }
    }

    fun filterByCompanyName(companyName: CompanyName) {
        busFilterData.operators.clear()
        busFilterData.operators.add(companyName)
        departure.value = busFilter?.getFilteredList(busFilterData)

        if (departure.value?.isNotEmpty() == true)
            departure.value?.size?.let { numberOfBusses.set(it) }
        else
            numberOfBusses.set(0)

        busFilterData.numberOfBuses = numberOfBusses.get()
    }

    fun openSeatSelection(departure: Departure) {
        sharedPrefsHelper.put(PrefKey.BUS_COACH_ID, departure.id)
        val bundle = Bundle()
        bundle.putString(
            BusSeatSelectionFragment.ARG_BUS_SELECTION_DEPARTURE_TIME,
            departure.departureTime
        )
        bundle.putString(
            BusSeatSelectionFragment.ARG_BUS_SELECTION_ARRIVAL_TIME,
            departure.arrivalTime
        )
        bundle.putString(
            BusSeatSelectionFragment.ARG_BUS_SELECTION_discount,
            departure.nonNullDiscount
        )
//        bundle.putString(ARG_BUS_SELECTION_DATE, departure.date)
        bundle.putString(ARG_BUS_SELECTION_DATE, "2022-02-13") // TODO remove on api stability
        bundle.putParcelable(
            BusSeatSelectionFragment.ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN,
            searchIdAndTripCoin
        )
        navigateToSeatSelection.value = bundle
    }

    fun openFilter() {
        if (!isDataLoading.value!!)
            if (busNameList.value != null) {
                _navigateToFilter.value = Event(true)
            }
    }

    fun searchBus() {
        executeSuspendedCodeBlock("BUS_LIST") {
            repository.getBusCoachList(busSearch)
        }
    }

    fun countBusNumber() {
        coaches.value?.coaches?.forEach {
            it.setTimeDuration()
            val count: Int? = countBus[it.company.name]
            countBus[it.company.name] = if (count == null) 1 else count + 1
        }

        val busNumber = arrayListOf<CompanyName>()
        var companyId = ""

        for ((name, count) in countBus) {
            for (coach in coaches.value?.coaches!!) {
                if (coach.company.name == name) {
                    companyId = coach.company.id
                    break
                }
            }
            busNumber.add(CompanyName(name, count, companyId))
        }

        busNameList.value = busNumber
    }

    fun handleActivityResult(newBusFilterData: FilterData) {
        busFilterData = FilterData(
            newBusFilterData.filterPrice,
            newBusFilterData.operators,
            newBusFilterData.classType,
            newBusFilterData.schedule,
            newBusFilterData.sortingType,
            newBusFilterData.numberOfBuses
        )

        departure.value = busFilter?.getFilteredList(busFilterData)

        if (departure.value?.isNotEmpty() == true)
            departure.value?.size?.let { numberOfBusses.set(it) }
        else
            numberOfBusses.set(0)

        busFilterData.numberOfBuses = numberOfBusses.get()
    }
}
