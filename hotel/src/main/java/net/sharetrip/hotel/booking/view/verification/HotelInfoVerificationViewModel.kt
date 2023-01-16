package net.sharetrip.hotel.booking.view.verification

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.hotel.booking.model.DiscountModel
import net.sharetrip.hotel.booking.model.HotelBookingParams
import net.sharetrip.hotel.booking.model.NationalityCode
import net.sharetrip.hotel.booking.model.PrimaryContact
import net.sharetrip.hotel.booking.view.summary.BookingSummaryFragment.Companion.ARG_SUMMARY_DISCOUNT_MODEL
import net.sharetrip.hotel.booking.view.summary.BookingSummaryFragment.Companion.ARG_SUMMARY_HOTEL_BOOKING_PARAMS
import net.sharetrip.hotel.booking.view.summary.BookingSummaryFragment.Companion.ARG_SUMMARY_ROOM_BOOKING_SUMMARY
import net.sharetrip.hotel.network.HotelBookingApiService
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.hotel.utils.MsgUtils.CHECK_ALL_INFORMATION
import net.sharetrip.hotel.utils.MsgUtils.INVALID_PRIMARY_CONTACT_NAME
import net.sharetrip.hotel.utils.MsgUtils.INVALID_PRIMARY_CONTACT_TITLE
import net.sharetrip.hotel.utils.SingleLiveEvent
import net.sharetrip.shared.utils.isNameValid

class HotelInfoVerificationViewModel(
    private val bookingParams: HotelBookingParams?,
    private val bookingSummary: String?,
    private val discountModel: DiscountModel,
    private var apiService: HotelBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    val enableDataUpdate = ObservableBoolean()
    val navigation = MutableLiveData<Boolean>()
    val isButtonActive = ObservableBoolean()
    val primaryContact = ObservableField<PrimaryContact>()
    val codeList = ArrayList<NationalityCode>()
    val showEditDialog = SingleLiveEvent<Any>()
    val navigateToSummary = SingleLiveEvent<Bundle>()
    val validTitle = listOf("Mr", "Ms", "Mstr", "Mrs")

    init {
        isButtonActive.set(false)
        enableDataUpdate.set(false)
        fetchPrimaryContact()
        getCountryDataInfo()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        when (operationTag) {
            HotelInfoApiCallingKey.COUNTRY_DATA.name -> {
                val response = (data.body as RestResponse<*>).response as List<NationalityCode>
                codeList.addAll(response)
            }

            HotelInfoApiCallingKey.PRIMARY_CONTACT.name -> {
                val response = (data.body as RestResponse<*>).response as PrimaryContact
                primaryContact.set(response)
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    fun onClickContinue() {
        val finalContact = primaryContact.get()

        if (finalContact == null || !finalContact.isInputDataValid) {
            showMessage(CHECK_ALL_INFORMATION)
            showEditDialog.call()
        } else if (finalContact.userTitle !in validTitle) {
            showMessage(INVALID_PRIMARY_CONTACT_TITLE)
        } else if (!finalContact.givenName.isNameValid() || !finalContact.surName.isNameValid()) {
            showMessage(INVALID_PRIMARY_CONTACT_NAME)
        } else {
            bookingParams!!.primaryContact = finalContact
            val bundle = Bundle()
            bundle.putString(
                ARG_SUMMARY_HOTEL_BOOKING_PARAMS,
                MoshiUtil.convertBookingParamToString(
                    bookingParams
                )
            )
            bundle.putString(ARG_SUMMARY_ROOM_BOOKING_SUMMARY, bookingSummary)
            bundle.putParcelable(ARG_SUMMARY_DISCOUNT_MODEL, discountModel)
            navigateToSummary.value = bundle
        }
    }

    fun onClickCheckBox(view: View) {
        isButtonActive.set((view as AppCompatCheckBox).isChecked)
    }

    fun onClickUpdate(view: View) {
        showEditDialog.call()
    }

    private fun getCountryDataInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        dataLoading.set(true)
        executeSuspendedCodeBlock(HotelInfoApiCallingKey.COUNTRY_DATA.name) {
            apiService.getNationalityCodeList(token)
        }
    }

    private fun fetchPrimaryContact() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        dataLoading.set(true)
        executeSuspendedCodeBlock(operationTag = HotelInfoApiCallingKey.PRIMARY_CONTACT.name) {
            apiService.getPrimaryContactResponse(token)
        }
    }

    enum class HotelInfoApiCallingKey {
        PRIMARY_CONTACT,
        COUNTRY_DATA
    }
}
