package net.sharetrip.hotel.booking.view.travelleroom

import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.TravellerRoomInfo
import net.sharetrip.hotel.databinding.FragmentTravellerRoomBinding
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment

class TravellerRoomFragment : BaseFragment<FragmentTravellerRoomBinding>() {
    private val viewModel by viewModels<TravellerRoomViewModel>()
    private lateinit var adapter: TravellerRoomAdapter

    override fun layoutId(): Int = R.layout.fragment_traveller_room

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.travelers_and_rooms), "")

        bindingView.viewModel = viewModel
        adapter = TravellerRoomAdapter()
        bindingView.listTravellerRoom.adapter = adapter

        viewModel.listTravellerRooms.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        val rooms = arguments?.getBundle(ARG_TRAVELER_DATA)
            ?.getParcelableArrayList<TravellerRoomInfo>(ARG_TRAVELER_ROOM_LIST)
        viewModel.bundleUpdate(rooms!!)

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            setNavigationResult(it.second as Intent, ARG_TRAVELER_DATA)
            findNavController().navigateUp()
        })
    }

    companion object {
        const val ARG_TRAVELER_ROOM_LIST = "ARG_TRAVELER_ROOM_LIST"
        const val ARG_TRAVELER_DATA = "ARG_TRAVELER_DATA"
        const val EXTRA_TRAVELER_ROOM_LIST = "EXTRA_TRAVELER_ROOM_LISTt"
    }
}
