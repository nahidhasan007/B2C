package net.sharetrip.bus.history.view.passengerdetails

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusHistoryPassengerDetailBinding
import net.sharetrip.bus.history.model.HistoryDetails
import net.sharetrip.bus.history.model.PassengerDetails

class BusHistoryPassengerDetailFragment : BaseFragment<FragmentBusHistoryPassengerDetailBinding>() {
    private val busHistoryData by lazy {
        requireArguments().getParcelable<HistoryDetails>(
            ARG_BUS_PASSENGER_DETAILS
        )!!
    }

    override fun layoutId(): Int = R.layout.fragment_bus_history_passenger_detail

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val detailsValues = mutableListOf<PassengerDetails>()
        detailsValues.add(
            PassengerDetails(
                getString(R.string.full_name_title),
                busHistoryData.passengerFirstName + " " + busHistoryData.passengerLastName,
                R.drawable.ic_profile_icon_of_name_side
            )
        )
        if (busHistoryData.passengerGender == "M")
            detailsValues.add(
                PassengerDetails(
                    getString(R.string.gender),
                    getString(R.string.male),
                    R.drawable.ic_male_mono
                )
            )
        else
            detailsValues.add(
                PassengerDetails(
                    getString(R.string.gender),
                    getString(R.string.female),
                    R.drawable.ic_female_mono
                )
            )

        detailsValues.add(
            PassengerDetails(
                getString(R.string.phone_number),
                busHistoryData.passengerPhone,
                R.drawable.ic_phone_mono
            )
        )
        detailsValues.add(
            PassengerDetails(
                getString(R.string.email_address),
                busHistoryData.passengerEmail,
                R.drawable.ic_email_mono
            )
        )

        bindingView.details = detailsValues
    }

    companion object {
        const val ARG_BUS_PASSENGER_DETAILS = "ARG_BUS_PASSENGER_DETAILS"
    }
}
