package net.sharetrip.hotel.booking.view.summary

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.DiscountOption
import net.sharetrip.hotel.booking.model.PromotionalCoupon
import net.sharetrip.hotel.booking.model.coupon.GPCouponInputState
import net.sharetrip.hotel.booking.model.coupon.ListOfCoupon
import net.sharetrip.hotel.booking.model.payment.PaymentMethod
import net.sharetrip.hotel.booking.view.hoteldiscount.HotelDiscountFragment
import net.sharetrip.hotel.databinding.FragmentBookingSummaryBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.network.PaymentGatewayType
import net.sharetrip.hotel.utils.MoshiUtil
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.ProfileActivity.Companion.ARG_PROFILE_ACTION
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import java.text.NumberFormat
import java.util.*

class BookingSummaryFragment : BaseFragment<FragmentBookingSummaryBinding>() {

    private val promotionalCoupons by lazy {
        arguments?.getParcelable<ListOfCoupon>(HotelDiscountFragment.ARG_EXTRA_HOTEL_PROMOTIONAL_COUPON) as ListOfCoupon
    }

    private val viewModel by lazy {
        val hotelSummaryBundle = arguments?.getBundle(ARG_HOTEL_SUMMARY_BUNDLE)!!
        val hotelBookingParams =
            MoshiUtil.convertStringToBookingParam(
                hotelSummaryBundle.getString(
                    ARG_SUMMARY_HOTEL_BOOKING_PARAMS
                )
            )
        val bookingSummary = MoshiUtil.convertStringToBookingSummary(
            hotelSummaryBundle.getString(
                ARG_SUMMARY_ROOM_BOOKING_SUMMARY
            )!!
        )!!

        ViewModelProvider(
            this,
            BookingSummaryVMFactory(
                hotelBookingParams,
                bookingSummary,
                hotelSummaryBundle.getParcelable(ARG_SUMMARY_DISCOUNT_MODEL)!!,
                promotionalCoupons,
                DataManager.hotelApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[BookingSummaryViewModel::class.java]
    }

    private val adapter = PaymentAdapter()
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var dots: Array<ImageView?>
    private lateinit var couponListAdapter: HotelCouponListAdapter
    private var isCountTotalVisible = true
    private var dotsCount = 0
    private var totalVisibleItem = 0

    override fun layoutId() = R.layout.fragment_booking_summary

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("InflateParams")
    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.bottomSheet.viewModel = viewModel

        layoutManager = GridLayoutManager(
            activity, 2,
            GridLayoutManager.HORIZONTAL, false
        )
        bindingView.listPaymentType.layoutManager = layoutManager
        bindingView.listPaymentType.adapter = adapter

        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            setTripCoin(it)
        }

        viewModel.hideKeyboard.observe(viewLifecycleOwner) {
            hideKeyboard()
        }

        viewModel.paymentList.observe(viewLifecycleOwner, EventObserver {

            viewModel.discountModel.gateway?.let {
                if (it.isNotEmpty()) viewModel.filterCouponGateway(it)
            }

            if (viewModel.paymentMethodWithCoupon.isEmpty()) {
                viewModel.noCardSeriesAvailable()
            }
        })

        viewModel.filteredPaymentList.observe(viewLifecycleOwner) { gatewayList ->
            setAdapterWithGridSpanSize(gatewayList)
        }

        bindingView.listPaymentType.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        setIndicatorPosition()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isCountTotalVisible) {
                    isCountTotalVisible = false
                    totalVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    setImageSliderDots(recyclerView.layoutManager!!.itemCount)
                }
            }
        })

        viewModel.seekBarProgress.observe(viewLifecycleOwner) {
            bindingView.seekBar.progress = it.toInt()
        }

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        viewModel.starRatingLiveData.observe(viewLifecycleOwner) {
            for (i in 1..it) {
                val item: View = inflater.inflate(R.layout.star_layout, null, true)
                bindingView.ratingBar.addView(item)
            }
        }

        viewModel.radioOption.observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    DiscountOption.CARD -> {
                        setAdapterWithGridSpanSize(viewModel.paymentMethodWithEarn)
                        viewModel.onCardChecked()
                        bindingView.seekBar.progress = 0
                    }
                    DiscountOption.COIN -> {
                        setAdapterWithGridSpanSize(viewModel.paymentMethodWithRedeem)
                        viewModel.onRedeemChecked()
                        viewModel.cardSeries = ""
                    }
                    DiscountOption.COUPON -> {
                        setAdapterWithGridSpanSize(
                            viewModel.filteredPaymentList.value?.toList()
                                ?: viewModel.paymentMethodWithCoupon
                        )
                        viewModel.onCouponChecked()
                        viewModel.cardSeries = ""
                        bindingView.seekBar.progress = 0
                    }
                }
            }
        }

        adapter.selectedPaymentMethod.observe(viewLifecycleOwner) {
            viewModel.selectedPaymentMethod.value = it
            viewModel.setPaymentGateWayId(it.id)
            bindingView.bottomSheet.btnBookNow.isEnabled = true

            if (it.currency.code == PaymentGatewayType.USD.toString()) {
                viewModel.updateUSDPayment()
            } else {
                viewModel.updateWithBDTPayment()
            }

            if (adapter.series != null) {
                val seriesList = ArrayList<String>()

                for (series in adapter.series!!) {
                    seriesList.add(series.series)
                }

                val cardAdapter =
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        seriesList
                    )

                if (seriesList.isNotEmpty()) {
                    bindingView.cardPrefixImageView.visibility = VISIBLE
                    bindingView.cardPrefixTextInputLayout.visibility = VISIBLE
                    viewModel.cardSeries = adapter.series!![0].id
                    bindingView.cardPrefixAutoCompleteTextView.setText(seriesList[0])
                    bindingView.cardPrefixAutoCompleteTextView.setAdapter(cardAdapter)
                } else {
                    bindingView.cardPrefixImageView.visibility = GONE
                    bindingView.cardPrefixTextInputLayout.visibility = GONE
                }
            }
        }

        viewModel.clickBooking.observe(viewLifecycleOwner, EventObserver { isClicked: Boolean ->
            if (isClicked) {
                if (viewModel.currency.get()!! == PaymentGatewayType.USD.toString()) {
                    showUSDPaymentDialog(viewModel.totalPrice.get().toDouble())
                } else {
                    viewModel.bookAHotel()
                }
            }
        })

        bindingView.cardPrefixAutoCompleteTextView.setOnItemClickListener { _: AdapterView<*>, _: View, pos: Int, _: Long ->
            val id = adapter.series?.get(pos)?.id
            viewModel.cardSeries = id!!
        }

        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)

        viewModel.navigateToPaymentScreen.observe(viewLifecycleOwner) { bundle ->
            hideAppbar(R.id.app_bar_layout)
            lifecycleScope.launchWhenResumed {
                findNavController().navigateSafe(R.id.navigate_payment_module, bundle)
            }
        }

        viewModel.navigateToPrivacyPolicy.observe(viewLifecycleOwner) {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ARG_PROFILE_ACTION, ProfileAction.ARG_CONTENT_PRIVACY)
            startActivity(intent)
        }

        viewModel.navigateToTermsAndCondition.observe(viewLifecycleOwner) {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ARG_PROFILE_ACTION, ProfileAction.ARG_CONTENT_TERM)
            startActivity(intent)
        }

        viewModel.couponList.observe(viewLifecycleOwner) { couponList ->
            try {
                couponListAdapter =
                    HotelCouponListAdapter(
                        viewModel, ::onCouponSelected,
                        couponList as ArrayList<PromotionalCoupon>
                    )
                bindingView.recyclerCouponList.adapter = couponListAdapter
                if (couponListAdapter.itemCount < 1) {
                    bindingView.textViewAvailableCoupon.visibility = GONE
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

        setClickableTermsAndCondition()
    }

    override fun onStart() {
        super.onStart()
        showAppbar(R.id.app_bar_layout)
    }

    private fun setClickableTermsAndCondition() {
        val spannableString =
            SpannableString(requireContext().getString(R.string.i_agree_to_terms_condition))
        val termsClickableSpan = object : ClickableSpan() {

            override fun onClick(textView: View) {
                viewModel.onClickTermsAndCondition()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                if (bindingView.tvTermsCondition.isPressed) {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_blue)
                } else {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_dark)
                }
                ds.isUnderlineText = false
                bindingView.tvTermsCondition.invalidate()
            }
        }

        val privacyPolicyClickableSpan = object : ClickableSpan() {

            override fun onClick(textView: View) {
                viewModel.onClickPrivacyPolicy()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                if (bindingView.tvTermsCondition.isPressed) {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_blue)
                } else {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_dark)
                }
                ds.isUnderlineText = false
                bindingView.tvTermsCondition.invalidate()

            }
        }

        spannableString.setSpan(termsClickableSpan, 15, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            privacyPolicyClickableSpan,
            36,
            51,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        bindingView.tvTermsCondition.text = spannableString
        bindingView.tvTermsCondition.movementMethod = LinkMovementMethod.getInstance()
        bindingView.tvTermsCondition.highlightColor = Color.TRANSPARENT
    }

    private fun setAdapterWithGridSpanSize(list: List<PaymentMethod>) {
        when {
            list.size > 4 -> layoutManager.spanCount = 2
            else -> layoutManager.spanCount = 1
        }
        isCountTotalVisible = true
        adapter.update(list)
    }

    private fun setImageSliderDots(count: Int) {

        if (count <= 6) {
            bindingView.linearSliderDots.visibility = GONE
            return
        }

        bindingView.linearSliderDots.removeAllViews()
        bindingView.linearSliderDots.visibility = VISIBLE

        bindingView.linearSliderDots.post {
            bindingView.linearSliderDots.invalidate()
            bindingView.linearSliderDots.requestLayout()
        }

        dotsCount = ((count - totalVisibleItem) / 2)
        if (count % 2 == 0) {
            dotsCount++
        } else {
            dotsCount += 2
        }

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(context)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 8, 8, 8)
            bindingView.linearSliderDots.addView(dots[i], params)
        }

        dots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )
    }

    private fun setIndicatorPosition() {
        val lastCompleteVisible = layoutManager.findLastCompletelyVisibleItemPosition() + 1

        val dotsSelected = if (lastCompleteVisible <= totalVisibleItem) {
            0
        } else {
            val valueOne = ((lastCompleteVisible - totalVisibleItem)) / 2
            val valueTwo = (lastCompleteVisible - totalVisibleItem) % 2
            valueOne + valueTwo
        }

        for (i in 0 until dotsCount) {
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )
        }

        if (dotsSelected < dotsCount) {
            dots[dotsSelected]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUSDPaymentDialog(newPrice: Double) {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_payment_with_usd)
        dialog.setCancelable(true)

        val dialogContinueButton = dialog.findViewById<Button>(R.id.bt_continue)
        val tvUpdatePrice = dialog.findViewById<AppCompatTextView>(R.id.tvPrice)
        tvUpdatePrice.text =
            PaymentGatewayType.USD.toString() + " " + NumberFormat.getNumberInstance(Locale.US)
                .format(newPrice)

        dialogContinueButton.setOnClickListener {
            dialog.dismiss()
            viewModel.bookAHotel()
        }

        dialog.show()
    }

    private fun onCouponSelected(hotelCoupon: PromotionalCoupon) {
    }

    companion object {
        const val ARG_HOTEL_SUMMARY_BUNDLE = "ARG_HOTEL_SUMMARY_BUNDLE"
        const val ARG_SUMMARY_HOTEL_BOOKING_PARAMS = "ARG_SUMMARY_HOTEL_BOOKING_PARAMS"
        const val ARG_SUMMARY_ROOM_BOOKING_SUMMARY = "ARG_SUMMARY_ROOM_BOOKING_SUMMARY"
        const val ARG_SUMMARY_DISCOUNT_MODEL = "ARG_SUMMARY_DISCOUNT_MODEL"
    }
}
