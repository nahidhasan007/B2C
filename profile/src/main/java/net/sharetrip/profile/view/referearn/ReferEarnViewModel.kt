package net.sharetrip.profile.view.referearn

import android.content.ClipboardManager
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel

class ReferEarnViewModel(private val sharedPrefsHelper: SharedPrefsHelper) : BaseViewModel() {

    val code: String = sharedPrefsHelper[PrefKey.USER_REFERRAL_CODE, ""]
    val referralCoin: String
    val intentLiveData = MutableLiveData<Intent>()
    lateinit var clipboardManager: ClipboardManager

    init {
        val coin = sharedPrefsHelper[PrefKey.USER_REFERRAL_COIN, ""]
        referralCoin = "Invite More Friends and Get $coin Trip Coins"
    }

}