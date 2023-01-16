package net.sharetrip.profile.view.travelerShow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.profile.model.Traveler
import java.lang.IllegalArgumentException

class TravelerShowVMFactory(
    private val passengerInfo: Traveler,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val travelerShowRepo: TravelerShowRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelerShowViewModel::class.java))
            return TravelerShowViewModel(passengerInfo, sharedPrefsHelper, travelerShowRepo) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
