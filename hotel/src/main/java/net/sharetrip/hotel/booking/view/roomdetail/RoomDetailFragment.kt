package net.sharetrip.hotel.booking.view.roomdetail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.RoomDetails
import net.sharetrip.hotel.databinding.FragmentRoomDetailBinding
import net.sharetrip.hotel.history.view.roomdetails.carousel.ImageAdapter
import net.sharetrip.shared.view.BaseFragment
import java.util.*

class RoomDetailFragment : BaseFragment<FragmentRoomDetailBinding>(),
    ViewPager.OnPageChangeListener {
    private var totalImageCount = 0
    private var currentPage = 0
    private var handler: Handler? = null
    private var update: Runnable? = null
    private var imageAdapter: ImageAdapter? = null

    override fun layoutId(): Int = R.layout.fragment_room_detail

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun initOnCreateView() {
        val roomDetailsBundle = arguments?.getBundle(ARG_ROOM_DETAIL_BUNDLE)
        val images: ArrayList<String> =
            roomDetailsBundle?.getSerializable(ARG_ROOM_DETAILS_IMAGES) as ArrayList<String>
        val roomDetails = roomDetailsBundle.getParcelable<RoomDetails>(ARG_ROOM_DETAIL_MODEL)
        bindingView.roomDetails = roomDetails
        bindingView.lifecycleOwner = viewLifecycleOwner

        if (images.isNotEmpty()) {
            totalImageCount = images.size
            bindingView.viewPagerImage.addOnPageChangeListener(this)
            bindingView.textViewImageCount.text = "1/$totalImageCount"
            imageAdapter =
                activity?.supportFragmentManager?.let { it1 -> ImageAdapter(it1, images) }
            if (imageAdapter != null) {
                bindingView.viewPagerImage.adapter = imageAdapter
                if (images.size > 1) {
                    autoScroll()
                }
            }
        }

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        roomDetails?.roomAmenities?.forEach {
            val view = inflater.inflate(R.layout.amenitices_item_layout, null, true)
            val amenitiesText: TextView = view.findViewById(R.id.amenitiesText)
            val amenitiesImg: ImageView = view.findViewById(R.id.amenitiesImg)
            amenitiesText.text = it.name
            Glide.with(requireContext()).load(it.logoPng).into(amenitiesImg)
            bindingView.amenitiesLayout.addView(view)
        }
    }

    override fun onStop() {
        super.onStop()
        if (handler != null && update != null) {
            handler!!.removeCallbacks(update!!)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    @SuppressLint("SetTextI18n")
    override fun onPageSelected(position: Int) {
        bindingView.textViewImageCount.text = "${position + 1}/$totalImageCount"
    }

    private fun autoScroll() {
        handler = Handler(Looper.getMainLooper())
        update = Runnable {
            if (currentPage == totalImageCount) {
                currentPage = 0
            }
            bindingView.viewPagerImage.setCurrentItem(currentPage++, true)
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler!!.post(update!!)
            }
        }, 100, 3000)
    }

    companion object {
        const val ARG_ROOM_DETAIL_BUNDLE = "ARG_ROOM_DETAIL_BUNDLE"
        const val ARG_ROOM_DETAIL_MODEL = "ROOM_DETAIL_MODEL"
        const val ARG_ROOM_DETAILS_IMAGES = "ROOM_DETAILS_IMAGES"
        const val ARG_HOTEL_SEARCH_CODE = "ARG_HOTEL_SEARCH_CODE"
        const val ARG_HOTEL_ID = "ARG_HOTEL_ID"
    }
}
