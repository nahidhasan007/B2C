package net.sharetrip.visa.booking.view.homeextended

import android.content.Intent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.model.CalenderData
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.utils.ARG_CALENDER_DATA
import net.sharetrip.shared.utils.ARG_SINGLE_DATE
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.visa.booking.model.ExtendedHomeNavigationKey
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.countrysearch.VisaCountrySearchFragment.Companion.ARG_COUNTRY_DATA
import net.sharetrip.visa.booking.view.homeextended.VisaHomeExtendedViewModel.Companion.PICK_ENTRY_DATE_REQUEST
import net.sharetrip.visa.booking.view.homeextended.VisaHomeExtendedViewModel.Companion.PICK_EXIT_DATE_REQUEST
import net.sharetrip.visa.booking.view.homeextended.VisaHomeExtendedViewModel.Companion.PICK_TRAVELER_COUNT_REQUEST
import net.sharetrip.visa.booking.view.homeextended.VisaHomeExtendedViewModel.Companion.PICK_VISA_COUNTRY_REQUEST
import net.sharetrip.visa.booking.view.selection.VisaSelectionFragment.Companion.ARG_VISA_SELECTION_MODEL
import net.sharetrip.visa.booking.view.traveller.VisaTravellerNumberFragment.Companion.ARG_NUMBER_OF_ADULT
import net.sharetrip.visa.booking.view.traveller.VisaTravellerNumberFragment.Companion.ARG_TRAVELLER_DATA
import net.sharetrip.visa.databinding.FragmentVisaHomeExtendedBinding

class VisaHomeExtendedFragment : BaseFragment<FragmentVisaHomeExtendedBinding>() {
    private val viewModel by lazy {
        val visaSearchQuery =
            arguments?.getParcelable<VisaSearchQuery>(ARG_VISA_SEARCH_QUERY_MODEL)!!
        ViewModelProvider(
            this,
            VisaHomeExtendedVMFactory(visaSearchQuery)
        ).get(
            VisaHomeExtendedViewModel::class.java
        )
    }

    private var requestCode: Int = -1



    override fun layoutId() = R.layout.fragment_visa_home_extended

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        viewModel.resetVisaSearchQuery()
        setTitle(getString(R.string.visa))

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                ExtendedHomeNavigationKey.Destination.name -> {
                    requestCode = PICK_VISA_COUNTRY_REQUEST
                    findNavController().navigateSafe(
                        R.id.action_visaHomeExtendedFragment_to_visaCountrySearchFragment
                    )
                }

                ExtendedHomeNavigationKey.EntryDate.name -> {
                    requestCode = PICK_ENTRY_DATE_REQUEST
                    setTitle((it.second as CalenderData).mDateHintText)
                    findNavController().navigateSafe(
                        R.id.action_visaHomeExtendedFragment_to_singleDateCalendarFragment,
                        bundleOf(ARG_CALENDER_DATA to it.second as CalenderData)
                    )
                }

                ExtendedHomeNavigationKey.ExitDate.name -> {
                    requestCode = PICK_EXIT_DATE_REQUEST
                    setTitle((it.second as CalenderData).mDateHintText)
                    findNavController().navigateSafe(
                        R.id.action_visaHomeExtendedFragment_to_singleDateCalendarFragment,
                        bundleOf(ARG_CALENDER_DATA to it.second as CalenderData)
                    )
                }

                ExtendedHomeNavigationKey.TravellerCount.name -> {
                    requestCode = PICK_TRAVELER_COUNT_REQUEST
                    findNavController().navigateSafe(
                        R.id.action_visaHomeExtendedFragment_to_visaTravellerNumberFragment,
                        bundleOf(ARG_NUMBER_OF_ADULT to it.second as Int)
                    )
                }

                ExtendedHomeNavigationKey.Search.name -> {
                    if (NetworkUtil.hasNetwork(requireContext())) {
                        findNavController().navigateSafe(
                            R.id.action_visaHomeExtendedFragment_to_visaSelectionFragment,
                            bundleOf(ARG_VISA_SELECTION_MODEL to it.second as VisaSearchQuery)
                        )
                    } else {
                        Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        })

        getNavigationResultLiveData<Long>(ARG_SINGLE_DATE)?.observe(viewLifecycleOwner) {
            viewModel.setTravelDate(requestCode, it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Long>(
                ARG_SINGLE_DATE
            )
        }

        getNavigationResultLiveData<Intent>(ARG_COUNTRY_DATA)?.observe(viewLifecycleOwner) {
            viewModel.handleActivityResult(requestCode, it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Intent>(
                ARG_COUNTRY_DATA
            )
        }

        getNavigationResultLiveData<Intent>(ARG_TRAVELLER_DATA)?.observe(viewLifecycleOwner) {
            viewModel.handleActivityResult(requestCode, it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<Intent>(
                ARG_TRAVELLER_DATA
            )
        }
    }

    

    private fun setTitle(title: String) {
        (activity as VisaBookingActivity).supportActionBar?.title = title
    }

    companion object {
        const val ARG_VISA_SEARCH_QUERY_MODEL = "ARG_VISA_SEARCH_QUERY_MODEL"
    }
}
