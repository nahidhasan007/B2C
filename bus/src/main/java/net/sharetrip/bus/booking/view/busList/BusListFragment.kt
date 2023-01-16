package net.sharetrip.bus.booking.view.busList

import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.BusSearch
import net.sharetrip.bus.booking.model.FilterData
import net.sharetrip.bus.booking.view.busList.adapter.BusAdapter
import net.sharetrip.bus.booking.view.busList.adapter.CompanyNameAdapter
import net.sharetrip.bus.booking.view.busList.filter.BusListFilterViewModel
import net.sharetrip.bus.booking.view.seatselection.BusSeatSelectionFragment.Companion.ARG_BUS_SELECTION_BUNDLE
import net.sharetrip.bus.databinding.FragmentBusListBinding
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.*
import java.text.ParseException

class BusListFragment : BaseFragment<FragmentBusListBinding>() {

    private val viewModel: BusListViewModel by viewModels {
        val busSearch = BusSearch(
            requireArguments().getString(ARG_DEPARTURE_DATE)!!,
            requireArguments().getString(ARG_DESTINATION_CITY)!!,
            requireArguments().getString(ARG_ORIGIN_CITY)!!
        )
        BusListViewModelFactory(
            busSearch,
            BusListRepository(DataManager.busBookingApiService),
            DataManager.getSharedPref(requireContext())
        )
    }

    private lateinit var slideUp: Animation

    var adapter = BusAdapter(tripValue = 0)

    override fun layoutId() = R.layout.fragment_bus_list

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitle()
        setInfoLabel()
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        bindingView.includeShimmer.shimmerLayout.startAnimation(slideUp)

        ItemClickSupport.addTo(bindingView.recyclerBusList)
            .setOnItemClickListener { _: RecyclerView?, position: Int, _: View? ->
                viewModel.openSeatSelection(adapter.getItem(position))
            }

        val companyNameAdapter = CompanyNameAdapter(viewModel)

        bindingView.viewModel = viewModel
        bindingView.recyclerCompanyName.itemAnimator = null;
        bindingView.recyclerCompanyName.adapter = companyNameAdapter
        bindingView.recyclerBusList.adapter = adapter

        viewModel.departure.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.resetItems(it)
                if (it.isEmpty() && viewModel.dataLoadingInit.value == true) {
                    viewModel.isNoDataFound.set(true)
                    bindingView.recyclerCompanyName.visibility = View.VISIBLE
                } else if (it.isEmpty()) {
                    viewModel.isNoDataFound.set(true)
                    bindingView.recyclerCompanyName.visibility = View.GONE
                } else {
                    viewModel.isNoDataFound.set(false)
                    bindingView.recyclerCompanyName.visibility = View.VISIBLE
                }
            }
        }

        viewModel.busNameList.observe(viewLifecycleOwner) {
            companyNameAdapter.addItems(it)
        }

        viewModel.errorMassage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.dataLoadingInit.observe(viewLifecycleOwner) {
            if (it) {
                adapter.tripValue = viewModel.tripCoinValue
            } else
                viewModel.isNoDataFound.set(true)
        }

        viewModel.navigateToFilter.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(BUS_LIST_FILTER_DATA to viewModel.busFilterData)
            findNavController().navigate(
                R.id.action_busListFragment_to_busListFilterFragment,
                bundle
            )
        })

        getNavigationResultLiveData<FilterData>(BusListFilterViewModel.ARG_BUS_FILTER_DATA)?.observe(
            viewLifecycleOwner
        ) { filterData ->
            viewModel.handleActivityResult(filterData!!)
        }

        viewModel.navigateToSeatSelection.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_busListFragment_to_busSeatSelectionFragment,
                bundleOf(ARG_BUS_SELECTION_BUNDLE to it)
            )
        }
    }

    private fun setTitle() {
        val mBuilder = SpannableStringBuilder()
        var destination = requireArguments().getString(ARG_DESTINATION_NAME)!!
        var origin = requireArguments().getString(ARG_ORIGIN_NAME)!!

        if (destination.length + origin.length >= 20)
            if (destination.length > origin.length) {
                destination = destination.substring(0, destination.length - 8)
                destination = "$destination.."
                if (destination.length + origin.length > 20) {
                    origin = origin.substring(0, origin.length - 8)
                    origin = "$origin.."
                }
            } else {
                origin = origin.substring(0, origin.length - 8)
                origin = "$origin.."
                if (destination.length + origin.length > 20) {
                    destination = destination.substring(0, destination.length - 8)
                    destination = "$destination.."
                }
            }

        val spannableDestination = SpannableString("  $destination")
        val mGroupSpan: ImageSpan = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val myDrawable =
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_dots_horizontal_24dp)
            myDrawable!!.setBounds(0, 0, myDrawable.intrinsicWidth, myDrawable.intrinsicHeight)
            ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE)
        } else {
            ImageSpan(requireContext(), R.drawable.ic_dots_horizontal_24dp)
        }
        spannableDestination.setSpan(mGroupSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBuilder.append("$origin ")
        mBuilder.append(spannableDestination)
        setSpannedTitle(mBuilder)
    }

    private fun setInfoLabel() {
        val mBuilder = StringBuilder()
        val depart = requireArguments().getString(ARG_DEPARTURE_DATE)!!

        try {
            val mDate = DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(depart)
            mBuilder.append(mDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        setFragmentSubtitle(mBuilder)
    }
}
