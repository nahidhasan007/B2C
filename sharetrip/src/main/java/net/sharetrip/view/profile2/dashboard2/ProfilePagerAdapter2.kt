package net.sharetrip.view.profile.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.sharetrip.view.profile.loyalty.LoyaltyFragment
import net.sharetrip.view.profile.mainprofile.ProfileMainFragment
import net.sharetrip.view.profile2.dashboard2.ProfileType2
import net.sharetrip.view.profile2.loyality2.LoyalityFragment2
import net.sharetrip.view.profile2.mainprofile2.ProfileMainFragment2

class ProfilePagerAdapter2(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                ProfileMainFragment2()
            }
            else -> {
                LoyalityFragment2()
            }
        }
    }

    override fun getItemCount(): Int = 2

    fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> ProfileType2.PROFILE.name
            else -> ProfileType2.LOYALTY.name
        }
    }
}