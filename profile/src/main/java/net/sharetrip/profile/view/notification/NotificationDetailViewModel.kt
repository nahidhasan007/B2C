package net.sharetrip.profile.view.notification

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.UserNotification
import net.sharetrip.shared.utils.getUserNotification
import com.sharetrip.base.viewmodel.BaseViewModel

class NotificationDetailViewModel(sharedPrefsHelper: SharedPrefsHelper): BaseViewModel() {
    val notification: UserNotification?

    val imageUrl = MutableLiveData<String>()

    init {
        val notificationString = sharedPrefsHelper[PrefKey.NOTIFICATION_DETAIL, ""]
        notification = notificationString.getUserNotification()
        imageUrl.value = notification?.imageUrl!!
    }
}
