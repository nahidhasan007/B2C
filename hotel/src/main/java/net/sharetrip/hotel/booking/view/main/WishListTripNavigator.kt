package net.sharetrip.hotel.booking.view.main

interface WishListTripNavigator {

    fun selectWannaGoThere(cityCode: String)

    fun selectHaveBeenThere(cityCode: String)

    fun navigateToNext(position: Int)
}
