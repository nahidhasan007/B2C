package net.sharetrip.flight.history.view.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentOfFlightHistoryListBinding
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.utils.setTitleSubTitle

class FlightHistoryListFragment : BaseFragment<FragmentOfFlightHistoryListBinding>() {

    private val flightHistoryViewModel: FlightHistoryListViewModel by lazy {
        val token = SharedPrefsHelper(requireContext())[PrefKey.ACCESS_TOKEN, ""]
        FlightHistoryListViewModelFactory(
            token,
            FlightHistoryListRepo(DataManager.flightHistoryApiService)
        ).create(FlightHistoryListViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.fragment_of_flight_history_list

    override fun getViewModel(): BaseViewModel = flightHistoryViewModel

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.flight_bookings))
        val adapter = FlightHistoryAdapter()
        val mDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        val mItemDrawable =
            ContextCompat.getDrawable(requireActivity(), R.drawable.space_item_decorator_vertical)
        bindingView.recyclerView.adapter = adapter

        mDecoration.setDrawable(mItemDrawable!!)
        bindingView.recyclerView.addItemDecoration(mDecoration)

        ItemClickSupport.addTo(bindingView.recyclerView)
            .setOnItemClickListener { _: RecyclerView?, position: Int, _: View? ->
                val historyResponse = adapter.getItemAtAdapterPosition(position)
                val bundle = Bundle()
                val gson = Gson()
                bundle.putString(ARG_FLIGHT_HISTORY_RESPONSE, gson.toJson(historyResponse))
                findNavController().navigateSafe(
                    R.id.action_flight_history_to_booking_details,
                    bundleOf(ARG_FLIGHT_HISTORY_RESPONSE to bundle)
                )
            }

        adapter.addLoadStateListener {
            if (it.append.endOfPaginationReached) {
                if (adapter.itemCount == 0)
                    Toast.makeText(requireContext(), "No Flight History Found", Toast.LENGTH_SHORT)
                        .show()
            }
            if (it.append == LoadState.Loading) {
                bindingView.progressBar.visibility = View.VISIBLE
            } else {
                bindingView.progressBar.visibility = View.GONE
            }
        }

        lifecycleScope.launchWhenCreated {
            flightHistoryViewModel.historyList.collectLatest {
                adapter.submitData(it)
            }
        }

        flightHistoryViewModel.showMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        const val ARG_FLIGHT_HISTORY_RESPONSE = "ARG_FLIGHT_HISTORY_RESPONSE"
        const val ARG_FLIGHT_VOID_QUOTATION = "ARG_FLIGHT_VOID_QUOTATION"
    }
}
