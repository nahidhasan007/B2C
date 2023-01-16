package net.sharetrip.flight.booking.view.flighthome

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.TripType
import net.sharetrip.flight.databinding.FlightHomeBinding
import net.sharetrip.flight.utils.setTitleSubTitle
import net.sharetrip.flight.utils.setTripCoin
import net.sharetrip.shared.view.BaseFragment
import java.text.NumberFormat
import java.util.*

class FragmentFlightHome : BaseFragment<FlightHomeBinding>() {
    private val viewModel: FragmentHomeViewModel by viewModels()

    var isDeepLinkHandled = false
    override fun layoutId(): Int = R.layout.flight_home

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.pager.adapter = FlightHomeAdapter(this)

        TabLayoutMediator(bindingView.tabLayout, bindingView.pager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    override fun onStart() {
        super.onStart()
        setTitleSubTitle(getString(R.string.flight))

        var points = SharedPrefsHelper(requireContext())[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }

        if (points.isBlank()) {
            points = "0"
            SharedPrefsHelper(requireContext()).put(PrefKey.USER_TRIP_COIN, "0")
        }

        setTripCoin(NumberFormat.getNumberInstance(Locale.US).format(points.toInt()))

        handleDeepLink()
    }

    private fun handleDeepLink() {
        val uri = requireActivity().intent.data
        if (uri != null && !isDeepLinkHandled) {
            try {
                val tripType = uri.getQueryParameter("tripType")

                if (tripType.equals(TripType.ONE_WAY,true)) {
                    bindingView.tabLayout.selectTab(bindingView.tabLayout.getTabAt(0))
                }else if(tripType.equals(TripType.ROUND_TRIP, true)){
                    bindingView.tabLayout.selectTab(bindingView.tabLayout.getTabAt(1))
                }else {
                    bindingView.tabLayout.selectTab(bindingView.tabLayout.getTabAt(2))
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Invalid Search Input! Try filling all fields", Toast.LENGTH_SHORT).show()
            }
            isDeepLinkHandled = true
        }
    }

    private fun getTabTitle(position: Int): String {
        return when (position) {
            0 -> getString(R.string.one_way)
            1 -> getString(R.string.round_trip)
            2 -> getString(R.string.multi_city)
            else -> throw IndexOutOfBoundsException()
        }
    }
}
