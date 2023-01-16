package net.sharetrip.view.notification

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.UserNotification
import net.sharetrip.shared.utils.convertString
import com.sharetrip.base.viewmodel.BaseViewModel
import java.text.NumberFormat
import java.util.*

class NotificationViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    repo: NotificationRepo
) : BaseViewModel(), DetailForward {

    val dealsLiveData = repo.getNotification().cachedIn(viewModelScope)
    var formattedPoints = ObservableField<String>()
    val goToNotificationDetails = MutableLiveData<Event<Boolean>>()

    init {
        setPoints()
    }

    private fun setPoints() {
        var points = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }
        if (points.isBlank()) {
            points = "0"
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        }
        formattedPoints.set(NumberFormat.getNumberInstance(Locale.US).format(points.toInt()))
    }

    override fun navigateToDetail(notification: UserNotification) {
        sharedPrefsHelper.put(PrefKey.NOTIFICATION_DETAIL, notification.convertString())
        goToNotificationDetails.value = Event(true)
    }
}

interface DetailForward {
    fun navigateToDetail(notification: UserNotification)
}
