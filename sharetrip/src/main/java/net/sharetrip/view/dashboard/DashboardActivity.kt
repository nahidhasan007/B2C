package net.sharetrip.view.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.UserNotification
import net.sharetrip.shared.model.user.User
import com.sharetrip.base.utils.ShareTripAppConstants
import net.sharetrip.shared.utils.convertString
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.BuildConfig
import net.sharetrip.R
import net.sharetrip.databinding.ActivityDashboardBinding
import net.sharetrip.databinding.AppbarLayout2Binding
import net.sharetrip.databinding.AppbarLayoutBinding
import net.sharetrip.model.FeatureEnum
import net.sharetrip.model.UserLoyaltyLevel
import net.sharetrip.network.MainDataManager
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.signup.view.RegistrationActivity
import java.text.NumberFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var bindingView: ActivityDashboardBinding
    private var valueFromNotification = 0
    private var notificationData: String? = null
    val userData = MutableLiveData<User?>()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            DashboardAVMFactory(
                MainDataManager.mainApiService,
                MainDataManager.getSharedPref(this)
            )
        )[DashboardActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        setNotificationData()

        if (notificationData != null) {
            valueFromNotification = 1
        }

        val topic = if (BuildConfig.DEBUG) "shareTripDebugAndroid" else "shareTripAndroid"
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.action_home -> showBottomNav(FeatureEnum.HOME)
                R.id.action_booking -> showBottomNav(FeatureEnum.BOOKING)
                R.id.action_notification -> showBottomNav(FeatureEnum.NOTIFICATION)
                R.id.action_blog -> showBottomNav(FeatureEnum.BLOG)
                R.id.action_profile -> showBottomNav(FeatureEnum.PROFILE)
                else -> hideBottomNav()
            }
        }

        viewModel.checkBoardingStatus(valueFromNotification, notificationData)

        viewModel.navigateToRegistration.observe(this) {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        viewModel.navigateToOnBoarding.observe(this) {
            navController.navigateSafe(R.id.action_homeFragment_to_on_boarding_dest)
        }

        viewModel.userInfo.observe(this) {
            userData.value = it
        }

        viewModel.guestUserInfo.observe(this) {
            userData.value = null
        }

        viewModel.navigateToProfile.observe(this) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(
                ProfileActivity.ARG_PROFILE_ACTION,
                ProfileAction.ARG_NOTIFICATION_DETAIL
            )
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUserStatus()
    }

    private fun showBottomNav(featureEnum: FeatureEnum) {
        when (featureEnum) {
            FeatureEnum.HOME -> {
                viewModel.homePageEventManager.clickOnHomeTab()
            }
            FeatureEnum.BOOKING -> {
                viewModel.homePageEventManager.clickOnBookingTab()
            }
            FeatureEnum.NOTIFICATION -> {
                viewModel.homePageEventManager.clickOnNotificationTab()
            }
            FeatureEnum.PROFILE -> {
                viewModel.homePageEventManager.clickOnProfileTab()
            }
            FeatureEnum.BLOG -> {
                viewModel.homePageEventManager.clickOnBlogTab()
            }
        }
        bottomNav.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottomNav.visibility = View.GONE
    }

    private fun getUserLevelColor(userLevel: String): Int {
        var color = 0

        when (userLevel) {
            UserLoyaltyLevel.SILVER.levelName -> color = R.color.silver
            UserLoyaltyLevel.GOLD.levelName -> color = R.color.gold
            UserLoyaltyLevel.PLATINUM.levelName -> color = R.color.platinum
        }
        return color
    }

    fun setUserData(userObj: User?, bindingView: AppbarLayout2Binding){
        if (MainDataManager.getSharedPref(this)[PrefKey.IS_LOGIN, false] && userObj != null) {
            bindingView.textViewUserLabel.visibility = View.VISIBLE
            if (userObj.firstName != null) {
                val name: String = userObj.lastName
                if (name.isEmpty()) {
                    bindingView.textViewUserName.text = getString(R.string.no_name)
                } else {
                    bindingView.textViewUserName.text = name
                }
            }

            bindingView.textViewUserLabel.text = userObj.userLevel
            bindingView.textViewTripCoin.text = NumberFormat.getNumberInstance(Locale.US)
                .format(SharedPrefsHelper(this)[PrefKey.USER_TRIP_COIN, userObj.totalPoints.toString()].filter { it in '0'..'9' }
                    .toLong())

            val drawable = bindingView.textViewUserLabel.compoundDrawables[0]

            drawable.setTint(
                ContextCompat.getColor(
                    this,
                    getUserLevelColor(userObj.userLevel)
                )
            )

            val requestOptions = RequestOptions().circleCrop()
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_avatar_light_40dp)
                .error(R.drawable.ic_avatar_light_40dp)
            Glide.with(applicationContext)
                .load(userObj.avatar)
                .apply(requestOptions)
                .into(bindingView.iconUser)
            FirebaseCrashlytics.getInstance()
                .setUserId(userObj.username)
        } else {
            updateGuestInfo(bindingView)
        }
    }

    private fun updateGuestInfo(bindingView: AppbarLayout2Binding) {
        bindingView.textViewUserName.text = getString(R.string.no_name)
        bindingView.textViewUserLabel.visibility = View.GONE
        bindingView.textViewTripCoin.text = "0"
    }

    private fun setNotificationData() {
        notificationData = intent.getStringExtra(ShareTripAppConstants.NOTIFICATION_DATA)

        if (notificationData == null) {
            val bundle = intent.extras
            if (bundle?.getString(NOTIFICATION_TITLE) != null) {
                val userNotification = UserNotification(
                    comment = "",
                    description = bundle.getString(NOTIFICATION_BODY),
                    imageUrl = bundle.getString(NOTIFICATION_IMAGE),
                    platform = "ANDROID",
                    tigerBy = "",
                    publishDate = bundle.getString(NOTIFICATION_DATE),
                    timeStamp = 1236544,
                    title = bundle.getString(NOTIFICATION_TITLE),
                    fromPushNotification = true
                )
                notificationData = userNotification.convertString()
            }
        }
    }

    private companion object {
        private const val NOTIFICATION_BODY = "body"
        private const val NOTIFICATION_IMAGE = "image"
        private const val NOTIFICATION_DATE = "from"
        private const val NOTIFICATION_TITLE = "title"
    }

}
