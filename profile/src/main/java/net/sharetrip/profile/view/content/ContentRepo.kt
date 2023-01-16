package net.sharetrip.profile.view.content

import androidx.lifecycle.MutableLiveData
import net.sharetrip.profile.base.BaseRepository
import net.sharetrip.profile.model.FaqResponse
import net.sharetrip.profile.model.TocResponse
import net.sharetrip.profile.network.ProfileApiService

class ContentRepo(private val apiService: ProfileApiService) : BaseRepository() {

    val liveData = MutableLiveData<FaqResponse>()
    val liveDataToc = MutableLiveData<TocResponse>()

    suspend fun fetchFaq() {
        callApi<FaqResponse>(
            executableCodeBlock = {
                apiService.getFAQResponse()
            },

            onSuccess = {
                liveData.value = it
            },

            onFailed = { _, _ -> }
        )
    }

    suspend fun fetchTermsAndCondition() {
        callApi<TocResponse>(
            executableCodeBlock = {
                apiService.getTOCResponse()
            },

            onSuccess = {
                liveDataToc.value = it
            },

            onFailed = { _, _ -> }
        )
    }
}
