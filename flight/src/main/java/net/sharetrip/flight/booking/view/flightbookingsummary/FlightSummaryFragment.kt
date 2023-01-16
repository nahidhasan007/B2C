package net.sharetrip.flight.booking.view.flightbookingsummary

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.coupon.CouponResponse
import net.sharetrip.flight.booking.model.coupon.GPCouponInputState
import net.sharetrip.flight.booking.model.flightresponse.DiscountModel
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.view.flightbookingsummary.FlightSummaryViewModel.Companion.GOTO_FLIGHT_DASHBOARD
import net.sharetrip.flight.booking.view.flightbookingsummary.FlightSummaryViewModel.Companion.GOTO_PRIVACY
import net.sharetrip.flight.booking.view.flightbookingsummary.FlightSummaryViewModel.Companion.GOTO_TERMS
import net.sharetrip.flight.booking.view.flightbookingsummary.adapter.FlightSummaryAdapter
import net.sharetrip.flight.booking.view.flightbookingsummary.adapter.PaymentAdapter
import net.sharetrip.flight.booking.view.flightbookingsummary.adapter.PaymentAdapter.DetailsLookup
import net.sharetrip.flight.booking.view.flightbookingsummary.adapter.PaymentAdapter.KeyProvider
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_COUPON_RESPONSE
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_DISCOUNT_MODEL
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_FLIGHTS
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_FLIGHT_SEARCH
import net.sharetrip.flight.booking.view.flightdetails.adapter.FlightCouponListAdapter
import net.sharetrip.flight.booking.view.travellerdetails.TravelerDetailsFragment.Companion.ARG_ALL_TRAVELLER
import net.sharetrip.flight.databinding.FragmentOfFlightSummaryBinding
import net.sharetrip.flight.history.view.bookingdetails.FlightBookingDetailsViewModel.Companion.GOTO_PAYMENT
import net.sharetrip.flight.network.PaymentGatewayType
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.payment.model.Series
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import java.text.NumberFormat
import java.util.*
import kotlin.math.ceil

class FlightSummaryFragment : BaseFragment<FragmentOfFlightSummaryBinding>() {
    private var selectionTracker: SelectionTracker<Long>? = null
    private var arrayAdapter: ArrayAdapter<String>? = null
    private var seriesArrayList: List<Series>? = ArrayList()
    private val paymentMethodWithEarn: MutableList<PaymentMethod> = ArrayList()
    private val paymentMethodWithRedeem: MutableList<PaymentMethod> = ArrayList()
    private var paymentMethodWithCoupon: MutableList<PaymentMethod> = ArrayList()
    private var paymentAdapter = PaymentAdapter()
    private var isCountTotalVisible = true
    private var paymentId: String? = null
    private var dotsCount = 0
    private var totalVisibleItem = 0
    private val viewModel: FlightSummaryViewModel by lazy {
        FlightSummaryViewModelFactory(
            flights,
            flightSearch,
            discountModel,
            couponResponse,
            itemTravelers,
            SharedPrefsHelper(requireContext())
        ).create(FlightSummaryViewModel::class.java)
    }
    private lateinit var alertDialog: AlertDialog
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var dots: Array<ImageView?>
    private lateinit var flightCouponListAdapter: FlightCouponListAdapter
    private lateinit var flightSearch: FlightSearch
    private lateinit var flights: Flights
    private lateinit var itemTravelers: MutableList<ItemTraveler>
    private lateinit var discountModel: DiscountModel
    private lateinit var couponResponse: CouponResponse
    private var couponPaymentMethodList: ArrayList<PaymentMethod> = ArrayList()
    private val flightBookingDetail: FlightBookingDetail
        get() {
            val bookingDetail = FlightBookingDetail()
            bookingDetail.sessionId = flightSearch.sessionId
            bookingDetail.sequenceCode = flightSearch.sequence
            bookingDetail.searchId = flightSearch.searchId
            bookingDetail.coupon = flightSearch.couponCode!!
            bookingDetail.gateWay = flightSearch.paymentGateWayId!!
            bookingDetail.verifiedMobileNumber = flightSearch.verifiedMobileNumber
            bookingDetail.otp = flightSearch.otp
            for (series in seriesArrayList!!) {
                if (bindingView.cardPrefixAutoCompleteTextView.text.toString() == series.series) {
                    bookingDetail.cardSeries = series.id
                    break
                }
            }
            val travelers = itemTravelers
            val passengers = Passengers()
            val adults: MutableList<ItemTraveler> = ArrayList()
            val child: MutableList<ItemTraveler> = ArrayList()
            val infants: MutableList<ItemTraveler> = ArrayList()
            for (itemTraveler in travelers) {
                val mPassengerType = itemTraveler.passengerType
                if (PassengerType.PRIMARY_ADULT == mPassengerType
                    || PassengerType.SECONDARY_ADULT == mPassengerType
                ) {
                    adults.add(itemTraveler)
                } else if (PassengerType.CHILDREN == mPassengerType) {
                    child.add(itemTraveler)
                } else if (PassengerType.INFANT == mPassengerType) {
                    infants.add(itemTraveler)
                }
            }
            passengers.adult = adults
            passengers.child = child
            passengers.infant = infants
            bookingDetail.passengers = passengers

            return bookingDetail
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loader = layoutInflater.inflate(R.layout.layout_please_wait, null, false)
        loader.layout(0, 0, 0, 0)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("")
            .setView(loader)
            .setCancelable(true)
        alertDialog = builder.create()
        arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, ArrayList()
        )
        flights = requireArguments().getBundle(ARG_FLIGHT_DATA)?.getParcelable(ARG_FLIGHTS)!!
        flightSearch =
            requireArguments().getBundle(ARG_FLIGHT_DATA)?.getParcelable(ARG_FLIGHT_SEARCH)!!
        itemTravelers = requireArguments().getBundle(ARG_FLIGHT_DATA)
            ?.getParcelableArrayList(ARG_ALL_TRAVELLER)!!
        discountModel =
            requireArguments().getBundle(ARG_FLIGHT_DATA)?.getParcelable(ARG_DISCOUNT_MODEL)!!
        couponResponse =
            requireArguments().getBundle(ARG_FLIGHT_DATA)?.getParcelable(ARG_COUPON_RESPONSE)!!
        flightCouponListAdapter = if (!flights.availableCoupons.isNullOrEmpty()) {
            FlightCouponListAdapter(
                viewModel,
                flights.availableCoupons as MutableList<PromotionalCoupon>,
                discountModel.coupon
            )
        } else {
            FlightCouponListAdapter(
                viewModel,
                selectedCoupon = discountModel.coupon
            )
        }
    }

    override fun layoutId() = R.layout.fragment_of_flight_summary

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.booking_summary))
        bindingView.viewModel = viewModel
        bindingView.flights = flights
        bindingView.seekBar.isEnabled = false
        bindingView.recyclerCouponList.adapter = flightCouponListAdapter

        if (viewModel.getMyTripCoins() > flights.earnPoint) {
            bindingView.seekBar.max = flights.earnPoint
        } else {
            bindingView.seekBar.max = viewModel.getMyTripCoins()
        }

        if (discountModel.type == "Coin" && viewModel.redeemCoin.get() < 0) {
            bindingView.seekBar.progress = discountModel.discountAmount.toInt()
        }

        setClickableTermsAndCondition()
        initializePaymentGatewayRecycler()

        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)

        viewModel.availCoupon.observe(viewLifecycleOwner) {
            bindingView.seekBar.isEnabled = false
            bindingView.radioButtonCardPayment.isChecked = false
            if (couponPaymentMethodList.isEmpty()) {
                setAdapterWithGridSpanSize(paymentMethodWithCoupon)
            } else {
                setAdapterWithGridSpanSize(couponPaymentMethodList)
            }
            viewModel.onCouponChecked()
            if (paymentAdapter.itemCount > 0) {
                selectionTracker!!.select(0L)
            }
        }

        viewModel.redeemChecked.observe(viewLifecycleOwner) {
            bindingView.seekBar.isEnabled = true
            bindingView.radioButtonCardPayment.isChecked = false
            setAdapterWithGridSpanSize(paymentMethodWithRedeem)
            viewModel.onRedeemChecked()
            if (paymentAdapter.itemCount > 0) {
                selectionTracker!!.select(0L)
            }
        }

        viewModel.cardPaymentChecked.observe(viewLifecycleOwner) {
            bindingView.seekBar.isEnabled = false
            bindingView.redeemCheckBox.isChecked = false
            setAdapterWithGridSpanSize(paymentMethodWithEarn)
            viewModel.onCardChecked()
            if (paymentAdapter.itemCount > 0) {
                selectionTracker!!.select(0L)
            }
        }

        viewModel.clickedBooking.observe(viewLifecycleOwner) { isClicked: Boolean ->
            if (isClicked) {
                if (selectionTracker!!.hasSelection()) {
                    val selection = selectionTracker!!.selection
                    val iterator: Iterator<Long> = selection.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        val details = paymentAdapter.getItemDetails(next.toInt())
                        paymentId = details.id
                        break
                    }
                }
                if (Strings.isBlank(paymentId)) {
                    showMessageDialog()
                } else {
                    viewModel.checkPriceBeforeBooking()
                }
            }
        }

        viewModel.isProceedToPayment.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.currency.get()!! == PaymentGatewayType.USD.toString()) {
                    showUSDPaymentDialog(viewModel.totalPrice.get())
                } else {
                    proceedToPayment()
                }
            }
        }

        viewModel.priceCheckRes.observe(viewLifecycleOwner) {
            showUpdatePriceDialog(it.first, it.second)
        }

        viewModel.flightInfoLiveData.observe(viewLifecycleOwner) {
            val adapter = FlightSummaryAdapter()
            bindingView.flightInfoRecycler.adapter = adapter
            adapter.resetItems(it as List<Any?>)
        }

        viewModel.showMessageWithDialog.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                getString(R.string.something_wrong_price),
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.usdPayment.observe(viewLifecycleOwner) {
            showUSDPaymentDialog(ceil(it).toInt())
        }

        viewModel.searchFlightAgain.observe(viewLifecycleOwner) {
            showSearchAgainDialog(it)
        }


        viewModel.tripCoinText.observe(viewLifecycleOwner, EventObserver {
            setTripCoin(it)
        })

        viewModel.showMessage.observe(viewLifecycleOwner, EventObserver { msg ->
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        })

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                GOTO_PAYMENT -> {
                    hideToolbar(R.id.toolbar)
                    lifecycleScope.launchWhenResumed {
                        findNavController().navigateSafe(
                            R.id.action_bookingSummary_to_payment,
                            it.second as Bundle
                        )
                    }
                }
                GOTO_PRIVACY -> {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(
                        ProfileActivity.ARG_PROFILE_ACTION,
                        ProfileAction.ARG_CONTENT_PRIVACY
                    )
                    startActivity(intent)
                }
                GOTO_TERMS -> {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(
                        ProfileActivity.ARG_PROFILE_ACTION,
                        ProfileAction.ARG_CONTENT_TERM
                    )
                    startActivity(intent)
                }

                GOTO_FLIGHT_DASHBOARD -> {
                    findNavController().navigateSafe(R.id.action_flightBookingSummary_to_flightHomeFragment)
                }
            }
        })

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
    }

    override fun onStart() {
        super.onStart()
        showToolbar(R.id.toolbar)
    }

    override fun onStop() {
        super.onStop()
        if (alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }

    private fun initializePaymentGatewayRecycler() {
        layoutManager = GridLayoutManagerWrapper(
            activity, 2,
            RecyclerView.HORIZONTAL, false
        )
        bindingView.listPaymentType.layoutManager = layoutManager
        bindingView.listPaymentType.adapter = paymentAdapter

        selectionTracker = SelectionTracker.Builder(
            this.javaClass.simpleName,
            bindingView.listPaymentType,
            KeyProvider(),
            DetailsLookup(bindingView.listPaymentType),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectSingleAnything()).build()

        bindingView.cardPrefixAutoCompleteTextView.setAdapter<ArrayAdapter<String>>(arrayAdapter)
        paymentAdapter.setSelectionTracker(selectionTracker!!)

        viewModel.paymentMethodList.observe(viewLifecycleOwner) { paymentMethods: List<PaymentMethod> ->

            paymentMethodWithEarn.apply {
                clear()
                addAll(paymentMethods.filter { it.isEarnTripCoinApplicable })
            }

            paymentMethodWithRedeem.apply {
                clear()
                addAll(paymentMethods.filter { it.isRedeemTripCoinApplicable })
            }

            paymentMethodWithCoupon.apply {
                clear()
                addAll(paymentMethods.filter { it.isCouponAvailable })
            }

            filterCouponGateway(null)

            viewModel.discountModel.gateway?.let {
                filterCouponGateway(it)
            }
            if (paymentMethodWithCoupon.isEmpty()) {
                viewModel.noCardSeriesAvailable()
            }
            if (paymentAdapter.itemCount > 0) {
                selectionTracker!!.select(-1L)
            }
        }

        selectionTracker!!.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                try {
                    if (selected) {
                        val itemDetails = paymentAdapter.getItemDetails(key.toInt())
                        viewModel.selectedPaymentMethod.value = itemDetails
                        if (itemDetails.currency.code == PaymentGatewayType.USD.toString()) {
                            viewModel.updateUSDPayment(itemDetails)
                        } else {
                            viewModel.updateWithBDTPayment()
                        }
                        setCardPrefix(itemDetails.series)
                    } else if (selectionTracker!!.selection.isEmpty) {
                        hideCardPrefix()
                    }
                } catch (e: Exception) {
                    e.fillInStackTrace()
                }
            }
        })

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
                    setImageSliderDots(paymentAdapter.itemCount)
                }
            }
        })
    }

    private fun filterCouponGateway(gatewayList: List<String>?) {
        couponPaymentMethodList.clear()
        gatewayList?.forEach { gateway ->
            paymentMethodWithCoupon.forEach { paymentMethod ->
                if (gateway == paymentMethod.id) {
                    couponPaymentMethodList.add(paymentMethod)
                }
            }
        }

        if (couponPaymentMethodList.isEmpty())
            couponPaymentMethodList.addAll(paymentMethodWithCoupon)

        setAdapterWithGridSpanSize(couponPaymentMethodList)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapterWithGridSpanSize(list: List<PaymentMethod>) {
        when {
            list.size > 4 -> layoutManager.spanCount = 2
            else -> layoutManager.spanCount = 1
        }

        paymentAdapter.apply {
            submitList(null)
            submitList(list)
        }

        isCountTotalVisible = true
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
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
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

    private fun proceedToPayment() {
        flightSearch.paymentGateWayId = paymentId
        viewModel.fetchPaymentUrl(flightBookingDetail)
    }

    private fun setCardPrefix(series: List<Series>?) {
        seriesArrayList = series
        val list: MutableList<String> = ArrayList()
        if (series != null) {
            for (data in series) {
                list.add(data.series)
            }
        }
        arrayAdapter!!.clear()
        arrayAdapter!!.addAll(list)
        if (list.size > 0) {
            showCardPrefix(list)
        } else {
            hideCardPrefix()
        }
    }

    private fun showCardPrefix(list: List<String>) {
        bindingView.cardPrefixTextInputLayout.visibility = VISIBLE
        bindingView.cardPrefixImageView.visibility = VISIBLE
        bindingView.cardPrefixAutoCompleteTextView.setText(list[0])
    }

    private fun hideCardPrefix() {
        bindingView.cardPrefixAutoCompleteTextView.setText("")
        bindingView.cardPrefixTextInputLayout.visibility = GONE
        bindingView.cardPrefixImageView.visibility = View.INVISIBLE
    }

    private fun showMessageDialog(message: String = "Select payment method") {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showUpdatePriceDialog(oldPrice: Int, newPrice: Int?) {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fragment_price_update)
        dialog.setCancelable(true)

        val dialogContinueButton = dialog.findViewById<Button>(R.id.bt_continue)
        val dialogAnotherFlightTv = dialog.findViewById<AppCompatTextView>(R.id.tv_another_flight)

        val tvOldTicketPrice = dialog.findViewById<AppCompatTextView>(R.id.tvOldTicketPrice)
        val tvUpdatePrice = dialog.findViewById<AppCompatTextView>(R.id.tv_update_price)

        tvOldTicketPrice.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(oldPrice)
        tvUpdatePrice.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(newPrice)

        dialogContinueButton.setOnClickListener {
            dialog.dismiss()
            proceedToPayment()
        }

        dialogAnotherFlightTv.setOnClickListener {
            dialog.dismiss()
            viewModel.goToFlightDashboard()
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showUSDPaymentDialog(newPrice: Int) {
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
            proceedToPayment()
        }

        dialog.show()
    }

    private fun showSearchAgainDialog(message: String) {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_fragment_validation_change)
        dialog.setCancelable(false)

        val dialogContinueButton = dialog.findViewById<Button>(R.id.bt_continue)
        dialog.findViewById<AppCompatTextView>(R.id.text_view_content).text = message

        dialogContinueButton.setOnClickListener {
            dialog.dismiss()
            viewModel.goToFlightDashboard()
        }
        dialog.show()
    }

    companion object {
        const val ARG_FLIGHT_DATA = "ARG_FLIGHT_DATA"
    }
}
