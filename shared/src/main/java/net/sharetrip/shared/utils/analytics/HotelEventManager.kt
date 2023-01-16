package net.sharetrip.shared.utils.analytics

import com.google.firebase.analytics.FirebaseAnalytics

class HotelEventManager private constructor(private var analyticsManager: DataAnalyticsManager) {

    fun searchHotels() {
        analyticsManager.track(searchHotels)
    }

    fun playBeenThere() {
        analyticsManager.track(playBeenThere)
    }

    fun clickOnHotelFilter() {
        analyticsManager.track(clickOnHotelFilter)
    }

    fun clickOnNeighborHood() {
        analyticsManager.track(clickOnNeighborHood)
    }

    fun clickOnLocationRange() {
        analyticsManager.track(clickOnLocationRange)
    }

    fun clickOnPOI() {
        analyticsManager.track(clickOnPOI)
    }

    fun clickOnSort() {
        analyticsManager.track(clickOnSort)
    }

    fun typeHotelName() {
        analyticsManager.track(typeHotelName)
    }

    fun clickOnPropertyRating() {
        analyticsManager.track(clickOnPropertyRating)
    }

    fun clickOnHotelDetail() {
        analyticsManager.track(clickOnHotelDetail)
    }

    fun scrollDown2SeeHotelDetail() {
        analyticsManager.track(scrollDown2SeeHotelDetail)
    }

    fun clickOnMap() {
        analyticsManager.track(clickOnMap)
    }

    fun clickOnRoomDetails() {
        analyticsManager.track(clickOnRoomDetails)
    }

    fun selectRooms() {
        analyticsManager.track(selectRooms)
    }

    fun redeemTripCoinOnHotelBooking() {
        analyticsManager.track(redeemTripCoinOnHotelBooking)
    }

    fun initialCheckoutHotel() {
        analyticsManager.track(initialCheckoutHotel)
    }

    class Builder {
        private var firebaseAnalytics: FirebaseAnalytics? =  null

        fun setFirebaseAnalytics(firebaseAnalytics: FirebaseAnalytics): Builder {
            this.firebaseAnalytics = firebaseAnalytics
            return this
        }

        fun build(): HotelEventManager {
            val analysisManager = DataAnalyticsManager(firebaseAnalytics)
            return HotelEventManager(analysisManager)
        }
    }

    companion object {
        private const val searchHotels = "Search_Hotels"
        private const val playBeenThere = "Play_Been_There"

        private const val clickOnHotelFilter = "Click_On_Hotel_Filter"
        private const val clickOnNeighborHood = "Click_On_Hotel_NeighborHood"
        private const val clickOnLocationRange = "Click_On_Hotel_Location_Range"
        private const val clickOnPOI = "Click_On_Hotel_POI"
        private const val clickOnSort = "Click_On_Hotel_Sort"
        private const val typeHotelName = "Type_Hotel_Name"
        private const val clickOnPropertyRating = "Click_On_Hotel_Property_Rating"

        private const val clickOnHotelDetail = "Click_On_Hotel_Detail"
        private const val clickOnMap = "Click_On_Hotel_Map"
        private const val scrollDown2SeeHotelDetail = "Scroll_Down2See_Hotel_Detail"
        private const val clickOnRoomDetails = "Click_On_Hotel_Room_Details"
        private const val selectRooms = "Select_Hotel_Rooms"
        private const val redeemTripCoinOnHotelBooking = "Redeem_TC_Hotel_Booking"
        private const val initialCheckoutHotel = "Initial_Checkout_Hotel"
    }
}