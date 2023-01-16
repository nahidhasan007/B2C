package net.sharetrip.flight.booking.view.flightlist

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sharetrip.base.event.Event
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.SortingType
import net.sharetrip.flight.booking.model.filter.Airline
import net.sharetrip.flight.booking.model.filter.FilterParams
import net.sharetrip.flight.booking.model.filter.FlightFilter
import net.sharetrip.flight.booking.model.filter.Refundable
import net.sharetrip.flight.booking.model.flightresponse.flights.filter.FilterData
import java.util.*

class FlightListViewModel(
    val flightSearch: FlightSearch,
    private val repository: FlightListRepository
) : BaseViewModel() {
    private val filterData = MutableStateFlow(FilterData())
    private val eventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())
    val sortingObserver = MutableLiveData<SortingType>()
    val isShowSort = ObservableBoolean(false)
    var flightCount = 0
    var filterParams = FilterParams()
    var onFilterClicked = MutableLiveData<Event<Boolean>>()
    var filter: FlightFilter? = null

    val searchId: LiveData<String>
        get() = Transformations.switchMap(repository.flightPagingSource) { obj: FlightPagingSource -> obj.searchId }

    val sessionId: LiveData<String>
        get() = Transformations.switchMap(repository.flightPagingSource) { obj: FlightPagingSource -> obj.sessionId }

    val isFirstPage: LiveData<Boolean>
        get() = Transformations.switchMap(repository.flightPagingSource) { obj: FlightPagingSource -> obj.isFirstPage }

    val filterDeal: LiveData<String>
        get() = Transformations.switchMap(repository.flightPagingSource) { obj: FlightPagingSource -> obj.filterDeal }

    val totalRecordCount: LiveData<Int>
        get() = Transformations.switchMap(repository.flightPagingSource) { obj: FlightPagingSource -> obj.totalRecord() }

    val airlinesList: LiveData<List<Airline>>
        get() = Transformations.switchMap(repository.flightPagingSource) { obj: FlightPagingSource -> obj.filterAirlinesList() }

    val flightList = filterData.flatMapLatest { filterData ->
        repository.getFlightListFromRepository(filterData)
    }.cachedIn(viewModelScope)

    private fun filterSearchData(params: FilterParams?, searchId: String?) {
        filterData.update { FilterData(searchId!!, 1, params!!) }
    }

    private fun getRefundable(): List<Refundable> {
        val refundable = Refundable("Refundable", 1)
        val nonRefundable = Refundable("Non-refundable", 0)
        return arrayListOf(refundable, nonRefundable)
    }

    fun onClickFilter() {
        eventManager.clickOnFlightFilter()
        filter = repository.flightPagingSource.value!!.flightFilter()
        filter?.refundableCustom = getRefundable()
        if (filter != null) {
            onFilterClicked.value = Event(true)
        }
    }

    fun onSortingBtnClick(view: View) {
        eventManager.clickOnFlightSort()
        if (view is AppCompatTextView) {
            when (view.id) {
                R.id.btnCheapest -> sortingObserver.value = SortingType.CHEAPEST
                R.id.btnFastest -> sortingObserver.value = SortingType.FASTEST
                R.id.btnEarliest -> sortingObserver.value = SortingType.EARLIEST
                R.id.sortOpenBtn -> isShowSort.set(!isShowSort.get())
            }
        }
    }

    fun setNumberOfFlight(numberOfFlight: Int) {
        flightCount = numberOfFlight
    }

    fun handleSortingFilter(sortingValue: String, searchId: String) {
        filterParams.sort = sortingValue
        filterSearchData(filterParams, searchId)
    }

    fun handleAirlineFilter(airlineList: List<String>, searchId: String) {
        filterData.update { FilterData() }
        filterParams.sort = sortingObserver.value.toString().lowercase(Locale.getDefault())
        filterParams.airlines = airlineList
        filterSearchData(filterParams, searchId)
    }

    fun handleFlightFilter(params: FilterParams, searchId: String) {
        filterParams = params
        filterParams.sort = sortingObserver.value.toString().lowercase(Locale.getDefault())
        filterSearchData(filterParams, searchId)
    }
}
