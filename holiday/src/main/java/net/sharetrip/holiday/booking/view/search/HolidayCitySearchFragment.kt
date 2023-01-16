package net.sharetrip.holiday.booking.view.search

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayCitySearchBinding
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.model.HolidayCity
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.*
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class HolidayCitySearchFragment : BaseFragment<FragmentHolidayCitySearchBinding>() {

    private val viewModel: HolidayCitySearchViewModel by viewModels {
        val position = requireArguments().getInt(ARG_HOLIDAY_CITY_POSITION)
        HolidayCitySearchViewModelFactory(
            position,
            HolidayCitySearchRepository(DataManager.holidayBookingApiService)
        )
    }

    private val adapter = HolidayCitySearchAdapter()

    private val onQueryTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.length >= 3) {
                    viewModel.fetchTourCityList(newText)
                }
                return false
            }
        }

    private val actionExpandListener: MenuItem.OnActionExpandListener =
        object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                findNavController().navigateUp()
                return false
            }
        }

    override fun layoutId(): Int = R.layout.fragment_holiday_city_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = this
        bindingView.cityRecyclerView.adapter = adapter

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.bundle_search, menu)
                val menuItem = menu.findItem(R.id.action_search)
                setupSearchView(menuItem)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.cities.observe(viewLifecycleOwner) {
            adapter.updateData(it as ArrayList<HolidayCity>)
        }

        ItemClickSupport.addTo(bindingView.cityRecyclerView)
            .setOnItemClickListener { _, position, _ ->
                val bundle = bundleOf(
                    ARG_HOLIDAY_CITY_CODE to viewModel.cities.value!![position].code,
                    ARG_HOLIDAY_CITY_NAME to viewModel.cities.value!![position].name,
                    ARG_HOLIDAY_DESTINATION_POSITION to viewModel.rootPosition
                )

                setNavigationResult(bundle, RESULT_ON_DESTINATION_SELECTION)
                findNavController().navigateUp()
            }
    }

    override fun onStart() {
        super.onStart()
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchMenuItem.setOnActionExpandListener(actionExpandListener)
        val searchView = searchMenuItem.actionView as SearchView
        searchMenuItem.expandActionView()
        searchView.requestFocus()
        searchView.queryHint = getString(R.string.destination_city)
        searchView.setOnQueryTextListener(onQueryTextListener)
    }
}
