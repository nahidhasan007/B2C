package net.sharetrip.bus.booking.view.busList.filter

import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.bus.booking.model.*

class BusFilter(val departure: List<Departure>) {

    fun filterByName(name: String): List<Departure> {
        return departure.filter { it.company.name == name }
    }

    fun getSortedList(
        filteredDeparture: List<Departure>,
        sortingType: SortingType
    ): List<Departure> {
        return when (sortingType) {
            SortingType.CHEAPEST -> {
                val all = filteredDeparture.sortedWith(compareBy { it.fare.toDouble() })
                val invalid = all.filter { !it.isValidDuration }
                val valid = all.filter { it.isValidDuration } as MutableList<Departure>
                valid.addAll(invalid)
                valid
            }
            SortingType.EARLIEST -> {
                filteredDeparture.map {
                    it.departureMilliSeconds =
                        DateUtil.parseStringToMillisecond(it.departureTime, "hh:mm a")
                }
                val all = filteredDeparture.sortedWith(compareBy { it.departureMilliSeconds })
                val invalid = all.filter { !it.isValidDuration }
                val valid = all.filter { it.isValidDuration } as MutableList<Departure>
                valid.addAll(invalid)
                valid
            }
            SortingType.FASTEST -> {
                val all =
                    filteredDeparture.sortedWith(compareBy { (it.duration?.hour.toString() + "." + it.duration?.minute.toString()).toFloat() })
                val invalid = all.filter { !it.isValidDuration }
                val valid = all.filter { it.isValidDuration } as MutableList<Departure>
                valid.addAll(invalid)
                valid
            }
            else -> {
                val all = departure.sortedWith(compareBy { it.fare.toDouble() })
                val invalid = all.filter { !it.isValidDuration }
                val valid = all.filter { it.isValidDuration } as MutableList<Departure>
                valid.addAll(invalid)
                valid
            }
        }
    }

    fun getFilteredList(filterData: FilterData): List<Departure> {
        var filteredList: List<Departure> = listOf()
        var isFiltered = false

        if (filterData.filterPrice.from != 0 && filterData.filterPrice.to != 0) {
            filteredList =
                departure.filter { it.fare.toDouble() > filterData.filterPrice.from && it.fare.toDouble() < filterData.filterPrice.to }
            isFiltered = true
        }

        if (!filterData.classType.isNullOrEmpty()) {
            filteredList = if (filteredList.isNotEmpty()) {
                filterByClass(filteredList, filterData.classType)
            } else {
                filterByClass(departure, filterData.classType)
            }
            isFiltered = true
        }

        if (!filterData.schedule.isNullOrEmpty()) {
            filteredList = if (filteredList.isNotEmpty()) {
                filterBySchedule(filteredList, filterData.schedule)
            } else {
                filterBySchedule(departure, filterData.schedule)
            }
            isFiltered = true
        }

        if (!filterData.operators.isNullOrEmpty()) {
            filteredList = if (filteredList.isNotEmpty()) {
                filterByOperators(filteredList, filterData.operators)
            } else {
                filterByOperators(departure, filterData.operators)
            }
            isFiltered = true
        }
        return if (isFiltered) {
            getSortedList(filteredList, filterData.sortingType)
        } else {
            getSortedList(departure, filterData.sortingType)
        }
    }

    fun filterByClass(departureList: List<Departure>, classList: List<String>): List<Departure> {
        val list = arrayListOf<Departure>()
        classList.forEach {
            departureList.forEach { departure ->
                if (it.trim().equals(departure.coachType.trim(), ignoreCase = true))
                    list.add(departure)
            }
        }
        return list
    }

    fun filterBySchedule(
        departureList: List<Departure>,
        scheduleList: List<FilterSchedule>
    ): ArrayList<Departure> {
        var list = arrayListOf<Departure>()
        scheduleList.forEach {
            departureList.forEach { departure ->
                if (it.scheduleType == "departure" && departure.departureTime != null && DateUtil.isTimeBetweenTwoTimes(
                        it.from,
                        it.to,
                        departure.departureTime!!
                    )
                ) {
                    list.add(departure)
                } else if (it.scheduleType == "arrival" && departure.arrivalTime != null && DateUtil.isTimeBetweenTwoTimes(
                        it.from,
                        it.to,
                        departure.arrivalTime!!
                    )
                ) {
                    list.add(departure)
                }
            }
        }
        return list.distinctBy { it.id } as ArrayList<Departure>
    }

    fun filterByOperators(
        departureList: List<Departure>,
        operatorList: List<CompanyName>
    ): ArrayList<Departure> {
        val list = arrayListOf<Departure>()
        operatorList.forEach { companyName ->
            departureList.forEach { departure ->
                if (companyName.companyId == departure.company.id
                ) {
                    list.add(departure)
                }
            }
        }
        return list
    }
}
