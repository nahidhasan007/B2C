package net.sharetrip.holiday.booking.view.reserve

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayReservationBinding
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.model.HolidayOffer
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.booking.model.RoomTypeGuestCount
import net.sharetrip.holiday.utils.ARG_HOLIDAY_OFFER_MODEL
import net.sharetrip.holiday.utils.ARG_HOLIDAY_PARAM_MODEL
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import java.text.SimpleDateFormat
import java.util.*

class HolidayReserveFragment : BaseFragment<FragmentHolidayReservationBinding>() {

    private val viewModel: HolidayReserveViewModel by viewModels {
        HolidayReserveViewModelFactory(param, holidayOffer)
    }

    private var navigate = true
    private lateinit var param: HolidayParam
    private lateinit var holidayOffer: HolidayOffer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        param = arguments?.get(ARG_HOLIDAY_PARAM_MODEL) as HolidayParam
        holidayOffer = arguments?.get(ARG_HOLIDAY_OFFER_MODEL) as HolidayOffer
    }

    override fun layoutId() = R.layout.fragment_holiday_reservation

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SimpleDateFormat")
    override fun initOnCreateView() {
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.holiday = holidayOffer

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.done, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_done == menuItem.itemId) {
                    viewModel.onClickSaveMenu()
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val minDate = holidayOffer.periodFrom?.let { sdf.parse(it) }
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val diffInMillies = minDate!!.time - today.time
            val diff = diffInMillies / (1000 * 60 * 60 * 24)

            if (diff < param.releaseTime) {
                calendar.add(Calendar.DATE, param.releaseTime)
            }
        } catch (e: Exception) {
            e.fillInStackTrace()
        }

        viewModel.msg.observe(viewLifecycleOwner, EventObserver {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                navigate = true
            }
        })

        viewModel.updated.observe(viewLifecycleOwner) {
            if (it == true) {
                setGuestAdapter()
            }
        }

        viewModel.navigateToCalender.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(ARG_CALENDER_DATA to viewModel.calenderData)
            findNavController().navigateSafe(
                R.id.action_holidayReserveFragment_to_holidayDateCalenderFragment,
                bundle
            )
        })

        viewModel.navigateToBooking.observe(viewLifecycleOwner, EventObserver {
            val isParamOk = viewModel.setBookingParams(
                bindingView.textFiledTravelDate.text.toString(),
                bindingView.textFieldSingleRoom.text.toString(),
                bindingView.textFieldDoubleRoom.text.toString(),
                bindingView.textFieldTwinRoom.text.toString(),
                bindingView.textFieldTripleRoom.text.toString(),
                bindingView.textFieldQuadRoom.text.toString(),

                bindingView.textFieldNumberOfAdult.text.toString(),
                bindingView.textFieldChild7to12.text.toString(),
                bindingView.textFieldChild3to6.text.toString(),
                bindingView.textFieldInfant.text.toString()
            )

            if (isParamOk) {
                val bundle = bundleOf(ARG_HOLIDAY_PARAM_MODEL to viewModel.param)
                findNavController().navigateSafe(
                    R.id.action_holidayReserveFragment_to_holidayBookingFragment,
                    bundle
                )
            }
        })

        getNavigationResultLiveData<Long>(ARG_SINGLE_DATE)?.observe(viewLifecycleOwner) { travelDateInMills ->
            bindingView.textFiledTravelDate.setText(
                DateUtil.parseDisplayCommonDatePatternFromMillisecond(
                    travelDateInMills
                )
            )
        }

        val numberList = requireContext().resources.getStringArray(R.array.package_room_number)
        val roomAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, numberList)
        bindingView.textFieldSingleRoom.setAdapter(roomAdapter)
        bindingView.textFieldSingleRoom.setText("0")
        bindingView.textFieldDoubleRoom.setAdapter(roomAdapter)
        bindingView.textFieldDoubleRoom.setText("0")
        bindingView.textFieldTwinRoom.setAdapter(roomAdapter)
        bindingView.textFieldTwinRoom.setText("0")
        bindingView.textFieldTripleRoom.setAdapter(roomAdapter)
        bindingView.textFieldTripleRoom.setText("0")
        bindingView.textFieldQuadRoom.setAdapter(roomAdapter)
        bindingView.textFieldQuadRoom.setText("0")
    }

    override fun onStart() {
        super.onStart()
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }

    private fun setGuestAdapter() {
        val adult = bindingView.textFieldSingleRoom.text.toString()
            .toInt() * RoomTypeGuestCount.singleRoom +
                bindingView.textFieldDoubleRoom.text.toString()
                    .toInt() * RoomTypeGuestCount.doubleRoom +
                bindingView.textFieldTwinRoom.text.toString()
                    .toInt() * RoomTypeGuestCount.twinRoom +
                bindingView.textFieldTripleRoom.text.toString()
                    .toInt() * RoomTypeGuestCount.tripleRoom +
                bindingView.textFieldQuadRoom.text.toString().toInt() * RoomTypeGuestCount.quadRoom

        val adultList = ArrayList<Int>()
        for (i in 1..adult) adultList.add(i)

        val guestAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, adultList
        )
        bindingView.textFieldNumberOfAdult.setAdapter(guestAdapter)

        val infant = bindingView.textFieldDoubleRoom.text.toString()
            .toInt() + bindingView.textFieldTwinRoom.text.toString().toInt() +
                bindingView.textFieldTripleRoom.text.toString().toInt()
        val infantList = ArrayList<Int>()
        for (i in 0..infant) infantList.add(i)
        val infantAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, infantList)

        bindingView.textFieldInfant.setAdapter(infantAdapter)
        bindingView.textFieldChild3to6.setAdapter(infantAdapter)

        val children = bindingView.textFieldDoubleRoom.text.toString()
            .toInt() + bindingView.textFieldTwinRoom.text.toString().toInt()
        val childList = ArrayList<Int>()
        for (i in 0..children) childList.add(i)
        val childAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, infantList)
        bindingView.textFieldChild7to12.setAdapter(childAdapter)
    }
}
