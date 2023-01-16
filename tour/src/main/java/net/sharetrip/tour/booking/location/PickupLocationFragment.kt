package net.sharetrip.tour.booking.location

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.booking.reserve.TourReserveFragment.Companion.ARG_TOUR_PARAM_MODEL
import net.sharetrip.tour.databinding.FragmentPickupLocationBinding
import net.sharetrip.tour.model.TourParam
import net.sharetrip.tour.utils.hideTripCoin
import net.sharetrip.tour.utils.setTitleAndSubtitle

class PickupLocationFragment : BaseFragment<FragmentPickupLocationBinding>() {

    private val viewModel: PickupLocationViewModel by viewModels {
        val tourParam = arguments?.getParcelable<TourParam>(ARG_TOUR_PARAM_MODEL)
        PickUpLocationVMF(tourParam!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    

    override fun layoutId(): Int = R.layout.fragment_pickup_location

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.pickup_location))
        hideTripCoin()

        viewModel.msg.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.navigateToContact.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                R.id.action_pickupLocationFragment_to_clientContactFragment,
                it
            )
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.done, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.action_done == item.itemId) {
            viewModel.navigateToContact(
                bindingView.addressLine1InputEditText.text.toString(),
                bindingView.editTextAdditionalRequirement.text.toString(),
                bindingView.checkBoxAgree.isChecked
            )

            return false
        }
        return super.onOptionsItemSelected(item)
    }

    
}
