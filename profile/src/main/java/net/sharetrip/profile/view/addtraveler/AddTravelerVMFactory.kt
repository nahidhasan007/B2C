package net.sharetrip.profile.view.addtraveler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class AddTravelerVMFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: AddTravelerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTravelerViewModel::class.java))
            return AddTravelerViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
