package net.sharetrip.flight.booking.model

import net.sharetrip.flight.booking.model.flightresponse.flights.flight.Flight
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown
import net.sharetrip.flight.booking.model.flightresponse.flights.segment.Segment

class FlightDetailsResponse(
    val advanceIncomeTax: Double,
    val attachment: Boolean,
    val attachmentMessage: String,
    val discount: Double,
    val discountAmount: Double,
    val domestic: Boolean,
    val flight: List<Flight>,
    val gatewayCurrency: String,
    val getWay: Any?,
    val instantPurchase: Boolean,
    val originPrice: Double,
    val points: PointsInfo,
    val price: Double,
    val priceBreakdown: PriceBreakdown,
    val promotionalCoupon: List<PromotionalCoupon>,
    val refundable: Int,
    val searchId: String,
    val seatsLeft: Int,
    val segments: List<Segment>,
    val sequenceCode: String,
    val sessionId: String,
    val weight: String
)
