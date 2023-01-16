package net.sharetrip.flight.history.view.flightdetails

import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.history.model.FlightHistoryResponse

class FlightDetailsHistoryViewModel(private val historyResponse : FlightHistoryResponse) : BaseViewModel() {
    val dataSet = MutableLiveData<List<Any>>()
    init {
        historyResponse.segments.map { value ->
            value.segment.map {
                it.classType = historyResponse.searchParams?.class_.toString()
            }
        }
        initDetailsAdapter()
    }

    private fun initDetailsAdapter() {
        val mList = ArrayList<Any>()
        val mFlight = historyResponse.flight
        val mItemSegments = historyResponse.segments
        if (mFlight.size == mItemSegments.size) {
            val mCount = mFlight.size
            for (i in 0 until mCount) {
                val flight = mFlight[i]
                val mSegments = mItemSegments[i]
                mList.add(flight)
                val segments = mSegments.segment
                segments[segments.size - 1].transitTime = null
                mList.addAll(segments)
            }
            dataSet.value = mList
        }
    }

}