package net.sharetrip.tour.booking.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.tour.model.TourParam
import java.lang.IllegalArgumentException

class ClientContactVMF(
    private val tourParam: TourParam,
    private val sharedPrefsHelper: SharedPrefsHelper, private val repo: ClientContactRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientContactViewModel::class.java))
            return ClientContactViewModel(tourParam, sharedPrefsHelper, repo) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
