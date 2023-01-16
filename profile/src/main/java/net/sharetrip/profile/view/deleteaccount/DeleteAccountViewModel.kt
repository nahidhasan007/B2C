package net.sharetrip.profile.view.deleteaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.profile.model.DeleteAccount
import net.sharetrip.profile.model.DeletionReason

class DeleteAccountViewModel(private val repo: DeleteAccountRepo, private val token: String) :
    BaseOperationalViewModel() {

    private val _deletionReasons = MutableLiveData<List<DeletionReason>>()
    val deletionReasons: LiveData<List<DeletionReason>>
        get() = _deletionReasons

    init {
        fetchAccountDeletionReasons()
    }

    private fun fetchAccountDeletionReasons() {
        executeSuspendedCodeBlock(DeleteAccountOperations.GET_REASONS.name) {
            repo.getReasons(token)
        }
    }

    fun deleteAccount(index: Int, comment: String) {
        executeSuspendedCodeBlock(DeleteAccountOperations.DELETE_ACCOUNT.name) {
            repo.deleteUserAccount(
                token,
                DeleteAccount(comment = comment, reason = _deletionReasons.value?.get(index)?.id!!)
            )
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            DeleteAccountOperations.GET_REASONS.name -> {
                val response = (data.body as RestResponse<*>).response as List<DeletionReason>
                _deletionReasons.postValue(response)
            }
            DeleteAccountOperations.DELETE_ACCOUNT.name -> {
                val responseMessage = (data.body as RestResponse<*>).message
                navigateWithArgument(DeleteAccountDestinations.LOGIN.name, responseMessage)
            }
        }
    }

    private enum class DeleteAccountOperations { GET_REASONS, DELETE_ACCOUNT }
    enum class DeleteAccountDestinations { LOGIN }
}