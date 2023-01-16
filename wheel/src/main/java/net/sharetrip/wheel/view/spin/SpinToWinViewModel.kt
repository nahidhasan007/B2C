package net.sharetrip.wheel.view.spin

import android.graphics.Color
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.PrefKey.IS_LOGIN
import com.sharetrip.base.data.PrefKey.USER_NAME
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.GuestPopUpData
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.wheel.R
import net.sharetrip.wheel.library.model.LuckyItem
import net.sharetrip.wheel.model.DialogBody
import net.sharetrip.wheel.model.SpinItem
import net.sharetrip.wheel.model.WheelType

class SpinToWinViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val repository: SpinToWinRepository
) : BaseViewModel(), GuestLoginListener {

    private val _navigateToSelf = MutableLiveData<Event<Boolean>>()
    val navigateToSelf: LiveData<Event<Boolean>>
        get() = _navigateToSelf

    private var data = ArrayList<LuckyItem>()
    private var spinItemList = ArrayList<SpinItem>()
    private var spinCode: String = ""
    lateinit var profileAction: String
    var isPlayEnable = false

    private val _coinValue = MutableLiveData(Pair("init", "0"))
    val coinValue: LiveData<Pair<String, String>>
        get() = _coinValue

    var oldTripCoinValue = 0

    private val apiStatus = repository.apiStatus
    private val spinResponse = repository.spinResponse
    private val profileUpdateResponse = repository.userProfileResponse
    private val gamificationEventManager =
        AnalyticsProvider.gamificationEventManager(AnalyticsProvider.getFirebaseAnalytics())

    val luckyDataList = MutableLiveData<ArrayList<LuckyItem>>()
    val dialogProperty = ObservableField<DialogBody>()
    val isDialogVisible = ObservableBoolean(false)
    val selectedIndex = MutableLiveData<Int>()
    val sorryMessage = repository.sorryMessage
    val shareUrl = MutableLiveData<String>()
    val isShowMissingName = MutableLiveData<Boolean>()
    val isLoginLiveData = MutableLiveData<Boolean>()
    val isDataLoading = repository.isDataLoading
    val showToast = repository.showToast
    val isLogin = ObservableBoolean()
    val navigateToLogin = MutableLiveData<Event<Boolean>>()
    val goToProfileDetails = MutableLiveData<Event<Boolean>>()

    private val colorList = arrayListOf(
        "#00c5f5", "#43a046",
        "#e37616", "#de1921",
        "#444db4", "#1882ff",
        "#00c5f5", "#43a046",
        "#e37616", "#de1921",
        "#444db4", "#1882ff"
    )

    val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.spin_title,
            R.string.spin_body,
            R.drawable.ic_spinning_wheel, this
        )
    }

    init {
        checkLoginInformation()
    }

    fun checkLoginInformation() {
        isLogin.set(sharedPrefsHelper[IS_LOGIN, false])
        isLoginLiveData.value = isLogin.get()
        if (isLogin.get()) {
            fetchSpinWheelData()
        } else {
            _coinValue.value = Pair("init", "0")
        }
    }

    override fun onClickLogin() {
        navigateToLogin.value = Event(true)
    }

    private fun fetchSpinWheelData() {
        viewModelScope.launch {
            val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
            repository.getLuckyWheel(token)

            if (apiStatus.value == Status.SUCCESS) {
                _coinValue.value = Pair("init", spinResponse.value!!.tripCoinBalance.toString())
                oldTripCoinValue = _coinValue.value!!.second.toInt()
                data.clear()
                spinCode = spinResponse.value?.code!!
                for (i in spinResponse.value?.spinList?.indices!!) {
                    val spinItem = spinResponse.value?.spinList!![i]
                    spinItemList.add(spinItem)
                    val luckyItem = LuckyItem()

                    when (spinItem.icon) {
                        WheelType.FLIGHT.name -> luckyItem.icon = R.drawable.ic_flight_game
                        WheelType.HOTEL.name -> luckyItem.icon = R.drawable.ic_hotel_game
                        WheelType.VISA.name -> luckyItem.icon = R.drawable.ic_visa_game
                        WheelType.TOUR.name -> luckyItem.icon = R.drawable.ic_tour_game
                        WheelType.PACKAGE.name -> luckyItem.icon =
                            R.drawable.ic_holiday_game
                        WheelType.TRANSFER.name -> luckyItem.icon =
                            R.drawable.ic_transfer_game
                        WheelType.TRIPCOIN.name -> luckyItem.icon =
                            R.drawable.ic_tripcoin_game
                        WheelType.ZERO.name -> luckyItem.icon = R.drawable.ic_retry_game
                        else -> luckyItem.icon = R.drawable.ic_retry_game
                    }

                    luckyItem.color = Color.parseColor(colorList[i])

                    data.add(luckyItem)
                }
                luckyDataList.value = data
            }
        }
    }

    private fun fetchSpinSelectedWheelData() {
        isPlayEnable = false
        viewModelScope.launch {
            val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
            repository.sendLuckWheel(token, spinCode)

            if (apiStatus.value == Status.SUCCESS) {
                val dialogBody = DialogBody(
                    spinResponse.value!!.title,
                    spinResponse.value!!.message,
                    spinResponse.value!!.type
                )
                dialogProperty.set(dialogBody)
                var index = 0

                for (i in 0 until spinItemList.size) {
                    if (spinResponse.value!!.selected == spinItemList[i].code) {
                        index = i
                        if (spinItemList[i].icon == WheelType.TRIPCOIN.name) {
                            _coinValue.value =
                                Pair("win", spinResponse.value!!.tripCoinBalance.toString())
                        } else {
                            _coinValue.value =
                                Pair("prev", spinResponse.value!!.tripCoinBalance.toString())
                        }
                        break
                    }
                }

                selectedIndex.value = index
            } else {
                isPlayEnable = true
            }
        }
    }

    fun onClickSpinPlay() {
        gamificationEventManager.playSpin2Win()
        if (isLogin.get()) {
            if (sharedPrefsHelper[USER_NAME, ""] == "No Name") {
                isShowMissingName.value = true
            } else {
                if (isPlayEnable) {
                    fetchSpinSelectedWheelData()
                }
            }
        }
    }

    fun onBackPressed() {
        //todo screenSwitcher.goBack()
    }

    fun onClickLeaderBoard() {
        gamificationEventManager.clickSpin2WinLeaderBoard()
        profileAction = ProfileAction.ARG_LEADER_BOARD
        goToProfileDetails.value = Event(true)

    }

    fun onClickRewards() {
        gamificationEventManager.clickSpin2WinRewards()
        if (isLogin.get()) {
            profileAction = ProfileAction.ARG_REWARD_BOARD
            goToProfileDetails.value = Event(true)
        }

    }

    fun onClickFAQ() {
        gamificationEventManager.clickSpin2WinRules()
        profileAction = ProfileAction.ARG_TERM_AND_CONDITION_SPIN_TO_WIN
        goToProfileDetails.value = Event(true)

    }

    fun onClickTryAgain() {
        _navigateToSelf.value = Event(true)
    }

    fun onClickCloseDialog() {
        isDialogVisible.set(false)
        onClickTryAgain()
    }

    fun fetchShareUrl() {
        viewModelScope.launch {
            val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
            repository.getShareUrl(token, "Win wheel", "OK")
        }
    }

    fun sendUpdateProfile(firstName: String, lastName: String) {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        viewModelScope.launch {
            repository.updateUserProfile(token, firstName, lastName)

            if (apiStatus.value == Status.SUCCESS) {
                sharedPrefsHelper.put(USER_NAME, profileUpdateResponse.value!!.givenName)
            }
        }
    }

    fun showDialog() {
        isDialogVisible.set(true)
    }

}
