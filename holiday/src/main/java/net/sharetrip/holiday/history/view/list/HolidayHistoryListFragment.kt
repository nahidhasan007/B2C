package net.sharetrip.holiday.history.view.list

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayHistoryListBinding
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.ARG_HOLIDAY_HISTORY_ITEM_BOOKING_CODE
import net.sharetrip.holiday.utils.setTitleAndSubtitle

class HolidayHistoryListFragment : BaseFragment<FragmentHolidayHistoryListBinding>() {

    private val viewModel: HolidayHistoryViewModel by viewModels {
        HolidayHistoryViewModelFactory(DataManager.getSharedPref(requireContext()), HolidayHistoryListRepo(DataManager.holidayHistoryApiService))
    }
    private val adapter = HolidayHistoryAdapter()

    override fun layoutId() = R.layout.fragment_holiday_history_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.holiday_history))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.recyclerViewHolidayDeparted.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.historyPagingData.collectLatest {
                adapter.submitData(it)
            }
        }

        ItemClickSupport.addTo(bindingView.recyclerViewHolidayDeparted)
            .setOnItemClickListener { _, position, _ ->
                val item = adapter.getItemAtAdapterPosition(position)
                item?.let {
                    val bundle = bundleOf(ARG_HOLIDAY_HISTORY_ITEM_BOOKING_CODE to item.bookingCode)
                    findNavController().navigateSafe(R.id.action_holidayHistoryListFragment_to_holidayBookingDetailFragment, bundle)
                }
            }

        viewModel.networkState.observe(viewLifecycleOwner) {
            if (it.status == Status.RUNNING) {
                bindingView.progressBar.visibility = View.VISIBLE
                bindingView.holidayHistoryEmptyContainer.visibility = View.GONE
            } else if (it.status == Status.SUCCESS) {
                bindingView.progressBar.visibility = View.GONE
                bindingView.holidayHistoryEmptyContainer.visibility = View.GONE
            } else if (it.status == Status.FAILED || it.status == Status.RESPONSE_EMPTY) {
                bindingView.progressBar.visibility = View.GONE
                bindingView.holidayHistoryEmptyContainer.visibility = View.VISIBLE
                bindingView.textViewMessage.text = it.message
            }
        }
    }
}
