package net.sharetrip.holiday.booking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.holiday.R
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.holiday.utils.*

class HolidayBookingActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigationAction: HolidayNavigationActions? =
            intent.extras?.get(HOLIDAY_NAVIGATION_ACTION) as HolidayNavigationActions?

        setContentView(R.layout.holiday_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        when (navigationAction) {
            HolidayNavigationActions.VISIT_HOLIDAY_LIST -> {
                val cityName = intent.getStringExtra(ARG_HOLIDAY_CITY_NAME)
                val cityCode = intent.getStringExtra(ARG_HOLIDAY_CITY_CODE)

                navController.navigateSafe(
                    R.id.action_holidayFragment_to_holidayListFragment,
                    bundleOf(
                        ARG_HOLIDAY_CITY_NAME to cityName,
                        ARG_HOLIDAY_CITY_CODE to "\"$cityCode\""
                    )
                )
            }

            HolidayNavigationActions.VISIT_HOLIDAY_DETAILS -> {
                val productCode = intent.getStringExtra(ARG_HOLIDAY_PRODUCT_CODE)
                val bundle = bundleOf(ARG_HOLIDAY_PRODUCT_CODE to productCode)
                navController.navigateSafe(R.id.action_holidayFragment_to_holidayDetailFragment, bundle)
            }

            else -> {}
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.popBackStack() || super.onSupportNavigateUp()
    }
}
