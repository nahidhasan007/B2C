package net.sharetrip.shared.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
data class CalenderData(
    val mDateInMillisecond: Long,
    val mEndDateInMillisecond: Long = 0,
    val mDateHintText: String,
    val mEndDateHintText: String = "",
    val fromAirportCode: String = "",
    val toAirportCode: String = "",
    val serviceType: String = "",
    val inActiveDays: ArrayList<Int> = ArrayList(),
    val holidayOfferValidTo: String = "",
    val visaPreparationMinimumDay: Int = 0,
    val searchResponse: AdvanceSearchResponse? = null,
    val isVisaStartDateSelected: Boolean = false,
    val visaStartDate :String = "",
    val releaseTime: Int? = null
) : Parcelable
