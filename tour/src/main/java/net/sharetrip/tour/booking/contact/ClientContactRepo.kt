package net.sharetrip.tour.booking.contact

import net.sharetrip.tour.network.TourBookingAPIService

class ClientContactRepo(private val apiService: TourBookingAPIService) {

     suspend fun getContactInfo(token: String) = apiService.getTourContactResponse(token)
}
