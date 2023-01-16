package net.sharetrip.flight.history.view.travelerList

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.passenger.PassengerFragment.Companion.ARG_IMAGE_DATA
import net.sharetrip.flight.databinding.FragmentTravellerListOfFlightHistoryBinding
import net.sharetrip.flight.history.FlightHistoryActivity
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.history.model.Traveller
import net.sharetrip.flight.history.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_TAG
import net.sharetrip.flight.history.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_URL
import net.sharetrip.flight.history.view.list.FlightHistoryListFragment.Companion.ARG_FLIGHT_HISTORY_RESPONSE
import net.sharetrip.flight.history.view.travelerList.adapter.TravelerHistoryAdapter

class TravellerListFragment : BaseFragment<FragmentTravellerListOfFlightHistoryBinding>() {
    private lateinit var viewModel: TravellerListViewModel
    private lateinit var adapter: TravelerHistoryAdapter
    private lateinit var historyResponse: FlightHistoryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gson = Gson()
        historyResponse = gson.fromJson(
            requireArguments().getBundle(ARG_FLIGHT_HISTORY_RESPONSE)
                ?.getString(ARG_FLIGHT_HISTORY_RESPONSE), FlightHistoryResponse::class.java
        )
    }

    override fun layoutId(): Int = R.layout.fragment_traveller_list_of_flight_history

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.travellers_info))
        viewModel =
            TravellerListViewModelFactory(historyResponse).create(TravellerListViewModel::class.java)
        bindingView.viewModel = viewModel
        adapter =
            TravelerHistoryAdapter(viewModel, historyResponse.travellers as ArrayList<Traveller>)
        bindingView.travellerRecycler.adapter = adapter

        viewModel.gotoImagePreview.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            bundle.putString(ARG_IMAGE_URL, it)

            if(viewModel.imageTag == "passport") {
                bundle.putString(ARG_IMAGE_TAG, getString(R.string.passport_copy_preview))
            }
            else {
                bundle.putString(ARG_IMAGE_TAG, getString(R.string.visa_copy_preview))
            }

            findNavController().navigateSafe(
                R.id.action_traveller_info_to_image_preview,
                bundleOf(ARG_IMAGE_DATA to bundle)
            )
        })
    }

    private fun setTitle(title: String) {
        (activity as FlightHistoryActivity).supportActionBar?.title = title
    }
}
