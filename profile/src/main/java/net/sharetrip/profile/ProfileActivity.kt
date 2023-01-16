package net.sharetrip.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import net.sharetrip.profile.model.ProfileAction
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_FAQ_NUMBER
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_PRIVACY_POLICY
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_READ_RULES
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_TERMS_AND_CONDITION_COMMON
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_TERMS_AND_CONDITION_LOYALTY
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_TERMS_AND_CONDITION_SPIN_TO_WIN

class ProfileActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_module)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.profile_navigation)
        val startDestinationArgs: Bundle = bundleOf()

        navGraph.startDestination = when (intent.extras?.get(ARG_PROFILE_ACTION)) {
            ProfileAction.ARG_PASSENGERS_QUICK_PICK -> R.id.quickPickFragment

            ProfileAction.ARG_PROFILE_EDIT -> R.id.myUserInfoFragment

            ProfileAction.ARG_REFER_EARN -> R.id.referEarnFragment

            ProfileAction.ARG_SAVED_CARDS -> R.id.savedCardsFragment

            ProfileAction.ARG_CHANGE_PASSWORD -> R.id.passwordFragment

            ProfileAction.ARG_FAQ -> R.id.faqFragment

            ProfileAction.ARG_LEADER_BOARD -> R.id.leaderBoardFragment

            ProfileAction.ARG_USER_TRIP_COIN -> R.id.tripCoinFragment

            ProfileAction.ARG_NOTIFICATION_DETAIL -> R.id.notificationDetailFragment

            ProfileAction.ARG_REWARD_BOARD -> R.id.rewardFragment

            ProfileAction.ARG_MANAGE_DATA -> R.id.manageYourDataFragment

            ProfileAction.ARG_READ_RULES -> {
                startDestinationArgs.putInt(ARG_FAQ_NUMBER, ARG_READ_RULES)
                R.id.contentFragment
            }

            ProfileAction.ARG_TERM_AND_CONDITION_SPIN_TO_WIN -> {
                startDestinationArgs.putInt(ARG_FAQ_NUMBER, ARG_TERMS_AND_CONDITION_SPIN_TO_WIN)
                R.id.contentFragment
            }

            ProfileAction.ARG_CONTENT_PRIVACY -> {
                startDestinationArgs.putInt(ARG_FAQ_NUMBER, ARG_PRIVACY_POLICY)
                R.id.contentFragment
            }

            ProfileAction.ARG_CONTENT_TERM -> {
                startDestinationArgs.putInt(ARG_FAQ_NUMBER, ARG_TERMS_AND_CONDITION_COMMON)
                R.id.contentFragment
            }

            ProfileAction.ARG_TERM_AND_CONDITION_LOYALTY -> {
                startDestinationArgs.putInt(ARG_FAQ_NUMBER, ARG_TERMS_AND_CONDITION_LOYALTY)
                R.id.contentFragment
            }

            else -> 0
        }

        navController.setGraph(navGraph, startDestinationArgs)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.popBackStack() || super.onSupportNavigateUp()
    }

    companion object {
        const val ARG_PROFILE_ACTION = "ProfileScreen.ProfileAction"
    }
}
