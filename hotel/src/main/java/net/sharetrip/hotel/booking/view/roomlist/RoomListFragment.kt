package net.sharetrip.hotel.booking.view.roomlist

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.HotelBookingActivity
import net.sharetrip.hotel.booking.model.PromotionalCoupon
import net.sharetrip.hotel.booking.model.Rooms
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment.Companion.ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment.Companion.ARG_HOTEL_DISCOUNT_BUNDLE
import net.sharetrip.hotel.booking.view.roomdetail.RoomDetailFragment.Companion.ARG_ROOM_DETAIL_BUNDLE
import net.sharetrip.hotel.booking.view.roomlist.adapter.BundleAdapter
import net.sharetrip.hotel.databinding.FragmentRoomListBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTripCoin
import net.sharetrip.shared.view.BaseFragment

class RoomListFragment : BaseFragment<FragmentRoomListBinding>() {
    private lateinit var firstRoomAdapter: BundleAdapter
    private val viewModel by lazy {
        val roomListBundle = arguments?.getBundle(ARG_ROOM_LIST_FRAGMENT_BUNDLE)!!
        val hotelId = roomListBundle.getString(ARG_EXTRA_HOTEL_ID)!!
        val searchCode = roomListBundle.getString(ARG_ROOM_EXTRA_HOTEL_SEARCH_CODE)!!
        val tripCoin = roomListBundle.getString(ARG_EARN_TRIP_COIN)!!
        val propertyName = activity?.intent?.extras?.getString(ARG_PROPERTY_NAME)

        ViewModelProvider(
            this,
            RoomListVMFactory(
                searchCode,
                hotelId,
                tripCoin,
                propertyName,
                DataManager.getSharedPref(requireContext()),
                DataManager.hotelApiService
            )
        )[RoomListViewModel::class.java]
    }

    override fun layoutId() = R.layout.fragment_room_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        clearSubTitle()
        bindingView.viewModel = viewModel
        bindingView.bottomSheet.viewModel = viewModel
        firstRoomAdapter = BundleAdapter(viewModel, 1)
        bindingView.listRoomOne.adapter = firstRoomAdapter

        viewModel.roomListResponse.observe(viewLifecycleOwner) {
            roomListVisualization(it.first.rooms as List<MutableList<Rooms>>, it.second)
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        viewModel.navigateToRoomDetails.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_roomListFragment_to_roomDetailFragment,
                bundleOf(ARG_ROOM_DETAIL_BUNDLE to it)
            )
        }

        viewModel.navigateToHotelDiscount.observe(viewLifecycleOwner) {
            val list = mutableListOf<PromotionalCoupon>()
            viewModel.promotionalCoupon?.forEach { coupon ->
                list.add(coupon)
            }
            findNavController().navigateSafe(
                R.id.action_roomListFragment_to_hotelDiscountFragment,
                bundleOf(
                    ARG_HOTEL_DISCOUNT_BUNDLE to it,
                    ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON to ListOfCoupon(list)
                )
            )
        }

        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            setTripCoin(it)
        }
    }

    private fun roomListVisualization(rooms: List<MutableList<Rooms>>, roomNumber: Int) {
        if (rooms.isNotEmpty()) {
            firstRoomAdapter.updateRoomInfo(rooms[0] as ArrayList<Rooms>, roomNumber)
        }
    }

    private fun clearSubTitle() {
        (activity as HotelBookingActivity).supportActionBar?.apply {
            this.subtitle = ""
        }
    }

    companion object {
        const val ARG_ROOM_LIST_FRAGMENT_BUNDLE = "ARG_ROOM_LIST_FRAGMENT_BUNDLE"
        const val ARG_EXTRA_HOTEL_ID = "ARG_EXTRA_HOTEL_ID"
        const val ARG_ROOM_EXTRA_HOTEL_SEARCH_CODE = "ARG_ROOM_EXTRA_HOTEL_SEARCH_CODE"
        const val ARG_EARN_TRIP_COIN = "ARG_EARN_TRIP_COIN"
        const val ARG_PROPERTY_NAME = "ARG_PROPERTY_NAME"
    }
}
