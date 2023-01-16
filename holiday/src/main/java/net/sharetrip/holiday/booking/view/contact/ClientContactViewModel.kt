package net.sharetrip.holiday.booking.view.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch
import net.sharetrip.holiday.booking.model.HolidayContact
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.booking.model.HolidaySummary
import net.sharetrip.holiday.booking.model.PrimaryContact

class ClientContactViewModel(
     val param: HolidayParam,
    private val sharedPrefsHelper: SharedPrefsHelper,
    private val clientContactRepository: ClientContactRepository
) : BaseViewModel() {

    lateinit var summary: HolidaySummary

    val contact = PrimaryContact()
    val primaryContact :LiveData<HolidayContact> = clientContactRepository.primaryContact
    val isDataLoading:LiveData<Boolean> = clientContactRepository.dataLoading
    val msg = MutableLiveData<String>()

    init {
        fetchContactInfo()
    }

    private fun fetchContactInfo() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        viewModelScope.launch {
            clientContactRepository.getHolidayContactFromRemoteSource(token)
        }
    }

    fun setSummaryParams(
        title: String,
        givenName: String,
        surName: String,
        mobile: String,
        email: String,
        address: String
    ): Boolean {
        contact.apply {
            this.titleName = title
            this.givenName = givenName
            this.surName = surName
            this.email = email
            this.mobileNumber = mobile
            this.address1 = address
        }

        return if (contact.isDataValid()) {
            val totalChildren = param.infant + param.child3to6 + param.child7to12
            var adultAndChild: String
            adultAndChild = if (param.adult > 1) {
                "${param.adult} Adults"
            } else {
                "${param.adult} Adult"
            }

            if (totalChildren > 0)
                adultAndChild = "$adultAndChild $totalChildren Children"

             summary = HolidaySummary(
                title = param.hotelNames,
                date = param.packageDate,
                adult = adultAndChild,
                room = (param.singleRoom + param.twinRoom + param.doubleRoom + param.tripleRoom + param.quadRoom),
                address = param.hotelCity,
                totalCost = param.totalAmount,
                earnTripCoin = param.earnPoint,
                discountedPrice = param.discountAmount
            )
            true
        } else {
            msg.value = "Fill up all required info"
            false
        }
    }
}
