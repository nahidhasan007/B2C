package net.sharetrip.bus.booking.view.busList.filter

import android.content.Intent
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.booking.model.*

class BusListFilterViewModel(
     var filterDataOld: FilterData
) : BaseViewModel() {

    private val _navigateBackToList = MutableLiveData<Pair<FilterData, Boolean>>()
    val navigateBackToList: LiveData<Pair<FilterData, Boolean>>
    get() = _navigateBackToList

    var checked = ObservableBoolean(false)
    var allSelectClass = MutableLiveData<Boolean>()
    var allSelectSchedule = MutableLiveData<Boolean>()
    var clicked = MutableLiveData<Int>()
    var tempValue = mutableListOf<String>()
    var scheduleValue = mutableListOf<FilterSchedule>()
    var operatorsValue = mutableListOf<CompanyName>()
    var tempSelected = 0
    var filterPriceRangeValue = MutableLiveData<String>()
    var filterClassValue = MutableLiveData<String>()
    var filterScheduleValue = MutableLiveData<String>()
    var priceRangeView = ObservableBoolean()
    var commonFilterView = ObservableBoolean()
    var arrivalFilterView = ObservableBoolean()
    var sunImageView = ObservableBoolean()
    var maxPrice = ObservableInt()
    var minPrice = ObservableInt()
    var filterType = ObservableField<@FilterTypeEnum Int>()
    var numberOfBusses = ObservableField<String>()
    var prevChecked = false

    val dataListClass = listOf("AC", "Non AC")
    val departureScheduleList =
        listOf(
            FilterSchedule("00:00", "05:59"),
            FilterSchedule("06:00", "11:59"),
            FilterSchedule("12:00", "17:59"),
            FilterSchedule("18:00", "23:59")
        )
    val arrivalScheduleList =
        listOf(
            FilterSchedule("00:00", "05:59", "arrival"),
            FilterSchedule("06:00", "11:59", "arrival"),
            FilterSchedule("12:00", "17:59", "arrival"),
            FilterSchedule("18:00", "23:59", "arrival")
        )

    init {
        if (filterDataOld.filterPrice.from > 0 || filterDataOld.filterPrice.to > 0)
            filterPriceRangeValue.value =
                "${filterDataOld.filterPrice.from} - ${filterDataOld.filterPrice.to}"

        numberOfBusses.set(filterDataOld.numberOfBuses.toString())
        scheduleValue.clear()
        tempValue.clear()

        filterDataOld.schedule.forEach {
            scheduleValue.add(FilterSchedule(it.from, it.to, it.scheduleType))
        }

        filterDataOld.classType.forEach {
            tempValue.add(it as String)
        }

        if (scheduleValue.size > 0) {
            prevChecked = true
            if (scheduleValue.size > 1)
                filterScheduleValue.value = "${scheduleValue.size} Selected"
            else
                filterScheduleValue.value =
                    "${scheduleValue[0].from} - ${scheduleValue[0].to}"
        }

        if (tempValue.size > 0) {
            prevChecked = true
            if (tempValue.size > 1)
                filterClassValue.value = "2 Selected"
            else
                filterClassValue.value = tempValue[0]
        }
    }

    fun onPriceRangeClicked() {
        clicked.value = PRICE_RANGE
        priceRangeView.set(true)
        commonFilterView.set(false)
        arrivalFilterView.set(false)
    }

    fun onClassClicked() {
        sunImageView.set(false)
        clicked.value = CLASS_FILTER
        priceRangeView.set(false)
        commonFilterView.set(true)
        arrivalFilterView.set(false)
    }

    fun onScheduleClicked() {
        clicked.value = SCHEDULE_FILTER
        sunImageView.set(true)
        priceRangeView.set(false)
        commonFilterView.set(true)
        arrivalFilterView.set(true)
    }

    fun onOperatorClicked() {
        sunImageView.set(false)
        clicked.value = OPERATOR_FILTER
        priceRangeView.set(false)
        commonFilterView.set(false)
        arrivalFilterView.set(true)
    }

    fun onResetClicked() {
        filterClassValue.value = "Any"
        filterPriceRangeValue.value = "Any"
        filterScheduleValue.value = "Any"
        minPrice.set(0)
        maxPrice.set(0)
        tempValue.clear()
        scheduleValue.clear()
        tempSelected = 0
    }

    fun onClickApplyFilter() {
        val intent = Intent()

        if (filterClassValue.value == "Any" && filterPriceRangeValue.value == "Any" && filterScheduleValue.value == "Any") {
            filterDataOld.filterPrice = FilterPrice(0, 0)
            filterDataOld.classType.clear()
            filterDataOld.schedule.clear()
        }

        intent.putExtra(ARG_BUS_FILTER_DATA, filterDataOld)
        _navigateBackToList.value = Pair(filterDataOld, true)
    }

    companion object {
        const val ARG_BUS_FILTER_DATA = "ARG_BUS_FILTER_DATA"
    }
}
