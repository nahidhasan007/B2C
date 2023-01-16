package net.sharetrip.profile.view.nahid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.profile.view.nahid.MyUserInfoViewModel

class MyUserInfoVMF(private val sharedPref: SharedPrefsHelper, private val myUserInfoRepository: MyUserInfoRepository)
    :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if(modelClass.isAssignableFrom(MyUserInfoViewModel::class.java))
           return MyUserInfoViewModel(sharedPref,myUserInfoRepository) as T
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}