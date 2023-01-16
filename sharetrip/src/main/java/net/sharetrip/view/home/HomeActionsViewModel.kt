package net.sharetrip.view.home

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.plattysoft.leonids.ParticleSystem
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.PrefKey.IS_LOGIN
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.R
import net.sharetrip.holiday.booking.model.HolidayCity
import net.sharetrip.holiday.utils.HolidayNavigationActions
import net.sharetrip.model.TreasureResponse
import net.sharetrip.network.MainApiService
import java.io.FileDescriptor
import java.text.NumberFormat
import java.util.*

class HomeActionsViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val apiService: MainApiService
) : TreasureBoxListener, BaseOperationalViewModel() {

    private val _navigateToTour = MutableLiveData<Event<Boolean>>()
    val navigateToTour: LiveData<Event<Boolean>>
        get() = _navigateToTour

    private val timerHandler = Handler(Looper.getMainLooper())
    private val player: MediaPlayer
    private var oldPoint = 0
    private var newPoint = 0

    val gotoHoliday = MutableLiveData<Event<HolidayNavigationActions>>()
    var holidayCityName: String = ""
    var holidayCityCode: String = ""
    val gotoHotel = MutableLiveData<Event<Boolean>>()
    val gotoVisa = MutableLiveData<Event<Boolean>>()
    val gotoBusBooking = MutableLiveData<Event<Boolean>>()
    val gotoWheelActivity = MutableLiveData<Event<Boolean>>()
    val gotoFlightTracker = MutableLiveData<Event<Boolean>>()
    val popularCity = MutableLiveData<List<HolidayCity>>()
    val openFlight = MutableLiveData<Event<Boolean>>()
    val isInternetAvailable = MutableLiveData<Boolean>()
    val timerInfo = ObservableField<String>()
    val showTimer = ObservableBoolean(true)
    val particleAnimation = MutableLiveData<Event<Boolean>>()
    val coinAnimation = MutableLiveData<Event<Boolean>>()
    val isTreasure = MutableLiveData<Event<Boolean>>()

    val homePageEventManager = AnalyticsProvider.homePageEventManager(AnalyticsProvider.getFirebaseAnalytics())
    val showToastMessage = MutableLiveData<Event<String>>()

    init {
        timerInfo.set("00:00:00")
        fetchPopularCity()
        player = MediaPlayer()
        if (PrefKey.IS_BANNER_FIRST_TIME && sharedPrefsHelper[PrefKey.NEED_TO_SHOW_BANNER, true]) {
            //showBanner.value = Event(true)
        }
    }

    private fun checkNetworkAndExecuteTask(executeCode: () -> Unit) {
        // if (NetworkUtil.hasNetwork(context))
        executeCode()
        //else
        //  isInternetAvailable.value = false
    }

    private fun fetchPopularCity() {
        executeSuspendedCodeBlock(
            operationTag = fetchHolidayList,
            codeBlock = { apiService.fetchPopularCityForHoliday() }
        )
    }

    private fun fetchTreasure(accessToken: String) {
        executeSuspendedCodeBlock(
            operationTag = fetchTreasure,
            codeBlock = { apiService.getTreasureResponse(accessToken) }
        )
    }

    fun openWheel() {
        homePageEventManager.clickOnSpin2Win()
        gotoWheelActivity.value = Event(true)
    }

    fun openTrivia() {
    }

    fun openHotel() {
        homePageEventManager.clickOnHotel()
        checkNetworkAndExecuteTask { gotoHotel.value = Event(true) }
    }

    fun openFlight() {
        homePageEventManager.clickOnFlight()
        checkNetworkAndExecuteTask { openFlight.value = Event(true) }
    }

    fun openBus() {
        homePageEventManager.clickOnBus()
        checkNetworkAndExecuteTask { gotoBusBooking.value = Event(true) }
    }

    fun openHoliday() {
        homePageEventManager.clickOnHoliday()
        checkNetworkAndExecuteTask {
            gotoHoliday.value = Event(HolidayNavigationActions.VISIT_HOLIDAY_SEARCH)
        }
    }

    fun openTransport() {
    }

    fun openTour() {
        checkNetworkAndExecuteTask {
            _navigateToTour.value = Event(true)
        }
    }

    fun openVisa() {
        checkNetworkAndExecuteTask {
            gotoVisa.value = Event(true)
        }
    }

    fun openFlightTracker() {
        checkNetworkAndExecuteTask {
            gotoFlightTracker.value = Event(true)
        }
    }

    fun navigateToHoliday() {
        homePageEventManager.selectHolidayPCityFromHomeScreen()
        checkNetworkAndExecuteTask {
            gotoHoliday.value = Event(HolidayNavigationActions.VISIT_HOLIDAY_SEARCH)
        }
    }

    fun navigateToHolidayList(position: Int) {
        homePageEventManager.selectHolidayPCityFromHomeScreen()
        checkNetworkAndExecuteTask {
            holidayCityName = popularCity.value!![position].name
            holidayCityCode = popularCity.value!![position].code
            gotoHoliday.value = Event(HolidayNavigationActions.VISIT_HOLIDAY_LIST)
        }
    }

    fun clickOnCoinBox() {
        showToastMessage.value = Event("Wait for open")
    }

    override fun showTimer() {
        updateTreasureBoxTimer()
    }

    override fun showWinCoin(context: Context, view: View) {
        if (sharedPrefsHelper[IS_LOGIN, false]) {
            fetchTreasure(context as Activity, view)
        } else {
            isTreasure.postValue(Event(true))
        }
         homePageEventManager.openTreasureBox()
    }

    private fun updateTreasureBoxTimer() {
        val currentTime = (System.currentTimeMillis() / 1000).toInt()
        val expiryTime =
            sharedPrefsHelper[PrefKey.FREE_TRIP_COIN_TIME, currentTime - 2000] // safely assume

        if (currentTime < expiryTime) {
            timerInfo.set(getSecondsToFormattedTime(expiryTime - currentTime))
            showTimer.set(true)
            timerHandler.postDelayed(timerRunnable, 1000)
        } else {
            showTimer.set(false)
            timerHandler.removeCallbacks(timerRunnable)
        }
    }

    private fun getSecondsToFormattedTime(seconds: Int): String {
        val hours = seconds / 3600
        val mins = (seconds - hours * 3600) / 60
        val seconds = (seconds - hours * 3600 - mins * 60)
        return "${if (hours < 10) "0" else ""}$hours:${if (mins < 10) "0" else ""}$mins:${if (seconds < 10) "0" else ""}$seconds"
    }

    private var timerRunnable: Runnable = Runnable {
        updateTreasureBoxTimer()
    }

    private fun playCoinDropSound(activity: Activity) {
        val afd: AssetFileDescriptor = activity.resources.openRawResourceFd(R.raw.coins_falling)
        val fileDescriptor: FileDescriptor = afd.fileDescriptor

        try {
            player.setDataSource(fileDescriptor, afd.startOffset, afd.length)
            player.isLooping = false
            player.prepare()
            player.start()
        } catch (exception: Exception) {
        }
    }

    private fun fetchTreasure(activity: Activity, view: View) {
        val currentTime = (System.currentTimeMillis() / 1000).toInt()
        val expiryTime =
            sharedPrefsHelper[PrefKey.FREE_TRIP_COIN_TIME, currentTime - 2000] // safely assume

        if (currentTime < expiryTime)
            return

        rotate.duration = 500
        rotate.interpolator = AccelerateDecelerateInterpolator()
        rotate.repeatCount = Animation.INFINITE
        view.startAnimation(rotate)
        playCoinDropSound(activity)
        val accessToken = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]

        fetchTreasure(accessToken)

    }

    fun animateTripCoinChange(tripCoinTextView: AppCompatTextView, from: Int, to: Int) {
        val originalSize = tripCoinTextView.textSize

        val animatorEnlarge = ValueAnimator.ofFloat(originalSize, originalSize + 20f)
        animatorEnlarge.duration = 100
        animatorEnlarge.addUpdateListener {
            tripCoinTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
        }
        animatorEnlarge.start()

        val animator = ValueAnimator.ofInt(from, to)
        animator.startDelay = 100
        animator.duration = 1000
        animator.addUpdateListener {
            tripCoinTextView.text = NumberFormat.getNumberInstance(Locale.US)
                .format(it.animatedValue)
        }
        animator.start()

        val animatorDecrease = ValueAnimator.ofFloat(tripCoinTextView.textSize, originalSize)
        animatorDecrease.startDelay = 1100
        animatorDecrease.duration = 100
        animatorDecrease.addUpdateListener {
            tripCoinTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.animatedValue as Float)
        }
        animatorDecrease.start()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            fetchHolidayList -> {
                val response = (data.body as RestResponse<*>).response as List<HolidayCity>
                if (!response.isNullOrEmpty()) {
                    popularCity.value = response
                }
            }
            fetchTreasure -> {
                processTreasureResponse(data.body as RestResponse<TreasureResponse>)
            }
        }
    }

    private fun processTreasureResponse(response: RestResponse<TreasureResponse>) {
        val nextFreeCoinTime =
            (System.currentTimeMillis() / 1000 + response.response.remaining?.toInt()!!).toInt()
        sharedPrefsHelper.put(PrefKey.FREE_TRIP_COIN_TIME, nextFreeCoinTime)

        updateTreasureBoxTimer()

        rotate.cancel()

        if (!response.message.contains("Sorry")) {
            val point = sharedPrefsHelper[PrefKey.USER_TRIP_COIN, "0"]
            val treasureBoxCoin = sharedPrefsHelper[PrefKey.TREASURE_BOX_COIN, 0]
            newPoint = point.replace(",", "").toInt() + treasureBoxCoin
            val userCoin =
                NumberFormat.getNumberInstance(Locale.US).format(newPoint)
            sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, userCoin)

            oldPoint = Integer.parseInt(point.replace(",", ""))
            particleAnimation.value = Event(true)
            coinAnimation.value = Event(true)
        }
    }

    @SuppressLint("RestrictedApi")
    fun runParticleAnimation(activity: Activity, startView: View) {
        ParticleSystem(
            activity,
            120,
            AppCompatDrawableManager.get()
                .getDrawable(activity as Context, R.drawable.ic_trip_coin_yellow_20dp),
            1000
        )
            .setSpeedModuleAndAngleRange(0.1f, 0.5f, 180, 360)
            .setRotationSpeed(144f)
            .oneShot(startView, 120)
    }

    fun animateCoin(view: AppCompatTextView) {
        animateTripCoinChange(view, oldPoint, newPoint)
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
         showMessage(errorMessage)
    }

    fun onDestroy() {
        player.release()
    }

    private companion object {
        const val fetchHolidayList = "fetch_holiday_list"
        const val fetchTreasure = "fetch_treasure"

        val rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
    }
}
