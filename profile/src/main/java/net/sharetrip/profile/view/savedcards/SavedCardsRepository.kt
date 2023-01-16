package net.sharetrip.profile.view.savedcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.RemoveCard
import net.sharetrip.profile.model.SavedCards
import net.sharetrip.profile.network.ProfileApiService

class SavedCardsRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _savedCardsList = MutableLiveData<List<SavedCards>>()
    val savedCardsList: LiveData<List<SavedCards>>
        get() = _savedCardsList

    suspend fun getSavedCards(token: String) {
        callApi<List<SavedCards>>(
            executableCodeBlock = {
                apiService.fetchSavedCards(token)
            },

            onSuccess = {
                _savedCardsList.value = it
            },

            onFailed = { _, _ ->
                showMessage.value = Event("Something went wrong")
            }
        )
    }

    suspend fun deleteSavedCard(token: String, removeCard: RemoveCard) {
        callApi<List<SavedCards>>(
            executableCodeBlock = {
                apiService.deleteSavedCard(token, removeCard)
            },

            onSuccess = {
                _savedCardsList.value = it
            },

            onFailed = { _, _ ->
                showMessage.value = Event("Something went wrong")
            }
        )
    }
}