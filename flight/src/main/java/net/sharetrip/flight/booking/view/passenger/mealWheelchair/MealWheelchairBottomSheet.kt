package net.sharetrip.flight.booking.view.passenger.mealWheelchair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharetrip.base.data.PrefKey
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.Ssr
import net.sharetrip.flight.booking.model.SsrEnum
import net.sharetrip.flight.databinding.LayoutMealWhealchairBottomSheetFlightBinding
import net.sharetrip.flight.utils.ShearedViewModel

class MealWheelchairBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bindingView: LayoutMealWhealchairBottomSheetFlightBinding
    private val mealTypeLiveData = MutableLiveData<Pair<String, String>>()
    private val requestWheelchairLiveData = MutableLiveData<Pair<String, String>>()
    private lateinit var shearedViewModel: ShearedViewModel

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
        val title = requireArguments().getString(PrefKey.TITLE, "")
        val selectedSsr = requireArguments().getString(PrefKey.SELECTED_SSR, "")
        val ssrList = requireArguments().getParcelableArrayList<Ssr>(PrefKey.SSR_LIST)
        shearedViewModel = ViewModelProvider(requireActivity()).get(ShearedViewModel::class.java)
        ssrList?.add(0, Ssr("", "None"))
        val mealWheelchairAdapter: MealWheelchairAdapter

        if (title == SsrEnum.MEAL.toString()) {
            bindingView.title.text = requireContext().resources.getString(R.string.meal_type)
            mealWheelchairAdapter = MealWheelchairAdapter(ssrList!!, selectedSsr, mealTypeLiveData)
        } else {
            bindingView.title.text =
                requireContext().resources.getString(R.string.request_wheelchair)
            mealWheelchairAdapter =
                MealWheelchairAdapter(ssrList!!, selectedSsr, requestWheelchairLiveData)
        }

        bindingView.optionRecycler.adapter = mealWheelchairAdapter

        mealTypeLiveData.observe(viewLifecycleOwner) {
            shearedViewModel.mealTypeViewModel.value = it
            dismiss()
        }
        requestWheelchairLiveData.observe(viewLifecycleOwner) {
            shearedViewModel.requestWheelchairViewModel.value = it
            dismiss()
        }

        bindingView.imageViewClose.setOnClickListener {
            dismiss()
        }
    }

    private fun layoutId() = R.layout.layout_meal_whealchair_bottom_sheet_flight
}
