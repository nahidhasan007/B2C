package net.sharetrip.tour.booking.main

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.event.EventObserver
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.sharetrip.tour.R
import net.sharetrip.tour.booking.detail.TourDetailFragment.Companion.ARG_TOUR_PRODUCT_CODE
import net.sharetrip.tour.booking.list.TourListFragment.Companion.ARG_TOUR_CITY_SEARCH_MODEL
import net.sharetrip.tour.booking.search.TourCitySearchFragment.Companion.SELECTED_TOUR_CITY
import net.sharetrip.tour.databinding.FragmentTourMainBinding
import net.sharetrip.tour.model.TourPopCity
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.setTitleAndSubtitle
import net.sharetrip.tour.utils.setTripCoin

class TourMainFragment : BaseFragment<FragmentTourMainBinding>() {

    private val viewModel: TourMainViewModel by viewModels {
        TourMainVMF(TourMainRepo(TourDataManager.tourBookingAPIService))
    }

    private lateinit var tourAdapter: TourAdapter

    override fun layoutId() = R.layout.fragment_tour_main

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.tours))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        tourAdapter = TourAdapter(viewModel)
        bindingView.listTours.adapter = tourAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagedListTour.collectLatest {
                tourAdapter.submitData(it)
            }
        }

        viewModel.navigateToDestinations.observe(viewLifecycleOwner, EventObserver {
            when(it.first){
                TourMainViewModel.TourMainNavDestinations.TO_TOUR_LIST ->{
                    val index = it.second as Int
                    findNavController().navigate(R.id.action_tourMainFragment_to_tourListFragment, bundleOf(ARG_TOUR_CITY_SEARCH_MODEL to viewModel.liveCities.value!![index]))
                }
                TourMainViewModel.TourMainNavDestinations.TO_CITY_SEARCH ->{
                    findNavController().navigate(R.id.action_tourMainFragment_to_tourCitySearchFragment)
                }
            }
        })

        getNavigationResultLiveData<TourPopCity>(SELECTED_TOUR_CITY)?.observe(viewLifecycleOwner){

        }

        ItemClickSupport.addTo(bindingView.listTours)
            .setOnItemClickListener { recyclerView, position, v ->
                if (position > 0)
                    findNavController().navigate(
                        R.id.action_tourMainFragment_to_tourDetailFragment,
                        bundleOf(ARG_TOUR_PRODUCT_CODE to tourAdapter.snapshot()[position - 1]!!.productCode)
                    )
            }
    }
}
