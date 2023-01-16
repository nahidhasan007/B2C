package net.sharetrip.flight.booking.view.filter

import androidx.annotation.IntDef

const val PRICE = 1
const val STOPS = 2
const val TIME = 3
const val AIRLINE = 4
const val LAYOVER = 5
const val WEIGHT = 6
const val REFUNDABLE = 7
const val PRICE_RANGE = 8
const val CLASS_FILTER = 9
const val SCHEDULE_FILTER = 10
const val OPERATOR_FILTER = 11

@Target(AnnotationTarget.TYPE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(
    PRICE,
    STOPS,
    TIME,
    AIRLINE,
    LAYOVER,
    WEIGHT,
    REFUNDABLE,
    PRICE_RANGE,
    CLASS_FILTER,
    SCHEDULE_FILTER,
    OPERATOR_FILTER
)
annotation class FilterTypeEnum