package net.sharetrip.view.profile2.dashboard2

import android.content.Intent
import net.sharetrip.databinding.FragmentProfileDashboard2Binding
import com.google.android.material.tabs.TabLayoutMediator
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.view.dashboard.DashboardActivity
import net.sharetrip.view.profile.dashboard.ProfilePagerAdapter2

class ProfileDashboardFragment2 : BaseFragment<FragmentProfileDashboard2Binding>(){



    override fun layoutId(): Int = R.layout.fragment_profile_dashboard2

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {

        bindingView.viewPager2.adapter = ProfilePagerAdapter2(this)
        TabLayoutMediator(bindingView.tabLayout, bindingView.viewPager2) { tab, pos ->
            when(pos) {
                0 -> tab.text = ProfilePagerAdapter2(this).getPageTitle(pos)
                1 -> tab.text = ProfilePagerAdapter2(this).getPageTitle(pos)
            }
        }.attach()

        bindingView.appBarLayout2.userInfoLayout.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, ProfileAction.ARG_PROFILE_EDIT)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).userData.observe(this) {
            (activity as DashboardActivity).setUserData(it, bindingView.appBarLayout2)
        }
    }


}