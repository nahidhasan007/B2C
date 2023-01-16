package net.sharetrip.tour.booking.contact

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.dateChangeToDDMMYYYY
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.booking.reserve.TourReserveFragment.Companion.ARG_TOUR_PARAM_MODEL
import net.sharetrip.tour.databinding.FragmentClientContactTourBinding
import net.sharetrip.tour.model.TourParam
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.hideTripCoin
import net.sharetrip.tour.utils.setTitleAndSubtitle
import java.text.SimpleDateFormat
import java.util.*

class ClientContactFragment : BaseFragment<FragmentClientContactTourBinding>() {

    private val viewModel: ClientContactViewModel by viewModels {
        val tourParam = arguments?.getParcelable<TourParam>(ARG_TOUR_PARAM_MODEL)
        ClientContactVMF(
            repo = ClientContactRepo(TourDataManager.tourBookingAPIService),
            tourParam = tourParam!!,
            sharedPrefsHelper = TourDataManager.getSharedPref(requireContext())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId(): Int = R.layout.fragment_client_contact_tour

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.contact_information))
        hideTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.msg.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.dateOfBirth.observe(viewLifecycleOwner) {
            bindingView.textFieldBirthDay.setText(it.dateChangeToDDMMYYYY().toString())
        }

        viewModel.navigateToSummary.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(R.id.action_clientContactFragment_to_tourSummaryFragment, it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.done, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.action_done == item.itemId) {
            viewModel.navigateToSummary(
                bindingView.textFieldTitle.text.toString(),
                bindingView.textFieldGivenName.text.toString(),
                bindingView.textFieldSurName.text.toString(),
                bindingView.textFieldPhoneNumber.text.toString(),
                bindingView.textFieldEmail.text.toString(),
                bindingView.textFieldAddress.text.toString(),
                bindingView.textFieldBirthDay.text.toString()
            )

            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        val title = requireContext().resources.getStringArray(R.array.person_title)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, title)
        bindingView.textFieldTitle.setAdapter(adapter)
        bindingView.textFieldTitle.setText(title[0])
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        bindingView.textFieldBirthDay.setBirthdayRange(sdf.format(Calendar.getInstance().time))
    }
}
