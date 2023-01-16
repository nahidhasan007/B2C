package net.sharetrip.profile.view.password

import com.sharetrip.base.event.Event
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.ChangePassword
import net.sharetrip.profile.model.ChangePasswordResponse
import net.sharetrip.profile.network.ProfileApiService

class PasswordRepository(private val apiService: ProfileApiService) : BaseRepository() {

    suspend fun updateUserPassword(token: String, password: ChangePassword) {
        callApi<ChangePasswordResponse>(
            executableCodeBlock = {
                apiService.updatePassword(token, password)
            },

            onSuccess = {
                showMessage.value = Event("Password updated")
            },

            onFailed = { _, errorMessage ->
                showMessage.value = Event(errorMessage)
            }
        )
    }
}
