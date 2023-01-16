package net.sharetrip.visa.booking.view.selection

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.Window
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.signup.view.RegistrationActivity
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.visa.booking.model.GuestPopUpData
import net.sharetrip.visa.booking.model.VisaProductsItem
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.model.VisaSelection
import net.sharetrip.visa.booking.view.application.VisaApplicationFragment.Companion.ARG_VISA_APPLICATION_DATA_MODEL
import net.sharetrip.visa.databinding.FragmentVisaSelectionBinding
import net.sharetrip.visa.databinding.GuestUserLayoutVisaBinding
import net.sharetrip.visa.network.DataManager

class VisaSelectionFragment : BaseFragment<FragmentVisaSelectionBinding>(),
    VisaSelectionAdapter.SingleClickListener {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            VisaSelectionVMFactory(
                visaSearchQuery,
                DataManager.visaBookingApiService,
                DataManager.getSharedPref(requireContext())
            )
        ).get(
            VisaSelectionViewModel::class.java
        )
    }

    private val popupData: GuestPopUpData by lazy {
        GuestPopUpData(
            R.string.common_title,
            R.string.visa_body,
            R.drawable.ic_visa_mono, viewModel
        )
    }

    private val visaSearchQuery by lazy {
        requireArguments().getParcelable<VisaSearchQuery>(
            ARG_VISA_SELECTION_MODEL
        )!!
    }

    private lateinit var adapter: VisaSelectionAdapter
    private lateinit var dialog: Dialog



    override fun layoutId() = R.layout.fragment_visa_selection

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        setTitle(visaSearchQuery.destinationCountry)
        bindingView.recyclerVisaSelection.layoutManager = LinearLayoutManager(context)
        adapter = VisaSelectionAdapter(this)
        bindingView.recyclerVisaSelection.adapter = adapter

        viewModel.visaSelection.observe(viewLifecycleOwner) {
            it.visaProducts?.let { value ->
                adapter.updateData(value)
            }
            updateUI(it)
        }

        viewModel.isShowDialog.observe(viewLifecycleOwner) {
            if (!it) {
                showGuestDialog()
            }
        }

        viewModel.isDismissDialog.observe(viewLifecycleOwner) {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModel.isViewUpdated.observe(viewLifecycleOwner) {
            bindingView.executePendingBindings()
        }

        viewModel.navigateToVisaApplication.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_visaSelectionFragment_to_visaApplicationFragment,
                bundleOf(ARG_VISA_APPLICATION_DATA_MODEL to it)
            )
        }

        viewModel.navigateLogin.observe(viewLifecycleOwner, EventObserver{
            val intent = Intent(requireContext(), RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })
    }

    

    private fun showGuestDialog() {
        try {
            dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context),
                R.layout.guest_user_layout_visa,
                null,
                false
            ) as GuestUserLayoutVisaBinding
            dialog.setContentView(dialogBinding.root)
            dialogBinding.data = popupData
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.lastSelectedPosition = viewModel.lastSelected
    }

    private fun updateUI(it: VisaSelection) {
        bindingView.tvWinCoin.text = it.points.earning.toString()
        bindingView.tvReedemCoin.text = it.points.earning.toString()
    }

    override fun onServiceSelected(visaProduct: VisaProductsItem?, lastSelectedPosition: Int) {
        viewModel.calculatePrice(visaProduct, lastSelectedPosition)
    }

    private fun setTitle(title: String) {
        (activity as VisaBookingActivity).supportActionBar?.title = title
    }

    companion object {
        const val ARG_VISA_SELECTION_MODEL = "ARG_VISA_SELECTION_MODEL"
    }
}
