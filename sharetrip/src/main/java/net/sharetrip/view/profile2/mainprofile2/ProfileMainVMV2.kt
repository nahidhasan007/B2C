package net.sharetrip.view.profile2.mainprofile2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.view.profile.mainprofile.ProfileMainViewModel2

class ProfileMainVMV2(val sharedPrefsHelper: SharedPrefsHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileMainViewModel2::class.java))
            return ProfileMainViewModel2(sharedPrefsHelper) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}