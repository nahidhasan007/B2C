package net.sharetrip.tour.booking.summary

import net.sharetrip.shared.model.coupon.CouponRequest
import net.sharetrip.tour.model.TourBookingParam
import net.sharetrip.tour.network.TourBookingAPIService

class TourSummaryRepo(private val apiService: TourBookingAPIService) {

    suspend fun validateCoupon(token: String, couponRequest: CouponRequest) =
        apiService.getValidateCoupon(token, couponRequest)

    suspend fun getPaymentGateways(service: String) = apiService.fetchPaymentGatewayRX(service)

    suspend fun bookTour(token: String, tourBookingParam: TourBookingParam) =
        apiService.fetchTourBookingResponse(token, tourBookingParam)
}
