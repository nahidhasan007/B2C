package net.sharetrip.holiday.booking.view.main

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayMainBinding
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.ServiceType
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.*

class HolidayMainFragment : BaseFragment<FragmentHolidayMainBinding>() {

    private val holidayHeaderViewModel: HolidayHeaderViewModel by viewModels {
        HolidayHeaderViewModelFactory(HolidayMainRepository(DataManager.holidayBookingApiService))
    }

    private val viewModel: HolidayMainViewModel by viewModels{
        HolidayMainViewModelFactory(HolidayMainRepository(DataManager.holidayBookingApiService))
    }

    private lateinit var holidayAdapter: HolidayAdapter

    override fun layoutId() = R.layout.fragment_holiday_main

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.holiday))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        holidayAdapter = HolidayAdapter(holidayHeaderViewModel)
        bindingView.listHolidays.adapter = holidayAdapter

        lifecycleScope.launchWhenStarted {
            viewModel.pagedListHoliday.collectLatest {
                holidayAdapter.submitData(it)
            }
        }

        holidayHeaderViewModel.navigateToHolidayList.observe(viewLifecycleOwner) { holidayListPageArgs ->
            if (holidayListPageArgs.second) {
                val bundle = bundleOf(
                    ARG_HOLIDAY_CITY_CODE to "\"" + holidayListPageArgs.first[0] + "\"",
                    ARG_HOLIDAY_CITY_NAME to holidayListPageArgs.first[1]
                )
                findNavController().navigateSafe(
                    R.id.action_holidayFragment_to_holidayListFragment,
                    bundle
                )
            }
        }
        
        ItemClickSupport.addTo(bindingView.listHolidays).setOnItemClickListener { _, position, _ ->
            if (position > 0) {
                val productCode =
                    bundleOf(ARG_HOLIDAY_PRODUCT_CODE to holidayAdapter.snapshot()[position - 1]?.productCode)
                findNavController().navigateSafe(
                    R.id.action_holidayFragment_to_holidayDetailFragment,
                    productCode
                )
            }
        }

        getNavigationResultLiveData<Long>(ARG_SINGLE_DATE)?.observe(viewLifecycleOwner) { travelDateInMills ->
            holidayHeaderViewModel.onUpdateTravelDate(travelDateInMills)
        }

        getNavigationResultLiveData<Bundle>(RESULT_ON_DESTINATION_SELECTION)?.observe(
            viewLifecycleOwner
        ) {
            val cityName = it.getString(ARG_HOLIDAY_CITY_NAME)
            val cityCode = it.getString(ARG_HOLIDAY_CITY_CODE)
            val position = it.getInt(ARG_HOLIDAY_DESTINATION_POSITION, 0)
            holidayHeaderViewModel.onUpdateDestinationName(cityCode!!, cityName!!, position)
        }

        holidayHeaderViewModel.navigateToTravelDate.observe(viewLifecycleOwner, EventObserver {
            val calenderData = CalenderData(
                mDateInMillisecond = DateUtil.parseDateToMillisecond(DateUtil.tomorrowApiDateFormat),
                mDateHintText = requireContext().getString(R.string.travel_date),
                fromAirportCode = "",
                toAirportCode = "",
                serviceType = ServiceType.HOLIDAY.serviceName
            )
            val bundle = bundleOf(ARG_CALENDER_DATA to calenderData)
            findNavController().navigateSafe(
                R.id.action_holidayFragment_to_holidayDateCalenderFragment,
                bundle
            )
        })

        holidayHeaderViewModel.navigateToCitySearch.observe(viewLifecycleOwner) {
            if (it.first) {
                val bundle = bundleOf(ARG_HOLIDAY_CITY_POSITION to it.second)
                findNavController().navigateSafe(
                    R.id.action_holidayFragment_to_holidayCitySearchFragment,
                    bundle
                )
            }
        }

        holidayHeaderViewModel.navigateToSearchedHolidayList.observe(
            viewLifecycleOwner,
            EventObserver {
                val bundle = bundleOf(
                    ARG_HOLIDAY_CITY_CODE to holidayHeaderViewModel.cityCodeList,
                    ARG_HOLIDAY_CITY_NAME to holidayHeaderViewModel.cityNameList
                )
                findNavController().navigateSafe(
                    R.id.action_holidayFragment_to_holidayListFragment,
                    bundle
                )
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        holidayHeaderViewModel.resetNavigationFlags()
    }
}
