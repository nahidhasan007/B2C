package net.sharetrip.view.home

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.sharetrip.base.data.PrefKey.BOOKING_DATE
import com.sharetrip.base.data.PrefKey.CRISP_WEB_ID
import com.sharetrip.base.data.PrefKey.IS_LOGIN
import com.sharetrip.base.data.PrefKey.RETURN_DATE
import com.sharetrip.base.data.PrefKey.USER_PROFILE
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.model.GuestPopUpData
import com.sharetrip.base.network.NetworkUtil
import com.sharetrip.base.utils.ShareTripAppConstants
import net.sharetrip.shared.utils.getUserInfo
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import im.crisp.client.ChatActivity
import im.crisp.client.Crisp
import net.sharetrip.R
import net.sharetrip.databinding.FragmentHomeBinding
import net.sharetrip.databinding.ItemDashboardHeaderBinding
import net.sharetrip.flight.booking.FlightBookingActivity
import net.sharetrip.holiday.booking.HolidayBookingActivity
import net.sharetrip.holiday.utils.ARG_HOLIDAY_PRODUCT_CODE
import net.sharetrip.holiday.utils.HOLIDAY_NAVIGATION_ACTION
import net.sharetrip.holiday.utils.HolidayNavigationActions
import net.sharetrip.hotel.booking.HotelBookingActivity
import net.sharetrip.network.MainDataManager
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.signup.view.RegistrationActivity
import net.sharetrip.tracker.FlightTrackerActivity
import net.sharetrip.tracker.FlightTrackerActivity.Companion.FLIGHT_TRACKER_ACTION
import net.sharetrip.utils.FlightBookingNotificationManager
import net.sharetrip.view.dashboard.DashboardActivity
import net.sharetrip.view.home.adapter.HolidayAdapter
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.wheel.WheelActivity
import net.sharetrip.wheel.databinding.GuestUserSpinLayoutBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(), GuestLoginListener {
    val viewModel by lazy {
        HomeVMFactory(
            MainDataManager.mainApiService,
            FirebaseRemoteConfig.getInstance(),
            MainDataManager.getSharedPref(requireContext())
        ).create(HomeViewModel::class.java)
    }

    private val homeActionViewModel by lazy {
        HomeActionsVMFactory(
            MainDataManager.mainApiService,
            MainDataManager.getSharedPref(requireContext())
        ).create(HomeActionsViewModel::class.java)
    }

    private lateinit var loginDialog: Dialog
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var crispWebId: String

    private val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (viewModel.requestCode == signup_request_code) {
                    loginDialog.dismiss()
                } else if (viewModel.requestCode == flight_request_code) {
                    val flightBookingDate = sharedPrefsHelper[BOOKING_DATE, 0L]
                    val flightReturnDate = sharedPrefsHelper[RETURN_DATE, 0L]
                    FlightBookingNotificationManager.setLocalNotification(
                        bookingDate = flightBookingDate,
                        returnBookingDate = flightReturnDate,
                        context = requireContext()
                    )

                }
            }
        }

    private lateinit var headerBinding: ItemDashboardHeaderBinding

    private lateinit var holidayAdapter: HolidayAdapter
    private var loading = true
    private var height = 0

    private val popupDataForTrivia: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.trivia_title,
            R.string.trivia_body,
            R.drawable.ic_st_trivia_color, this
        )
    }
    private val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.trip_coin_title,
            R.string.trip_coin_body,
            R.drawable.ic_onboarding_200dp, this
        )
    }

    override fun layoutId(): Int = R.layout.fragment_home

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        sharedPrefsHelper = SharedPrefsHelper(requireContext())

        headerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.item_dashboard_header,
            null,
            false
        )

        crispWebId = sharedPrefsHelper[CRISP_WEB_ID, ShareTripAppConstants.CRISP_WEBSITE_ID]
        if (crispWebId.isNotEmpty()) {
            Crisp.configure(requireContext(), crispWebId)
        }

        holidayAdapter = HolidayAdapter(homeActionViewModel, viewModel)

        bindingView.holidays.apply {
            setHasFixedSize(true)
            adapter = holidayAdapter
        }
        viewModel.holidayList.observe(viewLifecycleOwner) {
            loading = false
            holidayAdapter.update(it)
        }

        ItemClickSupport.addTo(bindingView.holidays)
            .setOnItemClickListener { _, position, _ ->
                if (position <= 0) {
                    return@setOnItemClickListener
                }
                if (NetworkUtil.hasNetwork(bindingView.holidays.context)) {
                    val intent = Intent(context, HolidayBookingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(
                        HOLIDAY_NAVIGATION_ACTION,
                        HolidayNavigationActions.VISIT_HOLIDAY_DETAILS
                    )
                    intent.putExtra(
                        ARG_HOLIDAY_PRODUCT_CODE,
                        viewModel.holidays[position - 1].productCode!!
                    )

                    homeActionViewModel.homePageEventManager.selectHolidayFromHomeScreen()
                    startActivity(intent)
                } else
                    Toast.makeText(bindingView.holidays.context, "No Internet", Toast.LENGTH_LONG)
                        .show()
            }

        bindingView.holidays.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                height += dy

                if (height >= recyclerView.getChildAt(0).height / 2) {
                    bindingView.stickyLayoutBar.visibility = View.VISIBLE

                } else {
                    bindingView.stickyLayoutBar.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val totalCount = recyclerView.layoutManager!!.itemCount
                val lastItemIndex =
                    (recyclerView.layoutManager!! as LinearLayoutManager).findLastVisibleItemPosition()

                if (!loading && totalCount == lastItemIndex + 1) {
                    viewModel.fetchHolidayList()
                    loading = true
                }
            }
        })

        viewModel.gotoHotel.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, HotelBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })

        viewModel.gotoFlight.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, FlightBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            viewModel.requestCode = flight_request_code
            registerResult.launch(intent)
        })

        viewModel.navigateToHoliday.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, HolidayBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(
                HOLIDAY_NAVIGATION_ACTION,
                HolidayNavigationActions.VISIT_HOLIDAY_SEARCH
            )
            startActivity(intent)
        })

        viewModel.gotoVisa.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, VisaBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })

//        viewModel.gotToTour.observe(viewLifecycleOwner){
//            val intent = Intent(context, TourBookingActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
//        }

        viewModel.showDialog.observe(viewLifecycleOwner) { dialog ->
            if (dialog) {
                viewModel.appVersion.value?.let { it -> showDialog(it) }
            }
        }

        homeActionViewModel.isTreasure.observe(viewLifecycleOwner, EventObserver {
            guestLoginDialog(it)
        })

        homeActionViewModel.coinAnimation.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                homeActionViewModel.animateCoin(
                    bindingView.appBarLayout2.textViewTripCoin
                )
            }
        })

        homeActionViewModel.gotoWheelActivity.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                val intent = Intent(context, WheelActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })

        homeActionViewModel.gotoFlightTracker.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                val intent = Intent(context, FlightTrackerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(
                    FLIGHT_TRACKER_ACTION,
                    FlightTrackerActivity.Companion.FlightTrackerAction.FLIGHT_TRACKER
                )
                startActivity(intent)
            }
        })

        bindingView.appBarLayout2.userInfoLayout.setOnClickListener {
            homeActionViewModel.homePageEventManager.clickOnToolbarProfileLink()
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, ProfileAction.ARG_PROFILE_EDIT)
            startActivity(intent)
        }

        homeActionViewModel.showToastMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        bindingView.chat.setOnClickListener {
            initCrispChat()
        }
    }

    private fun initCrispChat() {
        if (sharedPrefsHelper[IS_LOGIN, false]) {
            val userJson = sharedPrefsHelper[USER_PROFILE, ""]
            val userInfo = if (userJson.isNotEmpty()) userJson.getUserInfo() else null
            userInfo?.let {
                Crisp.resetChatSession(requireContext())
                Crisp.setUserEmail(userInfo.email)
                Crisp.setUserNickname(userInfo.firstName + " " + userInfo.lastName)
                Crisp.setUserPhone(userInfo.mobileNumber)
                Crisp.setUserAvatar(userInfo.avatar)
                Crisp.setTokenID(userInfo.username)
            }
        }

        val intent = Intent(requireContext(), ChatActivity::class.java)
        startActivity(intent)

    }

    private fun guestLoginDialog(isTreasure: Boolean) {
        loginDialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(context),
            R.layout.guest_user_spin_layout,
            null,
            false
        ) as GuestUserSpinLayoutBinding
        loginDialog.setContentView(dialogBinding.root)
        dialogBinding.data = if (isTreasure) popupData else popupDataForTrivia
        loginDialog.show()
    }

    private fun showDialog(version: String) {
        if (!(context as Activity).isFinishing) {
            val dialog = Dialog(context as Activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_update)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val description = dialog.findViewById<AppCompatTextView>(R.id.text_view_description)
            description.text = getString(R.string.app_update_msg, version)

            val btnUpdate = dialog.findViewById<TextView>(R.id.btnUpdate)
            btnUpdate.setOnClickListener {
                try {
                    (context as Activity).startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=net.sharetrip")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    (context as Activity).startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=net.sharetrip")
                        )
                    )
                }
            }
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).userData.observe(this) {
            (activity as DashboardActivity).setUserData(it, bindingView.appBarLayout2)
        }
    }

    override fun onClickLogin() {
        val intent = Intent(requireContext(), RegistrationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        viewModel.requestCode = signup_request_code
        registerResult.launch(intent)
    }

    private companion object {
        const val flight_request_code = 101
        const val signup_request_code = 102
    }
}

