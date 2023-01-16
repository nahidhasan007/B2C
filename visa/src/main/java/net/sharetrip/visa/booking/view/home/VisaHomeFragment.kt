package net.sharetrip.visa.booking.view.home

import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.visa.booking.view.homeextended.VisaHomeExtendedFragment.Companion.ARG_VISA_SEARCH_QUERY_MODEL
import net.sharetrip.visa.databinding.FragmentVisaHomeBinding
import net.sharetrip.visa.network.DataManager
import net.sharetrip.visa.utils.PagingLoadStateAdapter

class VisaHomeFragment : BaseFragment<FragmentVisaHomeBinding>() {
    private val viewModel: VisaHomeViewModel by lazy {
        VisaHomeViewModelFactory(
            DataManager.getSharedPref(requireContext()),
            DataManager.visaBookingApiService
        ).create(VisaHomeViewModel::class.java)
    }
    private val columnCount = 2
    private var isFirstPage = false
    private val adapter: VisaAdapter = VisaAdapter()



    override fun layoutId() = R.layout.fragment_visa_home

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.pagedListVisa.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        ItemClickSupport.addTo(bindingView.recyclerVisaCountry)
            .setOnItemClickListener { _, position, _ ->
                viewModel.navigateToDetail(adapter.snapshot().items[position])
            }

        val layoutManager = GridLayoutManager(requireActivity(), columnCount)
        bindingView.recyclerVisaCountry.layoutManager = layoutManager
        bindingView.recyclerVisaCountry.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    bindingView.progressBar.visibility = View.VISIBLE
                    bindingView.visaEmptyContainer.visibility = View.GONE
                } else if (loadState.refresh is LoadState.Error && isFirstPage) {
                    (loadState.refresh as LoadState.Error).error.message?.let {
                        bindingView.progressBar.visibility = View.GONE
                        if (adapter.itemCount == 0) {
                            bindingView.visaEmptyContainer.visibility = View.VISIBLE
                            (loadState.refresh as LoadState.Error).error.message?.let {
                                bindingView.textLabelSorry.text = it
                            }
                        }
                    }
                } else if (loadState.refresh is LoadState.NotLoading) {
                    bindingView.progressBar.visibility = View.GONE
                    bindingView.visaEmptyContainer.visibility = View.GONE
                    bindingView.recyclerVisaCountry.visibility = View.VISIBLE
                }
            }
        }

        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            (activity as VisaBookingActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
                it
        }

        viewModel.isFirstPage.observe(
            viewLifecycleOwner
        ) { isFirstPage = it }

        viewModel.navigateToHomeExtended.observe(
            viewLifecycleOwner
        ) {
            findNavController().navigateSafe(
                R.id.action_visaHome_to_visaHomeExtendedFragment,
                bundleOf(ARG_VISA_SEARCH_QUERY_MODEL to it)
            )
        }
    }

    
}
