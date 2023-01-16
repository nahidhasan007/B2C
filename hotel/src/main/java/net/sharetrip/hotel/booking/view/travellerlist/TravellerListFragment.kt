package net.sharetrip.hotel.booking.view.travellerlist

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.booking.view.guests.RoomGuestFragment.Companion.ARG_ROOM_GUEST_BUNDLE
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment
import net.sharetrip.hotel.booking.view.verification.HotelInfoVerificationFragment.Companion.ARG_HOTEL_INFO_VERIFICATION_BUNDLE
import net.sharetrip.hotel.databinding.FragmentTravellerListBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment

class TravellerListFragment : BaseFragment<FragmentTravellerListBinding>() {

    private val promotionalCoupons by lazy {
        arguments?.getParcelable<ListOfCoupon>(HotelDiscountFragment.ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON) as ListOfCoupon
    }

    private val viewModel by lazy {
        val travellerListBundle = arguments?.getBundle(ARG_TRAVELLER_LIST_BUNDLE)!!
        val hotelBookingParams =
            MoshiUtil.convertStringToBookingParam(
                travellerListBundle.getString(
                    ARG_TRAVELLER_LIST_HOTEL_BOOKING_PARAMS_MODEL
                )
            )
        val summary = travellerListBundle.getString(ARG_TRAVELLER_LIST_BOOKING_SUMMARY_STRING)!!

        ViewModelProvider(
            this,
            TravellerListVMFactory(
                hotelBookingParams,
                summary,
                travellerListBundle.getParcelable(
                    ARG_TRAVELLER_LIST_DISCOUNT_MODEL
                )!!,
                DataManager.getSharedPref(requireContext())
            )
        )[TravellerListViewModel::class.java]
    }

    override fun layoutId() = R.layout.fragment_traveller_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        hideTripCoin()

        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            setTripCoin(it)
        }

        getNavigationResultLiveData<Intent?>(ARG_TRAVELLER)?.observe(viewLifecycleOwner) {
            it?.let {
                viewModel.handleTravellerData(it)
            }
        }

        viewModel.navigateToGuestRoom.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_travellerListFragment_to_roomGuestFragment,
                bundleOf(ARG_ROOM_GUEST_BUNDLE to it)
            )
        }

        viewModel.navigateToHotelVerification.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_travellerListFragment_to_hotelInfoVerificationFragment,
                bundleOf(
                    ARG_HOTEL_INFO_VERIFICATION_BUNDLE to it,
                    HotelDiscountFragment.ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON to promotionalCoupons
                )
            )
        }
    }

    companion object {
        const val ARG_TRAVELLER_LIST_BUNDLE = "ARG_TRAVELLER_LIST_BUNDLE"
        const val ARG_TRAVELLER_LIST_HOTEL_BOOKING_PARAMS_MODEL =
            "ARG_TRAVELLER_LIST_HOTEL_BOOKING_PARAMS_MODEL"
        const val ARG_TRAVELLER_LIST_BOOKING_SUMMARY_STRING =
            "ARG_TRAVELLER_LIST_BOOKING_SUMMARY_STRING"
        const val ARG_TRAVELLER_LIST_DISCOUNT_MODEL = "ARG_TRAVELLER_LIST_DISCOUNT_MODEL"
    }
}
