package net.sharetrip.tracker.view.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class TravelAdviceSearchVMFactory(private val repository: TravelAdviceSearchRepo): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelAdviceSearchViewModel::class.java))
            return TravelAdviceSearchViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
