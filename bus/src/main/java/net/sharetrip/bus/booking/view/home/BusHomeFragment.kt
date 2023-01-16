package net.sharetrip.bus.booking.view.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.formatToTwoDigit
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusHomeBinding
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.*
import java.util.*

class BusHomeFragment : BaseFragment<FragmentBusHomeBinding>() {

    private val viewModel: BusHomeViewModel by viewModels{
        BusHomeViewModelFactory(
            BusHomeRepository(DataManager.busBookingApiService)
        )
    }

    override fun layoutId() = R.layout.fragment_bus_home

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setSpannedTitle(getString(R.string.bus))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.departureDateLayout.setOnClickListener {
            onClickDate()
        }

        viewModel.showMsg.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        getNavigationResultLiveData<Bundle>(PICK_FROM_ADDRESS_REQUEST)?.observe(viewLifecycleOwner) {
            val stationName = it.getString(EXTRA_STATION_NAME)
            val stationCode = it.getString(EXTRA_STATION_CODE)
            viewModel.originName.set(stationName?.replaceFirstChar { string-> string.uppercase()})
            viewModel.originStationCode.set(stationCode)
            viewModel.destinationName.set("")
        }

        getNavigationResultLiveData<Bundle>(PICK_TO_ADDRESS_REQUEST)?.observe(viewLifecycleOwner) {
            val stationName = it.getString(EXTRA_STATION_NAME)
            val stationCode = it.getString(EXTRA_STATION_CODE)
            viewModel.destinationName.set(stationName?.replaceFirstChar { string-> string.uppercase()})
            viewModel.destinationCode.set(stationCode)
        }

        viewModel.navigateToBusSearch.observe(viewLifecycleOwner, EventObserver{
            val bundle = Bundle()

            if (it.first == getString(R.string.from)){
                bundle.putString(ARG_SEARCH_FROM_WHERE, it.first)
                bundle.putString(ARG_ORIGIN_STATION_CODE, "")
            }else{
                bundle.putString(ARG_SEARCH_FROM_WHERE, it.first)
                bundle.putString(ARG_ORIGIN_STATION_CODE, viewModel.originStationCode.get())
            }

            findNavController().navigate(R.id.action_busHomeFragment_to_busSearchFragment, bundle)
        })

        viewModel.navigateToBusList.observe(viewLifecycleOwner, EventObserver{
            val bundle = bundleOf(
                ARG_DEPARTURE_DATE to viewModel.departureDate.get(),
                ARG_DESTINATION_CITY to viewModel.destinationCode.get(),
                ARG_ORIGIN_CITY to viewModel.originStationCode.get(),
                ARG_DESTINATION_NAME to viewModel.destinationName.get(),
                ARG_ORIGIN_NAME to viewModel.originName.get()
            )

            findNavController().navigate(R.id.action_busHomeFragment_to_busListFragment, bundle)
        })

    }

    fun onClickDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            bindingView.dateTextView.context, { _, year, monthOfYear, dayOfMonth ->
                val month = formatToTwoDigit(monthOfYear + 1)
                val day = formatToTwoDigit(dayOfMonth)
                viewModel.departureDate.set("$year-$month-$day")
            }, year, month, day
        )

        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }
}
