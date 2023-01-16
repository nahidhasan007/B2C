package net.sharetrip.bus.booking.view.locationpoints

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentDroppingPointsBinding

class DroppingPointsFragment(val viewModel: SelectLocationPointsViewModel) :
    BaseFragment<FragmentDroppingPointsBinding>() {

    override fun layoutId() = R.layout.fragment_dropping_points

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val adapter = PointsAdapter(viewModel.droppingPoints, viewModel)
        bindingView.droppinRecycler.adapter = adapter
    }
}
