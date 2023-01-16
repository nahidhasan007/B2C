package net.sharetrip.flight.booking.view.travellers

import android.content.Intent
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.TravellersInfo
import net.sharetrip.flight.booking.model.TravellerClassType
import net.sharetrip.flight.databinding.FragmentTravellerNumberBinding
import net.sharetrip.flight.utils.setTitleSubTitle
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment

class TravellerNumberFragment : BaseFragment<FragmentTravellerNumberBinding>() {
    private lateinit var viewModel: TravellerNumberViewModel
    private lateinit var childDobAdapter: ChildDOBAdapter
    private lateinit var travellersClassAdapter: TravellersClassAdapter

    override fun layoutId(): Int = R.layout.fragment_traveller_number

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.title_fragment_travellers_and_class))

        val travellerInfo = arguments?.getParcelable<TravellersInfo>(ARG_TRAVELLER)
        viewModel = TravellerNumberViewModelFactory(
            travellerInfo!!
        ).create(TravellerNumberViewModel::class.java)

        childDobAdapter = ChildDOBAdapter(viewModel)

        viewModel.childDOBNumber.observe(viewLifecycleOwner) {
            childDobAdapter.updateData(it)
        }

        travellersClassAdapter = TravellersClassAdapter(getTravellerClassList(), viewModel)

        viewModel.showMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        bindingView.viewModel = viewModel
        bindingView.recyclerBirthDate.adapter = childDobAdapter
        bindingView.recyclerTravelClass.adapter = travellersClassAdapter

        bindingView.buttonDone.setOnClickListener {
            if (viewModel.checkDob()) {
                val intent = Intent()
                intent.putExtra(EXTRA_NUMBER_OF_ADULT, viewModel.numberOfAdult)
                intent.putExtra(EXTRA_NUMBER_OF_CHILDREN, viewModel.numberOfChildren)
                intent.putExtra(EXTRA_NUMBER_OF_INFANT, viewModel.numberOfInfant)
                intent.putExtra(EXTRA_TRIP_CLASS_TYPE, viewModel.tripClassType)
                intent.putParcelableArrayListExtra(EXTRA_CHILD_DOB_LIST, viewModel.childDobList)
                setNavigationResult(intent, ARG_TRAVELLER)
                findNavController().navigateUp()
            }
        }
    }

    private fun getTravellerClassList(): List<String> {
        return listOf(
            TravellerClassType.ECONOMY.type,
            TravellerClassType.PREMIUM_ECONOMY.type,
            TravellerClassType.BUSINESS.type,
            TravellerClassType.FIRST_CLASS.type
        )
    }
}
