package net.sharetrip.hotel.history.view.historylist

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.FragmentHotelHistoryListBinding
import net.sharetrip.hotel.history.HotelHistoryActivity
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.PagingLoadStateAdapter

class HotelHistoryListFragment : BaseFragment<FragmentHotelHistoryListBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            HotelHistoryViewModelFactory(
                DataManager.getSharedPref(requireContext()),
                HotelHistoryListRepo(DataManager.hotelHistoryService)
            )
        ).get(
            HotelHistoryListViewModel::class.java
        )
    }

    private val adapter = HotelHistoryListAdapter()
    private var isFirstPage = false

    override fun layoutId(): Int = R.layout.fragment_hotel_history_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.booking_history))
        bindingView.listHotelHistory.adapter = adapter
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.historyLiveData.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        viewModel.isFirstPage.observe(
            viewLifecycleOwner
        ) { isFirstPage = it }

        ItemClickSupport.addTo(bindingView.listHotelHistory)
            .setOnItemClickListener { _, position, _ ->
                val list = adapter.snapshot().items
                val hotelItem = list?.get(position)
                findNavController().navigateSafe(
                    R.id.action_hotelHistoryFragment_to_hotelHistoryDetailsFragment,
                    bundleOf(ARG_HOTEL_HISTORY_ITEM to hotelItem)
                )
            }

        bindingView.listHotelHistory.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    bindingView.progressBar.visibility = View.VISIBLE
                    bindingView.hotelHistoryEmptyContainer.visibility = View.GONE
                } else if (loadState.refresh is LoadState.Error && isFirstPage) {
                    (loadState.refresh as LoadState.Error).error.message?.let {
                        bindingView.progressBar.visibility = View.GONE
                        if (adapter.itemCount == 0) {
                            bindingView.hotelHistoryEmptyContainer.visibility = View.VISIBLE
                            (loadState.refresh as LoadState.Error).error.message?.let {
                                bindingView.textLabelSorry.text = it
                            }
                        }
                    }
                } else if (loadState.refresh is LoadState.NotLoading) {
                    bindingView.progressBar.visibility = View.GONE
                    bindingView.hotelHistoryEmptyContainer.visibility = View.GONE
                    bindingView.listHotelHistory.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setTitle(title: String) {
        (activity as HotelHistoryActivity).supportActionBar?.apply {
            this.title = title
        }
    }

    companion object {
        const val ARG_HOTEL_HISTORY_ITEM = "ARG_HOTEL_HISTORY_ITEM"
    }
}
