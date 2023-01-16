package net.sharetrip.view.profile.loyalty

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.GuestPopUpData
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.RestResponse
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.R
import net.sharetrip.model.TripCoinItem
import net.sharetrip.model.TripCoinSummaryResponse
import net.sharetrip.model.UserLoyaltyLevel
import net.sharetrip.network.MainApiService
import net.sharetrip.profile.model.ProfileAction
import java.text.NumberFormat
import java.util.*

class LoyaltyViewModel(val apiService: MainApiService, val sharedPrefsHelper: SharedPrefsHelper) :
    BaseOperationalViewModel(), GuestLoginListener {

    val descriptionText =
        ObservableField("Users with upto 15,000 Trip Coins are Silver Users. Earn more than 15,000 Trip Coins to be a Gold User.")
    val tripCoinText = ObservableField("150 Trip Coins")
    val visaText = ObservableField("2 Times(Only Service Charge)")
    var couponText = ObservableField("BDT 5000")
    var isSilverSelected = ObservableBoolean(true)
    var isLogin = ObservableBoolean()
    var isGoldSelected = ObservableBoolean(false)
    val tripCoinItems = MutableLiveData<ArrayList<TripCoinItem>>()
    val availableTripCoin = ObservableField<String>()
    val pendingTripCoin = ObservableField<String>()
    val expiringTripCoin = ObservableField<String>()
    var isPlatinumSelected = ObservableBoolean(false)
    var userName = ObservableField<String>()
    var userType = ObservableField<String>()
    var userCard = MutableLiveData<String>()
    var userTripCoin = ObservableField<String>()
    var isCouponEnabled = ObservableBoolean(false)
    val goToProfileDetails = MutableLiveData<Event<Boolean>>()
    val goToLogin = MutableLiveData<Event<Boolean>>()
    lateinit var profileAction: String

    private val loyaltyEventManager = AnalyticsProvider.loyaltyEventManager(AnalyticsProvider.getFirebaseAnalytics())

    val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.loyalty_title,
            R.string.loyalty_body,
            R.drawable.ic_tripcoins_guest, this
        )
    }

    init {
        fetchTripCoinsData()
    }

    fun checkLoginInformation() {
        isLogin.set(sharedPrefsHelper[PrefKey.IS_LOGIN, false])
        if (isLogin.get()) {
            val userFullName =
                sharedPrefsHelper[PrefKey.USER_NAME, ""] + " " + sharedPrefsHelper[PrefKey.USER_LAST_NAME, ""]
            userName.set(userFullName)
            userType.set(sharedPrefsHelper[PrefKey.USER_LOYALTY_TYPE, UserLoyaltyLevel.SILVER.levelName].uppercase())
            userTripCoin.set(sharedPrefsHelper[PrefKey.USER_TRIP_COIN, "0"])
            userCard.value =
                sharedPrefsHelper[PrefKey.USER_LOYALTY_TYPE, UserLoyaltyLevel.SILVER.levelName].uppercase()
        }
    }

    private fun fetchTripCoinsData() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(
            operationTag = fetchTripCoin,
            codeBlock = { apiService.getTripSummary(token) }
        )
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            fetchTripCoin -> {
                val response = (data.body as RestResponse<*>).response as TripCoinSummaryResponse
                val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
                availableTripCoin.set(numberFormatter.format(response.availableCoins))
                pendingTripCoin.set(numberFormatter.format(response.pendingCoin))
                expiringTripCoin.set(numberFormatter.format(response.expiringSixtyDays))
                tripCoinItems.value = response.allPoints as ArrayList<TripCoinItem>?
            }
        }
    }

    fun onClickSilver(view: View) {
        if (view.tag == "not selected") {
            isCouponEnabled.set(false)
            isSilverSelected.set(true)
            isGoldSelected.set(false)
            isPlatinumSelected.set(false)
            descriptionText.set("Users with upto 15,000 Trip Coins are Silver Users. Earn more than 15,000 Trip Coins to be a Gold User.")
            tripCoinText.set("150 Trip Coins")
            visaText.set("2 Times(Only Service Charge)")
        }
    }

    fun onClickGold(view: View) {
        if (view.tag == "not selected") {
            isCouponEnabled.set(false)
            isSilverSelected.set(false)
            isGoldSelected.set(true)
            isPlatinumSelected.set(false)
            descriptionText.set("Users with 15,001-30,000 Trip Coins are Gold Users. Earn more than 30,000 Trip Coins to be a Platinum User.")
            tripCoinText.set("3,000 Trip Coins")
            couponText.set("BDT 5,000")
            visaText.set("5 Times(Only Service Charge)")
        }
    }

    fun onClickPlatinum(view: View) {
        if (view.tag == "not selected") {
            isCouponEnabled.set(false)
            isSilverSelected.set(false)
            isGoldSelected.set(false)
            isPlatinumSelected.set(true)
            descriptionText.set("Users with more than 30,000 Trip Coins are Platinum Users. Enjoy the exclusive privileges being a Platinum user.")
            tripCoinText.set("9,000 Trip Coins")
            couponText.set("BDT 15,000")
            visaText.set("Unlimited (Only Service Charge)")
        }
    }

    fun onClickLeaderBoard() {
        loyaltyEventManager.clickOnLoyaltyLeaderBoard()
        profileAction = ProfileAction.ARG_LEADER_BOARD
        goToProfileDetails.value = Event(true)
    }

    override fun onClickLogin() {
        goToLogin.value = Event(true)
    }

    fun onClickTermsAndCondition() {
        loyaltyEventManager.clickOnLoyaltyTermsAndCondition()
        profileAction = ProfileAction.ARG_TERM_AND_CONDITION_LOYALTY
        goToProfileDetails.value = Event(true)
    }

    fun onClickTripCoin() {
        profileAction = ProfileAction.ARG_USER_TRIP_COIN
        goToProfileDetails.value = Event(true)
    }

    fun onScrollView() {
        loyaltyEventManager.scrollUserTripCoinHistory()
    }

    private companion object {
        const val fetchTripCoin = "fetch_trip_coin"
    }
}