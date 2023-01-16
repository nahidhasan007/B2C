package net.sharetrip.hotel.booking.view.hotelfilter

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.Filter
import net.sharetrip.hotel.booking.model.HotelFilterData
import net.sharetrip.hotel.booking.model.PriceRange
import net.sharetrip.hotel.booking.model.PriceRangeDumy
import net.sharetrip.hotel.databinding.FragmentHotelFilterBinding
import net.sharetrip.hotel.utils.ShearedViewModel
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment

class HotelFilterFragment : BaseFragment<FragmentHotelFilterBinding>() {
    private val viewModel by viewModels<HotelFilterViewModel> { HotelFilterVMFactory() }
    private lateinit var hotelFilter: Filter
    private lateinit var priceRange: PriceRangeDumy
    private var hotelSearch: String = ""
    private var neighborhoodList = ArrayList<String>()
    private var locationRangeList: String = ""
    private var propertyRatingList = ArrayList<String>()
    private var mealsList = ArrayList<String>()
    private var propertyTypeList = ArrayList<String>()
    private var facilitiesList = ArrayList<String>()
    private var pointOfInterestList = ArrayList<String>()
    private val shearedViewModel by lazy { ViewModelProvider(requireActivity())[ShearedViewModel::class.java] }

    override fun layoutId(): Int = R.layout.fragment_hotel_filter

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {
        hotelFilter = requireArguments().getBundle(ARG_HOTEL_FILTER_BUNDLE)
            ?.getParcelable(ARG_HOTEL_FILTER_DATA_MODEL)!!
        hotelFilter.priceRange.apply {
            if (this.high <= this.low)
                this.high = this.low + 1
        }

        setTitleAndSubtitle(
            requireArguments().getBundle(ARG_HOTEL_FILTER_BUNDLE)
                ?.getString(ARG_HOTEL_FILTER_TITLE)!!
        )

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        dataVisualization()

        viewModel.liveData.observe(viewLifecycleOwner) { enum ->
            val bundle = Bundle()
            bundle.putInt("Title", enum)
            bundle.putString("hotelFilter", Gson().toJson(hotelFilter))
            val addPhotoBottomDialogFragment: HotelFilterBottomSheet =
                HotelFilterBottomSheet.newInstance(
                    priceRange,
                    hotelSearch,
                    neighborhoodList,
                    locationRangeList,
                    propertyRatingList,
                    mealsList,
                    propertyTypeList,
                    facilitiesList,
                    pointOfInterestList
                )
            addPhotoBottomDialogFragment.arguments = bundle
            childFragmentManager.let {
                addPhotoBottomDialogFragment.show(
                    it,
                    "HotelFilterFragment"
                )
            }
        }

        viewModel.clickOnSearch.observe(viewLifecycleOwner) {
            val filterData = HotelFilterData()
            val priceRangeMain = PriceRange()
            priceRangeMain.high = priceRange.highProgress
            priceRangeMain.low = priceRange.lowProgress
            filterData.priceRange = priceRangeMain.apply {
                if (this.high <= this.low)
                    this.high = this.low + 1
            }
            filterData.meals.addAll(mealsList)
            filterData.amenities.addAll(facilitiesList)
            filterData.pointOfInterests.addAll(pointOfInterestList)
            filterData.neighborhoods.addAll(neighborhoodList)
            filterData.propertyRating.addAll(propertyRatingList)
            filterData.propertyType.addAll(propertyTypeList)
            filterData.hotelName = hotelSearch
            filterData.distance = locationRangeList
            setNavigationResult(filterData, ARG_HOTEL_FILTER_ON_BACK_DATA_MODEL)
            findNavController().navigateUp()
        }

        viewModel.clickOnReset.observe(viewLifecycleOwner) {
            shearedViewModel.onClearViewModel()
            dataVisualization()
            viewModel.clickOnSearch.call()
        }

        shearedViewModel.hotelNewPrice.observe(viewLifecycleOwner) {
            it?.let {
                priceRange = it
                bindingView.priceRangeSummary.text =
                    "" + priceRange.lowProgress + " - " + priceRange.highProgress
            }
        }

        shearedViewModel.hotelSearchData.observe(viewLifecycleOwner) {
            it?.let {
                hotelSearch = it
                bindingView.searchSummary.text = it.ifEmpty { getString(R.string.any) }
            }
        }

        shearedViewModel.neighborhoodCodeSet.observe(viewLifecycleOwner) {
            it?.let {
                neighborhoodList = it
                bindingView.neighborhoodSummary.text =
                    if (it.isEmpty()) getString(R.string.any) else
                        it.toString().replace("[", "").replace("]", "")
            }
        }

        shearedViewModel.locationRangeData.observe(viewLifecycleOwner) {
            it?.let {
                locationRangeList = it
                bindingView.locationRangeSummary.text = it
            }
        }

        shearedViewModel.propertyRatingCodeSet.observe(viewLifecycleOwner) {
            it?.let {
                propertyRatingList = it
                bindingView.propertyRatingSummary.text =
                    if (it.isEmpty()) getString(R.string.any) else it.toString().replace("[", "")
                        .replace("]", "")
            }
        }

        shearedViewModel.mealsCodeSet.observe(viewLifecycleOwner) {
            it?.let {
                mealsList = it
                bindingView.mealSummay.text =
                    if (it.isEmpty()) getString(R.string.any) else it.toString().replace("[", "")
                        .replace("]", "")
            }
        }

        shearedViewModel.propertyTypeCodeSet.observe(viewLifecycleOwner) {
            it?.let {
                propertyTypeList = it
                bindingView.propertyTypeSummay.text =
                    if (it.isEmpty()) getString(R.string.any) else it.toString().replace("[", "")
                        .replace("]", "")
            }
        }

        shearedViewModel.facilityCodeSet.observe(viewLifecycleOwner) {
            it?.let {
                facilitiesList = it
                bindingView.facilitySummay.text =
                    if (it.isEmpty()) getString(R.string.any) else it.toString().replace("[", "")
                        .replace("]", "")
            }
        }

        shearedViewModel.pointOfInterestCodeSet.observe(viewLifecycleOwner) {
            it?.let {
                pointOfInterestList = it
                bindingView.interestSummay.text =
                    if (it.isEmpty()) getString(R.string.any) else it.toString().replace("[", "")
                        .replace("]", "")
            }
        }

        shearedViewModel.pointOfInterestName.observe(viewLifecycleOwner) {
            it?.let {
                bindingView.interestSummay.text =
                    if (it.isEmpty()) getString(R.string.any) else it.toString().replace("[", "")
                        .replace("]", "")
            }
        }
    }

    private fun dataVisualization() {
        priceRange = hotelFilter.priceRange
        hotelSearch = ""
        neighborhoodList.clear()
        locationRangeList = "25"
        propertyRatingList.clear()
        mealsList.clear()
        propertyTypeList.clear()
        facilitiesList.clear()
        pointOfInterestList.clear()
    }

    companion object {
        const val ARG_HOTEL_FILTER_BUNDLE = "ARG_HOTEL_FILTER_BUNDLE"
        const val ARG_HOTEL_FILTER_DATA_MODEL = "ARG_HOTEL_FILTER_DATA_MODEL"
        const val ARG_HOTEL_FILTER_TITLE = "ARG_HOTEL_FILTER_TITLE"
        const val ARG_HOTEL_FILTER_ON_BACK_DATA_MODEL = "ARG_HOTEL_FILTER_ON_BACK_DATA_MODEL"
    }
}
