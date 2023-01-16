package net.sharetrip.profile.view.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class CountryCurrencyVMFactory(
    private val checkingCode: String,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val countryCurrencyRepo: CountryCurrencyRepo
) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountryCurrencyViewModel::class.java))
            return CountryCurrencyViewModel(checkingCode, sharedPrefsHelper, countryCurrencyRepo) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
