package net.sharetrip.flight.history.view.bookingdetails

import android.app.Dialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.flightresponse.flights.price.PriceBreakdown
import net.sharetrip.flight.databinding.FragmentFlightHistoryDetailsBinding
import net.sharetrip.flight.history.model.FlightHistoryResponse
import net.sharetrip.flight.history.model.RuleType
import net.sharetrip.flight.history.view.bookingdetails.FlightBookingDetailsViewModel.Companion.GOTO_PAYMENT
import net.sharetrip.flight.history.view.list.FlightHistoryListFragment.Companion.ARG_FLIGHT_HISTORY_RESPONSE
import net.sharetrip.flight.history.view.list.FlightHistoryListFragment.Companion.ARG_FLIGHT_VOID_QUOTATION
import net.sharetrip.flight.history.view.priceinfo.PriceInformationFragment.Companion.EXTRA_PRICE_BREAK_DOWN
import net.sharetrip.flight.history.view.refundselectpassenger.SelectPassengerFragment.Companion.ARG_REFUND_VOID_DATA
import net.sharetrip.flight.history.view.rule.FlightRuleFragment.Companion.ARG_RULES_DATA
import net.sharetrip.flight.history.view.rule.FlightRuleFragment.Companion.EXTRA_BAGGAGE_COST
import net.sharetrip.flight.history.view.rule.FlightRuleFragment.Companion.EXTRA_BAGGAGE_DETAILS
import net.sharetrip.flight.history.view.rule.FlightRuleFragment.Companion.EXTRA_RULE_TYPE
import net.sharetrip.flight.history.view.rule.FlightRuleFragment.Companion.EXTRA_SEARCH_ID
import net.sharetrip.flight.history.view.rule.FlightRuleFragment.Companion.EXTRA_SEQUENCE_CODE
import net.sharetrip.flight.history.view.voidorrefundconfirmation.VoidOrRefundSuccessFragment.Companion.WAS_REFUND_OR_VOID
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.utils.MyClickableSpan
import net.sharetrip.flight.utils.setTitleSubTitle
import net.sharetrip.shared.utils.*

class FlightBookingDetailsFragment : BaseFragment<FragmentFlightHistoryDetailsBinding>() {
    private val viewModel: FlightBookingDetailsViewModel by lazy {
        FlightBookingDetailsViewModelFactory(
            historyResponse,
            SharedPrefsHelper(requireContext())[PrefKey.ACCESS_TOKEN, ""],
            DataManager.flightHistoryApiService
        ).create(FlightBookingDetailsViewModel::class.java)
    }
    private lateinit var historyResponse: FlightHistoryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gson = Gson()
        historyResponse = gson.fromJson(
            requireArguments().getBundle(ARG_FLIGHT_HISTORY_RESPONSE)
                ?.getString(ARG_FLIGHT_HISTORY_RESPONSE), FlightHistoryResponse::class.java
        )
    }

    override fun layoutId(): Int = R.layout.fragment_flight_history_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.booking_detail))
        setTextColor()

        bindingView.viewModel = viewModel

        viewModel.showToast.observe(viewLifecycleOwner) {
            showMessage(it)
        }

        viewModel.isLoaderVisible.observe(viewLifecycleOwner) {
            showOrHideLoader(it)
        }

        viewModel.isShowCancelDialog.observe(viewLifecycleOwner) {
            showCancelDialog()
        }

        viewModel.gotoFlightDetails.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            val gson = Gson()
            bundle.putString(ARG_FLIGHT_HISTORY_RESPONSE, gson.toJson(historyResponse))
            findNavController().navigateSafe(
                R.id.action_booking_details_to_flight_details,
                bundleOf(ARG_FLIGHT_HISTORY_RESPONSE to bundle)
            )
        })

        viewModel.gotoTravellerInfo.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            val gson = Gson()
            bundle.putString(ARG_FLIGHT_HISTORY_RESPONSE, gson.toJson(historyResponse))
            findNavController().navigateSafe(
                R.id.action_booking_details_to_travellers_info,
                bundleOf(ARG_FLIGHT_HISTORY_RESPONSE to bundle)
            )
        })

        viewModel.gotoPricingInfo.observe(viewLifecycleOwner, EventObserver {
            val priceBreakdown: PriceBreakdown? = historyResponse.priceBreakdown
            priceBreakdown?.luggageAmount = historyResponse.luggageAmount
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_PRICE_BREAK_DOWN, priceBreakdown)

            findNavController().navigateSafe(
                R.id.action_booking_details_to_pricing_info, bundleOf(
                    EXTRA_PRICE_BREAK_DOWN to bundle
                )
            )
        })

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            if (it.first == GOTO_PAYMENT) {
                hideToolbar(R.id.toolbar)
                lifecycleScope.launchWhenResumed {
                    findNavController().navigateSafe(
                        R.id.action_flightBookingDetails_to_payment_nav_graph,
                        it.second as Bundle
                    )
                }
            }
        })

        viewModel.gotoRule.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            when (it) {
                RuleType.AIR_FARE_RULE, RuleType.FARE_DETAILS -> {
                    bundle.putString(EXTRA_SEARCH_ID, historyResponse.searchId)
                    bundle.putString(EXTRA_SEQUENCE_CODE, historyResponse.sequenceCode)
                    bundle.putInt(EXTRA_RULE_TYPE, it)
                }

                RuleType.BAGGAGE -> {
                    bundle.putString(EXTRA_SEARCH_ID, historyResponse.searchId)
                    bundle.putString(EXTRA_SEQUENCE_CODE, historyResponse.sequenceCode)
                    bundle.putInt(EXTRA_RULE_TYPE, it)
                    bundle.putParcelable(EXTRA_BAGGAGE_DETAILS, historyResponse.baggage)
                    bundle.putDouble(EXTRA_BAGGAGE_COST, historyResponse.luggageAmount)
                }
            }

            findNavController().navigateSafe(
                R.id.action_booking_details_to_rule, bundleOf(
                    ARG_RULES_DATA to bundle
                )
            )
        })

        viewModel.goBack.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        bindingView.refundButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(ARG_REFUND_VOID_DATA, "refund")
            bundle.putParcelable(ARG_FLIGHT_HISTORY_RESPONSE, historyResponse)
            findNavController().navigate(
                R.id.action_flightBookingDetails_to_selectPassengerFragment,
                bundleOf(ARG_HISTORY_DATA to bundle)
            )
        }

        viewModel.gotoVoidConfirmation.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            bundle.putString(ARG_REFUND_VOID_DATA, "void")
            bundle.putParcelable(ARG_FLIGHT_VOID_QUOTATION, it)
            findNavController().navigate(
                R.id.action_flightBookingDetails_to_confirmationFragment,
                bundleOf(ARG_REFUND_VOID_DATA to bundle)
            )
        })

        getNavigationResultLiveData<String>(WAS_REFUND_OR_VOID)?.observe(viewLifecycleOwner) {
            if (it.lowercase() == "void") {
                bindingView.voidButton.visibility = View.GONE
                bindingView.refundButton.visibility = View.GONE
            }

            removeBackStackEntry<String>(WAS_REFUND_OR_VOID)
        }
    }

    override fun onStart() {
        super.onStart()
        showToolbar(R.id.toolbar)
    }

    private fun showCancelDialog() {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_flight_cancellation)
        dialog.setCancelable(false)
        val textViewMessage = dialog.findViewById<TextView>(R.id.dialogBody)
        val btnYes = dialog.findViewById<TextView>(R.id.btnYes)
        val btnNo = dialog.findViewById<TextView>(R.id.btnNo)

        val spannableContent = SpannableString(textViewMessage.text.toString())
        val wordClick = object : MyClickableSpan() {
            override fun onClick(view: View) {
                dialog.dismiss()
                viewModel.onClickedAirFareRulesButton()
            }
        }
        spannableContent.setSpan(wordClick, 16, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textViewMessage.linksClickable = true
        textViewMessage.movementMethod = LinkMovementMethod.getInstance()
        textViewMessage.setText(spannableContent, TextView.BufferType.SPANNABLE)
        btnYes.setOnClickListener {
            dialog.dismiss()
            viewModel.onDialogYesBtnClick()
        }
        btnNo.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showOrHideLoader(it: Boolean) = if (it) {
        bindingView.loader.visibility = View.VISIBLE
    } else {
        bindingView.loader.setVisibility(View.GONE)
    }

    private fun showMessage(mMessage: String) {
        try {
            Toast.makeText(context, mMessage, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
        }
    }

    private fun setTextColor() {
        val status: String = historyResponse.bookingStatus
        val paymentStatus: String = historyResponse.paymentStatus

        if (status.contains("Cancelled") || paymentStatus.equals("Unpaid", ignoreCase = true)) {
            bindingView.statusTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.error_color
                )
            )
        } else {
            bindingView.statusTextView.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mid_green
                )
            )
        }
    }

    companion object {
        const val ARG_HISTORY_DATA = "ARG_HISTORY_DATA"
    }
}
