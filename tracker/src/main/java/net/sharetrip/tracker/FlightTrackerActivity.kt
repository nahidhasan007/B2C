package net.sharetrip.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI

class FlightTrackerActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_tracker)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.flight_tracker_nav)

        navGraph.startDestination = when(intent.extras?.get(FLIGHT_TRACKER_ACTION) as FlightTrackerAction){
            FlightTrackerAction.FLIGHT_TRACKER -> R.id.flightTrackingSearchFragment
            else -> R.id.travelAdviceSearchFragment
        }

        navController.graph = navGraph
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return  navController.popBackStack() || super.onSupportNavigateUp()
    }

    companion object{
        const val FLIGHT_TRACKER_ACTION = "FLIGHT_TRACKER_ACTION"
        enum class FlightTrackerAction { FLIGHT_TRACKER, TRIP_ADVISOR }
    }
}