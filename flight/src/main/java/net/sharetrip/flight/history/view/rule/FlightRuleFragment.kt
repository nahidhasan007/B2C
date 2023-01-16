package net.sharetrip.flight.history.view.rule

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentFlightRuleBinding
import net.sharetrip.flight.history.FlightHistoryActivity
import net.sharetrip.flight.history.model.BaggageDetails
import net.sharetrip.flight.history.model.RuleType
import java.text.NumberFormat
import java.util.*

class FlightRuleFragment : BaseFragment<FragmentFlightRuleBinding>() {
    private lateinit var viewModel: FlightRuleViewModel
    private var baggage: BaggageDetails? = null
    private lateinit var basicBaggageAdapter: BasicBaggageAdapter
    private lateinit var extraBaggageAdapter: ExtraBaggageAdapter
    private var ruleType = 0
    private var baggageCost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val searchId = requireArguments().getBundle(ARG_RULES_DATA)?.getString(EXTRA_SEARCH_ID)
        val sequenceCode =
            requireArguments().getBundle(ARG_RULES_DATA)?.getString(EXTRA_SEQUENCE_CODE)
        ruleType = requireArguments().getBundle(ARG_RULES_DATA)?.getInt(EXTRA_RULE_TYPE, 1)!!
        baggage = requireArguments().getBundle(ARG_RULES_DATA)?.getParcelable(EXTRA_BAGGAGE_DETAILS)
        baggageCost =
            requireArguments().getBundle(ARG_RULES_DATA)?.getDouble(EXTRA_BAGGAGE_COST, 0.0)!!

        viewModel = FlightRuleViewModelFactory(searchId!!, sequenceCode!!, ruleType).create(
            FlightRuleViewModel::class.java
        )
    }

    override fun layoutId(): Int = R.layout.fragment_flight_rule

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {

        viewModel.data.observe(this) { data: CharSequence? ->
            bindingView.progressBar.visibility = View.INVISIBLE
            bindingView.dateTextView.visibility = View.VISIBLE
            bindingView.dateTextView.text = data
        }

        when {
            RuleType.AIR_FARE_RULE == ruleType -> {
                setTitle(getString(R.string.air_fare_rules))
            }

            RuleType.BAGGAGE == ruleType -> {
                setTitle(getString(R.string.baggage))

                bindingView.layoutBaggageInfo.visibility = View.VISIBLE
                bindingView.progressBar.visibility = View.GONE
                bindingView.dateTextView.visibility = View.GONE

                if (baggage?.basic?.isNotEmpty() == true) {
                    bindingView.layoutBasicBaggage.visibility = View.VISIBLE
                    basicBaggageAdapter = BasicBaggageAdapter()
                    bindingView.recyclerBasicBaggage.adapter = basicBaggageAdapter
                    basicBaggageAdapter.setBaggage(baggage!!.basic)
                }

                if (!baggage?.extra.isNullOrEmpty()) {
                    bindingView.layoutExtraBaggage.visibility = View.VISIBLE
                    extraBaggageAdapter = ExtraBaggageAdapter()
                    bindingView.recyclerExtraBaggage.adapter = extraBaggageAdapter
                    extraBaggageAdapter.setBaggage(baggage?.extra!!)
                    bindingView.textViewTotalBaggageCost.text =
                        "BDT ${NumberFormat.getNumberInstance(Locale.US).format(baggageCost)}"
                }
            }

            RuleType.FARE_DETAILS == ruleType -> {
                setTitle(getString(R.string.fare_details))
            }
        }

        viewModel.showMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setTitle(title: String) {
        (activity as FlightHistoryActivity).supportActionBar?.title = title
    }

    companion object {
        const val EXTRA_SEARCH_ID = "EXTRA_SEARCH_ID"
        const val EXTRA_SEQUENCE_CODE = "EXTRA_SEQUENCE_CODE"
        const val EXTRA_RULE_TYPE = "EXTRA_RULE_TYPE"
        const val EXTRA_BAGGAGE_DETAILS = "EXTRA_BAGGAGE_DETAILS"
        const val EXTRA_BAGGAGE_COST = "EXTRA_BAGGAGE_COST"
        const val ARG_RULES_DATA = "ARG_RULES_DATA"
    }
}
