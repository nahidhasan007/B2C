package net.sharetrip.profile.view.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.profile.model.FaqItem
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_TERMS_AND_CONDITION_COMMON
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_TERMS_AND_CONDITION_LOYALTY
import net.sharetrip.profile.view.content.ContentFragment.Companion.ARG_TERMS_AND_CONDITION_SPIN_TO_WIN

class ContentViewModel(private val faqNumber: Int, private val contentRepo: ContentRepo) :
    BaseViewModel() {

    fun loadTOCData() {
        viewModelScope.launch { contentRepo.fetchTermsAndCondition() }
    }

    fun loadFAQData() {
        viewModelScope.launch { contentRepo.fetchFaq() }
    }


    val tocResponse: LiveData<FaqItem> = Transformations.map(contentRepo.liveDataToc) {
        when (faqNumber) {
            ARG_TERMS_AND_CONDITION_SPIN_TO_WIN -> it.spin
            ARG_TERMS_AND_CONDITION_LOYALTY -> it.loyalty
            ARG_TERMS_AND_CONDITION_COMMON -> it.common
            else -> it.spin
        }
    }

    val faqResponse: LiveData<FaqItem> = Transformations.map(contentRepo.liveData) {
        when (faqNumber) {
            ContentFragment.ARG_FAQ_HOTEL -> it.hotel
            ContentFragment.ARG_FAQ_FLIGHT -> it.flight
            ContentFragment.ARG_FAQ_HOLIDAY -> it.holiday
            ContentFragment.ARG_FAQ_TOUR -> it.tour
            ContentFragment.ARG_FAQ_TRANSFER -> it.transfer
            ContentFragment.ARG_FAQ_TRIP_COIN -> it.trip_coin
            else -> it.common_overview
        }
    }

}
