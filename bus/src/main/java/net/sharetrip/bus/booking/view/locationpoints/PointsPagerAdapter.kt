package net.sharetrip.bus.booking.view.locationpoints

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.sharetrip.shared.view.adapter.SmartFragmentStatePagerAdapter

class PointsPagerAdapter(
    fragmentManager: FragmentManager,
    val viewModel: SelectLocationPointsViewModel
) : SmartFragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(mPosition: Int): Fragment {
        return when (mPosition) {
            0 -> BoardingPointsFragment(viewModel)

            else -> DroppingPointsFragment(viewModel)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Boarding Points"

            else -> "Dropping Points"
        }
    }

    override fun getCount() = 2
}
