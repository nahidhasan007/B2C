package net.sharetrip.profile.view.travelerShow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.shared.model.EmptyResponse
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.ImageUploadResponse
import net.sharetrip.profile.model.RemoveTravelerInfo
import net.sharetrip.profile.model.Traveler
import net.sharetrip.profile.network.ProfileApiService
import okhttp3.MultipartBody

class TravelerShowRepo(private val apiService: ProfileApiService) : BaseRepository() {

    private val _imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val imageUploadResponse: LiveData<ImageUploadResponse>
        get() = _imageUploadResponse

    suspend fun removeTraveller(token: String, removeTravelerInfo: RemoveTravelerInfo) {
        callApi<EmptyResponse>(
            executableCodeBlock = { apiService.deleteTravelerInfo(token, removeTravelerInfo) },

            onSuccess = {},

            onFailed = { _, _ -> }
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

    suspend fun updateTravelerInfo(token: String, traveler: Traveler) {
        callApi<EmptyResponse>(
            executableCodeBlock = {
                apiService.updateTravelerInfo(token, traveler)
            },

            onSuccess = {},

            onFailed = { _, _ -> }
        )
    }
}
