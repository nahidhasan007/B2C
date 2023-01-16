package net.sharetrip.hotel.booking.view.hotelfilter

import androidx.annotation.IntDef

const val PRICE = 1
const val SEARCH = 2
const val NEIGHBORHOOD = 3
const val LOCATION_RANGE = 4
const val PROPERTY_RATING = 5
const val MEAL = 6
const val PROPERTY_TYPE = 7
const val FACILITIES = 8
const val POINT_OF_INTEREST = 9

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@IntDef(
    PRICE,
    SEARCH,
    NEIGHBORHOOD,
    LOCATION_RANGE,
    PROPERTY_RATING,
    MEAL,
    PROPERTY_TYPE,
    FACILITIES,
    POINT_OF_INTEREST
)
annotation class HotelFilterTypeEnum