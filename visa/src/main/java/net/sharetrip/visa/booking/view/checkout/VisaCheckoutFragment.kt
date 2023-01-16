package net.sharetrip.visa.booking.view.checkout

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.summary.VisaBookingSummaryFragment.Companion.ARG_VISA_SUMMARY_DATA_MODEL
import net.sharetrip.visa.booking.view.travellerInfo.VisaTravellerInfoFragment.Companion.ARG_VISA_TRAVELLER_INFO_MODEL
import net.sharetrip.visa.databinding.FragmentVisaCheckoutBinding
import net.sharetrip.visa.utils.RecyclerDecoration

class VisaCheckoutFragment : BaseFragment<FragmentVisaCheckoutBinding>() {
    private val viewModel by lazy {
        val visaSearchQuery =
            requireArguments().getParcelable<VisaSearchQuery>(ARG_VISA_CHECKOUT_DATA_MODEL)!!

        ViewModelProvider(
            this,
            VisaCheckoutVMFactory(visaSearchQuery)
        ).get(
            VisaCheckoutViewModel::class.java
        )
    }

    private val textViewTripCoin by lazy {
        (activity as VisaBookingActivity).findViewById<TextView>(R.id.text_view_trip_coin)
    }

    private lateinit var adapter: TravellerAdapter



    override fun layoutId() = R.layout.fragment_visa_checkout

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        textViewTripCoin.visibility = View.VISIBLE
        bindingView.recyclerGuestList.layoutManager = LinearLayoutManager(context)
        adapter = TravellerAdapter()
        bindingView.recyclerGuestList.adapter = adapter

        bindingView.recyclerGuestList.addItemDecoration(
            RecyclerDecoration(
                requireContext(),
                space = 1
            )
        )

        viewModel.travellerList.observe(viewLifecycleOwner) {
            adapter.updateTravelerList(it)
        }

        ItemClickSupport.addTo(bindingView.recyclerGuestList)
            .setOnItemClickListener { _: RecyclerView, position: Int, _: View ->
                viewModel.onTravellerInfoUpdate(position)
            }

        viewModel.navigateToTravellerInfo.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_visaCheckoutFragment_to_visaTravellerInfoFragment,
                bundleOf(ARG_VISA_TRAVELLER_INFO_MODEL to it)
            )
        }

        viewModel.navigateToBookingSummary.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_visaCheckoutFragment_to_visaBookingSummaryFragment,
                bundleOf(ARG_VISA_SUMMARY_DATA_MODEL to it)
            )
        }
    }

    

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    companion object {
        const val ARG_VISA_CHECKOUT_DATA_MODEL = "ARG_VISA_CHECKOUT_DATA_MODEL"
    }
}
