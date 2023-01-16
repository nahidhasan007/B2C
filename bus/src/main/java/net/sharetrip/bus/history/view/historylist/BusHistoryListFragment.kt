package net.sharetrip.bus.history.view.historylist

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusHistoryListBinding
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.history.view.BusHistoryActivity
import net.sharetrip.bus.history.view.historydetails.BusHistoryDetailsFragment.Companion.ARG_BUS_HISTORY_DETAILS
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.PagingLoadStateAdapter

class BusHistoryListFragment : BaseFragment<FragmentBusHistoryListBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            BusHistoryVMFactory(
                DataManager.getSharedPref(requireContext()),
                BusHistoryListRepo(DataManager.busHistoryApiService),
                DataManager.busHistoryApiService
            )
        ).get(
            BusHistoryViewModel::class.java
        )
    }

    private val adapter = BusHistoryListAdapter()
    private var isFirstPage = false

    override fun layoutId() = R.layout.fragment_bus_history_list

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.busBookingHistoryRecyclerView.adapter = adapter
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.historyLiveData.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            (activity as BusHistoryActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
                it
        }

        viewModel.isFirstPage.observe(viewLifecycleOwner) {
            isFirstPage = it
        }

        viewModel.navigateToHistoryDetails.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_busHomeFragment_to_busHistoryDetailsFragment,
                bundleOf(ARG_BUS_HISTORY_DETAILS to it as HistoryDetails)
            )
        }

        ItemClickSupport.addTo(bindingView.busBookingHistoryRecyclerView)
            .setOnItemClickListener { _, position, _ ->
                val list = adapter.snapshot().items
                val busItem = list[position]
                viewModel.getHistoryDetails(busItem.bookingId)
            }

        bindingView.busBookingHistoryRecyclerView.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    bindingView.dataLoadingProgressBar.visibility = View.VISIBLE
                    bindingView.busHistoryEmptyContainer.visibility = View.GONE
                } else if (loadState.refresh is LoadState.Error && isFirstPage) {
                    (loadState.refresh as LoadState.Error).error.message?.let {
                        bindingView.dataLoadingProgressBar.visibility = View.GONE

                        if (adapter.itemCount == 0) {
                            bindingView.busHistoryEmptyContainer.visibility = View.VISIBLE
                            (loadState.refresh as LoadState.Error).error.message?.let {
                                bindingView.textLabelSorry.text = it
                            }
                        }
                    }
                } else if (loadState.refresh is LoadState.NotLoading) {
                    bindingView.dataLoadingProgressBar.visibility = View.GONE
                    bindingView.busHistoryEmptyContainer.visibility = View.GONE
                    bindingView.busBookingHistoryRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }
}
