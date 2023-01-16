package net.sharetrip.bus.booking.view.seatselection

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.bus.booking.model.*
import net.sharetrip.bus.booking.model.selectionenum.Case
import net.sharetrip.bus.booking.model.selectionenum.SeatStatus
import net.sharetrip.bus.booking.view.locationpoints.SelectLocationFragment.Companion.ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN
import net.sharetrip.bus.booking.view.locationpoints.SelectLocationFragment.Companion.ARG_DEPARTURE_MODEL
import net.sharetrip.bus.network.BusBookingApiService
import net.sharetrip.bus.utils.MsgUtils.selectOneSeat
import net.sharetrip.bus.utils.MsgUtils.unKnownErrorMsg
import net.sharetrip.bus.utils.SingleLiveEvent
import kotlin.math.ceil

class BusSeatSelectionViewModel(
    private val departureTime: String?,
    private val arrivalTime: String?,
    private val discount: String,
    private val searchIdAndTripCoin: SearchIdAndTripCoin,
    private val apiService: BusBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    var departureInfo: Departure? = null
    var isInit = MutableLiveData(false)
    var selectedSeatList = mutableMapOf<String, Seat>()
    var departureInfoMapped = mutableMapOf<Int, Seat>()
    var fare = ObservableField("0")
    var seatsAll = ObservableField("None")
    var seatCol: Int = 1
    val serviceCharge = ObservableField("0")
    val discountAmount = ObservableField("0")
    var journeyDate = ""
    var isNextEnabled = true
    var dialogLoading = MutableLiveData<Boolean>()
    var isVisible = ObservableBoolean(false)
    var prevTitle = SpannableStringBuilder()
    var prevDate = StringBuilder()
    var seatsSelected = MutableLiveData<MutableList<Seat>>()
    var seatsRejected = MutableLiveData<MutableList<String>>()
    var previousStatus = mutableMapOf<String, String>()
    var singleSeatServiceCharge = 0.0
    val navigateToLocationPoint = SingleLiveEvent<Bundle>()

    var seatModel = listOf(
        Seat("#5BB4FF", Fares("", ""), 0, "", "", "Available", 0, 0, 0, true, false, false),
        Seat("#2E7D31", Fares("", ""), 0, "", "", "Selected", 0, 0, 0, true, false, false),
        Seat("#DE1921", Fares("", ""), 0, "", "", "Sold Out", 0, 0, 0, true, false, false),
        Seat("#FCA213", Fares("", ""), 0, "", "", "Booked", 0, 0, 0, true, false, false),
        Seat("#E5E5EA", Fares("", ""), 0, "", "", "Blocked", 0, 0, 0, true, true, false)
    )

    init {
        getCoachDetails()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dialogLoading.value = false

        if (operationTag == BusSelectionApiCallingKey.CoachDetails.name) {
            val response = data.body as RestResponse<*>

            if (response.code == "SUCCESS") {
                departureInfo = response.response as Departure
                departureInfo!!.serviceCharge = "0"
                departureInfo!!.arrivalTime = arrivalTime
                departureInfo!!.departureTime = departureTime
                departureInfo!!.setTimeDuration()
                seatCol = departureInfo?.seatCol!!
                initVariables()
                isInit.value = true
                departureInfo!!.date = journeyDate
            } else
                showMessage(unKnownErrorMsg)

        } else {
            val response = data.body as RestResponse<*>
            val seatResponse = response.response as SelectSeatRequestResponse
            if (response.errors?.trace != null) {
                departureInfo?.selectedSeatId!!.remove(selectedSeatList[operationTag]?.seatId.toString())
                departureInfo?.selectedSeatsNo!!.remove(selectedSeatList[operationTag]?.seatNo.toString())
                val rejectedSeatId = mutableListOf<String>()
                seatResponse.tripInfos?.coachSeatList?.forEach { it ->
                    if (it.seatId.toString() !in departureInfo?.selectedSeatId!!)
                        departureInfo?.selectedSeatId!!.add(it.seatId.toString())
                }
                response.errors?.trace!!.forEach {
                    if (it.message != "FAILED_TO_REMOVE_SEAT") {
                        rejectedSeatId.add(it.seatId.trim())
                        departureInfo?.selectedSeatId!!.remove(it.seatId.trim())
                    }

                    showMessage(it.message)
                }

                seatsRejected.value?.forEach {
                    rejectedSeatId.add(it)
                }

                seatsRejected.value = rejectedSeatId
            }
            departureInfo!!.serviceCharge =
                seatResponse.tripInfos?.serviceCharge.toString()

            if (seatResponse.tripInfos?.coachSeatList != null) {
                singleSeatServiceCharge =
                    (departureInfo!!.serviceCharge.toDouble() / seatResponse.tripInfos.coachSeatList.size)

                seatResponse.tripInfos.coachSeatList.forEach {
                    if (it.seatId.toString() !in departureInfo?.selectedSeatId!!) {
                        departureInfo?.selectedSeatId!!.add(it.seatId.toString())
                    }
                }

                seatsSelected.value =
                    seatResponse.tripInfos.coachSeatList as MutableList<Seat>

                departureInfo?.selectedSeatsInfo = seatsSelected.value
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dialogLoading.value = false
        showMessage(errorMessage)

        if (operationTag != BusSelectionApiCallingKey.CoachDetails.name) {
            selectedSeatList.remove(operationTag)
        }
    }

    private fun getCoachDetails() {
        val coachId = sharedPrefsHelper[PrefKey.BUS_COACH_ID, ""]
        dialogLoading.value = true
        val coachValue = CoachDetails(searchIdAndTripCoin.searchId, coachId)

        executeSuspendedCodeBlock(BusSelectionApiCallingKey.CoachDetails.name) {
            apiService.getCoachDetails(
                coachValue
            )
        }
    }

    fun initVariables() {
        if (departureInfo?.seats != null) {
            for (it in departureInfo?.seats!!) {
                if (it.yaxis > seatCol) {
                    seatCol = it.yaxis
                    break
                }
            }

            departureInfo?.seats?.forEach {
                when (it.status) {
                    SeatStatus.AVAILABLE.status -> {
                        seatModel[0].count++
                        it.isVisibleSeatStatus = false
                    }
                    SeatStatus.SELECTED.status -> {
                        seatModel[1].count++
                        it.isVisibleSeatStatus = false
                    }
                    SeatStatus.SOLD.status -> {
                        seatModel[2].count++
                    }
                    SeatStatus.BOOKED.status -> seatModel[3].count++
                    SeatStatus.BLOCKED.status -> {
                        seatModel[4].count++
                        it.isVisibleClose = true
                    }
                }

                it.isVisibleValues = false
                departureInfoMapped[getSeatPos(it.xaxis, it.yaxis)] = it
            }
        }
        departureInfo?.selectedSeatsNo = mutableListOf()
        departureInfo?.selectedSeatId = mutableListOf()
        departureInfo?.selectedSeatsInfo = mutableListOf()
    }

    fun onClickNext() {
        if (departureInfo?.selectedSeatsInfo?.size!! > 0) {
            val bundle = Bundle()
            bundle.putParcelable(ARG_DEPARTURE_MODEL, departureInfo)
            bundle.putParcelable(ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN, searchIdAndTripCoin)
            navigateToLocationPoint.value = bundle
        } else {
            showMessage(selectOneSeat)
        }
    }

    fun updateFareAndSeats(case: Case, pos: Int) {
        var totalSeatFare = 0.0

        when (case) {
            Case.SELECT_SEAT -> {
                departureInfoMapped[pos]?.status = "Selected"
                departureInfo?.selectedSeatsNo?.clear()
                var tempServiceCharge = 0.0

                seatsSelected.value?.forEach {
                    totalSeatFare += it.fare.originalFare.toDouble()
                    departureInfo?.selectedSeatsNo?.add(it.seatNo)
                    tempServiceCharge += singleSeatServiceCharge
                }
                tempServiceCharge = ceil(tempServiceCharge)
                serviceCharge.set(tempServiceCharge.toString())

                if ("%" in discount) {
                    var discountValue = discount.replace("%", "").toDouble()
                    discountValue = (totalSeatFare * discountValue / 100)
                    totalSeatFare -= discountValue
                    discountAmount.set("-$discountValue")
                } else {
                    var discountValue = discount.toDouble()
                    discountValue *= seatsSelected.value?.size?.toDouble()!!
                    totalSeatFare -= discountValue
                    discountAmount.set("-$discountValue")
                }

                departureInfo?.selectedSeatsNo?.sort()
                departureInfo?.selectedSeatsInString =
                    departureInfo?.selectedSeatsNo?.joinToString()
                seatsSelected.value?.sortBy { it.seatNo }

                if (totalSeatFare > tempServiceCharge)
                    fare.set((totalSeatFare + tempServiceCharge).toString())
                else {
                    fare.set("0")
                    serviceCharge.set("0")
                    discountAmount.set("0")
                }

                if (fare.get()!!.toDouble() > 0) {
                    seatsAll.set(departureInfo!!.selectedSeatsInString)
                    departureInfo?.totalPayable = fare.get()
                } else
                    seatsAll.set("None")
            }
            Case.SELECTION_ONGOING -> {
                departureInfoMapped[pos]?.status = "Blocked"
                return
            }
            Case.SELECTION_FAILED -> {
                departureInfoMapped[pos]?.status =
                    previousStatus[departureInfoMapped[pos]?.seatId.toString()].toString()
            }
            else -> {
                departureInfoMapped[pos]?.status = "Available"
                departureInfo?.selectedSeatsNo?.clear()
                var tempServiceCharge = 0.0

                seatsSelected.value?.forEach {
                    totalSeatFare += it.fare.originalFare.toDouble()
                    departureInfo?.selectedSeatsNo?.add(it.seatNo)
                    tempServiceCharge += singleSeatServiceCharge
                }
                tempServiceCharge = ceil(tempServiceCharge)
                serviceCharge.set(tempServiceCharge.toString())

                if ("%" in discount) {
                    var discountValue = discount.replace("%", "").toDouble()
                    discountValue = (totalSeatFare * discountValue / 100)
                    totalSeatFare -= discountValue
                    discountAmount.set("-$discountValue")
                } else {
                    var discountValue = discount.toDouble()
                    discountValue *= seatsSelected.value?.size?.toDouble()!!
                    totalSeatFare -= discountValue
                    discountAmount.set("-$discountValue")
                }

                departureInfo?.selectedSeatsNo?.sort()
                departureInfo?.selectedSeatsInString =
                    departureInfo?.selectedSeatsNo?.joinToString()
                seatsSelected.value?.sortBy { it.seatNo }

                if (totalSeatFare > tempServiceCharge)
                    fare.set((totalSeatFare + tempServiceCharge).toString())
                else {
                    fare.set("0")
                    serviceCharge.set("0")
                    discountAmount.set("0")
                }

                departureInfo?.serviceCharge = serviceCharge.get()!!

                if (fare.get()!!.toDouble() > 0) {
                    seatsAll.set(departureInfo!!.selectedSeatsInString)
                    departureInfo?.totalPayable = fare.get()
                } else
                    seatsAll.set("None")
            }
        }

        seatModel[1].count = seatsSelected.value?.size ?: 0
        departureInfo?.discount = discountAmount.get().toString()
    }

    fun getColorCode(seat: Seat?): String {
        when (seat?.status) {
            SeatStatus.AVAILABLE.status -> {
                return "#5BB4FF"
            }
            SeatStatus.BOOKED.status -> {
                return "#FCA213"
            }
            SeatStatus.SELECTED.status -> {
                return "#2E7D31"
            }
            SeatStatus.BLOCKED.status -> {
                return "#E5E5EA"
            }
            SeatStatus.SOLD.status -> {
                return "#DE1921"
            }
        }
        return "none"
    }

    fun getSeatPos(xAxis: Int, yAxis: Int): Int {
        return (xAxis - 1) * seatCol + yAxis - 1
    }

    fun selectSeat(seat: Seat?, case: Case) {
        seat?.let {
            this.selectedSeatList[getSeatPos(seat.xaxis, seat.yaxis).toString()] = seat
        }

        if (case == Case.SELECT_SEAT) {
            departureInfo?.selectedSeatId!!.add(seat?.seatId.toString())
            val selectRec =
                SelectSeatRequest(
                    searchIdAndTripCoin.searchId,
                    departureInfo?.id!!,
                    null,
                    null,
                    seats = departureInfo?.selectedSeatId!!
                )

            seat?.let {
                executeSuspendedCodeBlock(getSeatPos(seat.xaxis, seat.yaxis).toString()) {
                    apiService.selectSeat(
                        selectRec
                    )
                }
            }
        } else {
            departureInfo?.selectedSeatId!!.remove(seat?.seatId.toString())
            departureInfo?.selectedSeatsNo!!.remove(seat?.seatNo.toString())
            val currentList = ArrayList(departureInfo?.selectedSeatsInfo)

            currentList.forEach {
                if (it.seatId == seat?.seatId)
                    departureInfo?.selectedSeatsInfo?.remove(it)
            }

            seatsSelected.value = departureInfo?.selectedSeatsInfo!!
        }
    }
}
