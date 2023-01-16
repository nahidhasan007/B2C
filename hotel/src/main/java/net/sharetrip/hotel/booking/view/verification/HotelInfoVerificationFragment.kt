package net.sharetrip.hotel.booking.view.verification

import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.model.HotelBookingParams
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment.Companion.ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON
import net.sharetrip.hotel.booking.view.summary.BookingSummaryFragment.Companion.ARG_HOTEL_SUMMARY_BUNDLE
import net.sharetrip.hotel.databinding.DialogItemPrimaryInfoBinding
import net.sharetrip.hotel.databinding.FragmentHotelRoomVerificationBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment

class HotelInfoVerificationFragment : BaseFragment<FragmentHotelRoomVerificationBinding>() {

    private val promotionalCoupons by lazy {
        arguments?.getParcelable<ListOfCoupon>(ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON) as ListOfCoupon
    }

    private val viewModel by lazy {
        val bookingSummary =
            hotelVerificationBundle.getString(ARG_VERIFICATION_ROOM_BOOKING_SUMMARY)

        ViewModelProvider(
            this,
            HotelVerificationVMFactory(
                hotelBookingParams,
                bookingSummary,
                hotelVerificationBundle.getParcelable(
                    ARG_VERIFICATION_DISCOUNT_MODEL
                )!!,
                DataManager.hotelApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[HotelInfoVerificationViewModel::class.java]
    }

    private val hotelBookingParams: HotelBookingParams by lazy {
        MoshiUtil.convertStringToBookingParam(
            hotelVerificationBundle.getString(
                ARG_VERIFICATION_HOTEL_BOOKING_PARAMS
            )
        )
    }

    private val hotelVerificationBundle by lazy {
        arguments?.getBundle(ARG_HOTEL_INFO_VERIFICATION_BUNDLE)!!
    }

    private lateinit var adapter: HotelInfoVerificationAdapter
    private lateinit var adapterTwo: HotelInfoVerificationAdapter
    private lateinit var adapterThree: HotelInfoVerificationAdapter
    private lateinit var adapterFour: HotelInfoVerificationAdapter
    private lateinit var dialogBinding: DialogItemPrimaryInfoBinding

    override fun layoutId(): Int = R.layout.fragment_hotel_room_verification

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        adapter =
            HotelInfoVerificationAdapter(
                hotelBookingParams.rooms[0].guests[0] as ArrayList<GuestInfo>,
                1
            )
        bindingView.recyclerRoomInformationList.adapter = adapter

        viewModel.showEditDialog.observe(viewLifecycleOwner) {
            showDialog()
        }

        viewModel.navigateToSummary.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_hotelInfoVerificationFragment_to_bookingSummaryFragment,
                bundleOf(
                    ARG_HOTEL_SUMMARY_BUNDLE to it,
                    ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON to promotionalCoupons
                )
            )
        }

        if (hotelBookingParams.rooms[0].guests.size > 1) {
            adapterTwo =
                HotelInfoVerificationAdapter(
                    hotelBookingParams.rooms[0].guests[1] as ArrayList<GuestInfo>,
                    2
                )
            bindingView.recyclerRoomInformationListTwo.adapter = adapterTwo
            bindingView.recyclerRoomInformationListTwo.visibility = View.VISIBLE
        }

        if (hotelBookingParams.rooms[0].guests.size > 2) {
            adapterThree =
                HotelInfoVerificationAdapter(
                    hotelBookingParams.rooms[0].guests[2] as ArrayList<GuestInfo>,
                    3
                )
            bindingView.recyclerRoomInformationListThree.adapter = adapterThree
            bindingView.recyclerRoomInformationListThree.visibility = View.VISIBLE
        }

        if (hotelBookingParams.rooms[0].guests.size > 3) {
            adapterFour =
                HotelInfoVerificationAdapter(
                    hotelBookingParams.rooms[0].guests[3] as ArrayList<GuestInfo>,
                    4
                )
            bindingView.recyclerRoomInformationListFour.adapter = adapterFour
            bindingView.recyclerRoomInformationListFour.visibility = View.VISIBLE
        }
    }

    private fun showDialog() {
        dialogBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_item_primary_info,
                null,
                false
            )
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        val title = requireContext().resources.getStringArray(R.array.hotel_person_title)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, title)
        dialogBinding.textFieldTitle.setAdapter(adapter)

        val nationalityList = viewModel.codeList.map { it.name }
        val adapterForNationality =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nationalityList
            )
        dialogBinding.textFieldNationality.setAdapter(adapterForNationality)

        if (!viewModel.primaryContact.get()?.nationality.isNullOrEmpty()) {
            dialogBinding.textFieldNationality.setText(
                viewModel.primaryContact.get()?.nationality,
                false
            )
        }

        dialogBinding.primaryContact = viewModel.primaryContact.get()

        dialogBinding.dialogClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.dialogApply.setOnClickListener {
            if (!dialogBinding.primaryContact!!.isPhoneNumberValid()) {
                Toast.makeText(requireContext(), "Enter a valid phone number", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val nationalityCode =
                viewModel.codeList.find { it.name == dialogBinding.primaryContact?.nationality }
            viewModel.primaryContact.set(dialogBinding.primaryContact)
            nationalityCode?.code?.let {
                viewModel.primaryContact.get()?.nationality = it
            }

            viewModel.primaryContact.notifyChange()
            dialog.dismiss()
        }

        dialog.show()
    }

    companion object {
        const val ARG_HOTEL_INFO_VERIFICATION_BUNDLE = "ARG_HOTEL_INFO_VERIFICATION_BUNDLE"
        const val ARG_VERIFICATION_HOTEL_BOOKING_PARAMS = "ARG_VERIFICATION_HOTEL_BOOKING_PARAMS"
        const val ARG_VERIFICATION_ROOM_BOOKING_SUMMARY = "ARG_VERIFICATION_ROOM_BOOKING_SUMMARY"
        const val ARG_VERIFICATION_DISCOUNT_MODEL = "ARG_VERIFICATION_DISCOUNT_MODEL"
    }
}
