package net.sharetrip.tour.booking.contact

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.DateUtil
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.tour.booking.summary.TourSummaryFragment
import net.sharetrip.tour.model.*
import java.text.SimpleDateFormat
import java.util.*

class ClientContactViewModel(
    private val tourParam: TourParam,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repo: ClientContactRepo
) : BaseOperationalViewModel() {

    private val _navigateToSummary = MutableLiveData<Event<Bundle>>()
    val navigateToSummary: LiveData<Event<Bundle>>
    get() = _navigateToSummary

    private val contact = PrimaryContact()
    private val bookingParam = TourBookingParam()

    val primaryContact = ObservableField<TourContact>()

    val dateOfBirth = MutableLiveData<String>()
    val msg = MutableLiveData<String>()

    init {
        fetchContactInfo()
    }

    private fun fetchContactInfo() {
        val token = sharedPrefsHelper.get(PrefKey.ACCESS_TOKEN, "")

        executeSuspendedCodeBlock(OperationTags.GET_CONTACT_INFO.name){
            repo.getContactInfo(token)
        }
    }

    fun navigateToSummary(
        title: String,
        givenName: String,
        surName: String,
        mobile: String,
        email: String,
        address: String,
        birthday: String
    ) {
        contact.apply {
            this.titleName = title
            this.givenName = givenName
            this.surName = surName
            this.email = email
            this.mobileNumber = mobile
            this.address1 = address
            this.age = DateUtil.getAge(birthday)
        }

        bookingParam.apply {
            tourId = tourParam.tourId
            tourPeriodId = tourParam.tourOfferId
            tourDate = tourParam.tourDate
            departureTime = tourParam.departureTime
            totalAmount = tourParam.totalAmount
            bookingCurrency = tourParam.bookingCurrency
            departurePickUpLocationName = tourParam.pickUpLocation
            countryCode = tourParam.countryCode
            adultsCount = tourParam.adult
            infantCount = tourParam.infant
            child3To6Count = tourParam.child3to6
            child7To12Count = tourParam.child7to12
            primaryContact = contact
        }

        val childCountTotal = tourParam.infant + tourParam.child3to6 + tourParam.child7to12

        val formatter = SimpleDateFormat("dd-MM-yyyy")
        var d: Date? = null
        var hasCancelPolicy: Boolean = false
        var cancelPolicyDate: String = ""
        try {
            d = formatter.parse(tourParam.tourDate)
            val bookingDate = Calendar.getInstance()
            val freeCancellationDate = Calendar.getInstance()
            val currentDate = Calendar.getInstance()
            bookingDate.time = d
            freeCancellationDate.time = d
            freeCancellationDate.add(Calendar.DATE, -tourParam.cxlPolicy.toInt())
            hasCancelPolicy = freeCancellationDate.time > currentDate.time
            cancelPolicyDate = formatter.format(freeCancellationDate.time)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val summary = TourSummary(
            "Offer " + tourParam.offerNo,
            tourParam.adult,
            childCountTotal,
            tourParam.child3to6,
            tourParam.child7to12,
            tourParam.infant,
            tourParam.adultAmount,
            tourParam.child3t6Amount,
            tourParam.child7t12Amount,
            tourParam.infantAmount,
            tourParam.pickUpLocation,
            tourParam.cityName,
            tourParam.countryName,
            tourParam.tourDuration,
            tourParam.tourDate,
            tourParam.totalAmount,
            tourParam.earnCoin,
            tourParam.cxlPolicy,
            hasCancelPolicy,
            cancelPolicyDate
        )

        if (contact.isDataValid) {
            _navigateToSummary.value = Event(bundleOf(TourSummaryFragment.ARG_BOOKING_PARAM_MODEL to bookingParam,
                TourSummaryFragment.ARG_TOUR_SUMMARY_MODEL to summary
            ))
        } else {
            msg.value = "Fill up all required info"
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
       when(operationTag){
           OperationTags.GET_CONTACT_INFO.name ->{
               val contactResponse = (data.body as RestResponse<*>).response as TourContact
               dataLoading.set(false)
               primaryContact.set(contactResponse)
               dateOfBirth.value = contactResponse.dateOfBirth!!
           }
       }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
    }

    enum class OperationTags { GET_CONTACT_INFO }
}
