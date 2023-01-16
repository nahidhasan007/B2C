package net.sharetrip.tour.booking.search

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.booking.list.TourListFragment.Companion.ARG_TOUR_CITY_SEARCH_MODEL
import net.sharetrip.tour.databinding.FragmentTourCitySearchBinding
import net.sharetrip.tour.model.TourPopCity
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.hideTripCoin

class TourCitySearchFragment : BaseFragment<FragmentTourCitySearchBinding>() {

    private val viewModel: TourCitySearchViewModel by viewModels {
        TourCitySearchVMF(TourCitySearchRepo(TourDataManager.tourBookingAPIService))
    }

    private val adapter = TourCitySearchAdapter()

    override fun layoutId(): Int = R.layout.fragment_tour_city_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setHasOptionsMenu(true)
        hideTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.cityRecyclerView.adapter = adapter

        viewModel.cities.observe(viewLifecycleOwner) {
            adapter.updateData(it as ArrayList<TourPopCity>)
        }

        ItemClickSupport.addTo(bindingView.cityRecyclerView)
            .setOnItemClickListener { recyclerView, position, v ->
                findNavController().navigate(
                    R.id.action_tourCitySearchFragment_to_tourListFragment,
                    bundleOf(ARG_TOUR_CITY_SEARCH_MODEL to viewModel.cities.value!![position])
                )
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bundle_search, menu)
        val menuItem = menu.findItem(R.id.action_search)
        setupSearchView(menuItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchMenuItem.setOnActionExpandListener(actionExpandListener)
        val searchView = searchMenuItem.actionView as SearchView
        searchMenuItem.expandActionView()
        searchView.requestFocus()
        searchView.queryHint = "Destination City"
        searchView.setOnQueryTextListener(onQueryTextListener)
    }

    val actionExpandListener: MenuItem.OnActionExpandListener =
        object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                findNavController().navigateUp()
                return false
            }
        }

    val onQueryTextListener: SearchView.OnQueryTextListener =
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

    companion object {
        val SELECTED_TOUR_CITY = "SELECTED_TOUR_CITY"
    }
}
