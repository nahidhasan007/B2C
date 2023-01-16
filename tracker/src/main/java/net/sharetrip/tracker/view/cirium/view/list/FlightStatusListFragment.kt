package net.sharetrip.tracker.view.cirium.view.list

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tracker.R
import net.sharetrip.tracker.databinding.FragmentFlightStatusListBinding

class FlightStatusListFragment : BaseFragment<FragmentFlightStatusListBinding>() {

    private val viewModel: FlightStatusListViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_flight_status_list

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {

        viewModel.statusList.observe(viewLifecycleOwner) {
            val adapter = StatusAdapter(it, viewModel)
            bindingView.listFlightStatus.adapter = adapter
            bindingView.labelFlightStatusCountMsg.text =
                it?.size.toString() + " matching flights found at search date"
        }

        viewModel.navigateToFlightStatusDetailsScreen.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_flightStatusListFragment_to_flightStatusDetailsFragment)
        })
    }

    
}
