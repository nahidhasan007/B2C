package net.sharetrip.profile.view.list

import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class CountryCurrencyViewModel(
    checkingCode: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: CountryCurrencyRepo
) : BaseViewModel(){

    val codeList = repository.codeList
    val isDataLoading = repository.isDataLoading

    init {
        when (checkingCode) {
            "country" -> getCountryDataInfo()
            "currency" -> getCurrencyDataInfo()
            "language" -> getLanguageDataInfo()
            "distance" -> getDistanceDataInfo()
        }
    }

    private fun getCountryDataInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getCountryDataFromRepo(token)
        }
    }

    private fun getCurrencyDataInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getCurrencyDataFromRepo(token)
        }
    }

    private fun getLanguageDataInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getLanguageDataFromRepo(token)
        }
    }

    private fun getDistanceDataInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getCountryDataFromRepo(token)
        }
    }

}
