package net.sharetrip.hotel.booking.view.hoteldetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.HotelDetail
import net.sharetrip.hotel.databinding.LayoutAmenitiesBottomSheetBinding

class AmenitiesBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bindingView: LayoutAmenitiesBottomSheetBinding
    private lateinit var hotelDetail: HotelDetail

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingView = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        val bundle = arguments
        hotelDetail = Gson().fromJson(bundle!!.getString("hotelDetails"), HotelDetail::class.java)

        hotelDetail.amenityGroups.forEach {
            val header = inflater.inflate(R.layout.ameneties_header_layout, null, true)
            val title: TextView = header.findViewById(R.id.amenitiesTitle)
            val bodyLayout: LinearLayout = header.findViewById(R.id.amenities_body_layout)
            title.text = it.groupName
            it.amenities.forEach { it2 ->
                val bodyItem = inflater.inflate(R.layout.amenitices_item_layout2, null, true)
                val amenitiesImg: ImageView = bodyItem.findViewById(R.id.amenitiesImg)
                val amenitiesText: TextView = bodyItem.findViewById(R.id.amenitiesText)
                amenitiesText.text = it2
                Glide.with(requireActivity())
                    .load(R.drawable.oval_round_black_background)
                    .into(amenitiesImg)
                bodyLayout.addView(bodyItem)
            }
            bindingView.amenitiesHeaderLayout.addView(header)
        }
        bindingView.back.setOnClickListener {
            dismiss()
        }
        return bindingView.root
    }

    private fun layoutId() = R.layout.layout_amenities_bottom_sheet

    companion object {
        fun newInstance(): AmenitiesBottomSheet {
            return AmenitiesBottomSheet()
        }
    }
}
