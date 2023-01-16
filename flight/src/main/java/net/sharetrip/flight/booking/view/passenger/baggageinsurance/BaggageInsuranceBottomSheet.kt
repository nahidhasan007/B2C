package net.sharetrip.flight.booking.view.passenger.baggageinsurance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharetrip.base.event.Event
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.BaggageInsurance
import net.sharetrip.flight.booking.model.BaggageInsuranceOption
import net.sharetrip.flight.booking.view.passenger.PassengerFragment
import net.sharetrip.flight.databinding.LayoutBaggageInsuranceBottomsheetBinding
import net.sharetrip.flight.utils.ShearedViewModel
import net.sharetrip.shared.utils.strikeText

class BaggageInsuranceBottomSheet : BottomSheetDialogFragment(),
    BaggageInsuranceAdapter.BaggageInsuranceListener,
    BaggageInsuranceProviderAdapter.ItemClickListener {
    private lateinit var bindingView: LayoutBaggageInsuranceBottomsheetBinding
    private lateinit var sharedViewModel: ShearedViewModel
    var baggageInsuranceOption: BaggageInsuranceOption? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingView = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(ShearedViewModel::class.java)

        val baggageInsuranceList = requireArguments().getParcelableArrayList<BaggageInsurance>(
            PassengerFragment.ARGS_BAGGAGE_INSURANCE_LIST
        )
        val selectedBaggageInsuranceOption: BaggageInsuranceOption =
            requireArguments().getParcelable(
                PassengerFragment.ARGS_SELECTED_BAGGAGE_INSURANCE_OPTION
            )!!

        val options = arrayListOf<BaggageInsuranceOption>()

        baggageInsuranceList?.forEach {
            if (it.options.isNotEmpty()) {
                it.options.forEach { option ->
                    val baggageInsuranceOption = BaggageInsuranceOption(
                        it.code,
                        option.code,
                        option.price,
                        it.name + ", " + option.name,
                        option.discountPrice,
                        option.isSelected,
                        it.logo,
                        default = option.default
                    )
                    options.add(baggageInsuranceOption)
                }
            } else {
                val baggageInsuranceOption = BaggageInsuranceOption(name = it.name)
                options.add(baggageInsuranceOption)
            }
        }

        bindingView.buttonFilterApply.setOnClickListener {
            sharedViewModel.baggageInsuranceOption.value = Event(baggageInsuranceOption!!)
            dismiss()
        }

        if (options.isNotEmpty()) {
            options.forEach {
                if (selectedBaggageInsuranceOption.optionCode.isEmpty()) {
                    if (it.default)
                        selectedBaggageInsuranceOption.optionCode = it.optionCode
                }
            }
        }

        val adapter = BaggageInsuranceAdapter(options, selectedBaggageInsuranceOption.optionCode, this)
        (bindingView.recyclerBaggageInsuranceAddon.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        bindingView.recyclerBaggageInsuranceAddon.adapter = adapter

        val baggageInsuranceProviderAdapter = baggageInsuranceList?.let {
            BaggageInsuranceProviderAdapter(
                it as ArrayList<BaggageInsurance>,
                this
            )
        }
        bindingView.recyclerTestCenter.layoutManager = GridLayoutManager(requireContext(), 2)
        bindingView.recyclerTestCenter.adapter = baggageInsuranceProviderAdapter

        bindingView.imageViewClose.setOnClickListener {
            dismiss()
        }
    }

    private fun layoutId() = R.layout.layout_baggage_insurance_bottomsheet

    override fun onClickItem(option: BaggageInsuranceOption) {
        baggageInsuranceOption = option
        val price = kotlin.math.ceil(option.price).toInt()
        val discountedPrice = kotlin.math.ceil(option.discountPrice).toInt()
        bindingView.textViewBaggageInsuranceDiscountPrice.text = "BDT $discountedPrice"
        bindingView.textViewBaggageInsuranceRegularPrice.strikeText("BDT $price")
    }

    override fun onItemClick(code: String) {
        sharedViewModel.baggageInsuranceDetails.value = code
    }

    companion object {
        fun newInstance(): BaggageInsuranceBottomSheet {
            return BaggageInsuranceBottomSheet()
        }
    }
}