package net.sharetrip.holiday.history.view.detail

import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayBookingHistoryDetailBinding
import com.google.gson.Gson
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.HOLIDAY_BOOKING_STATUS
import net.sharetrip.shared.utils.HOLIDAY_PAYMENT_STATUS
import net.sharetrip.shared.utils.RecyclerItemDecoration
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.history.model.HotelInfo
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.*

class HolidayBookingDetailFragment : BaseFragment<FragmentHolidayBookingHistoryDetailBinding>() {

    private val viewModel: HolidayBookingDetailViewModel by viewModels{
        val bookingCode = arguments?.getString(ARG_HOLIDAY_HISTORY_ITEM_BOOKING_CODE)
        HolidayBookingDetailViewModelFactory(
            bookingCode!!,
            DataManager.getSharedPref(requireContext()),
            HolidayBookingDetailRepository(DataManager.holidayHistoryApiService)
        )
    }

    override fun layoutId() = R.layout.fragment_holiday_booking_history_detail

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.booking_details))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.bookingDetails.observe(viewLifecycleOwner) {
            bindingView.bookingDetails = it
            bindingView.executePendingBindings()
        }

        viewModel.bookingStatus.observe(viewLifecycleOwner) {
            bindingView.textViewStatus.text = it
            bindingView.textViewStatus.setTextColor(
                ContextCompat.getColor(
                    bindingView.textViewStatus.context,
                    R.color.mid_green
                )
            )

            when (it) {
                HOLIDAY_BOOKING_STATUS.CANCELLED.value -> {
                    bindingView.textViewStatus.setTextColor(
                        ContextCompat.getColor(
                            bindingView.textViewStatus.context,
                            R.color.error_color
                        )
                    )
                }

                HOLIDAY_BOOKING_STATUS.PROCESSING.value -> {
                    bindingView.textViewStatus.text = it
                    if ((it == HOLIDAY_PAYMENT_STATUS.UNPAID.value)) {
                        bindingView.textViewStatus.setTextColor(
                            ContextCompat.getColor(
                                bindingView.textViewStatus.context,
                                R.color.error_color
                            )
                        )
                    }
                }
            }
        }

        viewModel.hotels.observe(viewLifecycleOwner) {
            try {
                val hotels = Gson().fromJson(it, Array<HotelInfo>::class.java).toList()
                bindingView.recyclerCityNHotel.layoutManager = GridLayoutManager(context, 2)
                val adapter = CustomAdapter(hotels)
                bindingView.recyclerCityNHotel.adapter = adapter
                bindingView.recyclerCityNHotel.addItemDecoration(
                    RecyclerItemDecoration(
                        requireContext(),
                        6
                    )
                )
                bindingView.recyclerCityNHotel.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel.navigateToHolidayInfo.observe(viewLifecycleOwner, EventObserver {
            val bundle =
                bundleOf(ARG_HOLIDAY_HISTORY_BOOKED_ITEM to viewModel.bookingDetails.value!!.bookedPackage)
            findNavController().navigateSafe(
                R.id.action_holidayBookingDetailFragment_to_holidayInfoFragment,
                bundle
            )
        })

        viewModel.navigateToPriceInfo.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(
                ARG_HOLIDAY_TOTAL_AMOUNT to viewModel.bookingDetails.value!!.totalAmount.toString(),
                ARG_HOLIDAY_PAID_AMOUNT to viewModel.bookingDetails.value!!.promotionalAmount.toString(),
                ARG_HOLIDAY_CONV_AMOUNT to viewModel.bookingDetails.value!!.convenienceFee.toString()

            )
            findNavController().navigateSafe(
                R.id.action_holidayBookingDetailFragment_to_holidayHistoryPricingFragment,
                bundle
            )
        })

        viewModel.navigateToHolidayContact.observe(viewLifecycleOwner, EventObserver {
            val bundle =
                bundleOf(ARG_HOLIDAY_HISTORY_PRIMARY_CONTACT to viewModel.bookingDetails.value!!.primaryContact)
            findNavController().navigateSafe(
                R.id.action_holidayBookingDetailFragment_to_holidayContactFragment,
                bundle
            )
        })

        viewModel.navigateToCancelPolicy.observe(viewLifecycleOwner, EventObserver {
            val bundle =
                bundleOf(ARG_CANCEL_POLICY to viewModel.bookingDetails.value!!.bookedPackage.cancellationPolicy)
            findNavController().navigateSafe(
                R.id.action_holidayBookingDetailFragment_to_holidayHistoryCancelPolicyFragment,
                bundle
            )
        })

        viewModel.navigateToDashboard.observe(viewLifecycleOwner, EventObserver{
            requireActivity().finish()
        })
    }
}
