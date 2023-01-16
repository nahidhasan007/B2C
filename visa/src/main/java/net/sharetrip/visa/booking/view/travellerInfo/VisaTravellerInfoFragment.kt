package net.sharetrip.visa.booking.view.travellerInfo

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.nationality.NationalityFragment.Companion.ARG_NATIONALITY_BACK_DATA
import net.sharetrip.visa.booking.view.nationality.NationalityViewModel.Companion.EXTRA_COUNTRY_CODE
import net.sharetrip.visa.booking.view.verification.TravelerVerificationFragment
import net.sharetrip.visa.databinding.FragmentVisaTravellerInfoBinding
import net.sharetrip.visa.network.DataManager
import net.sharetrip.visa.utils.MsgUtils.invalidEmail
import net.sharetrip.visa.utils.MsgUtils.invalidPassport
import net.sharetrip.visa.utils.MsgUtils.invalidPhoneNumber

class VisaTravellerInfoFragment : BaseFragment<FragmentVisaTravellerInfoBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            TravellerInfoVMFactory(
                visaSearchQuery,
                DataManager.visaBookingApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[VisaTravellerInfoViewModel::class.java]
    }

    private val visaSearchQuery by lazy {
        requireArguments().getParcelable<VisaSearchQuery>(
            ARG_VISA_TRAVELLER_INFO_MODEL
        )!!
    }

    override fun layoutId() = R.layout.fragment_visa_traveller_info

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(visaSearchQuery.travellerLabelInfo())
        hideTripCoin()

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_visa, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_done_text == menuItem.itemId) {
                    viewModel.onClickSaveMenu()
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        observeHideKeyboard()

        observeProfessionList()

        observePassengerList()

        observeDataValidation()

        observeNationalityBackData()

        observeTravellerVerificationNavigation()

        observeNationalityNavigation()

        observeLiveValidation()
    }

    private fun observeHideKeyboard() {
        viewModel.hideKeyboard.observe(viewLifecycleOwner) {
            hideKeyboard()
        }
    }

    private fun observeProfessionList() {
        viewModel.professionList.observe(viewLifecycleOwner) {
            val adapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    it.second
                )
            bindingView.professionAutoCompleteTextView.setAdapter(adapter)

            if (!it.first.isNullOrEmpty()) {
                bindingView.professionAutoCompleteTextView.setText(it.first, false)
                viewModel.updateSelectedProfession(it.second.indexOf(it.first))
            }

            bindingView.professionAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                viewModel.updateSelectedProfession(position)
            }
        }
    }

    private fun observeLiveValidation() {
        viewModel.liveValidation.observe(viewLifecycleOwner) {
            when (it.first) {
                1 -> if (it.second) {
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

                2 -> if (it.second) {
                    handleEmptyErrorErrorMsg(
                        bindingView.surNameTextInputLayout,
                        noError = it.second
                    )
                } else {
                    handleEmptyErrorErrorMsg(
                        bindingView.surNameTextInputLayout,
                        getString(R.string.only_use_letter),
                        it.second
                    )
                }

                3 -> handleEmptyErrorErrorMsg(
                    bindingView.birthDayTextInputLayout,
                    noError = it.second
                )

                4 -> handleEmptyErrorErrorMsg(
                    bindingView.nationalityTextInputLayout,
                    noError = it.second
                )

                5 -> handleEmptyErrorErrorMsg(
                    bindingView.currentAddressTextInputLayout,
                    noError = it.second
                )

                6 -> handleEmptyErrorErrorMsg(
                    bindingView.destinationAddressTextInputLayout,
                    noError = it.second
                )

                7 -> if (it.second) {
                    handleEmptyErrorErrorMsg(
                        bindingView.phoneTextInputLayout,
                        noError = it.second
                    )
                } else {
                    handleEmptyErrorErrorMsg(
                        bindingView.phoneTextInputLayout,
                        invalidPhoneNumber,
                        it.second
                    )
                }

                8 -> if (it.second) {
                    handleEmptyErrorErrorMsg(
                        bindingView.emailAddressTextInputLayout,
                        noError = it.second
                    )
                } else {
                    handleEmptyErrorErrorMsg(
                        bindingView.emailAddressTextInputLayout,
                        invalidEmail,
                        it.second
                    )
                }

                9 -> if (it.second) {
                    handleEmptyErrorErrorMsg(
                        bindingView.passportNumberTextInputLayout,
                        noError = it.second
                    )
                } else {
                    handleEmptyErrorErrorMsg(
                        bindingView.passportNumberTextInputLayout,
                        invalidPassport,
                        it.second
                    )
                }

                10 -> handleEmptyErrorErrorMsg(
                    bindingView.passportExpireDateTextInputLayout,
                    noError = it.second
                )

                12 -> handleEmptyErrorErrorMsg(
                    bindingView.contactAddressTextInputLayout,
                    noError = it.second
                )

                14 -> handleEmptyErrorErrorMsg(
                    bindingView.professionTextInputLayout,
                    noError = it.second
                )
            }
        }
    }

    private fun observePassengerList() {
        viewModel.passengerList.observe(viewLifecycleOwner) {
            val givenNameList = ArrayList<String>()

            for (passenger in it) {
                givenNameList.add(passenger.surName)
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

    private fun observeNationalityBackData() {
        getNavigationResultLiveData<Intent>(ARG_NATIONALITY_BACK_DATA)?.observe(
            viewLifecycleOwner
        ) { data ->
            val code = data.getStringExtra(EXTRA_COUNTRY_CODE)
            viewModel.setCountryCode(code!!)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Intent>(
                ARG_NATIONALITY_BACK_DATA
            )
        }
    }

    private fun observeTravellerVerificationNavigation() {
        viewModel.navigateToTravellerVerification.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_visaTravellerInfoFragment_to_travelerVerificationFragment,
                bundleOf(TravelerVerificationFragment.ARG_TRAVELLER_VERIFICATION_DATA_MODEL to it)
            )
        }
    }

    private fun observeNationalityNavigation() {
        viewModel.navigateToNationality.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(R.id.action_visaTravellerInfoFragment_to_nationalityFragment)
        }
    }

    private fun observeDataValidation() {
        viewModel.dataInvalidPosition.observe(viewLifecycleOwner) {
            when (it) {
                0 -> showEmptyErrorMsg(
                    bindingView.givenNameTextInputLayout,
                    getString(R.string.only_use_letter)
                )

                1 -> showEmptyErrorMsg(bindingView.givenNameTextInputLayout)

                2 -> showEmptyErrorMsg(bindingView.surNameTextInputLayout)

                3 -> showEmptyErrorMsg(bindingView.birthDayTextInputLayout)

                4 -> showEmptyErrorMsg(bindingView.nationalityTextInputLayout)

                5 -> showEmptyErrorMsg(bindingView.currentAddressTextInputLayout)

                6 -> showEmptyErrorMsg(bindingView.destinationAddressTextInputLayout)

                7 -> showEmptyErrorMsg(bindingView.phoneTextInputLayout)

                8 -> showEmptyErrorMsg(bindingView.emailAddressTextInputLayout)

                9 -> showEmptyErrorMsg(bindingView.passportNumberTextInputLayout)

                10 -> showEmptyErrorMsg(bindingView.passportExpireDateTextInputLayout)

                11 -> showEmptyErrorMsg(
                    bindingView.emailAddressTextInputLayout,
                    getString(R.string.give_a_valid_email)
                )

                12 -> showEmptyErrorMsg(bindingView.contactAddressTextInputLayout)

                14 -> showEmptyErrorMsg(bindingView.professionTextInputLayout)
            }
        }
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

    private fun showEmptyErrorMsg(
        inputLayout: TextInputLayout,
        message: String = getString(R.string.required)
    ) {
        inputLayout.isErrorEnabled = true
        inputLayout.error = message
        val vTop = inputLayout.top
        val vBottom = inputLayout.bottom
        val sHeight: Int = bindingView.containerNestedLayout.bottom
        bindingView.containerNestedLayout.smoothScrollTo((vTop + vBottom - sHeight) / 2, 0)
        bindingView.containerNestedLayout.scrollTo(0, inputLayout.bottom)
    }

    companion object {
        const val ARG_VISA_TRAVELLER_INFO_MODEL = "ARG_VISA_TRAVELLER_INFO_MODEL"
    }
}
