package net.sharetrip.tour.history.list

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourUpcomingBinding
import net.sharetrip.tour.history.detail.TourBookingDetailFragment
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.setTitleAndSubtitle
import net.sharetrip.tour.utils.setTripCoin

class TourHistoryListFragment : BaseFragment<FragmentTourUpcomingBinding>() {

    private val viewModel: TourHistoryListViewModel by viewModels {
        TourHistoryListVMF(
            TourDataManager.getSharedPref(requireContext()),
            TourHistoryListRepo(TourDataManager.tourHistoryAPIService)
        )
    }

    private val adapter = TourHistoryAdapter()

    

    override fun layoutId(): Int = R.layout.fragment_tour_upcoming

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.tour_history))
        setTripCoin()

        bindingView.listTourHistory.adapter = adapter

        viewModel.historyLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(this.lifecycle, it)
        }

        val mDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        val mItemDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.space_item_decorator_vertical)
        mDecoration.setDrawable(mItemDrawable!!)
        bindingView.listTourHistory.addItemDecoration(mDecoration)

        ItemClickSupport.addTo(bindingView.listTourHistory)
            .setOnItemClickListener { recyclerView, position, v ->
                val bundle = bundleOf(
                    TourBookingDetailFragment.ARG_TOUR_BOOKING_CODE to adapter.getItemAtAdapterPosition(
                        position
                    )!!.bookingCode
                )
                findNavController().navigate(
                    R.id.action_tourHistoryListFragment_to_tourBookingDetailFragment,
                    bundle
                )
            }
    }

    
}
