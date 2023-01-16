package net.sharetrip.tracker.view.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.TopLeftGravityDrawable
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setDrawableTintColor
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tracker.R
import net.sharetrip.tracker.databinding.BottomSheetTravelAdviceTermsBinding
import net.sharetrip.tracker.databinding.FragmentTravelAdviceSearchBinding
import net.sharetrip.tracker.model.RiskLevel
import net.sharetrip.tracker.model.RiskLevelDescription
import net.sharetrip.tracker.network.DataManager
import net.sharetrip.tracker.utils.setTitleAndSubtitle
import net.sharetrip.profile.view.list.CountryCurrencyFragment

class TravelAdviceSearchFragment : BaseFragment<FragmentTravelAdviceSearchBinding>() {

    private val viewModel: TravelAdviceSearchViewModel by viewModels {
        TravelAdviceSearchVMFactory(TravelAdviceSearchRepo(DataManager.flightTrackerApiService))
    }

    

    override fun layoutId() = R.layout.fragment_travel_advice_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.can_i_travel))

        bindingView.viewModel = viewModel

        viewModel.restrictionDetailsList.observe(viewLifecycleOwner) {
            bindingView.layoutRestrictionList.removeAllViews()
            addTextViewToContainer(bindingView.layoutRestrictionList, it, false)
        }

        viewModel.travelInfo.observe(viewLifecycleOwner) {
            bindingView.cardStatus.visibility = View.VISIBLE
            bindingView.cardKnowBefore.visibility = View.VISIBLE

            when (it.riskLevel) {
                RiskLevel.PERMITTED.value ->
                    showRestrictionInfo(RiskLevelDescription(RiskLevel.PERMITTED))

                RiskLevel.MODERATE.value ->
                    showRestrictionInfo(RiskLevelDescription(RiskLevel.MODERATE))

                RiskLevel.PROHIBITED.value ->
                    showRestrictionInfo(RiskLevelDescription(RiskLevel.PROHIBITED))
            }
        }

        viewModel.isTermsConditionVisible.observe(viewLifecycleOwner) {
            openBottomSheetTermsCondition()
        }

        viewModel.navigateToCountryCurrency.observe(viewLifecycleOwner, EventObserver{
            val bundle = bundleOf(CountryCurrencyFragment.ARG_COUNTRY_CURRENCY to "country")
            findNavController().navigateSafe(
                R.id.action_travelAdviceSearchFragment_to_countryCurrencyFragment2,
                bundle
            )
        })

        getNavigationResultLiveData<Bundle>(CountryCurrencyFragment.RESULT_COUNTRY_SELECTION)?.observe(
            viewLifecycleOwner
        ) {
            val code = it.getString(CountryCurrencyFragment.EXTRA_COUNTRY_CODE)
            val name = it.getString(CountryCurrencyFragment.EXTRA_COUNTRY_NAME)
            bindingView.travelersAndClassCountTextView.text = name
            viewModel.countryCode.set(code)
            viewModel.countryName.set(name)
        }

        addTextViewToContainer(
            bindingView.containerCovidSafetyMsg,
            resources.getStringArray(R.array.covid_prevention_info).toList() as ArrayList<String>,
            true
        )

        bindingView.textViewCovidTest.isSelected = true
        bindingView.textViewQuarantine.isSelected = true
        bindingView.textViewMask.isSelected = true
    }

    

    private fun addTextViewToContainer(
        layoutContainer: ViewGroup,
        arrayList: ArrayList<String>,
        hasMargin: Boolean
    ) {

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        arrayList.forEach {
            val textView = AppCompatTextView(requireContext())
            textView.text = it
            textView.includeFontPadding = false
            textView.textSize = 10f
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.little_active_dot)!!
            val gravityDrawable = TopLeftGravityDrawable(drawable)

            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            gravityDrawable.setBounds(0, 10, drawable.intrinsicWidth, drawable.intrinsicHeight)
            textView.setPadding(8, 0, 8, 0)
            textView.compoundDrawablePadding = 8
            textView.setCompoundDrawables(gravityDrawable, null, null, null)

            if (hasMargin) {
                layoutParams.setMargins(0, 8, 0, 0)
                layoutContainer.addView(textView, layoutParams)
            } else layoutContainer.addView(textView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK != resultCode && data == null)
            return

        when (requestCode) {
            TravelAdviceSearchViewModel.PICK_COUNTRY -> {
                viewModel.handleFromData(data!!)
            }
        }
    }

    private fun openBottomSheetTermsCondition() {
        val mBottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val sheetView = DataBindingUtil.inflate<BottomSheetTravelAdviceTermsBinding>(
            layoutInflater,
            R.layout.bottom_sheet_travel_advice_terms,
            null,
            false
        )

        mBottomSheetDialog.setContentView(sheetView.root)
        sheetView.tvHead.setOnClickListener {
            mBottomSheetDialog.dismiss()
        }

        sheetView.webView.loadDataWithBaseURL(
            null,
            getString(R.string.travel_advisory_level_info),
            "text/html",
            "utf-8",
            null
        )
        mBottomSheetDialog.show()
    }

    private fun showRestrictionInfo(description: RiskLevelDescription) {
        bindingView.textViewRestrictionLavel.text = description.levelMessage
        bindingView.textViewRestrictionLavel.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                description.textColor
            )
        )
        bindingView.textViewRestrictionLavel.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                description.backGroundColor
            )
        )
        bindingView.textViewRestrictionLavel.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_done_white_24dp,
            0,
            0,
            0
        )
        bindingView.textViewRestrictionLavel.setDrawableTintColor(description.tintColor)
    }

    companion object {
        const val EXTRA_COUNTRY_CODE = "extra_country_code"
        const val EXTRA_COUNTRY_NAME = "extra_country_name"
    }
}
