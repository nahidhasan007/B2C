package net.sharetrip.visa.booking.view.traveller

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaTravellerNumberBinding

class VisaTravellerNumberFragment : BaseFragment<FragmentVisaTravellerNumberBinding>() {
    private val viewModel by lazy {
        val bundle = requireArguments().getInt(ARG_NUMBER_OF_ADULT)
        ViewModelProvider(
            this,
            VisaTravellerNumberVMFactory(bundle)
        ).get(
            VisaTravellerNumberViewModel::class.java
        )
    }



    override fun layoutId() = R.layout.fragment_visa_traveller_number

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        viewModel.navigateBackWithData.observe(viewLifecycleOwner) {
            setNavigationResult(it, ARG_TRAVELLER_DATA)
            findNavController().navigateUp()
        }
    }

    

    companion object {
        const val ARG_NUMBER_OF_ADULT = "ARG_NUMBER_OF_ADULT"
        const val ARG_TRAVELLER_DATA = "ARG_TRAVELLER_DATA"
        const val EXTRA_NUMBER_OF_TRAVELLERS_FOR_VISA = "EXTRA_NUMBER_OF_TRAVELLERS_FOR_VISA"
    }
}
