package net.sharetrip.profile.view.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.ImageUploadResponse
import net.sharetrip.profile.model.UserProfile
import net.sharetrip.profile.network.ProfileApiService
import okhttp3.MultipartBody

class UserInfoRepository(private val apiService: ProfileApiService) : BaseRepository() {

    private val _userProfileResponse = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile>
        get() = _userProfileResponse

    private val _imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val imageUploadResponse: LiveData<ImageUploadResponse>
        get() = _imageUploadResponse

    suspend fun getProfileInfoFromRepo(token: String) {
        callApi<UserProfile>(
            executableCodeBlock = {
                apiService.getProfileResponse(token)
            },

            onSuccess = {
                _userProfileResponse.value = it
            },

            onFailed = { _, _ -> }
        )
    }

    suspend fun updateProfileInfoFromRepo(token: String, userProfile: UserProfile) {
        callApi<UserProfile>(
            executableCodeBlock = {
                apiService.updateProfile(token, userProfile)
            },

            onSuccess = {
                _userProfileResponse.value = it
            },

            onFailed = { _, _ -> }
        )
    }

    suspend fun uploadFileToServer(token: String, file: MultipartBody.Part) {
        callApiForFileUpload<ImageUploadResponse>(
            executableCodeBlock = {
                apiService.sendFile(token, file)
            },

            onSuccess = {
                _imageUploadResponse.value = it
            },

            onFailed = { _, _ -> }
        )
    }

    suspend fun uploadAvatarPhotoToServer(token: String, file: MultipartBody.Part) {
        callApiForFileUpload<ImageUploadResponse>(
            executableCodeBlock = {
                apiService.sendImageAvater(token, file)
            },

            onSuccess = {
                _imageUploadResponse.value = it
            },

            onFailed = { _, _ -> }
        )
    }
}