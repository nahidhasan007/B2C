package net.sharetrip.visa.booking.view.verification

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.booking.model.VisaItemTraveler
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.model.VisaType
import net.sharetrip.visa.utils.MsgUtils.TERMS_AND_CONDITION
import net.sharetrip.visa.utils.SingleLiveEvent

class TravelerVerificationViewModel(private val visaSearchQuery: VisaSearchQuery) :
    BaseViewModel() {
    val passenger: VisaItemTraveler?
    var travellerInfo = ObservableField<String>()
    var isStickerVisa = ObservableBoolean()
    val isCheckConfirm = ObservableBoolean(false)
    val navigateToCheckout = SingleLiveEvent<VisaSearchQuery>()
    val navigateToPhotoVerification = SingleLiveEvent<VisaSearchQuery>()

    init {
        passenger = visaSearchQuery.travellers[visaSearchQuery.currentTravellerPosition!!]
        travellerInfo.set(visaSearchQuery.travellerLabelInfo())

        if (visaSearchQuery.visaType.equals(VisaType.StickerVisa.productName)) {
            isStickerVisa.set(true)
        }
    }

    fun onNext() {
        if (isCheckConfirm.get()) {
            if (isStickerVisa.get()) {
                navigateToCheckout.value = visaSearchQuery
            } else {
                navigateToPhotoVerification.value = visaSearchQuery
            }
        } else {
             showMessage(TERMS_AND_CONDITION)
        }
    }
}
