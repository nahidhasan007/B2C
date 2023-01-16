package net.sharetrip.hotel.history.view.pricing

import androidx.lifecycle.ViewModelProvider
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.FragmentBookingRoomPriceBinding
import net.sharetrip.hotel.history.HotelHistoryActivity
import net.sharetrip.hotel.history.model.HotelHistoryItem

class PricingInfoFragment : BaseFragment<FragmentBookingRoomPriceBinding>() {
    private val viewModel by lazy {
        priceInfoBundle =  arguments?.getParcelable<HotelHistoryItem>(ARG_HOTEL_PRICE_INFO) as HotelHistoryItem

        ViewModelProvider(this,
            PricingInfoVMFactory(
                priceInfoBundle!!
            )).get(
            PricingInfoViewModel::class.java
        )
    }

    private var priceInfoBundle: HotelHistoryItem? = null

    override fun layoutId(): Int = R.layout.fragment_booking_room_price

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle(getString(R.string.pricing_information))
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
    }

    private fun setTitle(title: String) {
        (activity as HotelHistoryActivity).supportActionBar?.title = title
    }

    companion object {
        const val ARG_HOTEL_PRICE_INFO = "ARG_HOTEL_PRICE_INFO"
        const val ARG_HOTEL_BOOKED_ROOM_INFO_MODEL = "ARG_HOTEL_BOOKED_ROOM_INFO_MODEL"
        const val ARG_HOTEL_BOOKED_TOTAL_NIGHT_COUNT_INFO =
            "ARG_HOTEL_BOOKED_TOTAL_NIGHT_COUNT_INFO"
        const val ARG_HOTEL_BOOKED_TOTAL_PRICE_INFO = "ARG_HOTEL_BOOKED_TOTAL_PRICE_INFO"
        const val ARG_HOTEL_BOOKED_PRICE_AFTER_DISCOUNT_INFO =
            "ARG_HOTEL_BOOKED_PRICE_AFTER_DISCOUNT_INFO"
        const val ARG_HOTEL_BOOKED_DISCOUNT_AMOUNT = "ARG_HOTEL_BOOKED_DISCOUNT_AMOUNT_INFO"
        const val ARG_HOTEL_CONVENIENCE_AMOUNT_INFO = "ARG_HOTEL_BOOKED_DISCOUNT_AMOUNT_INFO"
    }
}
