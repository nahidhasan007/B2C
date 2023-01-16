package net.sharetrip.visa.booking.view.countrysearch

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaCountry
import net.sharetrip.visa.databinding.FragmentVisaCountrySearchBinding
import net.sharetrip.visa.network.DataManager

class VisaCountrySearchFragment : BaseFragment<FragmentVisaCountrySearchBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            VisaCountrySearchVMFactory(DataManager.visaBookingApiService)
        )[VisaCountrySearchViewModel::class.java]
    }

    private val adapter = VisaCountrySearchAdapter()

    override fun layoutId() = R.layout.fragment_visa_country_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.countryRecyclerView.adapter = adapter

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

        ItemClickSupport.addTo(bindingView.countryRecyclerView)
            .setOnItemClickListener { _, position, _ ->
                viewModel.onClickCountryItem(position)
            }

        viewModel.countryList.observe(viewLifecycleOwner) {
            adapter.updateData(it as ArrayList<VisaCountry>)
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.navigateBackWithData.observe(viewLifecycleOwner) {
            setNavigationResult(it, ARG_COUNTRY_DATA)
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
        searchMenuItem.setOnActionExpandListener(viewModel.actionExpandListener)
        val searchView = searchMenuItem.actionView as SearchView
        searchMenuItem.expandActionView()
        searchView.requestFocus()
        searchView.queryHint = getString(R.string.destination_country)
        searchView.setOnQueryTextListener(viewModel.onQueryTextListener)
    }

    companion object {
        const val EXTRA_VISA_PROPERTY = "EXTRA_VISA_PROPERTY"
        const val ARG_COUNTRY_DATA = "ARG_COUNTRY_DATA"
    }
}
