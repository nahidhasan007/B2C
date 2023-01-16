package net.sharetrip.visa.booking.view.application

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.checkout.VisaCheckoutFragment.Companion.ARG_VISA_CHECKOUT_DATA_MODEL
import net.sharetrip.visa.databinding.FragmentVisaApplicationBinding
import net.sharetrip.visa.network.DataManager

class VisaApplicationFragment : BaseFragment<FragmentVisaApplicationBinding>() {
    private val viewModel by lazy {
        val visaSearchQuery = requireArguments().getParcelable<VisaSearchQuery>(
            ARG_VISA_APPLICATION_DATA_MODEL
        )!!

        ViewModelProvider(
            this,
            VisaApplicationVMFactory(
                visaSearchQuery,
                DataManager.visaBookingApiService,
                DataManager.getSharedPref(requireContext())
            )
        ).get(
            VisaApplicationViewModel::class.java
        )
    }



    override fun layoutId() = R.layout.fragment_visa_application

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        val sectionAdapter = SectionedRecyclerViewAdapter()
        bindingView.recyclerRequiredDocs.adapter = sectionAdapter

        viewModel.reqDocList.observe(viewLifecycleOwner) { list ->
            list.forEach {
                sectionAdapter.addSection(ProfessionSection(it))
            }

            sectionAdapter.notifyDataSetChanged()
            viewModel.dataLoading.set(false)
        }

        viewModel.importanceNotice.observe(viewLifecycleOwner) {
            bindingView.tvImportanceNotice.text = it
        }

        val faqAdapter = FaqAdapter()
        bindingView.recyclerVisaFaq.adapter = faqAdapter

        viewModel.faqList.observe(viewLifecycleOwner) {
            faqAdapter.updateData(it)
            viewModel.isReqDocsVisible.set(true)
        }

        viewModel.headerText.observe(viewLifecycleOwner) {
            bindingView.topHeader.text = it
        }

        viewModel.navigateToCheckout.observe(
            viewLifecycleOwner
        ) {
            findNavController().navigateSafe(
                R.id.action_visaApplicationFragment_to_visaCheckoutFragment,
                bundleOf(ARG_VISA_CHECKOUT_DATA_MODEL to it)
            )
        }
    }

    

    companion object {
        const val ARG_VISA_APPLICATION_DATA_MODEL = "ARG_VISA_APPLICATION_DATA_MODEL"
    }
}
