package net.sharetrip.bus.booking.view.passengerInfo

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.booking.model.PassengerValidation
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.booking.view.verifyInfo.BusPassengerVerifyInformationFragment.Companion.ARG_BUS_PASSENGER_VERIFY_BUNDLE
import net.sharetrip.bus.booking.view.verifyInfo.BusPassengerVerifyInformationFragment.Companion.ARG_BUS_PASSENGER_VERIFY_SEARCH_AND_TRIP_COIN
import net.sharetrip.bus.databinding.FragmentBusPassengerInfoBinding
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.MsgUtils.invalidEmail
import net.sharetrip.bus.utils.MsgUtils.invalidPhoneNumberMsg
import net.sharetrip.bus.utils.getAlertDialog

class BusPassengerInfoFragment : BaseFragment<FragmentBusPassengerInfoBinding>() {
    private val viewModel by lazy {
        val departureInfo =
            bundle.getParcelable<Departure>(ARG_BUS_PASSENGER_INFO_DEPARTURE_INFO)!!

        ViewModelProvider(
            this,
            BusPassengerInfoVMFactory(
                departureInfo,
                DataManager.busBookingApiService, DataManager.getSharedPref(requireContext())
            )
        ).get(
            BusPassengerInfoViewModel::class.java
        )
    }
    val bundle by lazy { arguments?.getBundle(ARG_BUS_PASSENGER_INFO_BUNDLE)!! }

    val searchIdAndTripCoin by lazy {
        bundle.getParcelable<SearchIdAndTripCoin>(
            ARG_BUS_PASSENGER_INFO_SEARCH_AND_TRIP_COIN
        )
    }

    override fun layoutId() = R.layout.fragment_bus_passenger_info

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.model = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        val profDialog = getAlertDialog(layoutInflater, requireContext())

        observePassengerList()

        observeLiveValidation()

        observeLoadingDialog(profDialog)

        observeCheckBox()

        observeNavigateToVerification()
    }

    private fun observeNavigateToVerification() {
        viewModel.navigateToPassengerVerification.observe(viewLifecycleOwner) {
            it.putParcelable(ARG_BUS_PASSENGER_VERIFY_SEARCH_AND_TRIP_COIN, searchIdAndTripCoin)
            findNavController().navigate(
                R.id.action_busPassengerInfoFragment_to_busPassengerVerifyInformationFragment,
                bundleOf(ARG_BUS_PASSENGER_VERIFY_BUNDLE to it)
            )
        }
    }

    private fun observePassengerList() {
        viewModel.passengerList.observe(viewLifecycleOwner) {
            val givenNameList = ArrayList<String>()

            for (passenger in it) {
                givenNameList.add(passenger.givenName)
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                givenNameList
            )
            bindingView.quickPickAutoCompleteTextView.setAdapter(adapter)

            bindingView.quickPickAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                viewModel.setQuickPickerData(position)
            }
        }
    }

    private fun observeCheckBox() {
        viewModel.checkBoxClicked.observe(viewLifecycleOwner) {
            var addOrNot = true
            if (it) {
                viewModel.passengerList.value?.forEach { it1 ->
                    if (it1.givenName == viewModel.passenger.get()?.givenName && it1.surName == viewModel.passenger.get()?.surName && it1.gender == viewModel.passenger.get()?.gender && it1.mobileNumber == viewModel.passenger.get()?.mobileNumber && it1.email == viewModel.passenger.get()?.email)
                        addOrNot = false
                }
                if (addOrNot)
                    viewModel.onAddPassenger()
                else
                    toastShow(getString(R.string.person_already_exist))
            }
        }
    }

    private fun observeLoadingDialog(profDialog: AlertDialog) {
        viewModel.dialogLoading.observe(viewLifecycleOwner) {
            if (it) {
                if (!profDialog.isShowing) {
                    profDialog.show()
                }
            } else {
                profDialog.dismiss()
            }
        }
    }

    private fun observeLiveValidation() {
        viewModel.liveValidation.observe(viewLifecycleOwner) {
            when (it.first) {
                PassengerValidation.MobileValidation -> {
                    if (it.second) {
                        handleEmptyErrorErrorMsg(
                            bindingView.layoutInputPhoneNumber,
                            noError = it.second
                        )
                    } else {
                        handleEmptyErrorErrorMsg(
                            bindingView.layoutInputPhoneNumber,
                            invalidPhoneNumberMsg,
                            it.second
                        )
                    }
                }
                PassengerValidation.EmailValidation -> {
                    if (it.second) {
                        handleEmptyErrorErrorMsg(
                            bindingView.layoutInputEmail,
                            noError = it.second
                        )
                    } else {
                        handleEmptyErrorErrorMsg(
                            bindingView.layoutInputEmail,
                            invalidEmail,
                            it.second
                        )
                    }
                }
                PassengerValidation.GivenNameValidation -> {
                    if (it.second) {
                        handleEmptyErrorErrorMsg(
                            bindingView.givenNameTextInputLayout,
                            noError = it.second
                        )
                    } else {
                        handleEmptyErrorErrorMsg(
                            bindingView.givenNameTextInputLayout,
                            getString(R.string.only_use_letter),
                            noError = it.second
                        )
                    }
                }
                PassengerValidation.SurNameValidation -> {
                    if (it.second) {
                        handleEmptyErrorErrorMsg(
                            bindingView.surNameTextInputLayout,
                            noError = it.second
                        )
                    } else {
                        handleEmptyErrorErrorMsg(
                            bindingView.surNameTextInputLayout,
                            getString(R.string.only_use_letter),
                            noError = it.second
                        )
                    }
                }
            }
        }
    }

    private fun toastShow(it: String?) {
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
    }

    private fun handleEmptyErrorErrorMsg(
        inputLayout: TextInputLayout,
        message: String = getString(R.string.required),
        noError: Boolean
    ) {
        if (noError) {
            inputLayout.helperText = message
            inputLayout.isErrorEnabled = false
        } else {
            inputLayout.isErrorEnabled = true
            inputLayout.error = message
        }
    }

    companion object {
        const val ARG_BUS_PASSENGER_INFO_BUNDLE = "ARG_BUS_PASSENGER_INFO_BUNDLE"
        const val ARG_BUS_PASSENGER_INFO_DEPARTURE_INFO = "ARG_BUS_PASSENGER_INFO_DEPARTURE_INFO"
        const val ARG_BUS_PASSENGER_INFO_SEARCH_AND_TRIP_COIN =
            "ARG_BUS_PASSENGER_INFO_SEARCH_AND_TRIP_COIN"
    }
}
