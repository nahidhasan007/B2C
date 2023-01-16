package net.sharetrip.flight.booking.model

import android.os.Parcelable
import net.sharetrip.shared.utils.DateUtil.getApiDateFormat
import net.sharetrip.shared.utils.DateUtil.tomorrowApiDateFormat
import net.sharetrip.shared.utils.Strings
import net.sharetrip.shared.utils.Strings.isNull
import kotlinx.parcelize.Parcelize
import net.sharetrip.flight.booking.model.luggage.TravellerBaggageCode
import java.util.*

@Parcelize
data class FlightSearch(
    var originAddress: MutableList<String> = ArrayList(),
    var originCity: MutableList<String> = ArrayList(),
    var destinationCity: MutableList<String> = ArrayList(),
    var destinationAddress: MutableList<String> = ArrayList(),
    var depart: MutableList<String> = ArrayList(),
    var multiCityModels: MutableList<MultiCityModel> = mutableListOf(
        MultiCityModel(),
        MultiCityModel()
    ),
    var origin: MutableList<String> = ArrayList(),
    var destination: MutableList<String> = ArrayList(),
    var childDateOfBirthList: ArrayList<ChildrenDOB> = ArrayList(),
    var travellerBaggageCodes: ArrayList<TravellerBaggageCode> = ArrayList(),
    var baggage: List<Baggage>? = ArrayList(),
    var airFareRules: List<AirFareRule>? = ArrayList(),
    var classType: String = "",
    var tripType: String = "",
    var adult: Int = 0,
    var child: Int = 0,
    var infant: Int = 0,
    var searchId: String = "",
    var sequence: String = "",
    var sessionId: String = "",
    var fareDetails: String? = null,
    var stop: String? = null,
    var mCouponCode: String? = null,
    var paymentGateWayId: String? = null,
    var totalBaggageCost: Double = 0.0,
    var verifiedMobileNumber: String? = "",
    var otp: String? = ""
) : Parcelable {
    fun initForMultiCity() {
        origin.add("")
        origin.add("")

        originCity.add("")
        originCity.add("")

        originAddress.add("")
        originAddress.add("")

        destination.add("")
        destination.add("")

        destinationCity.add("")
        destinationCity.add("")

        destinationAddress.add("")
        destinationAddress.add("")

        classType = TravellerClassType.ECONOMY.type
        adult = 1
        child = 0
        infant = 0

        depart.add(tomorrowApiDateFormat)
        depart.add("")
        tripType = TripType.MULTI_CITY
    }

    fun initForRoundTrip() {
        init()
        var number = Random().nextInt(10)
        number = if (number == 0) 2 else number
        number = if (number == 1) 2 else number
        depart.add(getApiDateFormat(number))
        depart.add(getApiDateFormat(number + 7))
        tripType = TripType.ROUND_TRIP
    }

    fun initForOneWay() {
        init()
        depart.add(tomorrowApiDateFormat)
        tripType = TripType.ONE_WAY
    }

    private fun init() {
        origin.add("DAC")
        originCity.add("Dhaka")
        originAddress.add("Dhaka, Hazrat Shahjalal International Airport (DAC)")

        destination.add("JFK")
        destinationCity.add("New York")
        destinationAddress.add("New York, John F Kennedy International Airport (JFK)")

        classType = TravellerClassType.ECONOMY.type
        adult = 1
        child = 0
        infant = 0
    }

    val totalTravellers: Int
        get() = adult + child + infant
    var couponCode: String?
        get() = if (mCouponCode == null) "" else mCouponCode
        set(mCouponCode) {
            this.mCouponCode = mCouponCode
        }
    val baggageDetails: String
        get() {
            val mBuilder = StringBuilder()
            if (baggage != null && baggage!!.isNotEmpty()) {
                val perPerson = " / person"
                for (mBaggage in baggage!!) {
                    mBuilder.append(mBaggage.type).append(Strings.LINE_BREAK)
                    var mValue = mBaggage.adult
                    if (!isNull(mValue)) {
                        mBuilder.append("Adult : ").append(mValue).append(perPerson)
                            .append(Strings.LINE_BREAK)
                    }
                    mValue = mBaggage.child
                    if (!isNull(mValue)) {
                        mBuilder.append("Child : ").append(mValue).append(perPerson)
                            .append(Strings.LINE_BREAK)
                    }
                    mValue = mBaggage.infant
                    if (!isNull(mValue)) {
                        mBuilder.append("Infant : ").append(mValue).append(perPerson)
                            .append(Strings.LINE_BREAK)
                    }
                    mBuilder.append(Strings.LINE_BREAK)
                }
            } else {
                mBuilder.append("Baggage rule not found")
            }
            return mBuilder.toString()
        }
    val airFareRulesDetails: String
        get() {
            val mBuilder = StringBuilder()
            if (airFareRules != null) {
                for ((type, rules) in airFareRules!!) {
                    mBuilder.append(type)
                        .append(Strings.LINE_BREAK)
                        .append(Strings.LINE_BREAK)
                    for (mRule in rules!!) {
                        mBuilder.append(mRule.type).append(Strings.LINE_BREAK)
                            .append(Strings.LINE_BREAK)
                        mBuilder.append(mRule.text).append(Strings.LINE_BREAK)
                    }
                    mBuilder.append(Strings.LINE_BREAK).append(Strings.LINE_BREAK)
                }
            }
            if (mBuilder.isEmpty()) {
                mBuilder.append("Air fare rule not found")
            }
            return mBuilder.toString()
        }
    val numberOfTraveller: Int
        get() = adult + child + infant

    val childBirthListOnly: ArrayList<String>
        get() {
            val list: ArrayList<String> = ArrayList()
            for (child in childDateOfBirthList) {
                list.add(child.date)
            }
            return list
        }
}