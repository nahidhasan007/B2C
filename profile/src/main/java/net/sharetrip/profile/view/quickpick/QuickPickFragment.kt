package net.sharetrip.profile.view.quickpick

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentQuickPickBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.profile.view.travelerShow.TravelerShowFragment.Companion.ARG_PASSENGER_MODEL

class QuickPickFragment : BaseFragment<FragmentQuickPickBinding>() {

    private val viewModel: QuickPickViewModel by viewModels {
        QuickPickViewModelFactory(QuickPickRepository(DataManager.profileApiService), DataManager.getSharedPref(requireContext()))
    }

    private val adapter = QuickPickAdapter()

    

    override fun layoutId() = R.layout.fragment_quick_pick

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.favourite_guest_lists))
        hideTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.executePendingBindings()
        bindingView.listQuickPickPassenger.adapter = adapter

        viewModel.fetchProfileInfo()

        ItemClickSupport.addTo(bindingView.listQuickPickPassenger).setOnItemClickListener { _ , position, _ ->
            val bundle = bundleOf(ARG_PASSENGER_MODEL to viewModel.passengerList.value?.get(position)!!)
            findNavController().navigateSafe(R.id.action_quickPickFragment_to_travelerShowFragment, bundle)
        }

        viewModel.passengerList.observe(viewLifecycleOwner) {
            adapter.updateQuickPickList(it)
        }

        viewModel.navigateToAddTraveler.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigateSafe(R.id.action_quickPickFragment_to_addTravelerFragment)
        })
    }

    
}
