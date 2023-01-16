package net.sharetrip.flight.booking.view.flighthome

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.sharetrip.flight.booking.view.multicity.MultiCityFragment
import net.sharetrip.flight.booking.view.oneway.OneWayFragment
import net.sharetrip.flight.booking.view.roundtrip.RoundTripFragment

class FlightHomeAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreator: Map<Int, () -> Fragment> = mapOf(
        0 to { OneWayFragment() },
        1 to { RoundTripFragment() },
        2 to { MultiCityFragment() }
    )

    override fun getItemCount(): Int = tabFragmentsCreator.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreator[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
