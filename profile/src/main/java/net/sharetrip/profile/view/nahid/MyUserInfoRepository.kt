package net.sharetrip.profile.view.nahid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.ImageUploadResponse
import net.sharetrip.profile.model.UserProfile
import net.sharetrip.profile.network.ProfileApiService

class MyUserInfoRepository(private val api: ProfileApiService) : BaseRepository(){

    private val _myProfileResponse= MutableLiveData<UserProfile>()
    val myProfile: LiveData<UserProfile>
    get() = _myProfileResponse

    private val _imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val imageUploadResponse: LiveData<ImageUploadResponse>
    get() = _imageUploadResponse

    suspend fun getProfileInfoFromRepo(token: String) {
        callApi<UserProfile>(
                executableCodeBlock = {
                    api.getProfileResponse(token)
                },
        onSuccess = {
            _myProfileResponse.value = it
        },
        onFailed = { _, _ -> }
        )
    }

    suspend fun updateProfileInfoFromRepo(token: String, userProfile:
    UserProfile) {
        callApi<UserProfile>(
                executableCodeBlock = {
                    api.updateProfile(token, userProfile)
                },
        onSuccess = {
            _myProfileResponse.value = it
        },
        onFailed = { _, _ -> }
        )
    }

}

