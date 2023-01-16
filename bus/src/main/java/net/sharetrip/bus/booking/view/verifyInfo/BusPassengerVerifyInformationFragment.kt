package net.sharetrip.bus.booking.view.verifyInfo

import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.booking.model.ItemTraveler
import net.sharetrip.bus.booking.model.PassengerValidation
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.booking.view.summary.BusBookingSummaryFragment.Companion.ARG_BUS_SUMMARY_BUNDLE
import net.sharetrip.bus.databinding.FragmentBusPassengerVarifyInformationBinding
import net.sharetrip.bus.history.model.PassengerDetails
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.MsgUtils
import net.sharetrip.bus.utils.getAlertDialog

class BusPassengerVerifyInformationFragment :
    BaseFragment<FragmentBusPassengerVarifyInformationBinding>() {
    private val viewModel by lazy {
        val g = Gson()
        val passengerInfo = g.fromJson(
            bundle.getString(ARG_BUS_PASSENGER_VERIFY_TRAVELLER),
            ItemTraveler::class.java
        )
        val departureInfo = bundle.getParcelable<Departure>(ARG_BUS_PASSENGER_VERIFY_DEPARTURE)!!
        val searchIdAndTripCoin =
            bundle.getParcelable<SearchIdAndTripCoin>(ARG_BUS_PASSENGER_VERIFY_SEARCH_AND_TRIP_COIN)!!

        ViewModelProvider(
            this,
            BusPassengerVerifyVMFactory(
                passengerInfo, departureInfo, searchIdAndTripCoin,
                DataManager.busBookingApiService
            )
        ).get(
            BusPassengerVerifyInfoViewModel::class.java
        )
    }

    val bundle by lazy { arguments?.getBundle(ARG_BUS_PASSENGER_VERIFY_BUNDLE)!! }

    override fun layoutId(): Int = R.layout.fragment_bus_passenger_varify_information

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.model = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        val profDialog = getAlertDialog(layoutInflater, requireContext())

        bindingView.fullName.passengerDetails =
            PassengerDetails(
                getString(R.string.full_name_title),
                viewModel.passengerInfo.givenName + " " + viewModel.passengerInfo.surName,
                R.drawable.ic_profile_icon_of_name_side
            )

        if (viewModel.passengerInfo.gender == "Male") {
            bindingView.gender.passengerDetails =
                PassengerDetails(
                    getString(R.string.gender),
                    viewModel.passengerInfo.gender!!,
                    R.drawable.ic_male_gray_mono
                )
        } else {
            bindingView.gender.passengerDetails =
                PassengerDetails(
                    getString(R.string.gender),
                    viewModel.passengerInfo.gender!!,
                    R.drawable.ic_female_gray_mono
                )
        }

        bindingView.contactInfoPhoneNumber.passengerDetails =
            PassengerDetails(
                getString(R.string.phone_number),
                viewModel.passengerInfo.mobileNumber!!,
                R.drawable.ic_phone_mono
            )

        bindingView.phoneNumberEditText.setText(viewModel.passengerInfo.mobileNumber!!)
        bindingView.mailIdEditText.setText(viewModel.passengerInfo.email!!)
        bindingView.contactInfoEmail.passengerDetails = PassengerDetails(
            getString(R.string.email_address),
            viewModel.passengerInfo.email!!,
            R.drawable.ic_email_gary_mono
        )

        observeLiveValidation()

        viewModel.isEditClicked.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.contactInfoPhoneNumber.valueOfNameTextView.text = ""
                bindingView.contactInfoEmail.valueOfNameTextView.text = ""
            }
        }

        viewModel.phnNumber.observe(viewLifecycleOwner) {
            viewModel.passengerInfo.mobileNumber = it
        }

        viewModel.mail.observe(viewLifecycleOwner) {
            viewModel.passengerInfo.email = it
        }

        viewModel.dialogLoading.observe(viewLifecycleOwner) {
            if (it)
                profDialog.show()
            else
                profDialog.dismiss()
        }

        viewModel.navigateToSummary.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_busPassengerVerifyInformationFragment_to_busBookingSummaryFragment,
                bundleOf(ARG_BUS_SUMMARY_BUNDLE to it)
            )
        }
    }

    private fun observeLiveValidation() {
        viewModel.liveValidation.observe(viewLifecycleOwner) {
            when (it.first) {
                PassengerValidation.MobileValidation -> {
                    if (it.second) {
                        handleEmptyErrorErrorMsg(
                            bindingView.phoneNumberEditText,
                            noError = it.second
                        )
                    } else {
                        handleEmptyErrorErrorMsg(
                            bindingView.phoneNumberEditText,
                            MsgUtils.invalidPhoneNumberMsg,
                            it.second
                        )
                    }
                }
                PassengerValidation.EmailValidation -> {
                    if (it.second) {
                        handleEmptyErrorErrorMsg(
                            bindingView.mailIdEditText,
                            noError = it.second
                        )
                    } else {
                        handleEmptyErrorErrorMsg(
                            bindingView.mailIdEditText,
                            MsgUtils.invalidEmail,
                            it.second
                        )
                    }
                }
            }
        }
    }


    private fun handleEmptyErrorErrorMsg(
        inputLayout: AppCompatEditText,
        message: String = getString(R.string.required),
        noError: Boolean
    ) {
        if (noError) {
            inputLayout.error = null
        } else {
            inputLayout.error = message
        }
    }

    companion object {
        const val ARG_BUS_PASSENGER_VERIFY_BUNDLE = "ARG_BUS_PASSENGER_VERIFY_BUNDLE"
        const val ARG_BUS_PASSENGER_VERIFY_TRAVELLER = "ARG_BUS_PASSENGER_VERIFY_TRAVELLER"
        const val ARG_BUS_PASSENGER_VERIFY_DEPARTURE = "ARG_BUS_PASSENGER_VERIFY_DEPARTURE"
        const val ARG_BUS_PASSENGER_VERIFY_SEARCH_AND_TRIP_COIN =
            "ARG_BUS_PASSENGER_VERIFY_SEARCH_AND_TRIP_COIN"
    }
}
