package net.sharetrip.tour.booking.list

import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.booking.detail.TourDetailFragment
import net.sharetrip.tour.databinding.FragmentTourListBinding
import net.sharetrip.tour.model.TourPopCity
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.setTitleAndSubtitle
import net.sharetrip.tour.utils.setTripCoin

class TourListFragment : BaseFragment<FragmentTourListBinding>() {

    private val viewModel: TourListViewModel by viewModels {
        val city = arguments?.getParcelable<TourPopCity>(ARG_TOUR_CITY_SEARCH_MODEL)
        TourListVMF(city!!, TourListRepo(TourDataManager.tourBookingAPIService))
    }

    private val adapter = TourListAdapter()

    override fun layoutId(): Int = R.layout.fragment_tour_list

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setHasOptionsMenu(true)
        setTitleAndSubtitle(getString(R.string.tours))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.listTours.adapter = adapter

        viewModel.pagedListTour.observe(viewLifecycleOwner) {
            adapter.submitData(this.lifecycle, it)
        }

        viewModel.navigateToCitySearch.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(R.id.action_tourListFragment_to_tourCitySearchFragment)
        })

        adapter.addLoadStateListener {
            if( it.append.endOfPaginationReached)
            if (adapter.itemCount == 0){
                Toast.makeText(requireContext(), "No Tours Found!", Toast.LENGTH_SHORT).show()
            }
        }

        ItemClickSupport.addTo(bindingView.listTours)
            .setOnItemClickListener { recyclerView, position, v ->
                if (NetworkUtil.hasNetwork(requireContext())) {
                    findNavController().navigate(
                        R.id.action_tourListFragment_to_tourDetailFragment, bundleOf(
                            TourDetailFragment.ARG_TOUR_PRODUCT_CODE to adapter.snapshot()[position]!!.productCode
                        )
                    )
                } else {
                    Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        val ARG_TOUR_CITY_SEARCH_MODEL = "ARG_TOUR_CITY_SEARCH_MODEL"
    }
}
