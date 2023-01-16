package net.sharetrip.visa.booking.view.traveller

import android.content.Intent
import androidx.databinding.ObservableInt
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.booking.view.traveller.VisaTravellerNumberFragment.Companion.EXTRA_NUMBER_OF_TRAVELLERS_FOR_VISA
import net.sharetrip.visa.utils.SingleLiveEvent

class VisaTravellerNumberViewModel(private var numberOfAdult: Int) : BaseViewModel() {
    val navigateBackWithData = SingleLiveEvent<Intent>()
    val travellerNumber = ObservableInt()

    init {
        travellerNumber.set(numberOfAdult)
    }

    fun onClickedAdultRemove() {
        if (numberOfAdult > 1) {
            numberOfAdult--
            travellerNumber.set(numberOfAdult)
        }
    }

    fun onDoneClicked() {
        val intent = Intent()
        intent.putExtra(EXTRA_NUMBER_OF_TRAVELLERS_FOR_VISA, numberOfAdult)
        navigateBackWithData.value = intent
    }

    fun onClickedAdultAdd() {
        numberOfAdult++
        travellerNumber.set(numberOfAdult)
    }
}
