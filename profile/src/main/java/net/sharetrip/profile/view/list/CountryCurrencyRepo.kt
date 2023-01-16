package net.sharetrip.profile.view.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.CountryCode
import net.sharetrip.profile.network.ProfileApiService
import java.util.ArrayList

class CountryCurrencyRepo(private val apiService: ProfileApiService) : BaseRepository(){

    private val _codeList = MutableLiveData<ArrayList<CountryCode>>()
    val codeList: LiveData<ArrayList<CountryCode>>
    get() = _codeList

    suspend fun getCountryDataFromRepo(token: String) {
        callApi<List<CountryCode>>(
            executableCodeBlock = {
                apiService.getCountryList(token)
            },

            onSuccess = {
                _codeList.value =  it as ArrayList<CountryCode>
            },

            onFailed = {_,_ ->}
        )
    }

    suspend fun getCurrencyDataFromRepo(token: String) {
        callApi<List<CountryCode>>(
            executableCodeBlock = {
                apiService.getCurrencyList(token)
            },

            onSuccess = {
                _codeList.value =  it as ArrayList<CountryCode>
            },

            onFailed = {_,_ ->}
        )
    }

    suspend fun getLanguageDataFromRepo(token: String) {
        callApi<List<CountryCode>>(
            executableCodeBlock = {
                apiService.getLanguageList(token)
            },

            onSuccess = {
                _codeList.value =  it as ArrayList<CountryCode>
            },

            onFailed = {_,_ ->}
        )
    }

    suspend fun getDistanceDataFromRepo(token: String){
        callApi<List<CountryCode>>(
            executableCodeBlock = {
                apiService.getDistanceList(token)
            },

            onSuccess = {
                _codeList.value =  it as ArrayList<CountryCode>
            },

            onFailed = {_,_ ->}
        )
    }
}
