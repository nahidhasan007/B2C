package net.sharetrip.flight.booking.model

import androidx.annotation.IntDef

const val FLIGHT = 1
const val SEGMENT = 2
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(FLIGHT, SEGMENT)
annotation class FlightTypeEnum