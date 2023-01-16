package net.sharetrip.wheel.view.spin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import java.lang.IllegalArgumentException

class SpinToWinViewModelFactory(private val sharedPrefsHelper: SharedPrefsHelper, private val repository: SpinToWinRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpinToWinViewModel::class.java))
            return SpinToWinViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel")
    }
}
