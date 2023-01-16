package net.sharetrip.profile.view.password

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.Status
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.ChangePassword

class PasswordViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: PasswordRepository
) : BaseViewModel() {

    val password = ObservableField<ChangePassword>()
    val toastMsg = MutableLiveData<String>()
    val apiStatus = repository.apiStatus
    val showToast = repository.showMessage
    val navigateLogin = MutableLiveData<Event<Boolean>>()

    init {
        password.set(ChangePassword())
    }

    fun onClickUpdatePassword() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        viewModelScope.launch {
            repository.updateUserPassword(token, password.get()!!)

            if (apiStatus.value == Status.SUCCESS) {
                sharedPrefsHelper.put(PrefKey.ACCESS_TOKEN, "")
                sharedPrefsHelper.put(PrefKey.IS_LOGIN, false)
                sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
                sharedPrefsHelper.put(PrefKey.QUIZ_NEXT_DAY, 0L)
                navigateLogin.value = Event(true)
            }
        }
    }

}
