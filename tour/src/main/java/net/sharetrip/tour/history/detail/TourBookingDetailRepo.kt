package net.sharetrip.tour.history.detail

import net.sharetrip.tour.network.TourHistoryAPIService

class TourBookingDetailRepo(
    private val apiService: TourHistoryAPIService
) {

    suspend fun getTourBookingDetail(token: String, bookingCode: String) =
        apiService.getTourBookingDetail(token, bookingCode)
}