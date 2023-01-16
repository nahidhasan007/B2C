package net.sharetrip.hotel.booking.view.hoteldiscount

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.viewmodel.BaseViewModel
import eightbitlab.com.blurview.RenderScriptBlur
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.PromotionalCoupon
import net.sharetrip.hotel.booking.model.coupon.GPCouponInputState
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.booking.view.summary.HotelCouponListAdapter
import net.sharetrip.hotel.booking.view.travellerlist.TravellerListFragment.Companion.ARG_TRAVELLER_LIST_BUNDLE
import net.sharetrip.hotel.databinding.FragmentHotelDiscountBinding
import net.sharetrip.hotel.databinding.GuestUserLayoutHotelBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.shared.model.GuestPopUpData
import net.sharetrip.shared.utils.UtilText
import net.sharetrip.shared.utils.hideKeyboard
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.signup.view.RegistrationActivity

class HotelDiscountFragment : BaseFragment<FragmentHotelDiscountBinding>() {

    private val promotionalCoupons by lazy {
        arguments?.getParcelable<ListOfCoupon>(ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON) as ListOfCoupon
    }

    private val viewModel by lazy {
        val hotelDetailsBundle = arguments?.getBundle(ARG_HOTEL_DISCOUNT_BUNDLE)!!
        val hotelBookingParams =
            MoshiUtil.convertStringToBookingParam(hotelDetailsBundle.getString(ARG_EXTRA_HOTEL_PARAM))
        val summary = MoshiUtil.convertStringToBookingSummary(
            hotelDetailsBundle.getString(
                ARG_EXTRA_HOTEL_SUMMARY
            )
        )

        ViewModelProvider(
            this,
            HotelDiscountVMFactory(
                hotelBookingParams,
                summary,
                promotionalCoupons,
                DataManager.hotelApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[HotelDiscountViewModel::class.java]
    }
    private lateinit var dialog: Dialog
    private val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.hotel_body,
            R.drawable.ic_hotel_56dp_blue, viewModel
        )
    }
    private val blurData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.discount_body,
            R.drawable.ic_discount_mono, viewModel
        )
    }

    private val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.checkLoginInformation()
                viewModel.fetchUserProfile()
            }
        }

    private lateinit var couponListAdapter: HotelCouponListAdapter

    override fun layoutId() = R.layout.fragment_hotel_discount

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.bottomSheet.viewModel = viewModel
        bindingView.blurLayout.data = blurData

        viewModel.redeemChecked.observe(viewLifecycleOwner) {
            bindingView.seekBar.isEnabled = true
            bindingView.radioButtonCardPayment.isChecked = false
            viewModel.onRedeemChecked()
        }

        viewModel.availCoupon.observe(viewLifecycleOwner) {
            bindingView.seekBar.isEnabled = false
            bindingView.radioButtonCardPayment.isChecked = false
            viewModel.onCouponChecked()
        }

        viewModel.cardPaymentChecked.observe(viewLifecycleOwner) {
            bindingView.bottomSheet.textLabelDiscount.text =
                requireContext().getString(R.string.discount)
            bindingView.seekBar.isEnabled = false
            bindingView.redeemCheckBox.isChecked = false
            viewModel.onCardChecked()
        }

        viewModel.maxTripCoin.observe(viewLifecycleOwner) {
            bindingView.seekBar.max = it
        }

        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            setTripCoin(it)
        }

        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)

        viewModel.isShowDialog.observe(viewLifecycleOwner) {
            if (!it) showGuestDialog()
        }

        viewModel.isLoginLiveData.observe(viewLifecycleOwner) {
            if (!it) setupBlurView()
        }

        viewModel.isDismissDialog.observe(viewLifecycleOwner) {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
            }
        }

        viewModel.hideKeyboard.observe(viewLifecycleOwner) {
            hideKeyboard()
        }

        viewModel.navigateToTravellerList.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_hotelDiscountFragment_to_travellerListFragment,
                bundleOf(
                    ARG_TRAVELLER_LIST_BUNDLE to it,
                    ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON to promotionalCoupons
                )
            )
        }

        viewModel.navigateToLogin.observe(viewLifecycleOwner) {
            val intent = Intent(requireContext(), RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            registerResult.launch(intent)
        }

        viewModel.couponList.observe(viewLifecycleOwner) { couponList ->
            viewModel.isCouponAvailable.set(couponList.isNotEmpty())
            try {
                couponListAdapter =
                    HotelCouponListAdapter(
                        viewModel, viewModel::onCouponSelected,
                        couponList as ArrayList<PromotionalCoupon>
                    )
                bindingView.recyclerCouponList.adapter = couponListAdapter
                if (couponListAdapter.itemCount < 1) {
                    bindingView.textViewAvailableCoupon.visibility = View.GONE
                    viewModel.onCardChecked()
                }
            } catch (e: Exception) {
                viewModel.onCardChecked()
            }
        }

        viewModel.couponState.observe(viewLifecycleOwner) {
            if (it == GPCouponInputState.MobileInputState.name) {
                viewModel.gpCouponTitle.set(UtilText.couponTitle)
                viewModel.gpCouponSubTitle.set(UtilText.couponSubTitle)
            } else {
                viewModel.gpCouponTitle.set(UtilText.otpTitle)
                viewModel.gpCouponSubTitle.set(
                    getString(R.string.we_have_sent_a_6_digit_otp_to) + viewModel.gpStarNumber + " " + getString(
                        R.string.please_input_that_number_to_proceed
                    )
                )
            }
        }

        viewModel.isGpStarCouponVerified.observe(viewLifecycleOwner) {
            couponListAdapter.clearSelection()
        }
    }

    private fun showGuestDialog() {
        try {
            dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding =
                DataBindingUtil.inflate<ViewDataBinding>(
                    LayoutInflater.from(context),
                    R.layout.guest_user_layout_hotel, null, false
                ) as GuestUserLayoutHotelBinding
            dialog.setContentView(dialogBinding.root)
            dialogBinding.data = popupData
            dialog.show()
        } catch (e: Exception) {
        }
    }

    private fun setupBlurView() {
        val radius = 3f
        val windowBackground = requireActivity().window.decorView.background
        bindingView.blurLayout.topBlurView.setupWith(bindingView.root)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(context))
            .setBlurRadius(radius)
            .setHasFixedTransformationMatrix(true)
        bindingView.blurLayout.topBlurView.setBlurRadius(3f)
    }

    companion object {
        const val ARG_HOTEL_DISCOUNT_BUNDLE = "ARG_HOTEL_DISCOUNT_BUNDLE"
        const val ARG_EXTRA_HOTEL_PARAM = "ARG_EXTRA_HOTEL_PARAM"
        const val ARG_EXTRA_HOTEL_SUMMARY = "ARG_EXTRA_HOTEL_SUMMARY"
        const val ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON = "ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON"
    }
}
