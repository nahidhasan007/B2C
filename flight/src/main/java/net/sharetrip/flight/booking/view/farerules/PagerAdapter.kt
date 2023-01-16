package net.sharetrip.flight.booking.view.farerules

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.sharetrip.shared.view.adapter.SmartFragmentStatePagerAdapter

class PagerAdapter(fragmentManager: FragmentManager, private val rules: List<String>) :
SmartFragmentStatePagerAdapter(fragmentManager) {
    var title = arrayOf("Baggage", "Refund Policy", "Fare Details")

    override fun getCount() = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> RulesDetailsFragment(rules[0], 0)
            1 -> RulesDetailsFragment(rules[1], 1)
            else -> RulesDetailsFragment(rules[2], 2)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> title[0]
            1 -> title[1]
            else -> title[2]
        }
    }
}