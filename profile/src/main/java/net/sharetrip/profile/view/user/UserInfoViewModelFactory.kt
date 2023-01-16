package net.sharetrip.profile.view.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import java.lang.IllegalArgumentException

class UserInfoViewModelFactory(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: UserInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserInfoViewModel::class.java))
            return UserInfoViewModel(sharedPrefsHelper, repository) as T

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
