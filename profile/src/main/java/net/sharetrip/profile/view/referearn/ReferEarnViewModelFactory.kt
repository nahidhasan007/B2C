package net.sharetrip.profile.view.referearn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class ReferEarnViewModelFactory(private val sharedPrefsHelper: SharedPrefsHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReferEarnViewModel::class.java))
            return ReferEarnViewModel(sharedPrefsHelper) as T

        throw IllegalArgumentException("Known ViewModel Class")
    }
}