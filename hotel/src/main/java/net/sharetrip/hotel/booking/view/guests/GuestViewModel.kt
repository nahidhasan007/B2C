package net.sharetrip.hotel.booking.view.guests

import androidx.databinding.ObservableField
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.model.Traveler

class GuestViewModel(guestInfo: GuestInfo, count: Int) {
    val guest = ObservableField<GuestInfo>()
    val title = ObservableField<String>()
    val guestType: String = guestInfo.type + " " + count

    init {
        guest.set(guestInfo)
    }

    fun setQuickPickerData(guestInfo: GuestInfo, passenger: Traveler) {
        guestInfo.titleName = passenger.titleName
        guestInfo.givenName = passenger.givenName
        guestInfo.surName = passenger.surName
        guestInfo.nationality = passenger.nationality
        guest.set(guestInfo)
    }
}
