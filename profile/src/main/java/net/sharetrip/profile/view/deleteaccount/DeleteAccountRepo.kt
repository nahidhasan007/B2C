package net.sharetrip.profile.view.deleteaccount

import net.sharetrip.profile.model.DeleteAccount
import net.sharetrip.profile.network.ProfileApiService

class DeleteAccountRepo(private val profileApiService: ProfileApiService) {

    suspend fun getReasons(token: String) = profileApiService.getAccountDeletionReasons(token)

    suspend fun deleteUserAccount(token: String, deleteAccount: DeleteAccount) = profileApiService.deleteAccount(token, deleteAccount)
}