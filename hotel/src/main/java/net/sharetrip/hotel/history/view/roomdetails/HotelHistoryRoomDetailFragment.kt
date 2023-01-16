package net.sharetrip.hotel.history.view.roomdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.FragmentHotelHistoryRoomDetailBinding
import net.sharetrip.hotel.history.HotelHistoryActivity
import net.sharetrip.hotel.history.model.BookedRoom
import net.sharetrip.hotel.history.view.roomdetails.carousel.ImageAdapter
import java.util.*

class HotelHistoryRoomDetailFragment : BaseFragment<FragmentHotelHistoryRoomDetailBinding>(),
    ViewPager.OnPageChangeListener {
    private val viewModel by lazy {
        roomDetailsBundle = arguments?.getBundle(ARG_HOTEL_HISTORY_ROOM_DETAIL)
        roomInfo = roomDetailsBundle?.getParcelable(ARG_EXTRA_ROOM_DETAIL_MODEL)
        freeCancel = roomDetailsBundle?.getString(ARG_EXTRA_FREE_CANCELLATION_DATE)
        ViewModelProvider(this,
            HotelHistoryRoomDetailsVMFactory(roomInfo, freeCancel)).get(
            HotelHistoryRoomDetailViewModel::class.java
        )
    }
    private var roomDetailsBundle: Bundle? = null
    private var roomInfo: BookedRoom? = null
    private var freeCancel: String? = null
    private var totalImageCount = 0
    private var currentPage = 0
    private var handler: Handler? = null
    private var update: Runnable? = null
    private var imageAdapter: ImageAdapter? = null

    override fun layoutId() = R.layout.fragment_hotel_history_room_detail

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.room_details))
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        roomInfo?.images?.let {
            val imageList =
                Gson().fromJson(roomInfo?.images, Array<String>::class.java).toList()
            totalImageCount = imageList.size
            bindingView.viewPagerImage.addOnPageChangeListener(this)
            bindingView.textViewImageCount.text = "1/$totalImageCount"
            imageAdapter =
                activity?.supportFragmentManager?.let { it1 -> ImageAdapter(it1, imageList) }
            imageAdapter?.let {
                bindingView.viewPagerImage.adapter = it
                if (totalImageCount > 1) {
                    autoScroll()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (handler != null && update != null) {
            handler!!.removeCallbacks(update!!)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

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

    private fun setTitle(title: String) {
        (activity as HotelHistoryActivity).supportActionBar?.title = title
    }

    companion object {
        const val ARG_HOTEL_HISTORY_ROOM_DETAIL = "ARG_HOTEL_HISTORY_ROOM_DETAIL"
        const val ARG_EXTRA_ROOM_DETAIL_MODEL = "ARG_EXTRA_ROOM_DETAIL_MODEL"
        const val ARG_EXTRA_FREE_CANCELLATION_DATE = "ARG_EXTRA_FREE_CANCELLATION_DATE"
    }
}
