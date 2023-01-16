package net.sharetrip.hotel.booking.view.hotelfilter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.sharetrip.base.event.Event
import com.sharetrip.base.event.EventObserver
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.Filter
import net.sharetrip.hotel.booking.model.LocationRange
import net.sharetrip.hotel.booking.model.PriceRangeDumy
import net.sharetrip.hotel.booking.model.SearchHotel
import net.sharetrip.hotel.utils.ShearedViewModel
import net.sharetrip.shared.databinding.LayoutFilterBottomSheetBinding

class HotelFilterBottomSheet(
    private val priceRange: PriceRangeDumy, private val hotelSearch: String,
    private val neighborhoodList: ArrayList<String>,
    private val locationRangeList: String, private val propertyRatingList: ArrayList<String>,
    private val mealsList: ArrayList<String>, private val propertyTypeList: ArrayList<String>,
    private val facilitiesList: ArrayList<String>,
    private val pointOfInterestList: ArrayList<String>
) : BottomSheetDialogFragment() {
    private lateinit var bindingView: LayoutFilterBottomSheetBinding
    private var enum: Int = 0
    private lateinit var hotelFilter: Filter
    private lateinit var hotelFilterAdapter: HotelFilterAdapter
    private val shearedViewModel by lazy { ViewModelProvider(requireActivity())[ShearedViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingView = DataBindingUtil.inflate(inflater, layoutId(), container, false)
        bindingView.lifecycleOwner = viewLifecycleOwner
        return bindingView.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingView.textSelection.text = getString(R.string.select_all)
        val bundle = arguments
        try {
            enum = bundle!!.getInt("Title")
            hotelFilter = Gson().fromJson(bundle.getString("hotelFilter"), Filter::class.java)
            when (enum) {
                PRICE -> {
                    viewPrice()
                }

                SEARCH -> {
                    viewSearch()
                }

                NEIGHBORHOOD -> {
                    viewNeighborhood()
                }

                LOCATION_RANGE -> {
                    viewLocationRange()
                }

                PROPERTY_RATING -> {
                    viewPropertyRating()
                }

                MEAL -> {
                    viewMeal()
                }

                PROPERTY_TYPE -> {
                    viewPropertyType()
                }

                FACILITIES -> {
                    viewFacilities()
                }

                POINT_OF_INTEREST -> {
                    viewPointOfInterest()
                }

            }
        } catch (_: Exception) {
        }

        bindingView.imageviewClose.setOnClickListener {
            dismiss()
        }

        bindingView.applyButton.setOnClickListener {
            when (enum) {
                PRICE -> shearedViewModel.hotelNewPrice.value =
                    hotelFilterAdapter.priceRangeDummyData()

                SEARCH -> shearedViewModel.hotelSearchData.value = hotelFilterAdapter.hotelName

                NEIGHBORHOOD -> shearedViewModel.neighborhoodCodeSet.value =
                    hotelFilterAdapter.codeList

                LOCATION_RANGE -> shearedViewModel.locationRangeData.value =
                    hotelFilterAdapter.locationRange

                PROPERTY_RATING -> shearedViewModel.propertyRatingCodeSet.value =
                    hotelFilterAdapter.codeList

                MEAL -> shearedViewModel.mealsCodeSet.value = hotelFilterAdapter.codeList

                PROPERTY_TYPE -> shearedViewModel.propertyTypeCodeSet.value =
                    hotelFilterAdapter.codeList

                FACILITIES -> shearedViewModel.facilityCodeSet.value = hotelFilterAdapter.codeList

                POINT_OF_INTEREST -> {
                    shearedViewModel.pointOfInterestCodeSet.value =
                        hotelFilterAdapter.codeList
                    shearedViewModel.pointOfInterestName.value =
                        hotelFilterAdapter.pointOfInterestName
                }
            }

            dismiss()
        }

        shearedViewModel.isSelectAll.observe(viewLifecycleOwner, EventObserver {
            bindingView.textSelection.text =
                if (it) getString(R.string.deselect_all) else getString(R.string.select_all)
        })

        bindingView.textSelection.setOnClickListener {
            if (bindingView.textSelection.text.toString().equals("Select all", true)) {
                bindingView.textSelection.text = getString(R.string.deselect_all)
                hotelFilterAdapter.setSelection(true)
            } else {
                bindingView.textSelection.text = getString(R.string.select_all)
                hotelFilterAdapter.setSelection(false, enum)
            }

            hotelFilterAdapter.notifyDataSetChanged()
        }
    }

    private fun viewPrice() {
        bindingView.textSelection.visibility = View.GONE
        bindingView.title.text = getString(R.string.price_range)
        val priceRangeList = ArrayList<PriceRangeDumy>()
        priceRangeList.add(priceRange)
        hotelFilterAdapter = HotelFilterAdapter(priceRangeList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewSearch() {
        bindingView.textSelection.visibility = View.GONE
        bindingView.title.text = getString(R.string.search_hotel)
        val searchList = ArrayList<SearchHotel>()
        hotelFilter.searchHotel.name = hotelSearch
        searchList.add(hotelFilter.searchHotel)
        hotelFilterAdapter = HotelFilterAdapter(searchList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewNeighborhood() {
        bindingView.textSelection.visibility = View.VISIBLE
        bindingView.title.text = getString(R.string.neighbourhood)
        var flag = true

        hotelFilter.neighborhoodList.forEach {
            if (it.id !in neighborhoodList) {
                flag = false
                it.isSelected = false
            } else
                it.isSelected = true
        }

        shearedViewModel.isSelectAll.value = Event(flag)
        hotelFilterAdapter =
            HotelFilterAdapter(hotelFilter.neighborhoodList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewLocationRange() {
        bindingView.textSelection.visibility = View.GONE
        bindingView.title.text = getString(R.string.location_range)
        val locationRnage = ArrayList<LocationRange>()
        hotelFilter.locationRange.progress = locationRangeList.toInt()
        locationRnage.add(hotelFilter.locationRange)
        hotelFilterAdapter = HotelFilterAdapter(locationRnage, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewPropertyRating() {
        bindingView.textSelection.visibility = View.VISIBLE
        bindingView.title.text = getString(R.string.property_rating)
        var flag = true

        hotelFilter.ratingList.forEach {
            if (it.rating.toString() !in propertyRatingList) {
                flag = false
                it.isSelected = false
            } else
                it.isSelected = true
        }

        shearedViewModel.isSelectAll.value = Event(flag)
        hotelFilterAdapter =
            HotelFilterAdapter(hotelFilter.ratingList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewMeal() {
        bindingView.title.text = getString(R.string.meal)
        var flag = true

        hotelFilter.mealList.forEach {
            if (it.id !in mealsList) {
                flag = false
                it.isSelected = false
            } else
                it.isSelected = true
        }

        shearedViewModel.isSelectAll.value = Event(flag)
        hotelFilterAdapter = HotelFilterAdapter(hotelFilter.mealList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewPropertyType() {
        bindingView.title.text = getString(R.string.property_type)
        var flag = true

        hotelFilter.propertyTypeList.forEach {
            if (it.id !in propertyTypeList) {
                flag = false
                it.isSelected = false
            } else
                it.isSelected = true
        }

        shearedViewModel.isSelectAll.value = Event(flag)
        hotelFilterAdapter =
            HotelFilterAdapter(hotelFilter.propertyTypeList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewFacilities() {
        bindingView.textSelection.visibility = View.VISIBLE
        bindingView.title.text = getString(R.string.facilities_amenities)
        var flag = true

        hotelFilter.amenityList.forEach {
            if (it.id !in facilitiesList) {
                flag = false
                it.isSelected = false
            } else {
                it.isSelected = true
            }
        }

        shearedViewModel.isSelectAll.value = Event(flag)
        hotelFilterAdapter =
            HotelFilterAdapter(hotelFilter.amenityList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun viewPointOfInterest() {
        bindingView.textSelection.visibility = View.VISIBLE
        bindingView.title.text = getString(R.string.point_of_interest)
        var flag = true

        hotelFilter.pointOfInterestList.forEach {
            if (it.id !in pointOfInterestList) {
                flag = false
                it.isSelected = false
            } else
                it.isSelected = true
        }

        shearedViewModel.isSelectAll.value = Event(flag)
        hotelFilterAdapter =
            HotelFilterAdapter(hotelFilter.pointOfInterestList, shearedViewModel)
        bindingView.filterRecycler.adapter = hotelFilterAdapter
    }

    private fun layoutId() = R.layout.layout_filter_bottom_sheet

    companion object {
        fun newInstance(
            priceRange: PriceRangeDumy, hotelSearch: String, neighborhoodList: ArrayList<String>,
            locationRangeList: String, propertyRatingList: ArrayList<String>,
            mealsList: ArrayList<String>, propertyTypeList: ArrayList<String>,
            facilitiesList: ArrayList<String>, pointOfInterestList: ArrayList<String>,
        ): HotelFilterBottomSheet {
            return HotelFilterBottomSheet(
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
        }
    }
}
