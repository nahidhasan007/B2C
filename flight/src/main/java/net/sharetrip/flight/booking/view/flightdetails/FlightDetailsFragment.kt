package net.sharetrip.flight.booking.view.flightdetails

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.utils.ShareTripAppConstants.NO_BAGGAGE
import com.sharetrip.base.viewmodel.BaseViewModel
import eightbitlab.com.blurview.RenderScriptBlur
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.PromotionalCoupon
import net.sharetrip.flight.booking.model.coupon.GPCouponInputState
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.model.luggage.OptionsItem
import net.sharetrip.flight.booking.model.luggage.RouteOptionsItem
import net.sharetrip.flight.booking.model.luggage.TravellerBaggage
import net.sharetrip.flight.booking.model.luggage.TravellerType
import net.sharetrip.flight.booking.view.farerules.RulesFragment.Companion.ARG_RULES
import net.sharetrip.flight.booking.view.farerules.RulesFragment.Companion.ARG_SELECTED_POSITION
import net.sharetrip.flight.booking.view.flightdetails.adapter.*
import net.sharetrip.flight.booking.view.segment.FragmentSegment.Companion.ARA_ITEM_FLIGHTS_SEGMENT
import net.sharetrip.flight.booking.view.segment.FragmentSegment.Companion.ARA_ITEM_FLIGHTS_SEGMENT_DATA
import net.sharetrip.flight.booking.view.segment.FragmentSegment.Companion.ARA_ITEM_FLIGHTS_SEGMENT_POSITION
import net.sharetrip.flight.databinding.FragmentFlightDetailsBinding
import net.sharetrip.flight.databinding.GuestUserLayoutBaseBinding
import net.sharetrip.flight.network.DataManager
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.signup.view.RegistrationActivity
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class FlightDetailsFragment : BaseFragment<FragmentFlightDetailsBinding>() {
    private var flightDetailsAdapter: FlightDetailsAdapter? = null
    private lateinit var flightSearch: FlightSearch
    private lateinit var flights: Flights
    private lateinit var dialog: Dialog
    private lateinit var flightCouponListAdapter: FlightCouponListAdapter
    private lateinit var alertDialog: AlertDialog
    lateinit var toolbar: ActionBar

    private val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onLoginSuccess()
            }
        }

    private val viewModel by lazy {
        FlightDetailsViewModelFactory(
            flightSearch,
            flights,
            SharedPrefsHelper(requireContext()),
            DataManager.flightApiService
        ).create(FlightDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flightSearch =
            requireArguments().getBundle("ARG_SELECTED_FLIGHT")
                ?.getParcelable(ARG_FLIGHT_SEARCH_MODEL)!!
        flights =
            requireArguments().getBundle("ARG_SELECTED_FLIGHT")?.getParcelable(ARG_ITEM_FLIGHTS)!!
    }

    override fun onStart() {
        super.onStart()
        setTitleAndInfo()

        var points = SharedPrefsHelper(requireContext())[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }

        if (points.isBlank()) {
            points = "0"
            SharedPrefsHelper(requireContext()).put(PrefKey.USER_TRIP_COIN, "0")
        }

        setTripCoin(NumberFormat.getNumberInstance(Locale.US).format(points.toInt()))
    }

    override fun layoutId(): Int = R.layout.fragment_flight_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        flights.setBaggageWeightText()

        flights.segments.forEach {
            it.segment.forEach { innerSeg ->
                innerSeg.classType = flightSearch.classType
            }
        }

        bindingView.flights = flights
        bindingView.viewModel = viewModel
        bindingView.blurLayout.data = viewModel.blurData

        if (flights.priceBreakdown.promotionalDiscount != 0.0) {
            bindingView.priceTextView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        viewModel.luggageResponse.observe(viewLifecycleOwner) { it ->
            val wholeFlight =
                it.wholeFlightOptions?.filter { it.travellerType == TravellerType.ADT.toString() }
            val routeOptionsList = it.routeOptions

            if (it.wholeFlight && !it.isPerPerson) {

                baggageForWholeFlight(wholeFlight!!, it.isLuggageOptional)

            } else if (!it.wholeFlight && !it.isPerPerson) {

                baggageForRouteWise(routeOptionsList!!, it.isLuggageOptional)

            } else if (it.wholeFlight && it.isPerPerson) {

                baggageForPassengerwise(it.travellerBaggageList, it.isLuggageOptional)

            } else if (!it.wholeFlight && it.isPerPerson) {

                baggageForRouteAndPassengerwise(routeOptionsList!!, it.isLuggageOptional)

            }
        }

        viewModel.flightCouponList.observe(viewLifecycleOwner) {
            flightCouponListAdapter =
                FlightCouponListAdapter(viewModel, it as MutableList<PromotionalCoupon>)
            bindingView.recyclerCouponList.adapter = flightCouponListAdapter
        }

        if (viewModel.getMyTripCoins() > flights.earnPoint)
            bindingView.seekBar.max = flights.earnPoint
        else
            bindingView.seekBar.max = viewModel.getMyTripCoins()

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
            bindingView.seekBar.isEnabled = false
            bindingView.redeemCheckBox.isChecked = false
            viewModel.onCardChecked()
        }

        viewModel.isShowDialog.observe(viewLifecycleOwner) {
            if (!it)
                showGuestDialog()
        }

        viewModel.isLoginLiveData.observe(viewLifecycleOwner) {
            if (!it)
                setupBlurView()
        }

        viewModel.isDismissDialog.observe(viewLifecycleOwner) {
            try {
                dialog.dismiss()
            } catch (_: Exception) {
            }
        }

        viewModel.isLoginClicked.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(requireContext(), RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            registerResult.launch(intent)
        })

        viewModel.ruleCase.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            bundle.putStringArrayList(ARG_RULES, viewModel.rules)
            when (it) {
                0 -> {
                    bundle.putInt(ARG_SELECTED_POSITION, 0)
                    findNavController().navigateSafe(
                        R.id.action_flightDetails_to_rules,
                        bundleOf(ARG_RULES_DETAILS to bundle)
                    )
                }
                1 -> {
                    bundle.putInt(ARG_SELECTED_POSITION, 1)
                    findNavController().navigateSafe(
                        R.id.action_flightDetails_to_rules,
                        bundleOf(ARG_RULES_DETAILS to bundle)
                    )
                }
                else -> {
                    bundle.putInt(ARG_SELECTED_POSITION, 2)
                    findNavController().navigateSafe(
                        R.id.action_flightDetails_to_rules,
                        bundleOf(ARG_RULES_DETAILS to bundle)
                    )
                }
            }
        })
        initUi()
        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)
        setTitleAndInfo()
        val loader = layoutInflater.inflate(R.layout.layout_please_wait, null, false)
        loader.layout(0, 0, 0, 0)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("")
            .setView(loader)
            .setCancelable(false)
        alertDialog = builder.create()

        viewModel.gotoSegment.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            bundle.putParcelable(ARA_ITEM_FLIGHTS_SEGMENT, flights)
            bundle.putInt(ARA_ITEM_FLIGHTS_SEGMENT_POSITION, it)
            findNavController().navigateSafe(
                R.id.action_flightDetails_to_segment,
                bundleOf(ARA_ITEM_FLIGHTS_SEGMENT_DATA to bundle)
            )
        })

        viewModel.gotoCheckout.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                flightSearch.verifiedMobileNumber = viewModel.gpStarNumber
                flightSearch.otp = viewModel.verifiedOTP
                val bundle = Bundle()
                bundle.putParcelable(ARG_FLIGHTS, flights)
                bundle.putParcelable(ARG_FLIGHT_SEARCH, flightSearch)
                bundle.putParcelable(ARG_DISCOUNT_MODEL, viewModel.discountModel)
                bundle.putParcelable(ARG_COUPON_RESPONSE, viewModel.couponResponse)
                findNavController().navigateSafe(
                    R.id.action_flightDetails_to_travelerDetailsFragment,
                    bundleOf(ARG_FLIGHT_DETAILS_DATA to bundle)
                )
            }
        })

        viewModel.isDataLoading.observe(viewLifecycleOwner) {
            if (it)
                alertDialog.show()
            else
                alertDialog.dismiss()
        }

        viewModel.tripCoinText.observe(viewLifecycleOwner) {
            setTripCoin(it)
        }

        viewModel.allPaymentMethod.observe(viewLifecycleOwner) {
            if (it.isNotEmpty() && viewModel.isCouponSelected.get())
                viewModel.onCouponChecked()
            else
                viewModel.onEarnTripCoinChecked()
        }

        viewModel.couponState.observe(viewLifecycleOwner) {
            if (it == GPCouponInputState.MobileInputState.name) {
                viewModel.gpCouponTitle.set(UtilText.couponTitle)
                viewModel.gpCouponSubTitle.set(UtilText.couponSubTitle)
            } else {
                viewModel.gpCouponTitle.set(UtilText.otpTitle)
                viewModel.gpCouponSubTitle.set(
                    UtilText.otpSubFirstPart + viewModel.gpStarNumber + " " + getString(
                        R.string.please_input_that_number_to_proceed
                    )
                )
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun baggageForRouteAndPassengerwise(
        routeOptionsList: List<RouteOptionsItem>,
        luggageOptional: Boolean
    ) {
        val recyclerView = RecyclerView(requireContext())
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val sectionAdapter = SectionedRecyclerViewAdapter()
        recyclerView.adapter = sectionAdapter

        routeOptionsList.forEachIndexed { index, routeOptionsItem ->
            val baggageList = ArrayList<TravellerBaggage>()
            routeOptionsItem.travellerBaggageList.forEach {
                val travellerBaggage = TravellerBaggage(
                    it.title,
                    it.optionList,
                    it.travellerType,
                    it.isLuggageOptional
                )
                baggageList.add(travellerBaggage)
            }
            sectionAdapter.addSection(
                RoutewiseTravellersAdapter(
                    viewModel,
                    routeOptionsItem,
                    baggageList,
                    luggageOptional,
                    index
                )
            )
        }

        sectionAdapter.notifyDataSetChanged()
        bindingView.layoutLuggageContainer.addView(recyclerView)
    }

    private fun baggageForPassengerwise(
        travellerBaggageList: List<TravellerBaggage>,
        isBaggageOptional: Boolean
    ) {
        val recyclerView = RecyclerView(requireContext())
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val travellerBaggageAdapter = TravellerBaggageAdapter(viewModel, isBaggageOptional)
        travellerBaggageAdapter.addTravellerList(travellerBaggageList)
        recyclerView.adapter = travellerBaggageAdapter

        bindingView.layoutLuggageContainer.addView(recyclerView)
    }

    private fun baggageForRouteWise(
        routeOptionsList: List<RouteOptionsItem>,
        isBaggageOptional: Boolean
    ) {
        val recyclerView = RecyclerView(requireContext())
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val baggageAdapter = RouteBaggageAdapter(viewModel, isBaggageOptional)
        baggageAdapter.addRouteOptions(routeOptionsList)
        recyclerView.adapter = baggageAdapter

        bindingView.layoutLuggageContainer.addView(recyclerView)
    }

    @SuppressLint("SetTextI18n")
    private fun baggageForWholeFlight(wholeFlight: List<OptionsItem>, isBaggageOptional: Boolean) {
        var position = 0
        val baggageRadioGroup = RadioGroup(requireContext())
        wholeFlight.forEach {
            val radioButton = RadioButton(requireContext())
            radioButton.tag = it.code
            position++
            radioButton.text =
                "Add " + it.details + " " + it.quantity + "(" + it.currency + " " + it.amount + " " + "Max " + it.quantity + "Bags)"
            baggageRadioGroup.addView(radioButton)
        }

        if (isBaggageOptional) {
            val radioButton = RadioButton(requireContext())
            radioButton.tag = NO_BAGGAGE
            radioButton.text = "Add No Baggage"
            baggageRadioGroup.addView(radioButton)
        }

        baggageRadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val radioButton = radioGroup.findViewById(checkedId) as RadioButton
            val tag = radioButton.tag.toString()
            val optionList = ArrayList<OptionsItem>()

            if (tag.contentEquals(NO_BAGGAGE)) {
                val option = OptionsItem()
                optionList.add(option)
                viewModel.wholeFlightBaggage(optionList)
            } else {
                val option = wholeFlight.find { it.code == tag }
                optionList.add(option!!)
                viewModel.wholeFlightBaggage(optionList)
            }
        }

        bindingView.layoutLuggageContainer.addView(baggageRadioGroup)
    }

    private fun showGuestDialog() {
        try {
            dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context),
                R.layout.guest_user_layout_base,
                null,
                false
            ) as GuestUserLayoutBaseBinding
            dialog.setContentView(dialogBinding.root)
            dialogBinding.data = viewModel.popupData
            dialog.show()
        } catch (_: Exception) {
        }
    }

    private fun initUi() {
        flightDetailsAdapter = FlightDetailsAdapter(viewModel)
        bindingView.flightRecyclerView.adapter = flightDetailsAdapter
        initDetailsAdapter()
    }

    private fun initDetailsAdapter() {
        val mList = ArrayList<Any>()
        val mFlight = flights.flight
        val mItemSegments = flights.segments
        if (mFlight.size == mItemSegments.size) {
            val mCount = mFlight.size
            for (i in 0 until mCount) {
                val flight = mFlight[i]
                mList.add(flight)
            }
            flightDetailsAdapter!!.resetItems(mList as List<Any?>)
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

    private fun setTitleAndInfo() {
        val titleBuilder = SpannableStringBuilder()
        val destination = flightSearch.destination[flightSearch.destination.size - 1]
        val spannableDestination = SpannableString("  $destination")
        val mGroupSpan: ImageSpan = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val myDrawable =
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_dots_horizontal_24dp
                )
            myDrawable!!.setBounds(0, 0, myDrawable.intrinsicWidth, myDrawable.intrinsicHeight)
            ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE)
        } else {
            ImageSpan(requireContext(), R.drawable.ic_dots_horizontal_24dp)
        }
        spannableDestination.setSpan(mGroupSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val origin = flightSearch.origin[0]
        titleBuilder.append("$origin ")
        titleBuilder.append(spannableDestination)

        val subtitleBuilder = StringBuilder()
        val depart = flightSearch.depart
        if (depart.size == 1) {
            try {
                val mDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[0])
                subtitleBuilder.append(mDate)
                    .append(" " + flightSearch.numberOfTraveller.toString() + " Traveller(s)")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            try {
                val mStartDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[0])
                val mEndDate =
                    DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[depart.size - 1])
                subtitleBuilder.append(mStartDate).append(" - ").append(mEndDate)
                    .append(" " + flightSearch.numberOfTraveller.toString() + " Traveller(s)")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        setTitleAndSubtitle(titleBuilder, subtitleBuilder)
    }

    companion object {
        const val ARG_FLIGHT_SEARCH_MODEL = "ARG_FLIGHT_SEARCH_MODEL"
        const val ARG_ITEM_FLIGHTS = "ARG_ITEM_FLIGHTS"
        const val ARG_RULES_DETAILS = "ARG_RULES_DETAILS"
        const val ARG_FLIGHT_DETAILS_DATA = "ARG_FLIGHT_DETAILS_DATA"
        const val ARG_FLIGHT_SEARCH = "ARG_FLIGHT_SEARCH"
        const val ARG_FLIGHTS = "ARG_FLIGHTS"
        const val ARG_DISCOUNT_MODEL = "ARG_DISCOUNT_MODEL"
        const val ARG_COUPON_RESPONSE = "ARG_COUPON_RESPONSE"
    }
}
