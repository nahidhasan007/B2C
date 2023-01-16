package net.sharetrip.visa.booking.view.verification

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.checkout.VisaCheckoutFragment.Companion.ARG_VISA_CHECKOUT_DATA_MODEL
import net.sharetrip.visa.booking.view.photoverification.PhotoVerificationFragment
import net.sharetrip.visa.databinding.FragmentTravelerVerificationBinding

class TravelerVerificationFragment : BaseFragment<FragmentTravelerVerificationBinding>() {
    private val viewModel by lazy {
        val visaSearchQuery =
            arguments?.getParcelable<VisaSearchQuery>(ARG_TRAVELLER_VERIFICATION_DATA_MODEL)!!

        ViewModelProvider(
            this,
            TravellerVerificationVMFactory(visaSearchQuery)
        )[TravelerVerificationViewModel::class.java]
    }

    override fun layoutId() = R.layout.fragment_traveler_verification

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_visa_verification, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_verification_done_text == menuItem.itemId) {
                    viewModel.onNext()
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.navigateToCheckout.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_travelerVerificationFragment_to_visaCheckoutFragment,
                bundleOf(ARG_VISA_CHECKOUT_DATA_MODEL to it)
            )
        }

        viewModel.navigateToPhotoVerification.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_travelerVerificationFragment_to_photoVerificationFragment,
                bundleOf(PhotoVerificationFragment.ARG_PHOTO_VERIFICATION_DATA_MODEL to it)
            )
        }
    }

    companion object {
        const val ARG_TRAVELLER_VERIFICATION_DATA_MODEL = "ARG_TRAVELLER_VERIFICATION_DATA_MODEL"
    }
}
