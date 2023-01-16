package net.sharetrip.visa.history.view.list

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.network.model.Status
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaHistoryListBinding
import net.sharetrip.visa.network.DataManager
import net.sharetrip.visa.utils.ARG_VISA_BOOKING_CODE
import net.sharetrip.visa.utils.setTitleAndSubtitle
import net.sharetrip.visa.utils.setToolbarTripCoin

class VisaHistoryListFragment : BaseFragment<FragmentVisaHistoryListBinding>() {

    private val viewModel: VisaHistoryListViewModel by lazy {
        VisaHistoryListViewModelFactory(
            VisaHistoryListRepo(DataManager.visaHistoryApiService),
            DataManager.getSharedPref(requireContext())
        ).create(VisaHistoryListViewModel::class.java)
    }

    private val adapter = VisaHistoryAdapter()

    

    override fun layoutId() = R.layout.fragment_visa_history_list

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.activity_visa_booking_history))
        setToolbarTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.recyclerViewVisaList.adapter = adapter

        lifecycleScope.launchWhenStarted {
            viewModel.historyLiveData.collectLatest {
                adapter.submitData(it)
            }
        }

        ItemClickSupport.addTo(bindingView.recyclerViewVisaList)
            .setOnItemClickListener { _, position, _ ->
                val item = adapter.getItemAtAdapterPosition(position)
                if (item != null) {
                    val bundle = bundleOf(ARG_VISA_BOOKING_CODE to item.bookingCode)
                    findNavController().navigateSafe(R.id.action_visaHistoryList_to_visaHistoryDetailsFragment, bundle)
                }
            }

        viewModel.networkState.observe(viewLifecycleOwner) {
            if (it.status == Status.RUNNING) {
                bindingView.progressBar.visibility = View.VISIBLE
                bindingView.visaHistoryEmptyContainer.visibility = View.GONE
            } else if (it.status == Status.SUCCESS) {
                bindingView.progressBar.visibility = View.GONE
                bindingView.visaHistoryEmptyContainer.visibility = View.GONE
            } else if (it.status == Status.FAILED || it.status == Status.RESPONSE_EMPTY) {
                bindingView.progressBar.visibility = View.GONE
                bindingView.visaHistoryEmptyContainer.visibility = View.VISIBLE
                bindingView.textViewMessage.text = it.message
            }
        }
    }

    
}
