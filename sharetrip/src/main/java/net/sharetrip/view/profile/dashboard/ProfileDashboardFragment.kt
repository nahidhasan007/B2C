package net.sharetrip.view.profile.dashboard

import android.content.Intent
import com.google.android.material.tabs.TabLayoutMediator
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentProfileDashboardBinding
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.view.dashboard.DashboardActivity

class ProfileDashboardFragment : BaseFragment<FragmentProfileDashboardBinding>(){



    override fun layoutId(): Int = R.layout.fragment_profile_dashboard

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {

        bindingView.viewPager.adapter = ProfilePagerAdapter(this)
        TabLayoutMediator(bindingView.tabLayout, bindingView.viewPager) { tab, pos ->
            when(pos) {
                0 -> tab.text = ProfilePagerAdapter(this).getPageTitle(pos)
                1 -> tab.text = ProfilePagerAdapter(this).getPageTitle(pos)
            }
        }.attach()

        bindingView.appBarLayout.userInfoLayout.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, ProfileAction.ARG_PROFILE_EDIT)
            startActivity(intent)
        }
    }

   /* override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).userData.observe(this) {
            (activity as DashboardActivity).setUserData(it, bindingView.appBarLayout)
        }
    }

    */

    
}