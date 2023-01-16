package net.sharetrip.signup.view.forget

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.isEmailValid
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.signup.network.SingUpApiService
import net.sharetrip.signup.utils.UtilText.SUCCESS
import net.sharetrip.signup.utils.UtilText.emptyEmailField
import net.sharetrip.signup.utils.UtilText.provideValidEmailMsg

class ForgetPasswordViewModel(val apiService: SingUpApiService) : BaseOperationalViewModel() {
    var mailId = ObservableField<String>()
    var snackBarMsg = ObservableField<String>()
    val goBack = MutableLiveData<Boolean>()

    fun onClickCancel() {
        goBack.value = true
    }

    fun onClickNext() {
        mailId.notifyChange()
        if (mailId.get().isNullOrEmpty()){
            snackBarMsg.set(emptyEmailField)
            snackBarMsg.notifyChange()
            return
        }
        if(mailId.get().isEmailValid()) {
            executeSuspendedCodeBlock(
                operationTag = forgetPassword,
                codeBlock = { apiService.getForgetPasswordResponse(mailId.get()!!) }
            )
        } else {
            snackBarMsg.set(provideValidEmailMsg)
            snackBarMsg.notifyChange()
        }
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            forgetPassword -> {
                val response = (data.body as RestResponse<*>)
                snackBarMsg.set(
                    if (response.code == SUCCESS) {
                        //goBack.value = true
                        response.message
                    } else {
                        response.message
                    }
                )
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        snackBarMsg.set(errorMessage)
    }

    private companion object {
        const val forgetPassword = "forget_password"
    }
}