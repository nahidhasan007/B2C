package net.sharetrip.visa.booking.view.application

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.visa.booking.model.ReqDocsItem
import net.sharetrip.visa.booking.model.UpdateSearchRecord
import net.sharetrip.visa.booking.model.VisaFaq
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.MsgUtils.unKnownErrorMsg
import net.sharetrip.visa.utils.SingleLiveEvent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class VisaApplicationViewModel(
    private val visaQuery: VisaSearchQuery,
    private val apiService: VisaBookingApiService,
    sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    var reqDocList = MutableLiveData<List<ReqDocsItem>>()
    var importanceNotice = MutableLiveData<String>()
    var faqList = MutableLiveData<List<VisaFaq>>()
    var headerText = MutableLiveData<String>()
    var token = ""
    var isReqDocsVisible = ObservableBoolean(false)
    val navigateToCheckout = SingleLiveEvent<VisaSearchQuery>()

    init {
        try {
            token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
            dataLoading.set(true)
            reqDocList.postValue(
                visaQuery.visaSelection!!.visaProducts!![visaQuery.selectedVisaType!!]
                    .requiredDocList
            )
            importanceNotice.postValue(visaQuery.visaSelection!!.importantNotes)
            faqList.postValue(visaQuery.visaSelection!!.faqList)
            val text = "Required Documents for ${visaQuery.destinationCountry} " +
                    "${visaQuery.visaSelection!!.visaProducts!![visaQuery.selectedVisaType!!].title}"
            headerText.postValue(text)
            updateSearchRecord()
        } catch (e: Exception) {
             showMessage(unKnownErrorMsg)
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
         showMessage(errorMessage)
    }

    private fun updateSearchRecord() {
        val body: RequestBody = makeJson().toRequestBody("application/json".toMediaTypeOrNull())

        executeSuspendedCodeBlock { apiService.updateSearchRecord(token, body) }
    }

    private fun makeJson(): String {
        val gson = Gson()
        val searchID = visaQuery.searchID
        val productCode =
            visaQuery.selectedVisaType?.let { visaQuery.visaSelection?.visaProducts?.get(it)?.productCode }
        val visaType =
            visaQuery.selectedVisaType?.let { visaQuery.visaSelection?.visaProducts?.get(it)?.type }
        val price =
            visaQuery.selectedVisaType?.let { visaQuery.visaSelection?.visaProducts?.get(it)?.visaFee }
        val searchRecord = UpdateSearchRecord(searchID, productCode, visaType, price)
        return gson.toJson(searchRecord)
    }

    fun onNext() {
        navigateToCheckout.value = visaQuery
    }
}
