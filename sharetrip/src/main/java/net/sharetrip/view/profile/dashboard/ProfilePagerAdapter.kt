package net.sharetrip.view.profile.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.sharetrip.view.profile.loyalty.LoyaltyFragment
import net.sharetrip.view.profile.mainprofile.ProfileMainFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ProfileMainFragment()
            }
            else -> {
                LoyaltyFragment()
            }
        }
    }

    override fun getItemCount(): Int = 2

    fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> ProfileType.PROFILE.name
            else -> ProfileType.LOYALTY.name
        }
    }
}