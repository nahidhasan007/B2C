package net.sharetrip.bus.history.view.pricingdetails

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusHistoryPricingDetailBinding
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.history.model.SeatClassPrice

class BusHistoryPricingDetailFragment : BaseFragment<FragmentBusHistoryPricingDetailBinding>() {
    private val busHistoryData by lazy {
        requireArguments().getParcelable<HistoryDetails>(
            ARG_BUS_PRICING_DETAILS
        )!!
    }

    override fun layoutId(): Int = R.layout.fragment_bus_history_pricing_detail

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val price =
            (busHistoryData.gatewayAmount - busHistoryData.serviceCharge) / busHistoryData.seats.size

        for ((countView, it) in busHistoryData.seats.withIndex()) {
            val v = ItemBusHistoryPricingSeatView(requireContext())
            v.binding.allData =
                SeatClassPrice("${it.seatNo[0]}-${it.seatNo[1]}", it.seatTypeId, price.toString())
            bindingView.pricingOnwardsSeats.busSeatContainer.addView(v, countView)
        }

        bindingView.historydata = busHistoryData
    }

    companion object {
        const val ARG_BUS_PRICING_DETAILS = "ARG_BUS_PRICING_DETAILS"
    }
}
