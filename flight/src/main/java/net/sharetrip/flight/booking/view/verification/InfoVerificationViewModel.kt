package net.sharetrip.flight.booking.view.verification

import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.isPhoneNumberValid
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.model.PrimaryContact
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.EMAIL_ADDRESS_IS_NOT_VALID
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.PHONE_NUMBER_IS_EMPTY
import net.sharetrip.flight.booking.model.repository.localdatasource.UIMessageData.Companion.PLEASE_PROVIDE_A_VALID_PHONE_NUMBER
import net.sharetrip.flight.network.DataManager

class InfoVerificationViewModel(
    var itemTravelers: MutableList<ItemTraveler>,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel(), ShowImageListener {
    val enableDataUpdate = ObservableBoolean()
    val isButtonActive = ObservableBoolean()
    val phoneNumber = ObservableField<String>()
    val emailId = ObservableField<String>()
    val msg = MutableLiveData<String>()
    val onShowImageClicked = MutableLiveData<Event<String>>()
    val onContinueClicked = MutableLiveData<Event<Boolean>>()
    var imageTag = ""

    init {
        isButtonActive.set(false)
        enableDataUpdate.set(false)
        fetchPrimaryContact()
    }

    override fun showImage(imageUrl: String, tag: String) {
        onShowImageClicked.value = Event(imageUrl)
        imageTag = tag
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        if (operationTag == "PrimaryContact") {
            phoneNumber.set(((data.body as RestResponse<*>).response as PrimaryContact).phoneNumber)
            emailId.set(((data.body as RestResponse<*>).response as PrimaryContact).email)
        }
    }

    private fun isValidContact(): Boolean {
        when {
            TextUtils.isEmpty(phoneNumber.get().toString()) -> {
                msg.value = PHONE_NUMBER_IS_EMPTY
                return false
            }

            !phoneNumber.get().isPhoneNumberValid() -> {
                msg.value = PLEASE_PROVIDE_A_VALID_PHONE_NUMBER
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(emailId.get().toString()).matches() -> {
                msg.value = EMAIL_ADDRESS_IS_NOT_VALID
                return false
            }

            phoneNumber.get()!!.length !in 6..15 -> {
                msg.value = PLEASE_PROVIDE_A_VALID_PHONE_NUMBER
                return false
            }

            else -> return true
        }
    }

    private fun fetchPrimaryContact() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock("PrimaryContact") {
            DataManager.flightApiService.getPrimaryContactResponse(token)
        }
    }

    fun onClickContinueButton() {
        if (isValidContact()) {
            itemTravelers[0].mobileNumber = phoneNumber.get()
            itemTravelers[0].email = emailId.get()
            onContinueClicked.value = Event(true)
        }
    }

    fun onClickCheckBox(view: View) {
        isButtonActive.set((view as AppCompatCheckBox).isChecked)
    }

    fun onClickUpdate(view: View) {
        val title = (view as AppCompatTextView).text
        if (title.toString().lowercase() == PrefKey.EDIT.lowercase()) {
            view.text = PrefKey.SAVE
            enableDataUpdate.set(true)
        } else {
            if (isValidContact()) {
                view.text = PrefKey.EDIT
                enableDataUpdate.set(false)
            }
        }
    }
}
