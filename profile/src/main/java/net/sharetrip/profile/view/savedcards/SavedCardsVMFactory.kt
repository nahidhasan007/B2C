package net.sharetrip.profile.view.savedcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper

class SavedCardsVMFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: SavedCardsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedCardsViewModel::class.java))
            return SavedCardsViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
