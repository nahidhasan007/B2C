package net.sharetrip.hotel.booking.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HotelNamesResponse(
    @field:Json(name = "hotel")
    var hotelList: List<HotelProperty>,
    @field:Json(name = "city")
    var cityList: List<HotelProperty>
) : Parcelable {
    fun getHotelNamesList(): List<HotelProperty> {
        return hotelList
    }

    fun setHotelNamesList1(list: List<HotelProperty>) {
        hotelList = list
    }

    fun getCityNamesList(): List<HotelProperty> {
        return cityList
    }

    fun getBothHotelCityNamesList(): List<HotelProperty> {
        val newList: MutableList<HotelProperty> = cityList as MutableList<HotelProperty>
        newList.addAll(hotelList)
        return newList
    }

    fun setCityNamesList(cityList: List<HotelProperty>) {
        this.cityList = cityList
    }
}
