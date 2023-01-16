package net.sharetrip.visa.history.view.traveller

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import net.sharetrip.visa.history.model.TravellersItem
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.FragmentVisaHistoryTravellerDetailsBinding
import net.sharetrip.visa.history.model.TravelerSection
import net.sharetrip.visa.utils.VISA_HISTORY_TRAVELLERS
import net.sharetrip.visa.utils.setTitleAndSubtitle
import net.sharetrip.visa.utils.setToolbarTripCoin

class VisaHistoryTravellerDetailsFragment : BaseFragment<FragmentVisaHistoryTravellerDetailsBinding>() {

    private var travelersList: List<TravellersItem>? = null

    

    override fun layoutId() = R.layout.fragment_visa_history_traveller_details

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.travellers_detail))
        setToolbarTripCoin()

        travelersList = arguments?.get(VISA_HISTORY_TRAVELLERS) as List<TravellersItem>

        val sectionAdapter = SectionedRecyclerViewAdapter()

        val glm = GridLayoutManager(context, 2)
        glm.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (sectionAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                    return 2
                }
                return 1
            }
        }

        travelersList?.let {
            it.forEachIndexed { index, item ->
                sectionAdapter.addSection(TravelerSection(item, index + 1))
            }
            bindingView.recyclerTraveller.layoutManager = glm
            bindingView.recyclerTraveller.adapter = sectionAdapter
            sectionAdapter.notifyDataSetChanged()
        }
    }

    
}