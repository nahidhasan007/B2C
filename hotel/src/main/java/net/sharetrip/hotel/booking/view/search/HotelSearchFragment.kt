package net.sharetrip.hotel.booking.view.search

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.view.main.HotelBookingMainViewModel.Companion.EXTRA_HOTEL_PROPERTY
import net.sharetrip.hotel.databinding.FragmentHotelSearchBinding
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class HotelSearchFragment : BaseFragment<FragmentHotelSearchBinding>(),
    MenuItem.OnActionExpandListener {
    private val viewModel by viewModels<HotelSearchViewModel>()
    private val adapter = PropertyAdapter()

    override fun layoutId() = R.layout.fragment_hotel_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.listHotelProperty.adapter = adapter

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.hotel_search, menu)
                val menuItem = menu.findItem(R.id.search_action)
                setupSearchView(menuItem)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.properties.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }

        viewModel.goBack.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateUp()
        })

        ItemClickSupport.addTo(bindingView.listHotelProperty)
            .setOnItemClickListener { _, position, _ ->
                val property = viewModel.properties.value!![position]
                val intent = Intent()
                intent.putExtra(EXTRA_HOTEL_PROPERTY, property)
                setNavigationResult(intent, ARG_HOTEL_PROPERTY)
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

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        findNavController().navigateUp()
        return false
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchMenuItem.setOnActionExpandListener(this)
        val searchView = searchMenuItem.actionView as SearchView
        searchMenuItem.expandActionView()
        searchView.requestFocus()
        searchView.queryHint = getString(R.string.search_city_or_destination)
        searchView.setOnQueryTextListener(viewModel.onQueryTextListener)
    }

    companion object {
        const val ARG_HOTEL_PROPERTY = "ARG_HOTEL_PROPERTY"
    }
}
