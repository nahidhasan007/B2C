package net.sharetrip.tour.booking.detail

import net.sharetrip.tour.network.TourBookingAPIService

class TourDetailRepo(private val apiService: TourBookingAPIService) {

 suspend fun getTourDetail(tourCode: String) = apiService.fetchTourDetailResponse( tourCode)

 suspend fun getShareUrl(token:String, type: String, value: String) = apiService.getShareResponse(token, type, value)
}
