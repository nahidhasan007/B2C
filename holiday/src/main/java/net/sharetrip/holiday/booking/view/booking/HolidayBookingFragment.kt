package net.sharetrip.holiday.booking.view.booking

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.*
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayBookingBinding
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.HolidayBookingActivity
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.ARG_HOLIDAY_PARAM_MODEL
import net.sharetrip.shared.databinding.GuestUserLayoutBinding
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.signup.view.RegistrationActivity
import timber.log.Timber

class HolidayBookingFragment : BaseFragment<FragmentHolidayBookingBinding>() {

    private val sharedPref: SharedPrefsHelper by lazy {
        DataManager.getSharedPref(requireContext())
    }

    private val viewModel: HolidayBookingViewModel by viewModels {
        val holidayParam: HolidayParam = arguments?.get(ARG_HOLIDAY_PARAM_MODEL) as HolidayParam
        HolidayBookingViewModelFactory(
            holidayParam,
            sharedPref,
            HolidayBookingRepository(DataManager.holidayBookingApiService)
        )
    }

    private val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                dialog.dismiss()
                viewModel.fetchUserProfile()
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                //dialog.dismiss()
            }
        }

    private var navigate = true
    private var arrivalType = "Airlines"
    private var departureType = "Airlines"
    private lateinit var dialog: Dialog

    override fun layoutId() = R.layout.fragment_holiday_booking

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.done, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_done == menuItem.itemId && navigate) {
                    if (sharedPref[PrefKey.IS_LOGIN, false]) {
                        navigateToContact()
                    } else {
                        showGuestDialog()
                    }
                }

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bindingView.radioGroupArrivalType.setOnCheckedChangeListener { group, checkedId ->
            arrivalType = group.findViewById<View>(checkedId).tag.toString()
        }

        bindingView.radioGroupDepartureType.setOnCheckedChangeListener { group, checkedId ->
            departureType = group.findViewById<RadioButton>(checkedId).tag.toString()
        }

        viewModel.msg.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            navigate = true
        }

        viewModel.isLoginLiveData.observe(viewLifecycleOwner) {
            if (!it) {
                showGuestDialog()
            }
        }

        viewModel.tripCoin.observe(viewLifecycleOwner) {
            setAndSaveTripCoin(it)
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                val intent = Intent(requireContext(), RegistrationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                registerResult.launch(intent)
            }
        })
    }

    private fun navigateToContact() {
        navigate = false
        viewModel.setHolidayParamValues(
            arrivalType,
            bindingView.textFieldArrivalTransport.text.toString(),
            bindingView.textFieldArrivalTransportCode.text.toString(),
            bindingView.textFieldArrivalTime.text.toString(),

            departureType,
            bindingView.textFieldArrivalTransport.text.toString(),
            bindingView.textFieldDepartureTransportCode.text.toString(),
            bindingView.textFieldDepartureTime.text.toString(),
            bindingView.textFieldPickupTime.text.toString()
        )

        val bundle = bundleOf(ARG_HOLIDAY_PARAM_MODEL to viewModel.holidayParam)
        findNavController().navigateSafe(
            R.id.action_holidayBookingFragment_to_clientContactFragment,
            bundle
        )
    }

    private fun setAndSaveTripCoin(tripCoin: String) {
        (activity as HolidayBookingActivity)
            .findViewById<TextView>(R.id.text_view_trip_coin).text = tripCoin
        sharedPref.put(PrefKey.USER_TRIP_COIN, tripCoin)
    }

    private fun showGuestDialog() {
        try {
            dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context),
                R.layout.guest_user_layout,
                null,
                false
            ) as GuestUserLayoutBinding
            dialog.setContentView(dialogBinding.root)
            dialogBinding.data = viewModel.popupData
            dialog.show()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onResume() {
        super.onResume()
        navigate = true
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }
}
