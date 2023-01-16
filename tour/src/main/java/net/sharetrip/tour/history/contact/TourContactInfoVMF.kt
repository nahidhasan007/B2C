package net.sharetrip.tour.history.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.tour.model.PrimaryContact
import java.lang.IllegalArgumentException

class TourContactInfoVMF(private val contact: PrimaryContact): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourContactInfoViewModel::class.java))
            return TourContactInfoViewModel(contact) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
