package net.sharetrip.flight.booking.view.passenger.covidTest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import net.sharetrip.shared.utils.strikeText
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.CovidAddOnResponseItem
import net.sharetrip.flight.booking.model.CovidTestOption
import net.sharetrip.flight.databinding.LayoutCovidInfoFlightBinding
import net.sharetrip.flight.utils.ShearedViewModel
import net.sharetrip.flight.booking.view.passenger.PassengerFragment
import net.sharetrip.flight.booking.view.passenger.PassengerFragment.Companion.ARGS_COVID_ADD_ON_LIST
import java.util.*

class CovidInfoBottomSheet : BottomSheetDialogFragment(), CovidAdapter.CovidAddOnsListener,
    TestCenterAdapter.ItemClickListener {
    private lateinit var bindingView: LayoutCovidInfoFlightBinding
    private lateinit var sharedViewModel: ShearedViewModel
    var covidTestOption: CovidTestOption? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bindingView = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        return bindingView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(ShearedViewModel::class.java)
        val covidAddOnList = requireArguments().getParcelableArrayList<CovidAddOnResponseItem>(ARGS_COVID_ADD_ON_LIST)
        val selectedCovidTestOption: CovidTestOption = requireArguments().getParcelable(
            PassengerFragment.ARGS_SELECTED_COVID_ADD_ON)!!

        val options = arrayListOf<CovidTestOption>()

        covidAddOnList?.forEach {
            if (!it.options.isNullOrEmpty()) {
                it.options.forEach { option ->
                    val covidTestOption = CovidTestOption(option.isAddress, option.code, option.price, it.name + ", " + option.name, option.discountPrice, option.isSelected, it.code, "", it.name, self = it.self)
                    if (selectedCovidTestOption.code == option.code) {
                        covidTestOption.addressDetails = selectedCovidTestOption.addressDetails
                    }
                    options.add(covidTestOption)
                }
            } else {
                val covidTestOption = CovidTestOption(testCenterName = it.name, name = it.name, testCode = it.code, self = it.self)
                options.add(covidTestOption)
            }

        }
        bindingView.buttonFilterApply.setOnClickListener {
            sharedViewModel.covidTestOption.value = covidTestOption
            dismiss()
        }

        if (options.isNotEmpty()){
            options.forEach {
                  if (selectedCovidTestOption.code.isEmpty() && it.self) {
                        selectedCovidTestOption.code = it.code
                    }
            }
        }

        val adapter = CovidAdapter(options, selectedCovidTestOption.code, this)
        (bindingView.recyclerCovidAddon.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        bindingView.recyclerCovidAddon.adapter = adapter

        val covidTestCenterAdapter = covidAddOnList?.let { TestCenterAdapter(it.filter { list -> !list.self } as ArrayList<CovidAddOnResponseItem>, this) }
        bindingView.recyclerTestCenter.layoutManager = GridLayoutManager(requireContext(), 2)
        bindingView.recyclerTestCenter.adapter = covidTestCenterAdapter

        bindingView.imageViewClose.setOnClickListener {
            dismiss()
        }
    }

    private fun layoutId() = R.layout.layout_covid_info_flight

    override fun onClickItem(option: CovidTestOption) {
        covidTestOption = option

        if (option.discountPrice == 0.0) {
            bindingView.textViewCovidRegularPrice.visibility = View.GONE
            bindingView.textViewCovidDiscountPrice.text = "BDT " + option.price.toString()
        } else {
            bindingView.textViewCovidDiscountPrice.text = "BDT " + option.discountPrice.toString()
            bindingView.textViewCovidRegularPrice.visibility = View.VISIBLE
            bindingView.textViewCovidRegularPrice.strikeText("BDT " + option.price.toString())
        }
    }

    override fun onSetAddress(option: CovidTestOption) {
        covidTestOption = option
    }

    override fun onItemClick(code: String) {
        sharedViewModel.covidServiceDetails.value = code
    }

    companion object {
        fun newInstance(): CovidInfoBottomSheet {
            return CovidInfoBottomSheet()
        }
    }
}