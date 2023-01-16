package net.sharetrip.bus.booking.view.summary

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.Departure
import net.sharetrip.bus.booking.model.ItemTraveler
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.booking.model.payment.PaymentMethod
import net.sharetrip.bus.booking.view.seatselection.ItemBusHistoryPricingSeatView
import net.sharetrip.bus.databinding.FragmentBusBookingSummaryBinding
import net.sharetrip.bus.history.model.SeatClassPrice
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.getAlertDialog

class BusBookingSummaryFragment : BaseFragment<FragmentBusBookingSummaryBinding>() {
    private val viewModel by lazy {
        val g = Gson()
        val passengerInfo = g.fromJson(
            bundle.getString(ARG_BUS_SUMMARY_PASSENGER_INFO),
            ItemTraveler::class.java
        )
        val departureInfo = bundle.getParcelable<Departure>(ARG_BUS_SUMMARY_DEPARTURE)!!
        val searchIdAndTripCoin =
            bundle.getParcelable<SearchIdAndTripCoin>(ARG_BUS_SUMMARY_SEARCH_AND_TRIP_COIN)!!

        ViewModelProvider(
            this,
            BusSummaryVMFactory(
                departureInfo, passengerInfo, searchIdAndTripCoin,
                DataManager.busBookingApiService, DataManager.getSharedPref(requireContext())
            )
        ).get(
            BookingSummaryViewModel::class.java
        )
    }

    val bundle by lazy { arguments?.getBundle(ARG_BUS_SUMMARY_BUNDLE)!! }

    private lateinit var paymentAdapter: BusPaymentAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var cardPrefixAdapter: ArrayAdapter<String>
    private lateinit var dots: Array<ImageView?>
    private var visibleItem = 6
    private var prevDot = -1
    private val seriesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun setDotSelected() {
        visibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
        dots[visibleItem / 6]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )

        if (prevDot >= 0 && visibleItem / 6 != prevDot)
            dots[prevDot]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )
        prevDot = visibleItem / 6
    }

    fun setSliderDot() {
        bindingView.linearSliderDots.removeAllViews()
        val dotCount: Int = if (viewModel.paymentMethodListFinal.size > 6)
            viewModel.paymentMethodListFinal.size / 6
        else
            1
        dots = arrayOfNulls(dotCount)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        for (i in 0 until dotCount) {
            dots[i] = ImageView(context)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )
            bindingView.linearSliderDots.addView(dots[i], params)
        }
        dots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )
        prevDot = -1
    }

    fun updatePricingDetail() {
        if (viewModel.departureInfo.selectedSeatsInfo?.size!! > 0) {
            viewModel.departureInfo.selectedSeatsInfo!!.forEach {
                val child = ItemBusHistoryPricingSeatView(requireContext())
                child.binding.allData =
                    SeatClassPrice(it.seatNo, it.seatTypeId, it.fare.originalFare)
                bindingView.layoutPricingDetails.addView(child)
            }
        }
    }

    override fun layoutId() = R.layout.fragment_bus_booking_summary

    fun initPaymentRecycler() {
        bindingView.listPaymentType.layoutManager = layoutManager
        bindingView.listPaymentType.adapter = paymentAdapter
    }

    fun setCardPrefixList() {
        if (seriesList.isNotEmpty())
            seriesList.clear()

        if (viewModel.paymentMethodListFinal[viewModel.prevSelectedMethod].series.isNotEmpty()) {
            bindingView.cardPrefixTextInputLayout.visibility = View.VISIBLE
            bindingView.cardPrefixImageView.visibility = View.VISIBLE
            for (series in viewModel.paymentMethodListFinal[viewModel.prevSelectedMethod].series)
                seriesList.add(series.series)
            cardPrefixAdapter.clear()
            cardPrefixAdapter.addAll(seriesList)
            bindingView.cardPrefixAutoCompleteTextView.setText(seriesList[0])
            viewModel.selectedSeries = 0
        } else {
            bindingView.cardPrefixTextInputLayout.visibility = View.GONE
            bindingView.cardPrefixImageView.visibility = View.GONE
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {
        bindingView.model = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        val loadingDialog = getAlertDialog(layoutInflater, requireContext())

        bindingView.layoutTiming.departure = viewModel.departureInfo
        bindingView.tvRoute.text =
            viewModel.departureInfo.route.from.name + " --- " + viewModel.departureInfo.route.to.name
        paymentAdapter = BusPaymentAdapter(viewModel)

        cardPrefixAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, ArrayList()
        )
        bindingView.cardPrefixAutoCompleteTextView.setAdapter(cardPrefixAdapter)

        updatePricingDetail()

        viewModel.dialogLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        viewModel.navigateToPayment.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Walhala - Payment Url " + it, Toast.LENGTH_SHORT)
                .show()
        }

        viewModel.maxRedeemCoin.observe(viewLifecycleOwner) {
            bindingView.seekBar.max = it.toInt()
        }

        viewModel.paymentMethodList.observe(
            viewLifecycleOwner
        ) { paymentMethods: List<PaymentMethod> ->
            viewModel.paymentMethodWithEarn.addAll(paymentMethods.filter { it.isEarnTripCoinApplicable })
            viewModel.paymentMethodWithRedeem.addAll(paymentMethods.filter { it.isRedeemTripCoinApplicable })
            viewModel.paymentMethodWithCoupon.addAll(paymentMethods.filter { it.isCouponAvailable })
            viewModel.paymentMethodWithEarn[0].checked = true
            viewModel.paymentMethodWithCoupon[0].checked = true
            viewModel.paymentMethodWithRedeem[0].checked = true
            layoutManager =
                GridLayoutManagerWrapper(
                    activity, 2,
                    RecyclerView.HORIZONTAL, false
                )
            setSliderDot()
            initPaymentRecycler()
        }

        viewModel.isCouponClicked.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.changePaymentSelected(0)
                viewModel.prevSelectedMethod = 0
                viewModel.paymentMethodListFinal = viewModel.paymentMethodWithCoupon
                viewModel.changePaymentSelected(0)
                if (viewModel.paymentMethodListFinal.size < 4)
                    layoutManager.spanCount = 1
                else
                    layoutManager.spanCount = 2
                paymentAdapter.notifyDataSetChanged()
                setSliderDot()
                setCardPrefixList()
            }
        }

        viewModel.isEarnAndAvailClicked.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.changePaymentSelected(0)
                viewModel.prevSelectedMethod = 0
                viewModel.paymentMethodListFinal = viewModel.paymentMethodWithEarn
                viewModel.changePaymentSelected(0)
                if (viewModel.paymentMethodListFinal.size < 4)
                    layoutManager.spanCount = 1
                else
                    layoutManager.spanCount = 2
                paymentAdapter.notifyDataSetChanged()
                setSliderDot()
                setCardPrefixList()
            }
        }

        viewModel.isRedeemClicked.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.changePaymentSelected(0)
                viewModel.prevSelectedMethod = 0
                viewModel.paymentMethodListFinal = viewModel.paymentMethodWithRedeem
                viewModel.changePaymentSelected(0)
                if (viewModel.paymentMethodListFinal.size < 4)
                    layoutManager.spanCount = 1
                else
                    layoutManager.spanCount = 2
                paymentAdapter.notifyDataSetChanged()
                setSliderDot()
                setCardPrefixList()
            }
        }

        ItemClickSupport.addTo(bindingView.listPaymentType)
            .setOnItemClickListener { rv: RecyclerView?, pos: Int, _: View? ->
                viewModel.changePaymentSelected(pos)
                if (viewModel.prevSelectedMethod >= 0 && viewModel.prevSelectedMethod != pos) {
                    rv?.adapter?.notifyItemChanged(viewModel.prevSelectedMethod)
                }
                viewModel.prevSelectedMethod = pos
                rv?.adapter?.notifyItemChanged(pos)
                setCardPrefixList()
            }

        bindingView.listPaymentType.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {

                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                setDotSelected()
            }
        })

        bindingView.cardPrefixAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            viewModel.selectedSeries = position
        }
    }

    companion object {
        const val ARG_BUS_SUMMARY_BUNDLE = "ARG_BUS_SUMMARY_BUNDLE"
        const val ARG_BUS_SUMMARY_PASSENGER_INFO = "ARG_BUS_SUMMARY_PASSENGER_INFO"
        const val ARG_BUS_SUMMARY_DEPARTURE = "ARG_BUS_SUMMARY_DEPARTURE"
        const val ARG_BUS_SUMMARY_SEARCH_AND_TRIP_COIN = "ARG_BUS_SUMMARY_SEARCH_AND_TRIP_COIN"
    }
}
