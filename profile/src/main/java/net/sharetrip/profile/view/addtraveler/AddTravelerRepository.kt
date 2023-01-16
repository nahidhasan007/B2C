package net.sharetrip.profile.view.addtraveler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.EmptyResponse
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.ImageUploadResponse
import net.sharetrip.profile.model.Traveler
import net.sharetrip.profile.network.ProfileApiService
import okhttp3.MultipartBody

class AddTravelerRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val imageUploadResponse: LiveData<ImageUploadResponse>
        get() = _imageUploadResponse

    suspend fun addTraveller(token: String, traveler: Traveler) {
        callApi<EmptyResponse>(
            executableCodeBlock = { apiService.addTraveler(token, traveler) },

            onSuccess = {
                showMessage.value = Event("New traveler has been added")
            },

            onFailed = { _, _ ->
                showMessage.value = Event("New traveler addition not successful")
            }
        )
    }

    suspend fun uploadImage(token: String, file: MultipartBody.Part) {
        callApiForFileUpload<ImageUploadResponse>(
            executableCodeBlock = { apiService.sendFile(token, file) },

            onSuccess = {
                _imageUploadResponse.value = it
            },

            onFailed = { _, _ -> }
        )
    }
}
