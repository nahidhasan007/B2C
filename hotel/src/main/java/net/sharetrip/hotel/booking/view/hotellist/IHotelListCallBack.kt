package net.sharetrip.hotel.booking.view.hotellist

import net.sharetrip.hotel.booking.model.Filter

interface IHotelListCallBack {
    fun hotelCount(count : Int)

    fun hotelCountServer(count : Int)

    fun searchCode(searchId : String)

    fun filter (filter : Filter)

    fun isFirstPage(value : Boolean)
}
