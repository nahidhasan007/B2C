package net.sharetrip.bus.booking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterData(
    var filterPrice: FilterPrice = FilterPrice(0, 0),
    val operators: ArrayList<CompanyName> = arrayListOf(),
    var classType: ArrayList<String> = arrayListOf(),
    var schedule: ArrayList<FilterSchedule> = arrayListOf(),
    var sortingType: SortingType = SortingType.CHEAPEST,
    var numberOfBuses: Int =0
) : Parcelable
