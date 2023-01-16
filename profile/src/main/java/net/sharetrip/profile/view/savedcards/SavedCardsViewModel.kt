package net.sharetrip.profile.view.savedcards

import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.RemoveCard

class SavedCardsViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: SavedCardsRepository
) : BaseViewModel() {

    val isDataLoading = repository.isDataLoading
    val savedCardList = repository.savedCardsList
    val showToast = repository.showMessage

    init {
        fetchSavedCards()
    }

    private fun fetchSavedCards() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.getSavedCards(token)
        }
    }

    fun deleteSavedCards(uid: String) {
        dataLoading.set(true)

        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.deleteSavedCard(token, RemoveCard(uid))
        }
    }

    fun onShow() {
        fetchSavedCards()
    }

}
