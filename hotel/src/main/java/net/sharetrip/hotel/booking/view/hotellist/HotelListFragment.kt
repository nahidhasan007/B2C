package net.sharetrip.hotel.booking.view.hotellist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.HotelFilterData
import net.sharetrip.hotel.booking.view.hoteldetail.HotelDetailsFragment
import net.sharetrip.hotel.booking.view.hoteldetail.HotelDetailsFragment.Companion.ARG_HOTEL_DETAILS_BUNDLE
import net.sharetrip.hotel.booking.view.hotelfilter.HotelFilterFragment
import net.sharetrip.hotel.booking.view.hotelfilter.HotelFilterFragment.Companion.ARG_HOTEL_FILTER_ON_BACK_DATA_MODEL
import net.sharetrip.hotel.booking.view.hotelfilter.HotelFilterFragment.Companion.ARG_HOTEL_FILTER_TITLE
import net.sharetrip.hotel.databinding.FragmentHotelListBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.hotel.utils.HotelSearchQuery
import net.sharetrip.hotel.utils.PagingLoadStateAdapter
import net.sharetrip.hotel.utils.ShearedViewModel
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class HotelListFragment : BaseFragment<FragmentHotelListBinding>() {
    private var hotelCount: Int = 0
    private var isFirstPage: Boolean = false
    private var searchCode: String? = null
    private lateinit var subTitle: String
    private lateinit var adapter: HotelListAdapter

    private val hotelSearchQuery by lazy {
        requireArguments().getParcelable<HotelSearchQuery>(ARG_HOTEL_QUERY_MODEL)!!
    }

    private val shearedViewModel by lazy {
        ViewModelProvider(requireActivity())[ShearedViewModel::class.java]
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            HotelListViewModelFactory(
                hotelSearchQuery,
                DataManager.hotelApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[HotelListViewModel::class.java]
    }

    override fun layoutId(): Int = R.layout.fragment_hotel_list

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {
        bindingView.includeShimmer.shimmerLayout.startShimmer()
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        getNavigationResultLiveData<HotelFilterData>(ARG_HOTEL_FILTER_ON_BACK_DATA_MODEL)?.observe(
            viewLifecycleOwner
        ) {
            viewModel.handleFilterSearchData(it)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<HotelFilterData>(
                ARG_HOTEL_FILTER_ON_BACK_DATA_MODEL
            )
        }

        subTitle =
            "${viewModel.dateRange}, ${viewModel.roomCount} Room(s), ${viewModel.guestCount} Person(s)"
        setTitleAndSubtitle(hotelSearchQuery.propertyName, subTitle)
        initRecyclerView()

        viewModel.hotelCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                hotelCount = it
            }
            bindingView.filterTitleTextView.text = "$hotelCount Available Hotels"
        }

        viewModel.hotelCountServer.observe(viewLifecycleOwner) {
            activity?.runOnUiThread {
                adapter.refresh()
            }
        }

        viewModel.isFirstPage.observe(viewLifecycleOwner) {
            isFirstPage = it
        }

        viewModel.searchCode.observe(viewLifecycleOwner) {
            searchCode = it
        }

        viewModel.navigateToHotelFilter.observe(viewLifecycleOwner) {
            val bundle = Bundle()
            bundle.putParcelable(
                HotelFilterFragment.ARG_HOTEL_FILTER_DATA_MODEL,
                viewModel.filter.value
            )
            bundle.putString(ARG_HOTEL_FILTER_TITLE, hotelSearchQuery.propertyName)
            findNavController().navigateSafe(
                R.id.action_hotelListFragment_to_hotelFilterFragment,
                bundleOf(HotelFilterFragment.ARG_HOTEL_FILTER_BUNDLE to bundle)
            )
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.filter.observe(viewLifecycleOwner) {
            it?.let {
                bindingView.filterImageButton.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroyCall()
        shearedViewModel.onClearViewModel()
    }

    private fun initRecyclerView() {
        bindingView.listHotel.layoutManager = LinearLayoutManager(context)
        adapter = HotelListAdapter(hotelSearchQuery.rooms.size)
        bindingView.listHotel.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )

        ItemClickSupport.addTo(bindingView.listHotel).setOnItemClickListener { _, position, _ ->
            try {
                viewModel.hotelEventManager.clickOnHotelDetail()

                val list = adapter.snapshot().items
                val hotel = list[position]
                val bundle = Bundle()
                bundle.putString(HotelDetailsFragment.ARG_HOTEl_ID, hotel.hotelId)
                bundle.putString(HotelDetailsFragment.ARG_SEARCH_CODE, searchCode)
                bundle.putString(HotelDetailsFragment.ARG_HOTEL_NAME, hotel.name)
                bundle.putInt(HotelDetailsFragment.ARG_ROOM_COUNT, hotelSearchQuery.rooms.size)
                bundle.putString(HotelDetailsFragment.ARG_DETAILS_SUBTITLE, subTitle)
                findNavController().navigateSafe(
                    R.id.action_hotelListFragment_to_hotelDetailsFragment,
                    bundleOf(ARG_HOTEL_DETAILS_BUNDLE to bundle)
                )
            } catch (e: Exception) {
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.hotelDataList.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collect { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    showRunningStatus()
                } else if (loadState.refresh is LoadState.Error && isFirstPage) {
                    (loadState.refresh as LoadState.Error).error.message?.let {
                        showFailedStatus(it)
                    }
                } else if (loadState.refresh is LoadState.NotLoading) {
                    showDefaultStatus()
                }
            }
        }
    }

    private fun showRunningStatus() {
        bindingView.progressContainer.visibility = View.VISIBLE
        bindingView.includeShimmer.shimmerLayout.startShimmer()
    }

    private fun showFailedStatus(it: String) {
        bindingView.filterConstrainLayout.visibility = View.INVISIBLE
        bindingView.hotelErrorContainer.visibility = View.VISIBLE
        bindingView.includeShimmer.shimmerLayout.stopShimmer()
        bindingView.progressContainer.visibility = View.GONE
        bindingView.textViewMessage.text = it
    }

    private fun showDefaultStatus() {
        bindingView.includeShimmer.shimmerLayout.stopShimmer()
        bindingView.progressContainer.visibility = View.GONE
        bindingView.filterConstrainLayout.visibility = View.VISIBLE
    }

    companion object {
        const val ARG_HOTEL_QUERY_MODEL = "ARG_HOTEL_QUERY_MODEL"
    }
}
