package net.sharetrip.hotel.history.view.hotel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.FragmentHotelInfoBinding
import net.sharetrip.hotel.history.HotelHistoryActivity
import net.sharetrip.hotel.history.model.Amenities
import net.sharetrip.hotel.history.model.HotelHistoryItem
import net.sharetrip.hotel.history.model.HotelLocation
import net.sharetrip.hotel.history.view.roomdetails.HotelHistoryRoomDetailFragment.Companion.ARG_EXTRA_FREE_CANCELLATION_DATE
import net.sharetrip.hotel.history.view.roomdetails.HotelHistoryRoomDetailFragment.Companion.ARG_EXTRA_ROOM_DETAIL_MODEL
import net.sharetrip.hotel.history.view.roomdetails.HotelHistoryRoomDetailFragment.Companion.ARG_HOTEL_HISTORY_ROOM_DETAIL
import net.sharetrip.hotel.utils.Constants.GEO_LOCATION_FORMAT
import java.util.*

class HotelInfoFragment : BaseFragment<FragmentHotelInfoBinding>() {
    private val viewModel by lazy {
        hotelResponse =
            arguments?.getParcelable<HotelHistoryItem>(ARG_HOTEL_BOOKING_HOTEL_INFO) as HotelHistoryItem
        ViewModelProvider(this,
            HotelInfoVMFactory(hotelResponse)).get(
            HotelInfoViewModel::class.java
        )
    }
    private var hotelResponse: HotelHistoryItem? = null
    private lateinit var viewAdapter: BookedRoomAdapter

    override fun layoutId(): Int = R.layout.fragment_hotel_info

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.hotel_info))
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        generateAmenities(hotelResponse)
        hotelResponse?.let {
            viewAdapter =
                BookedRoomAdapter(hotelResponse!!.bookedRooms, hotelResponse!!.hotel!!.thumbnail)
            bindingView.listRooms.adapter = viewAdapter
            bindingView.imageViewDetail.loadImage(hotelResponse!!.hotel!!.thumbnail)
            ItemClickSupport.addTo(bindingView.listRooms)
                .setOnItemClickListener { _, position, _ ->
                    val room = viewAdapter.roomInfoList[position]
                    val bundle = Bundle()
                    bundle.putParcelable(ARG_EXTRA_ROOM_DETAIL_MODEL, room)
                    bundle.putString(ARG_EXTRA_FREE_CANCELLATION_DATE,
                        viewModel.hotelInfo!!.freeCancellationDate)
                    findNavController().navigateSafe(R.id.action_historyHotelInfoFragment_to_hotelHistoryRoomDetailsFragment,
                        bundleOf(ARG_HOTEL_HISTORY_ROOM_DETAIL to bundle))
                }
        }

        viewModel.openMap.observe(viewLifecycleOwner) {
            try {
                val hotelLocation =
                    Gson().fromJson(viewModel.hotelInfo?.hotel?.center, HotelLocation::class.java)
                val latitude = hotelLocation.lat
                val longitude = hotelLocation.lon

                val hotelName = viewModel.hotelInfo?.hotel?.name
                val uri =
                    String.format(
                        Locale.ENGLISH,
                        GEO_LOCATION_FORMAT,
                        latitude,
                        longitude,
                        hotelName
                    )
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                context?.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun generateAmenities(hotelResponse: HotelHistoryItem?) {
        try {
            val amenities =
                Gson().fromJson(hotelResponse?.hotel?.amenities, Array<Amenities>::class.java)
                    .toList()
            val inflater =
                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            amenities.forEach {
                val view = inflater.inflate(R.layout.amenitices_item_layout, null, true)
                val amenitiesImg: ImageView = view.findViewById(R.id.amenitiesImg)
                val amenitiesText: TextView = view.findViewById(R.id.amenitiesText)
                amenitiesText.text = it.title
                amenitiesImg.loadImage(it.logoPng)

                bindingView.linearAmenities.addView(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setTitle(title: String) {
        (activity as HotelHistoryActivity).supportActionBar?.title = title
    }

    companion object {
        const val ARG_HOTEL_BOOKING_HOTEL_INFO = "ARG_HOTEL_BOOKING_HOTEL_INFO"
    }
}
