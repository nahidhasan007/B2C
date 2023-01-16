package net.sharetrip.flight.booking.view.travellerdetails

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.sharetrip.base.event.Event
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.ItemTraveler
import net.sharetrip.flight.booking.model.coupon.CouponResponse
import net.sharetrip.flight.booking.model.flightresponse.DiscountModel
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_COUPON_RESPONSE
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_DISCOUNT_MODEL
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_FLIGHTS
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_FLIGHT_DETAILS_DATA
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_FLIGHT_SEARCH
import net.sharetrip.flight.booking.view.passenger.PassengerFragment.Companion.FLIGHT_DATE_PASSENGERS
import net.sharetrip.flight.booking.view.travellerdetails.adapter.TravelerAddAdapter
import net.sharetrip.flight.databinding.FragmentTravelerDetailsOfFlightBinding
import net.sharetrip.shared.utils.*

class TravelerDetailsFragment : BaseFragment<FragmentTravelerDetailsOfFlightBinding>() {

    private var travelerAddAdapter: TravelerAddAdapter = TravelerAddAdapter()
    private var adapterPosition = -1

    private val viewModel by lazy {
        TravelerDetailsViewModelFactory(
            requireArguments().getBundle(ARG_FLIGHT_DETAILS_DATA)
                ?.getParcelable(ARG_FLIGHT_SEARCH)!!, travelerAddAdapter
        ).create(
            TravelerDetailsViewModel::class.java
        )
    }

    private val travelerDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            val mItemTravelers = travelerAddAdapter.dataSet
            var validData = true
            for (miItemTraveler in mItemTravelers) {
                validData = miItemTraveler.isValidData
                if (!validData) break
            }
            viewModel.updatePassengerList(mItemTravelers as ArrayList<ItemTraveler>)
            bindingView.continueButton.isEnabled = validData
        }
    }

    override fun onStart() {
        super.onStart()
        travelerAddAdapter.registerAdapterDataObserver(travelerDataObserver)
        setTitleAndSubtitle(getString(R.string.title_activity_flight_checkout))
    }

    override fun onStop() {
        super.onStop()
        travelerAddAdapter.unregisterAdapterDataObserver(travelerDataObserver)
    }

    override fun layoutId(): Int = R.layout.fragment_traveler_details_of_flight

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        val flightSearch: FlightSearch = requireArguments().getBundle(ARG_FLIGHT_DETAILS_DATA)
            ?.getParcelable(ARG_FLIGHT_SEARCH)!!

        initUi()

        viewModel.onAddTravelerDetailsClicked.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            bundle.putParcelable(
                ARG_FLIGHTS,
                requireArguments().getBundle(ARG_FLIGHT_DETAILS_DATA)?.getParcelable(ARG_FLIGHTS)
            )
            bundle.putString(FLIGHT_DATE_PASSENGERS, flightSearch.depart[0])
            bundle.putParcelable(ARG_TRAVELLER, travelerAddAdapter.getItem(it))
            bundle.putInt(ARG_ADAPTER_POSITION, it)

            findNavController().navigateSafe(
                R.id.action_travelerAdd_to_passenger, bundleOf(
                    ARG_TRAVELLER_DATA to bundle,
                    ARG_FLIGHT_SEARCH to flightSearch
                )
            )
        })

        getNavigationResultLiveData<ItemTraveler>("itemTraveler")?.observe(viewLifecycleOwner) { traveller: ItemTraveler ->
            if (adapterPosition >= 0) {
                travelerAddAdapter.getItem(adapterPosition).apply {
                    givenName = traveller.givenName ?: ""
                    surName = traveller.surName
                    passportCopy = traveller.passportCopy
                    visaCopy = traveller.visaCopy
                    dateOfBirth = traveller.dateOfBirth
                    nationality = traveller.nationality
                    passportNumber = traveller.passportNumber
                    passportExpireDate = traveller.passportExpireDate
                    isValidData = true
                    gender = traveller.gender
                    titleName =
                        traveller.gender.getUserTitleForFlight(
                            traveller.dateOfBirth!!,
                            flightSearch.depart[0]
                        )
                    wheelChair = traveller.wheelChair
                    mealPreference = traveller.mealPreference
                    covid = traveller.covid
                    covidTestOption = traveller.covidTestOption
                    wheelChairText = traveller.wheelChairText
                    mealPreferenceText = traveller.mealPreferenceText
                }

                travelerAddAdapter.notifyItemChanged(adapterPosition)
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<ItemTraveler>("itemTraveler")
            }
        }

        viewModel.onContinueClicked.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            val flights = requireArguments().getBundle(ARG_FLIGHT_DETAILS_DATA)
                ?.getParcelable<Flights>(ARG_FLIGHTS)
            val discountModel = requireArguments().getBundle(ARG_FLIGHT_DETAILS_DATA)
                ?.getParcelable<DiscountModel>(ARG_DISCOUNT_MODEL)
            val couponResponse = requireArguments().getBundle(ARG_FLIGHT_DETAILS_DATA)
                ?.getParcelable<CouponResponse>(ARG_COUPON_RESPONSE)
            bundle.putParcelableArrayList(ARG_ALL_TRAVELLER, viewModel.passengerList)
            bundle.putParcelable(ARG_FLIGHT_SEARCH, flightSearch)
            bundle.putParcelable(ARG_FLIGHTS, flights)
            bundle.putParcelable(ARG_DISCOUNT_MODEL, discountModel)
            bundle.putParcelable(ARG_COUPON_RESPONSE, couponResponse)
            findNavController().navigateSafe(
                R.id.action_travelerAdd_to_infoVerification,
                bundleOf(ARG_ALL_TRAVELLER_AND_DATA to bundle)
            )
        })
    }

    private fun initUi() {
        setTitleAndSubtitle(getString(R.string.title_activity_flight_checkout))
        bindingView.continueButton.visibility = View.VISIBLE
        initTravelerRecyclerView()
        bindingView.viewModel = viewModel
    }

    private fun initTravelerRecyclerView() {
        bindingView.travelerAddRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        bindingView.travelerAddRecyclerView.layoutManager = mLayoutManager
        bindingView.travelerAddRecyclerView.adapter = travelerAddAdapter
        val mDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        val mItemDrawable =
            ContextCompat.getDrawable(requireActivity(), R.drawable.space_item_decorator_vertical)

        mDecoration.setDrawable(mItemDrawable!!)
        bindingView.travelerAddRecyclerView.addItemDecoration(mDecoration)

        ItemClickSupport.addTo(bindingView.travelerAddRecyclerView)
            .setOnItemClickListener { _: RecyclerView?, position: Int, _: View? ->
                adapterPosition = position
                viewModel.onAddTravelerDetailsClicked.value = Event(position)
            }
    }

    companion object {
        const val ARG_ADAPTER_POSITION = "ARG_ADAPTER_POSITION"
        const val ARG_TRAVELLER_DATA = "ARG_TRAVELLER_DATA"
        const val ARG_FLIGHT_SEARCH = "ARG_FLIGHT_SEARCH"
        const val ARG_ALL_TRAVELLER = "ARG_ALL_TRAVELLER"
        const val ARG_ALL_TRAVELLER_AND_DATA = "ARG_ALL_TRAVELLER_AND_DATA"
    }
}
