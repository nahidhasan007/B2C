package net.sharetrip.visa.booking.view.nationality

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.visa.booking.model.NationalityCode
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.SingleLiveEvent

class NationalityViewModel(
    private val apiService: VisaBookingApiService,
    private val sharedPrefsHelper: SharedPrefsHelper,
) : BaseOperationalViewModel() {
    val navigateBack = SingleLiveEvent<Intent>()
    val codeList = MutableLiveData<ArrayList<NationalityCode>>()

    init {
        getCountryDataInfo()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        val listResponse = (data.body as RestResponse<*>).response as ArrayList<NationalityCode>
        codeList.value = listResponse
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun getCountryDataInfo() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock { apiService.getNationalityCodeList(token) }
    }

    fun onClickItem(position: Int) {
        val intent = Intent()
        intent.putExtra(EXTRA_COUNTRY_CODE, codeList.value!![position].code)
        intent.putExtra(EXTRA_COUNTRY_NAME, codeList.value!![position].name)
        navigateBack.value = intent
    }

    companion object {
        const val EXTRA_COUNTRY_CODE = "EXTRA_COUNTRY_CODE"
        const val EXTRA_COUNTRY_NAME = "EXTRA_COUNTRY_NAME"
    }
}
