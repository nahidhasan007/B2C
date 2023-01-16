package net.sharetrip.holiday.booking.view.list

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayListBinding
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.*
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class HolidayListFragment : BaseFragment<FragmentHolidayListBinding>() {

    private val adapter = HolidayListAdapter()
    private val viewModel: HolidayListViewModel by viewModels {
        HolidayListViewModelFactory(
            cityCodes,
            HolidayListRepository(DataManager.holidayBookingApiService)
        )
    }

    private lateinit var cityNames: String
    private lateinit var cityCodes: String

    override fun layoutId() = R.layout.fragment_holiday_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        cityCodes = requireArguments().getString(ARG_HOLIDAY_CITY_CODE)!!
        cityNames = requireArguments().getString(ARG_HOLIDAY_CITY_NAME)!!

        setTitleAndSubtitle(cityNames)
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.listHolidays.adapter = adapter.withLoadStateFooter(ListLoadStateAdapter {
            adapter.retry()
        })

        lifecycleScope.launchWhenStarted {
            viewModel.pagedListHoliday.collectLatest {
                adapter.submitData(it)
            }
        }

        ItemClickSupport.addTo(bindingView.listHolidays)
            .setOnItemClickListener { _, position, _ ->
                if (position in 0 until adapter.snapshot().size) {
                    val productCode = adapter.snapshot()[position]?.productCode
                    val bundle = bundleOf(ARG_HOLIDAY_PRODUCT_CODE to productCode)
                    findNavController().navigateSafe(
                        R.id.action_holidayListFragment_to_holidayDetailFragment,
                        bundle
                    )
                }
            }
    }
}
