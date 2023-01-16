package net.sharetrip.tour.booking.search

import net.sharetrip.tour.network.TourBookingAPIService

class TourCitySearchRepo(private val apiService: TourBookingAPIService) {

    suspend fun getPopularCities() = apiService.fetchPopularCityForTour()

    suspend fun getCityListByKey(cityKey: String) = apiService.searchCityListForTour(cityKey)
}
