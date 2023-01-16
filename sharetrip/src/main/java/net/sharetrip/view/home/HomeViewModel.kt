package net.sharetrip.view.home

import androidx.lifecycle.MutableLiveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.RemoteConfigParam
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.BuildConfig
import net.sharetrip.R
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.holiday.booking.model.HolidayListResponse
import net.sharetrip.network.MainApiService

class HomeViewModel(
    private val apiService: MainApiService,
    private val remoteConfig: FirebaseRemoteConfig,
    private val sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {

    val holidayList = MutableLiveData<ArrayList<HolidayItem>>()
    val holidays = ArrayList<HolidayItem>()
    val navigateToHoliday = MutableLiveData<Event<Boolean>>()
    val gotoFlight = MutableLiveData<Event<Boolean>>()
    val gotoHotel = MutableLiveData<Event<Boolean>>()
    val gotoVisa = MutableLiveData<Event<Boolean>>()
    val gotToTour = MutableLiveData<Event<Boolean>>()
    val showDialog = MutableLiveData<Boolean>()
    val appVersion = MutableLiveData<String>()
    private var count = 0
    var requestCode = 0

    init {
        configRemote()
        fetchRemoteConfigData()
        fetchHolidayList()
    }

    fun fetchHolidayList() {
        if (holidays.isEmpty() || holidays.size < count) {
            executeSuspendedCodeBlock(fetchHolidayList) {
                apiService.fetchPopularHoliday(10, holidays.size)
            }
        }
    }

    fun openHotel() {
        gotoHotel.value = Event(true)
    }

    fun openFlight() {
        gotoFlight.value = Event(true)
    }

    fun openBundle() {
        navigateToHoliday.value = Event(true)
    }

    fun openVisa() {
        gotoVisa.value = Event(true)
    }

    fun openTour() {
        gotToTour.value = Event(true)
    }

    fun openTransport() {
        // TODO
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            fetchHolidayList -> {
                val response = (data.body as RestResponse<*>).response as HolidayListResponse
                if (response!= null){
                    count = try {
                        response.count
                    } catch (e: Exception) {
                        30
                    }
                    val holidayItemList = response.data
                    for (holidayItem in holidayItemList) {
                        if (holidayItem.image.isNotEmpty()) {
                            val shuffledImages = holidayItem.image.shuffled()
                            holidayItem.image.clear()
                            holidayItem.image.addAll(shuffledImages)
                        }
                    }
                    holidayList.value = holidayItemList as ArrayList<HolidayItem>?
                    holidays.addAll(holidayItemList)
                }
            }
        }
    }

    private fun configRemote() {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    private fun fetchRemoteConfigData() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val codeVersion = BuildConfig.VERSION_CODE
                    appVersion.value =
                        remoteConfig.getString(RemoteConfigParam.ANDROID_APP_VERSION.key)
                    val latestCodeVersion =
                        remoteConfig.getLong(RemoteConfigParam.ANDROID_CODE_VERSION.key)
                    val isForceUpdateEnable =
                        remoteConfig.getBoolean(RemoteConfigParam.FORCE_UPDATE_ENABLE.key)
                    sharedPrefsHelper.put(
                        PrefKey.FLIGHT_DISCOUNT_OFFER_BANK_LIST,
                        remoteConfig.getString(RemoteConfigParam.FLIGHT_DISCOUNT_BANK_LIST.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.FLIGHT_SEARCH_THRESHOLD_TIME,
                        remoteConfig.getString(RemoteConfigParam.FLIGHT_SEARCH_THRESHOLD_TIME.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.QUIZ_TERM_AND_CONDITION,
                        remoteConfig.getString(RemoteConfigParam.QUIZ_TERM_AND_CONDITION.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.QUIZ_FAQ,
                        remoteConfig.getString(RemoteConfigParam.QUIZ_FAQ.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.QUIZ_HOMEPAGE_PROMOTION_TEXT,
                        remoteConfig.getString(RemoteConfigParam.QUIZ_HOMEPAGE_PROMOTION_TEXT.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.QUIZ_TIMEOUT_TIME,
                        remoteConfig.getString(RemoteConfigParam.QUIZ_TIMEOUT_TIME.key).toLong()
                    )
                    sharedPrefsHelper.put(
                        PrefKey.FLIGHT_DISCOUNT_OPTIONS,
                        remoteConfig.getString(RemoteConfigParam.FLIGHT_DISCOUNT_OPTIONS.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.HOTEL_DISCOUNT_OPTIONS,
                        remoteConfig.getString(RemoteConfigParam.HOTEL_DISCOUNT_OPTIONS.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.NEED_TO_SHOW_BANNER,
                        remoteConfig.getBoolean(RemoteConfigParam.NEED_TO_SHOW_BANNER.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.BOOK_TODAY_FLIGHT,
                        remoteConfig.getBoolean(RemoteConfigParam.BOOK_TODAY_FLIGHT.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.ENCRYPT_KEY,
                        remoteConfig.getString(RemoteConfigParam.ENCRYPT_KEY.key)
                    )
                    sharedPrefsHelper.put(
                        PrefKey.PAYMENT_URLS,
                        remoteConfig.getString(RemoteConfigParam.PAYMENT_SUCCESS_URL.key)
                    )

                    sharedPrefsHelper.put(
                        PrefKey.CRISP_WEB_ID,
                        remoteConfig.getString(RemoteConfigParam.CRISP_WEBSITE_ID.key)
                    )

                    if (codeVersion < latestCodeVersion && isForceUpdateEnable) {
                        if (!BuildConfig.DEBUG) {
                            showDialog.value = true
                        }
                    }
                }
            }
    }

    private companion object {
        const val fetchHolidayList = "fetch_holiday_list"
    }
}