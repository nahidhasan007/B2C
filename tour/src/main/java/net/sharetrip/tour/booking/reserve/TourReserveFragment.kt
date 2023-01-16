package net.sharetrip.tour.booking.reserve

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourReservationBinding
import net.sharetrip.tour.model.PeriodX
import net.sharetrip.tour.model.TourParam
import net.sharetrip.tour.utils.hideTripCoin
import net.sharetrip.tour.utils.setTitleAndSubtitle
import java.text.SimpleDateFormat
import java.util.*

class TourReserveFragment : BaseFragment<FragmentTourReservationBinding>() {

    private val viewModel: TourReserveViewModel by viewModels {
        TourReserveVMF(tourOffer!!, tourParam!!)
    }

    private val tourOffer by lazy {
        arguments?.getParcelable<PeriodX>(ARG_TOUR_OFFER_MODEL)
    }

    private val tourParam by lazy {
        arguments?.getParcelable<TourParam>(ARG_TOUR_PARAM_MODEL)
    }

    private var navigate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId(): Int = R.layout.fragment_tour_reservation

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.reserve))
        hideTripCoin()

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            var minDate = sdf.parse(tourOffer!!.periodFrom)
            val maxDate = sdf.parse(tourOffer!!.periodTo)
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val diffInMillies = minDate.time - today.time
            val diff = diffInMillies / (1000 * 60 * 60 * 24)
            if (diff < tourParam!!.releaseTime) {
                calendar.add(Calendar.DATE, tourParam!!.releaseTime)
                minDate = calendar.time
            }
            if (minDate < maxDate) {
                bindingView.editTextTravelDate.setRange(sdf.format(minDate), tourOffer!!.periodTo)
            } else {
                bindingView.editTextTravelDate.dialogClose()
            }
        } catch (e: Exception) {
            e.fillInStackTrace()
        }

        viewModel.navigation.observe(viewLifecycleOwner) {
            if (navigate == true) {
                navigate = false
                viewModel.navigationToPickLocation(
                    bindingView.editTextTravelDate.text.toString(),
                    bindingView.editTextNumberOfAdult.text.toString(),
                    bindingView.editTextChild7to12.text.toString(),
                    bindingView.editTextChild3to6.text.toString(),
                    bindingView.editTextInfant.text.toString()
                )
            }
        }

        viewModel.msg.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                navigate = true
            }
        }

        viewModel.navigateToPickUpLocation.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                R.id.action_tourReserveFragment_to_pickupLocationFragment,
                bundleOf(ARG_TOUR_PARAM_MODEL to it)
            )
        })

        val list = ArrayList<Int>()
        for (i in 1..10)
            list.add(i)
        val listAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, list)

        bindingView.editTextNumberOfAdult.setAdapter(listAdapter)
        bindingView.editTextNumberOfAdult.setText("1")
        bindingView.editTextChild7to12.setAdapter(listAdapter)
        bindingView.editTextChild7to12.setText("0")
        bindingView.editTextChild3to6.setAdapter(listAdapter)
        bindingView.editTextChild3to6.setText("0")
        bindingView.editTextInfant.setAdapter(listAdapter)
        bindingView.editTextInfant.setText("0")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.action_done == item.itemId && navigate) {
            viewModel.onClickSaveMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ARG_TOUR_OFFER_MODEL = "TourReserveScreen.TourOffer.Model"
        const val ARG_TOUR_PARAM_MODEL = "TourReserveScreen.TourParam.Model"
    }
}
