package net.sharetrip.bus.booking.view.locationpoints

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusBoardingPointsBinding

class BoardingPointsFragment(val viewModel: SelectLocationPointsViewModel) :
    BaseFragment<FragmentBusBoardingPointsBinding>() {

    override fun layoutId() = R.layout.fragment_bus_boarding_points

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val adapter = PointsAdapter(viewModel.boardingPoints, viewModel)
        bindingView.boardingRecycler.adapter = adapter
    }
}
