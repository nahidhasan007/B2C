package net.sharetrip.flight.booking.view.nationality

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.booking.model.ApiCallingKey
import net.sharetrip.flight.booking.model.NationalityCode
import net.sharetrip.flight.network.DataManager

class NationalityViewModel(var sharedPrefsHelper: SharedPrefsHelper) : BaseOperationalViewModel() {

    val codeList = MutableLiveData<ArrayList<NationalityCode>>()

    init {
        getCountryDataInfo()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        if (operationTag == ApiCallingKey.CountryDataInfo.name) {
            codeList.value = (data.body as RestResponse<*>).response as ArrayList<NationalityCode>?
        }
        dataLoading.set(false)
    }

    private fun getCountryDataInfo() {
        dataLoading.set(true)
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        executeSuspendedCodeBlock(ApiCallingKey.CountryDataInfo.name) {
            DataManager.flightApiService.getNationalityCodeList(token)
        }
    }
}