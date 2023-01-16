package net.sharetrip.hotel.booking.view.hoteldetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.view.roomlist.RoomListFragment.Companion.ARG_ROOM_LIST_FRAGMENT_BUNDLE
import net.sharetrip.hotel.databinding.FragmentHotelDetailsBinding
import net.sharetrip.hotel.history.view.roomdetails.carousel.ImageAdapter
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.Constants.GEO_LOCATION_FORMAT
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment
import java.util.*

class HotelDetailsFragment : BaseFragment<FragmentHotelDetailsBinding>(),
    ViewPager.OnPageChangeListener {
    private val viewModel by lazy {
        val hotelDetailsBundle = arguments?.getBundle(ARG_HOTEL_DETAILS_BUNDLE)
        hotelId = hotelDetailsBundle?.getString(ARG_HOTEl_ID)!!
        searchCode = hotelDetailsBundle.getString(ARG_SEARCH_CODE)!!
        roomCount = hotelDetailsBundle.getInt(ARG_ROOM_COUNT)

        ViewModelProvider(
            this,
            HotelDetailsVMFactory(
                hotelId, searchCode, roomCount!!,
                DataManager.hotelApiService, DataManager.getSharedPref(requireContext())
            )
        )[HotelDetailViewModel::class.java]
    }
    private var totalImageCount = 0
    private var currentPage = 0
    private var handler: Handler? = null
    private var update: Runnable? = null
    private var imageAdapter: ImageAdapter? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private lateinit var hotelName: String
    private lateinit var hotelId: String
    private lateinit var searchCode: String
    private var roomCount: Int? = null

    override fun layoutId(): Int = R.layout.fragment_hotel_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(
            viewModel.generateHotelName(
                arguments?.getBundle(ARG_HOTEL_DETAILS_BUNDLE)?.getString(ARG_HOTEL_NAME)!!
            ),
            arguments?.getBundle(ARG_HOTEL_DETAILS_BUNDLE)?.getString(ARG_DETAILS_SUBTITLE)!!
        )

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        observeHotelDetails()
        observeImageLink()
        observeShareUrl()
        observeTitle()
        observeOpenMap()
        observeRoomNavigation()
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

    private fun observeOpenMap() {
        viewModel.openMap.observe(viewLifecycleOwner) {
            try {
                if (latitude != null && longitude != null) {
                    val uri = String.format(
                        Locale.ENGLISH,
                        GEO_LOCATION_FORMAT,
                        latitude,
                        longitude,
                        hotelName
                    )
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    requireContext().startActivity(intent)
                } else {
                    requireContext().getString(R.string.location_data_not_available)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Couldn't load map", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeTitle() {
        viewModel.title.observe(viewLifecycleOwner) {
            setTitleAndSubtitle(
                it,
                arguments?.getBundle(ARG_HOTEL_DETAILS_BUNDLE)?.getString(ARG_DETAILS_SUBTITLE)!!
            )
        }
    }

    private fun observeRoomNavigation() {
        viewModel.navigateToRoomList.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_hotelDetailsFragment_to_roomListFragment,
                bundleOf(ARG_ROOM_LIST_FRAGMENT_BUNDLE to it)
            )
        }
    }

    private fun observeShareUrl() {
        viewModel.shareUrl.observe(viewLifecycleOwner) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "\n\n")
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                requireContext().resources.getString(R.string.referral_msg_other)
            )
            startActivity(
                Intent.createChooser(
                    sharingIntent,
                    resources.getString(R.string.app_name)
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeImageLink() {
        viewModel.imageLink.observe(viewLifecycleOwner) {
            totalImageCount = it.size
            bindingView.viewPagerImage.addOnPageChangeListener(this)
            bindingView.textViewImageCount.text = "1/$totalImageCount"
            imageAdapter = activity?.supportFragmentManager?.let { it1 -> ImageAdapter(it1, it) }
            if (imageAdapter != null) {
                bindingView.viewPagerImage.adapter = imageAdapter
                if (it.size > 1) {
                    autoScroll()
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun observeHotelDetails() {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        viewModel.hotelDetails.observe(viewLifecycleOwner) { data ->
            latitude = data.contact[0].centerPoint!!.lat.toString()
            longitude = data.contact[0].centerPoint!!.lon.toString()
            hotelName = data.name
            for (i in 1..data!!.starRating.toInt()) {
                val item: View = inflater.inflate(R.layout.star_layout, null, true)
                bindingView.ratingBar.addView(item)
            }

            data.overview!!.forEach {
                val header = inflater.inflate(R.layout.hotel_description_header, null, true)
                val title: TextView = header.findViewById(R.id.description_header)
                val bodyLayout: LinearLayout = header.findViewById(R.id.body_layout)
                title.text = it.title
                it.paragraphs!!.forEach { it2 ->
                    val bodyItem =
                        inflater.inflate(R.layout.hotel_description_body_layout, null, true)
                    val body: TextView = bodyItem.findViewById(R.id.description_body)
                    body.text = it2
                    bodyLayout.addView(bodyItem)
                }
                bindingView.descriptionLayout.addView(header)
            }

            data.policyDescription.forEach {
                val header = inflater.inflate(R.layout.hotel_description_header, null, true)
                val title: TextView = header.findViewById(R.id.description_header)
                val bodyLayout: LinearLayout = header.findViewById(R.id.body_layout)
                title.text = it.title
                it.paragraphs.forEach { it2 ->
                    val bodyItem =
                        inflater.inflate(R.layout.hotel_description_body_layout, null, true)
                    val body: TextView = bodyItem.findViewById(R.id.description_body)
                    body.text = it2
                    bodyLayout.addView(bodyItem)
                }
                bindingView.descriptionLayout.addView(header)
            }

            data.amenityLogo.forEach {
                val view = inflater.inflate(R.layout.amenitices_item_layout, null, true)
                val amenitiesImg: ImageView = view.findViewById(R.id.amenitiesImg)
                val amenitiesText: TextView = view.findViewById(R.id.amenitiesText)
                amenitiesText.text = it.name
                Glide.with(requireActivity())
                    .load(it.logo)
                    .into(amenitiesImg)
                bindingView.amenitiesLayout.addView(view)
            }

            bindingView.allAmenities.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("hotelDetails", Gson().toJson(data))
                val addBottomDialogFragment: AmenitiesBottomSheet =
                    AmenitiesBottomSheet.newInstance()
                addBottomDialogFragment.arguments = bundle
                childFragmentManager.let {
                    addBottomDialogFragment.show(it, "")
                }
            }
        }
    }

    private fun autoScroll() {
        handler = Handler(Looper.getMainLooper())
        update = Runnable {
            if (currentPage == totalImageCount)
                currentPage = 0
            bindingView.viewPagerImage.setCurrentItem(currentPage++, true)
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler!!.post(update!!)
            }
        }, 100, 3000)
    }

    companion object {
        const val ARG_HOTEL_NAME = "ARG_HOTEL_NAME"
        const val ARG_HOTEL_DETAILS_BUNDLE = "ARG_HOTEL_DETAILS_BUNDLE"
        const val ARG_HOTEl_ID = "ARG_HOTEl_ID"
        const val ARG_SEARCH_CODE = "ARG_SEARCH_CODE"
        const val ARG_ROOM_COUNT = "ARG_ROOM_COUNT"
        const val ARG_DETAILS_SUBTITLE = "ARG_DETAILS_SUBTITLE"
    }
}
