package net.sharetrip.bus.booking.view.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.City
import net.sharetrip.bus.databinding.FragmentBusSearchBinding
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.*

class BusSearchFragment : BaseFragment<FragmentBusSearchBinding>(), MenuItem.OnActionExpandListener,
    SearchView.OnQueryTextListener {

    private  val viewModel: BusSearchViewModel by viewModels{
        val originStationCode = requireArguments().getString(ARG_ORIGIN_STATION_CODE)
        BusSearchViewModelFactory(originStationCode!!, BusSearchRepository(DataManager.busBookingApiService))
    }
    private val cityAdapter = BusCityAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId() = R.layout.fragment_bus_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTripCoinGone()

        val searchFromWhere = requireArguments().getString(ARG_SEARCH_FROM_WHERE)

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.recyclerViewCity.adapter = cityAdapter

        viewModel.stationList.observe(viewLifecycleOwner) {
            val cityList = arrayListOf<City>()

            it.forEach { station ->
                cityList.add(City(station.code, station.name, station.stationCode))
            }

            cityAdapter.resetItems(cityList)
            cityAdapter.filter("A")
        }

        viewModel.routes.observe(viewLifecycleOwner) {
            if (it != null) {
                val cityList = arrayListOf<City>()

                it.forEach { station ->
                    cityList.add(City(station.to.id, station.to.name, station.to.code))
                }

                cityAdapter.resetItems(cityList)
                cityAdapter.filter("A")
            }
        }

        ItemClickSupport.addTo(bindingView.recyclerViewCity)
            .setOnItemClickListener { _, position, _ ->
                val bundle = bundleOf(
                    EXTRA_STATION_NAME to cityAdapter.getItem(position).name,
                    EXTRA_STATION_CODE to cityAdapter.getItem(position).code
                )

                if (searchFromWhere == getString(R.string.from)) {
                    setNavigationResult(key = PICK_FROM_ADDRESS_REQUEST, result = bundle)
                } else {
                    setNavigationResult(key = PICK_TO_ADDRESS_REQUEST, result = bundle)
                }

                findNavController().navigateUp()
            }
    }

    override fun onCreateOptionsMenu(mMenu: Menu, mInflater: MenuInflater) {
        mInflater.inflate(R.menu.bus_search, mMenu)
        val mSearchMenuItem = mMenu.findItem(R.id.action_search)
        setupSearchView(mSearchMenuItem)
        super.onCreateOptionsMenu(mMenu, mInflater)
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
        if (newText != null) {
            var query = newText
            if (newText.isEmpty()) {
                query = " "
            }
            cityAdapter.filter(query)
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).findViewById<TextView>(R.id.text_view_trip_coin).visibility = View.VISIBLE
    }

    private fun setupSearchView(mSearchMenuItem: MenuItem) {
        mSearchMenuItem.setOnActionExpandListener(this)
        val mSearchView = mSearchMenuItem.actionView as SearchView
        mSearchMenuItem.expandActionView()
        mSearchView.requestFocus()
        mSearchView.setOnQueryTextListener(this)
        val mSearchQueryHint =
            requireArguments().getString(ARG_SEARCH_QUERY_HINT)
        mSearchView.queryHint = mSearchQueryHint
    }
}
