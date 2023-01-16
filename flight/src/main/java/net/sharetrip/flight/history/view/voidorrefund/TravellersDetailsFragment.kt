package net.sharetrip.flight.history.view.voidorrefund

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentFlightRefundVoidTravellersDetailsBinding
import net.sharetrip.flight.history.model.Traveller
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_SELECTED_PASSENGERS
import net.sharetrip.flight.utils.setTitleSubTitle

class TravellersDetailsFragment : BaseFragment<FragmentFlightRefundVoidTravellersDetailsBinding>() {

    override fun layoutId(): Int = R.layout.fragment_flight_refund_void_travellers_details

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.travellers_detail))

        val travellers = requireArguments().getBundle(ARG_SELECTED_PASSENGERS)
            ?.getParcelableArrayList<Traveller>(ARG_SELECTED_PASSENGERS) as List<Traveller>

        bindingView.passengerRecyclerView.adapter = TravellerDetailsAdapter(travellers)
    }
}
