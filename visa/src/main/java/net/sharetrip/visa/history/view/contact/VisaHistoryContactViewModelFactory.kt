package net.sharetrip.visa.history.view.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.sharetrip.visa.history.model.PrimaryContactItem
import java.lang.IllegalArgumentException

class VisaHistoryContactViewModelFactory(val contact: PrimaryContactItem): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisaHistoryContactViewModel::class.java))
            return VisaHistoryContactViewModel(contact) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}