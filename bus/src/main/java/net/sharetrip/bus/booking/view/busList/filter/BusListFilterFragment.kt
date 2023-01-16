package net.sharetrip.bus.booking.view.busList.filter

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.FilterData
import net.sharetrip.bus.databinding.FragmentBusListFilterBinding
import net.sharetrip.bus.utils.BUS_LIST_FILTER_DATA
import net.sharetrip.bus.utils.setTripCoin

class BusListFilterFragment() : BaseFragment<FragmentBusListFilterBinding>() {

    private val viewModel: BusListFilterViewModel by viewModels {
        val filterData = requireArguments().get(BUS_LIST_FILTER_DATA) as FilterData
        BusListFilterViewModelFactory(filterData)
    }

    override fun layoutId(): Int = R.layout.fragment_bus_list_filter

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTripCoin()

        bindingView.model = viewModel
        bindingView.filterClass.filterTitleTextView.text = getString(R.string.title_class)
        bindingView.filterSchedule.filterTitleTextView.text = getString(R.string.title_schedule)

        viewModel.filterScheduleValue.observe(viewLifecycleOwner) {
            bindingView.filterSchedule.filterValueTextView.text = it
        }

        viewModel.filterClassValue.observe(viewLifecycleOwner) {
            bindingView.filterClass.filterValueTextView.text = it
        }

        viewModel.filterPriceRangeValue.observe(viewLifecycleOwner) {
            bindingView.filterPriceRange.filterValueTextView.text = it
        }

        viewModel.clicked.observe(viewLifecycleOwner) { it ->
            val bundle = Bundle()
            bundle.putInt("Title", it)
            val addBuBottomDialogFragment: BusFilterBottomSheet =
                BusFilterBottomSheet.newInstance(viewModel)
            addBuBottomDialogFragment.arguments = bundle
            requireActivity().supportFragmentManager.let {
                addBuBottomDialogFragment.show(
                    it,
                    "BusListFilterFragment"
                )
            }
        }

        viewModel.navigateBackToList.observe(viewLifecycleOwner){ (filterData, navigate) ->
            if (navigate){
                setNavigationResult(key = BusListFilterViewModel.ARG_BUS_FILTER_DATA,
                result = filterData)
                findNavController().navigateUp()
            }
        }
    }
}
