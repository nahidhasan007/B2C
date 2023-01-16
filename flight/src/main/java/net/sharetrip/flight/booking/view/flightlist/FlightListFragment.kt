package net.sharetrip.flight.booking.view.flightlist

import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.FlightBookingActivity
import net.sharetrip.flight.booking.model.FlightSearch
import net.sharetrip.flight.booking.model.SortingType
import net.sharetrip.flight.booking.model.TripType
import net.sharetrip.flight.booking.model.filter.FilterParams
import net.sharetrip.flight.booking.model.filter.FlightFilter
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.view.filter.FlightFilterFragment.Companion.ARG_FLIGHT_FILTER_BUNDLE
import net.sharetrip.flight.booking.view.filter.FlightFilterFragment.Companion.ARG_FLIGHT_FILTER_INFO_STRING_FLIGHT_COUNT
import net.sharetrip.flight.booking.view.filter.FlightFilterFragment.Companion.ARG_FLIGHT_FILTER_INFO_STRING_MODEL
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment
import net.sharetrip.flight.booking.view.flightlist.adapter.AirlinesAdapter
import net.sharetrip.flight.booking.view.flightlist.adapter.AirlinesListener
import net.sharetrip.flight.booking.view.flightlist.adapter.FlightAdapter
import net.sharetrip.flight.databinding.FragmentFlightListBinding
import net.sharetrip.flight.network.DataManager
import net.sharetrip.flight.utils.MoshiUtil
import net.sharetrip.flight.utils.PagingLoadStateAdapter
import net.sharetrip.flight.utils.setTripCoin
import java.io.IOException
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

class FlightListFragment : BaseFragment<FragmentFlightListBinding>(), AirlinesListener {
    private lateinit var flightSearch: FlightSearch
    private val flightsAdapter: FlightAdapter by lazy {
        FlightAdapter()
    }
    private lateinit var slideUp: Animation
    private var isFirstTime = true
    private var isFirstTimeAirline = true
    private var isFirstPage = false
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            FlightListViewModelFactory(
                flightSearch,
                FlightListRepository(
                    flightSearch,
                    DataManager.flightApiService,
                )
            )
        )[FlightListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        flightSearch = requireArguments().getParcelable(ARG_FLIGHT_LIST_DATA)!!
        if (flightSearch.classType.contains("First"))
            flightSearch.classType = "First"
    }

    override fun layoutId(): Int = R.layout.fragment_flight_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        viewModel.airlinesList.observe(viewLifecycleOwner) {
            if (isFirstTimeAirline) {
                bindingView.airlinesRecycler.adapter = AirlinesAdapter(it, this)
            }
            isFirstTimeAirline = false
        }

        viewModel.searchId.observe(viewLifecycleOwner) { s: String? ->
            flightSearch.searchId = s!!
        }

        viewModel.isFirstPage.observe(viewLifecycleOwner) { isFirstPage = it }

        viewModel.sessionId.observe(viewLifecycleOwner) { s: String? ->
            flightSearch.sessionId = s!!
        }

        viewModel.totalRecordCount.observe(viewLifecycleOwner) { count: Int? ->
            bindingView.filterTitleTextView.text = getString(R.string.flights_found, count)
            viewModel.setNumberOfFlight(count!!)
        }

        viewModel.sortingObserver.observe(viewLifecycleOwner) {
            if (it != SortingType.NONE && !isFirstTime) {
                viewModel.handleSortingFilter(
                    it.toString().lowercase(),
                    flightSearch.searchId
                )
            }
            isFirstTime = false
        }

        viewModel.filterDeal.observe(viewLifecycleOwner) { data: String ->
            if (isFirstTime) {
                when (data) {
                    SortingType.FASTEST.toString() -> {
                        viewModel.sortingObserver.value = SortingType.FASTEST
                    }
                    SortingType.CHEAPEST.toString() -> {
                        viewModel.sortingObserver.value = SortingType.CHEAPEST
                    }
                    SortingType.EARLIEST.toString() -> {
                        viewModel.sortingObserver.value = SortingType.EARLIEST
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.flightList.collectLatest {
                flightsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        viewModel.onFilterClicked.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            bundle.putString(
                ARG_FLIGHT_FILTER_INFO_STRING_MODEL, MoshiUtil.convertModelClassToString(
                    viewModel.filter, FlightFilter::class.java
                )
            )
            bundle.putInt(ARG_FLIGHT_FILTER_INFO_STRING_FLIGHT_COUNT, viewModel.flightCount)

            findNavController().navigateSafe(
                R.id.action_flightList_to_flightFilter,
                bundleOf(ARG_FLIGHT_FILTER_BUNDLE to bundle)
            )
        })

//        ItemClickSupport.addTo(bindingView.flightRecyclerView).setOnItemClickListener { _, pos, _ ->
//            findNavController().navigate(R.id.action_flightList_to_flightDetails)
//        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            ARG_FLIGHT_FILTER_BUNDLE
        )?.observe(viewLifecycleOwner) { result ->
            result?.let {
                try {
                    val params = MoshiUtil.convertStringToModelClass(
                        result.getString(ARG_FLIGHT_FILTER_INFO_STRING_MODEL),
                        FilterParams::class.java
                    )
                    viewModel.handleFlightFilter(params, flightSearch.searchId)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        bindingView.includeShimmer.shimmerLayout.startShimmer()
        initRecyclerView()
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
    }

    override fun onCodeSelect(code: String) {
        val airlineList = ArrayList<String>()
        airlineList.add(code)
        viewModel.handleAirlineFilter(airlineList, flightSearch.searchId)
    }

    override fun onStart() {
        super.onStart()
        setTitle()
        setInfoLabel()
        var points = SharedPrefsHelper(requireContext())[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }

        if (points.isBlank()) {
            points = "0"
            SharedPrefsHelper(requireContext()).put(PrefKey.USER_TRIP_COIN, "0")
        }

        setTripCoin(NumberFormat.getNumberInstance(Locale.US).format(points.toInt()))
    }

    private fun showRunningStatus() {
        bindingView.includeShimmer.shimmerLayout.visibility = View.VISIBLE
        bindingView.includeShimmer.shimmerLayout.startAnimation(slideUp)
        bindingView.flightRecyclerView.visibility = View.GONE
        bindingView.filterConstrainLayout.visibility = View.GONE
        bindingView.cardInfo.visibility = View.GONE
        bindingView.filterConstrainLayout.visibility = View.GONE
        bindingView.airlinesRecycler.visibility = View.GONE
    }

    private fun showFailedStatus(message: String) {
        bindingView.includeShimmer.shimmerLayout.visibility = View.GONE
        bindingView.includeShimmer.shimmerLayout.stopShimmer()
        bindingView.flightRecyclerView.visibility = View.GONE
        bindingView.cardInfo.visibility = View.VISIBLE
        bindingView.textViewInfoMsg.text = message
        bindingView.filterConstrainLayout.visibility = View.GONE
        bindingView.airlinesRecycler.visibility = View.GONE
    }

    private fun showDefaultStatus() {
        bindingView.includeShimmer.shimmerLayout.visibility = View.GONE
        bindingView.includeShimmer.shimmerLayout.stopShimmer()
        bindingView.flightRecyclerView.visibility = View.VISIBLE
        bindingView.filterConstrainLayout.visibility = View.VISIBLE
        bindingView.airlinesRecycler.visibility = View.VISIBLE
        bindingView.cardInfo.visibility = View.GONE
    }

    private fun initRecyclerView() {
        ItemClickSupport.addTo(bindingView.flightRecyclerView)
            .setOnItemClickListener { _: RecyclerView?, position: Int, _: View? ->
                val list = flightsAdapter.snapshot().items
                val flights = list[position]
                handleFlightItemSelect(flights)
            }
        bindingView.flightRecyclerView.adapter = flightsAdapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { flightsAdapter.retry() }
        )

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            flightsAdapter.loadStateFlow.collect { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    showRunningStatus()
                } else if (loadState.refresh is LoadState.Error && isFirstPage) {
                    (loadState.refresh as LoadState.Error).error.message?.let {
                        showFailedStatus(it)
                    }
                } else if (loadState.refresh is LoadState.NotLoading) {
                    showDefaultStatus()
                }
            }
        }
    }

    private fun setTitle() {
        val mBuilder = SpannableStringBuilder()
        val destination = flightSearch.destination[flightSearch.destination.size - 1]
        val spannableDestination = SpannableString("  $destination")
        val mGroupSpan: ImageSpan = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val myDrawable =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_dots_horizontal_24dp)
            myDrawable!!.setBounds(0, 0, myDrawable.intrinsicWidth, myDrawable.intrinsicHeight)
            ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE)
        } else {
            ImageSpan(requireContext(), R.drawable.ic_dots_horizontal_24dp)
        }

        spannableDestination.setSpan(mGroupSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        val origin = flightSearch.origin[0]
        mBuilder.append("$origin ")
        mBuilder.append(spannableDestination)
        (activity as FlightBookingActivity).supportActionBar?.title = mBuilder
    }

    private fun setInfoLabel() {
        val mBuilder = StringBuilder()
        val depart = flightSearch.depart
        if (flightSearch.tripType.equals(TripType.ONE_WAY, true)) {
            try {
                val mDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[0])
                mBuilder.append(mDate)
                    .append(" " + flightSearch.numberOfTraveller.toString() + " Traveller(s)")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            try {
                val mStartDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[0])
                val mEndDate =
                    DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart[depart.size - 1])
                mBuilder.append(mStartDate).append(" - ").append(mEndDate)
                    .append(" " + flightSearch.numberOfTraveller.toString() + " Traveller(s)")
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        (activity as FlightBookingActivity).supportActionBar?.subtitle = mBuilder
    }

    private fun handleFlightItemSelect(flights: Flights) {
        when (flights.deal) {
            "Preferred" -> flightEventManager.clickOnPreferredAirline()
            "Best Deal" -> flightEventManager.clickOnBestDeals()
            else -> flightEventManager.clickOnFlightDetails()
        }

        flightSearch.sequence = flights.sequence
        val bundle = Bundle()
        bundle.putParcelable(FlightDetailsFragment.ARG_FLIGHT_SEARCH_MODEL, viewModel.flightSearch)
        bundle.putParcelable(FlightDetailsFragment.ARG_ITEM_FLIGHTS, flights)
        findNavController().navigateSafe(
            R.id.action_flightList_to_flightDetails,
            bundleOf("ARG_SELECTED_FLIGHT" to bundle)
        )
    }

    companion object {
        const val ARG_FLIGHT_LIST_DATA = "ARG_FLIGHT_LIST_DATA"
    }
}
