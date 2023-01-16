package net.sharetrip.flight.booking.view.searchairport

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.view.multicity.MultiCityFragment.Companion.ARG_CLASS_NAME
import net.sharetrip.flight.booking.view.oneway.OneWayFragment.Companion.ARG_FLIGHT_SEARCH_BUNDLE
import net.sharetrip.flight.booking.view.oneway.OneWayFragment.Companion.IS_ORIGIN_OR_DESTINATION
import net.sharetrip.flight.booking.view.searchairport.data.AirportDao
import net.sharetrip.flight.booking.view.searchairport.data.ShareTripDatabase
import net.sharetrip.flight.databinding.FragmentSearchAirportBinding
import net.sharetrip.flight.network.DataManager
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class SearchAirportFragment : BaseFragment<FragmentSearchAirportBinding>(),
    MenuItem.OnActionExpandListener, SearchView.OnQueryTextListener {
    private val airportAdapter = AirportAdapter()
    private lateinit var airportDao: AirportDao
    private lateinit var viewModel: SearchAirportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database: ShareTripDatabase = ShareTripDatabase.getInstance(
            requireContext()
        )!!
        airportDao = database.airportDao()

        viewModel = SearchAirportViewModelFactory(
            SearchAirportRepo(DataManager.flightApiService, airportDao)
        ).create(SearchAirportViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.fragment_search_airport

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.flight_search, menu)
                val searchMenuItem = menu.findItem(R.id.action_search)
                setupSearchView(searchMenuItem)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bindingView.airportRecyclerView.adapter = airportAdapter
        val mItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        bindingView.airportRecyclerView.addItemDecoration(mItemDecoration)

        viewModel.airportList.observe(viewLifecycleOwner) {
            airportAdapter.resetItems(it)
            airportAdapter.filter(" ")
        }

        ItemClickSupport.addTo(bindingView.airportRecyclerView)
            .setOnItemClickListener { _, position, _ ->
                if (position >= 0) {
                    val intent = Intent()
                    val airport = airportAdapter.getItem(position)
                    intent.apply {
                        putExtra(
                            IS_ORIGIN_OR_DESTINATION,
                            arguments?.getBundle(ARG_FLIGHT_SEARCH_BUNDLE)?.getBoolean(
                                IS_ORIGIN_OR_DESTINATION
                            )
                        )
                        putExtra(ARG_AIRPORT_CODE, airport.iata)
                        putExtra(ARG_AIRPORT_CITY, airport.city)
                        putExtra(ARG_AIRPORT_ADDRESS, airport.name)
                    }
                    setNavigationResult(
                        intent,
                        arguments?.getBundle(ARG_FLIGHT_SEARCH_BUNDLE)
                            ?.getString(ARG_CLASS_NAME, "")!!
                    )
                    findNavController().navigateUp()
                    viewModel.handleSelectedItem(airportAdapter.getItem(position))
                }
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

    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        findNavController().navigateUp()
        return false
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!newText.isNullOrEmpty() && newText.length >= 3) {
            viewModel.getAirportListByName(newText)
            airportAdapter.filter(newText)
        } else if (!newText.isNullOrEmpty()) {
            airportAdapter.filter(newText)
        } else if (newText.isNullOrEmpty()) {
            viewModel.fetchTopAirportListWithLocal()
        }
        return false
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchMenuItem.setOnActionExpandListener(this)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.origin_city_or_Airport)
        searchMenuItem.expandActionView()
        searchView.requestFocus()
        searchView.setOnQueryTextListener(this)
    }

    companion object {
        const val ARG_AIRPORT_CODE = "ARG_AIRPORT_CODE"
        const val ARG_AIRPORT_CITY = "ARG_AIRPORT_CITY"
        const val ARG_AIRPORT_ADDRESS = "ARG_AIRPORT_ADDRESS"
    }
}
