package net.sharetrip.tour.history.contact

import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourBookingHistoryContactPersonBinding
import net.sharetrip.tour.model.PrimaryContact

class TourContactInfoFragment : BaseFragment<FragmentTourBookingHistoryContactPersonBinding>() {

    private val viewModel: TourContactInfoViewModel by viewModels {
        val contact =
            arguments?.getParcelable<PrimaryContact>(ARG_TOUR_BOOKING_CONTACT_INFO_PRIMARY)
                    as PrimaryContact
        TourContactInfoVMF(contact)
    }

    override fun layoutId(): Int = R.layout.fragment_tour_booking_history_contact_person

    

    

    companion object {
        const val ARG_TOUR_BOOKING_CONTACT_INFO_PRIMARY = "TourContactInfoScreen.TourHistoryItem"
    }

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
    }
}
