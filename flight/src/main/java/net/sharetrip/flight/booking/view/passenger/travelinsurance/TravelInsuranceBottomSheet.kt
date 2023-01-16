package net.sharetrip.flight.booking.view.passenger.travelinsurance

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
import net.sharetrip.flight.booking.model.TravelInsuranceItem
import net.sharetrip.flight.booking.model.TravelInsuranceOption
import net.sharetrip.flight.booking.view.passenger.PassengerFragment.Companion.ARGS_SELECTED_TRAVEL_INSURANCE_OPTION
import net.sharetrip.flight.booking.view.passenger.PassengerFragment.Companion.ARGS_TRAVEL_INSURANCE_LIST
import net.sharetrip.flight.databinding.LayoutTravelInsuranceInfoBinding
import net.sharetrip.flight.utils.ShearedViewModel
import net.sharetrip.shared.utils.strikeText

class TravelInsuranceBottomSheet : BottomSheetDialogFragment(),
    TravelInsuranceAdapter.TravelInsuranceAddOnsListener,
    InsuranceProviderAdapter.ItemClickListener {
    private lateinit var bindingView: LayoutTravelInsuranceInfoBinding
    private lateinit var sharedViewModel: ShearedViewModel
    var travelInsuranceOption: TravelInsuranceOption? = null

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

        val travelInsuranceList = requireArguments().getParcelableArrayList<TravelInsuranceItem>(
            ARGS_TRAVEL_INSURANCE_LIST
        )
        val selectedTravelInsuranceOption: TravelInsuranceOption = requireArguments().getParcelable(
            ARGS_SELECTED_TRAVEL_INSURANCE_OPTION
        )!!

        val options = arrayListOf<TravelInsuranceOption>()

        travelInsuranceList?.forEach {
            if (it.options.isNotEmpty()) {
                it.options.forEach { option ->
                    val travelInsuranceOption = TravelInsuranceOption(
                        it.code,
                        option.code,
                        option.price,
                        it.name + ", " + option.name,
                        option.discountPrice,
                        option.isSelected,
                        it.logo,
                        default = option.default
                    )
                    options.add(travelInsuranceOption)
                }
            } else {
                val travelInsuranceOption = TravelInsuranceOption(name = it.name)
                options.add(travelInsuranceOption)
            }
        }

        bindingView.buttonFilterApply.setOnClickListener {
            sharedViewModel.travelInsuranceOption.value = Event(travelInsuranceOption!!)
            dismiss()
        }

        if (options.isNotEmpty()) {
            options.forEach {
                if (selectedTravelInsuranceOption.code.isEmpty()) {
                    if (it.default)
                        selectedTravelInsuranceOption.code = it.code
                }
            }
        }

        val adapter = TravelInsuranceAdapter(options, selectedTravelInsuranceOption.code, this)
        (bindingView.recyclerTravelInsuranceAddon.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        bindingView.recyclerTravelInsuranceAddon.adapter = adapter

        val travelInsuranceProviderAdapter = travelInsuranceList?.let {
            InsuranceProviderAdapter(
                it as ArrayList<TravelInsuranceItem>,
                this
            )
        }
        bindingView.recyclerTestCenter.layoutManager = GridLayoutManager(requireContext(), 2)
        bindingView.recyclerTestCenter.adapter = travelInsuranceProviderAdapter

        bindingView.imageViewClose.setOnClickListener {
            dismiss()
        }
    }

    private fun layoutId() = R.layout.layout_travel_insurance_info

    override fun onClickItem(option: TravelInsuranceOption) {
        travelInsuranceOption = option
        val price = kotlin.math.ceil(option.price).toInt()
        val discountedPrice = kotlin.math.ceil(option.discountPrice).toInt()
        bindingView.textViewTravelInsuranceDiscountPrice.text = "BDT $discountedPrice"
        bindingView.textViewTravelInsuranceRegularPrice.strikeText("BDT $price")
    }

    override fun onItemClick(code: String) {
        sharedViewModel.travelInsuranceDetails.value = code
    }

    companion object {
        fun newInstance(): TravelInsuranceBottomSheet {
            return TravelInsuranceBottomSheet()
        }
    }
}