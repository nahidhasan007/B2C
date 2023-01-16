package net.sharetrip.holiday.booking.view.reserve

import android.annotation.SuppressLint
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.ServiceType
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.MoshiUtil.convertStringOfIntToArrayList
import net.sharetrip.shared.utils.SimpleTextWatcher
import net.sharetrip.shared.utils.toIntExt
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.model.HolidayOffer
import net.sharetrip.holiday.booking.model.HolidayParam
import java.text.SimpleDateFormat
import java.util.*

class HolidayReserveViewModel(
    val param: HolidayParam,
    private val selectedOffer: HolidayOffer
) : BaseViewModel() {

    private val _navigateToCalender = MutableLiveData<Event<Boolean>>()
    val navigateToCalender: LiveData<Event<Boolean>>
        get() = _navigateToCalender

    private val _navigateToBooking = MutableLiveData<Event<Boolean>>()
    val navigateToBooking: LiveData<Event<Boolean>>
        get() = _navigateToBooking

    val navigation = MutableLiveData<Event<Boolean>>()
    val msg = MutableLiveData<Event<String>>()
    val updated = MutableLiveData<Boolean>()

    lateinit var calenderData: CalenderData

    var roomWatcher: TextWatcher = object : SimpleTextWatcher() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            updated.value = true
        }
    }

    fun setBookingParams(
        date: String, singleRooms: String, doubleRooms: String, twinRooms: String,
        tripleRooms: String, quadRooms: String, adultNo: String, child7to12No: String,
        child3to6No: String, infantNo: String
    ): Boolean {
        param.apply {
            packageDate = date

            singleRoom = singleRooms.toIntExt()
            doubleRoom = doubleRooms.toIntExt()
            twinRoom = twinRooms.toIntExt()
            tripleRoom = tripleRooms.toIntExt()
            quadRoom = quadRooms.toIntExt()

            adult = adultNo.toIntExt()
            infant = infantNo.toIntExt()
            child3to6 = child3to6No.toIntExt()
            child7to12 = child7to12No.toIntExt()
            var nameOfHotel = ""
            var nameOfCity = ""
            for (hotelInfo in selectedOffer.hotels!!) {
                nameOfHotel = nameOfHotel + "" + hotelInfo.hotelName + ", "
                nameOfCity = nameOfCity + "" + hotelInfo.cityName + ""
            }
            hotelNames = nameOfHotel
            hotelCity = nameOfCity
        }

        val singleRoomPrice = param.singleRoom * selectedOffer.singlePerPax
        val doubleRoomPrice = param.doubleRoom * selectedOffer.doublePerPax * 2
        val twinRoomPrice = param.twinRoom * selectedOffer.twinPerPax * 2
        val tripleRoomPrice = param.tripleRoom * selectedOffer.triplePerPax * 3
        val quadRoomPrice = param.quadRoom * selectedOffer.quadPerPax * 4
        val child7To12Price = param.child7to12 * selectedOffer.child7To12
        val child3To6Price = param.child3to6 * selectedOffer.child3To6
        val infantPrice = param.infant * selectedOffer.infant
        val totalPrice =
            singleRoomPrice + doubleRoomPrice + twinRoomPrice + tripleRoomPrice + quadRoomPrice + child7To12Price + child3To6Price + infantPrice

        val singleRoomPriceDiscount = param.singleRoom * selectedOffer.singlePerPaxDiscountPrice
        val doubleRoomPriceDiscount = param.doubleRoom * selectedOffer.doublePerPaxDiscountPrice * 2
        val twinRoomPriceDiscount = param.twinRoom * selectedOffer.twinPerPaxDiscountPrice * 2
        val tripleRoomPriceDiscount = param.tripleRoom * selectedOffer.triplePerPaxDiscountPrice * 3
        val quadRoomPriceDiscount = param.quadRoom * selectedOffer.quadPerPaxDiscountPrice * 4
        val child7To12PriceDiscount = param.child7to12 * selectedOffer.child7To12DiscountPrice
        val child3To6PriceDiscount = param.child3to6 * selectedOffer.child3To6DiscountPrice
        val infantPriceDiscount = param.infant * selectedOffer.infantDiscountPrice

        val totalDiscountedPrice =
            singleRoomPriceDiscount + doubleRoomPriceDiscount + twinRoomPriceDiscount + tripleRoomPriceDiscount + quadRoomPriceDiscount + child7To12PriceDiscount + child3To6PriceDiscount + infantPriceDiscount

        param.singleRoomPrice = singleRoomPrice.toDouble()
        param.doubleRoomPrice = doubleRoomPrice.toDouble()
        param.twinRoomPrice = twinRoomPrice.toDouble()
        param.tripleRoomPrice = tripleRoomPrice.toDouble()
        param.quadRoomPrice = quadRoomPrice.toDouble()
        param.child3to6Price = child3To6Price.toDouble()
        param.child7to12Price = child7To12Price.toDouble()
        param.infantPrice = infantPrice.toDouble()
        param.totalAmount = totalPrice.toDouble()
        param.discountAmount = totalDiscountedPrice.toDouble()

        val formatter = SimpleDateFormat("dd-MM-yyyy")
        var d: Date? = null
        try {
            d = formatter.parse(date)
            val bookingDate = Calendar.getInstance()
            val freeCancellationDate = Calendar.getInstance()
            val currentDate = Calendar.getInstance()
            bookingDate.time = d
            freeCancellationDate.time = d
            freeCancellationDate.add(Calendar.DAY_OF_MONTH, -param.cancelPolicy.toInt())
            param.hasCancelPolicy = freeCancellationDate.time > currentDate.time
            param.cancelPolicyDate = formatter.format(freeCancellationDate.time)
        } catch (e: Exception) {
            e.printStackTrace()
            param.hasCancelPolicy = false
        }

        return if (param.isHolidayReservationValid()) {
            true
        } else {
            msg.value = Event("Select minimum one room and one adult")
            false
        }
    }

    fun onClickSaveMenu() {
        _navigateToBooking.value = Event(true)
    }

    @SuppressLint("SimpleDateFormat")
    fun onClickDate() {
        val mCalendar = Calendar.getInstance()
        val mDateHintText = "Travel Date"

        if (DateUtil.parseDateToMillisecond(
                DateUtil.increaseOrDecreaseDate(
                    DateUtil.getCurrentDate(),
                    param.releaseTime
                )
            ) <= DateUtil.parseDateToMillisecond(
                selectedOffer.periodFrom
            )
        ) {
            mCalendar.time = selectedOffer.periodFrom?.let {
                SimpleDateFormat(DateUtil.API_DATE_PATTERN).parse(
                    it
                )
            } as Date
        } else {
            mCalendar.add(Calendar.DATE, param.releaseTime)
        }

        if (selectedOffer.specificDays!!.isNotEmpty()) {
            val weekday = convertStringOfIntToArrayList(selectedOffer.specificDays)
            calenderData = CalenderData(
                mDateInMillisecond = mCalendar.timeInMillis,
                mDateHintText = mDateHintText,
                fromAirportCode = "",
                toAirportCode = "",
                serviceType = ServiceType.HOLIDAY.serviceName,
                inActiveDays = getInactiveDays(weekday) as ArrayList<Int>,
                holidayOfferValidTo = DateUtil.increaseOrDecreaseDate(
                    selectedOffer.periodTo!!,
                    -param.releaseTime
                )
            )

            _navigateToCalender.value = Event(true)
            return
        }

        calenderData = CalenderData(
            mCalendar.timeInMillis,
            mDateHintText = mDateHintText,
            fromAirportCode = "",
            toAirportCode = "",
            serviceType = ServiceType.HOLIDAY.serviceName,
            holidayOfferValidTo = DateUtil.increaseOrDecreaseDate(
                selectedOffer.periodTo!!,
                -param.releaseTime
            )
        )

        _navigateToCalender.value = Event(true)
    }

    private fun getInactiveDays(activeDays: MutableList<Int>): List<Int> {
        val data = arrayListOf(1, 2, 3, 4, 5, 6, 7)

        for (i in activeDays) {
            data.remove(i)
        }

        val inactiveDays = mutableListOf<Int>()

        for (index in data) {
            when {
                index > 2 -> {
                    inactiveDays.add(index - 2)
                }
                index == 1 -> {
                    inactiveDays.add(6)
                }
                index == 2 -> {
                    inactiveDays.add(7)
                }
            }
        }

        return inactiveDays
    }

}
