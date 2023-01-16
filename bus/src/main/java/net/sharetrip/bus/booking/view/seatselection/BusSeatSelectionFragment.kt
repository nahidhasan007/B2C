package net.sharetrip.bus.booking.view.seatselection

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.SearchIdAndTripCoin
import net.sharetrip.bus.booking.model.Seat
import net.sharetrip.bus.booking.model.selectionenum.Case
import net.sharetrip.bus.booking.view.locationpoints.SelectLocationFragment.Companion.ARG_BUS_LOCATION_BUNDLE
import net.sharetrip.bus.databinding.FragmentBusSeatSelectionBinding
import net.sharetrip.bus.history.model.SeatClassPrice
import net.sharetrip.bus.network.DataManager
import net.sharetrip.bus.utils.getAlertDialog

class BusSeatSelectionFragment : BaseFragment<FragmentBusSeatSelectionBinding>() {
    private val viewModel by lazy {
        val departureTime: String? = bundle.getString(ARG_BUS_SELECTION_DEPARTURE_TIME)
        val arrivalTime: String? = bundle.getString(ARG_BUS_SELECTION_ARRIVAL_TIME)
        val discount = bundle.getString(ARG_BUS_SELECTION_discount)!!
        val searchIdAndTripCoin =
            bundle.getParcelable<SearchIdAndTripCoin>(ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN)!!

        ViewModelProvider(
            this,
            BusSelectionVMFactory(
                departureTime, arrivalTime, discount, searchIdAndTripCoin,
                DataManager.busBookingApiService, DataManager.getSharedPref(requireContext())
            )
        ).get(
            BusSeatSelectionViewModel::class.java
        )
    }

    val bundle by lazy { arguments?.getBundle(ARG_BUS_SELECTION_BUNDLE)!! }

    private var tempSelected = 0
    private val clickedSeat = mutableMapOf<String, Int>()
    private var previousSelected = mutableListOf<Seat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId() = R.layout.fragment_bus_seat_selection

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.model = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        viewModel.journeyDate = bundle.getString(ARG_BUS_SELECTION_DATE)!!
        val profDialog = getAlertDialog(layoutInflater, requireContext())

        viewModel.isInit.observe(viewLifecycleOwner) {
            if (it) {
                try {
                    bindingView.model = viewModel
                    bindingView.recyclerBusSeat.layoutManager =
                        GridLayoutManager(requireContext(), viewModel.seatCol)
                    bindingView.recyclerBusSeat.adapter = BusSeatSelectionAdapter(viewModel)
                    bindingView.recyclerSeatStatus.adapter = BusSeatSelectionAdapter(viewModel, 5)
                    bindingView.recyclerBusSeat.itemAnimator = null
                    bindingView.recyclerSeatStatus.itemAnimator = null
                } catch (e: Throwable) {
                }
            }
        }

        ItemClickSupport.addTo(bindingView.recyclerBusSeat)
            .setOnItemClickListener { _: RecyclerView?, pos: Int, _: View? ->
                if (viewModel.departureInfoMapped[pos] != null) {
                    if (viewModel.departureInfoMapped[pos]?.status == "Available") {
                        if (viewModel.seatModel[1].count < 4 && tempSelected < 4 && previousSelected.size < 4) {
                            clickedSeat[viewModel.departureInfoMapped[pos]?.seatId.toString()] = pos
                            viewModel.previousStatus[viewModel.departureInfoMapped[pos]?.seatId.toString()] =
                                "Available"
                            if (tempSelected < previousSelected.size)
                                tempSelected = previousSelected.size
                            tempSelected++
                            viewModel.updateFareAndSeats(Case.SELECTION_ONGOING, pos)
                            notifyChange(pos, 1)
                            viewModel.selectSeat(
                                viewModel.departureInfoMapped[pos],
                                Case.SELECT_SEAT
                            )
                        } else
                            toastShow(getString(R.string.seat_selection_limit))

                    } else if (viewModel.departureInfoMapped[pos]?.status == "Selected") {
                        viewModel.previousStatus[viewModel.departureInfoMapped[pos]?.seatId.toString()] =
                            "Selected"
                        viewModel.updateFareAndSeats(Case.SELECTION_ONGOING, pos)
                        viewModel.selectSeat(viewModel.departureInfoMapped[pos], Case.DESELECT_SEAT)
                        notifyChange(pos, 1)
                    } else
                        toastShow("Already " + viewModel.departureInfoMapped[pos]?.status)
                }
            }

        viewModel.dialogLoading.observe(viewLifecycleOwner) {
            if (it)
                profDialog.show()
            else if (!it && viewModel.departureInfo != null) {
                viewModel.departureInfo?.setTimeDuration()
                profDialog.dismiss()
            } else
                profDialog.dismiss()
        }

        viewModel.seatsSelected.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val selected = ArrayList(it)
                tempSelected = selected.size
                selected.forEach { it1 ->
                    viewModel.updateFareAndSeats(
                        Case.SELECT_SEAT,
                        clickedSeat[it1.seatId.toString()]!!
                    )
                    notifyChange(clickedSeat[it1.seatId.toString()]!!, 1)
                    notifyChange(1, 2)
                }
                for (seat in previousSelected) {
                    var deselect = true
                    for (seatNew in selected)
                        if (seat.seatId == seatNew.seatId) {
                            deselect = false
                            break
                        }
                    if (deselect) {
                        viewModel.updateFareAndSeats(
                            Case.DESELECT_SEAT,
                            clickedSeat[seat.seatId.toString()]!!
                        )
                        notifyChange(clickedSeat[seat.seatId.toString()]!!, 1)
                        notifyChange(1, 2)
                    }
                }
                previousSelected = ArrayList(it)
                updatePricingDetail()
            }
        }

        viewModel.seatsRejected.observe(viewLifecycleOwner) {
            it?.forEach { it1 ->
                clickedSeat[it1]?.let { it2 ->
                    viewModel.updateFareAndSeats(
                        Case.SELECTION_FAILED,
                        it2
                    )
                }

                for (seat in viewModel.selectedSeatList) {
                    if (seat.value.seatId.toString() == it1) {
                        notifyChange(clickedSeat[it1]!!, 1)
                        break
                    }
                }
            }
        }

        viewModel.navigateToLocationPoint.observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_busSeatSelectionFragment_to_boardingPointsFragment,
                bundleOf(ARG_BUS_LOCATION_BUNDLE to it)
            )
        }
    }

    private fun toastShow(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    private fun updatePricingDetail() {
        bindingView.layoutBookingPlan.removeAllViews()
        bindingView.labelPricingDetails.invalidate()
        viewModel.seatsSelected.value?.forEach {
            val child = ItemBusHistoryPricingSeatView(requireContext())
            child.binding.allData = SeatClassPrice(it.seatNo, it.seatTypeId, it.fare.originalFare)
            bindingView.layoutBookingPlan.addView(child)
        }
    }

    private fun notifyChange(pos: Int, case: Int) {
        if (case == 1)
            bindingView.recyclerBusSeat.adapter?.notifyItemChanged(pos)
        else if (case == 2)
            bindingView.recyclerSeatStatus.adapter?.notifyItemChanged(pos)
    }

    companion object {
        const val ARG_BUS_SELECTION_BUNDLE = "ARG_BUS_SELECTION_BUNDLE"
        const val ARG_BUS_SELECTION_DATE = "ARG_BUS_SELECTION_DATE"
        const val ARG_BUS_SELECTION_DEPARTURE_TIME = "ARG_BUS_SELECTION_DEPARTURE_TIME"
        const val ARG_BUS_SELECTION_ARRIVAL_TIME = "ARG_BUS_SELECTION_ARRIVAL_TIME"
        const val ARG_BUS_SELECTION_discount = "ARG_BUS_SELECTION_discount"
        const val ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN = "ARG_BUS_SELECTION_SEARCH_ID_TRIP_COIN"
    }
}
