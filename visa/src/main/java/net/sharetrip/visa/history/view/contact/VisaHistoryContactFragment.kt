package net.sharetrip.visa.history.view.contact

import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaHistoryContactBinding
import net.sharetrip.visa.history.model.PrimaryContactItem
import net.sharetrip.visa.utils.VISA_HISTORY_CONTACT
import net.sharetrip.visa.utils.setTitleAndSubtitle

class VisaHistoryContactFragment : BaseFragment<FragmentVisaHistoryContactBinding>() {
     private val viewModel: VisaHistoryContactViewModel by viewModels() {
         val contact =  requireArguments().getParcelable<PrimaryContactItem>(VISA_HISTORY_CONTACT)!!
         VisaHistoryContactViewModelFactory(contact)
     }

    

    override fun layoutId() = R.layout.fragment_visa_history_contact

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.contact_info))

        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
    }

    
}