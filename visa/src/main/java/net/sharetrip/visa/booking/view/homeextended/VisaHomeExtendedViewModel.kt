package net.sharetrip.visa.booking.view.homeextended

import android.content.Intent
import androidx.databinding.ObservableField
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.ServiceType
import net.sharetrip.shared.utils.DateUtil
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.visa.booking.model.ExtendedHomeNavigationKey
import net.sharetrip.visa.booking.model.VisaCountry
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.countrysearch.VisaCountrySearchFragment.Companion.EXTRA_VISA_PROPERTY
import net.sharetrip.visa.booking.view.traveller.VisaTravellerNumberFragment.Companion.EXTRA_NUMBER_OF_TRAVELLERS_FOR_VISA
import net.sharetrip.visa.utils.MsgUtils.exitDateWarningMsg
import net.sharetrip.visa.utils.MsgUtils.selectDateMsg
import org.threeten.bp.LocalDate
import java.text.ParseException
import java.util.*

class VisaHomeExtendedViewModel(private val visaSearchQuery: VisaSearchQuery) : BaseViewModel() {
    private var visaCountry: VisaCountry? = null
    private var entryDate = ""
    var countryName = ObservableField<String>()
    var dateOfEntry = ObservableField<String>()
    var dateOfExit = ObservableField<String>()
    var travellersCount = ObservableField<String>()

    init {
        initUI()
    }

    private fun initUI() {
        val travelers = visaSearchQuery.travellerCount.toString() + " Traveller(s)"
        travellersCount.set(travelers)
        countryName.set(visaSearchQuery.destinationCountry)
        setInitialTravelDate()
    }

    private fun setInitialTravelDate() {
        visaSearchQuery.entryDate =
            LocalDate.now().plusDays((visaSearchQuery.visaPrepMinDays!! + 1).toLong()).toString()
        visaSearchQuery.exitDate =
            LocalDate.now().plusDays((visaSearchQuery.visaPrepMinDays!! + 8).toLong()).toString()
        val entry = DateUtil.parseDisplayDateFormatFromApiDateFormat(visaSearchQuery.entryDate)
        val out = DateUtil.parseDisplayDateFormatFromApiDateFormat(visaSearchQuery.exitDate)
        dateOfEntry.set(entry)

        if (entry != out) {
            dateOfExit.set(out)
        } else {
            dateOfExit.set("Select date")
        }

        entryDate = visaSearchQuery.entryDate
    }

    fun resetVisaSearchQuery() {
        visaSearchQuery.nationality = "BD"
    }

    private fun getStartDateInMillisecond(): Long {
        val mStartDateInMillisecond: Long = try {
            val mStartDateString = visaSearchQuery.entryDate
            DateUtil.parseDateToMillisecond(mStartDateString)
        } catch (e: ParseException) {
            Calendar.getInstance().timeInMillis
        }

        return mStartDateInMillisecond
    }

    private fun getEndDateInMillisecond(): Long {
        val mEndDateInMillisecond: Long = try {
            val mEndDateString = visaSearchQuery.exitDate
            DateUtil.parseDateToMillisecond(mEndDateString)
        } catch (e: ParseException) {
            DateUtil.getDayAfterTomorrowDateInMillisecondForVisa()
        }

        return mEndDateInMillisecond
    }

    fun handleActivityResult(requestCode: Int, data: Intent) {
        when (requestCode) {
            PICK_VISA_COUNTRY_REQUEST -> handleCountry(data)

            PICK_TRAVELER_COUNT_REQUEST -> handleTravelersCount(data)
        }
    }

    private fun handleCountry(data: Intent) {
        visaCountry = data.getParcelableExtra(EXTRA_VISA_PROPERTY)
        visaSearchQuery.visaCountryCode = visaCountry?.visaCountryCode!!
        visaSearchQuery.destinationCountry = visaCountry?.countryName!!
        visaSearchQuery.visaPrepMinDays = visaCountry?.visaPrepMinDays

        setInitialTravelDate()
        countryName.set(visaCountry?.countryName)
    }

    fun setTravelDate(requestCode: Int, dateLong: Long) {
        val mStartDate = DateUtil.parseApiDateFormatFromMillisecond(dateLong)
        val entry = DateUtil.parseDisplayDateFormatFromApiDateFormat(mStartDate)
        entryDate = mStartDate

        when (requestCode) {
            PICK_ENTRY_DATE_REQUEST -> {
                visaSearchQuery.entryDate = mStartDate
                dateOfEntry.set(entry)

                if (visaSearchQuery.exitDate.isNotEmpty()) {
                    if (DateUtil.parseDateToMillisecond(visaSearchQuery.exitDate) <= DateUtil.parseDateToMillisecond(
                            visaSearchQuery.entryDate
                        )
                    ) {
                        visaSearchQuery.exitDate =
                            LocalDate.parse(entryDate).plusDays(1).toString()
                        dateOfExit.set(selectDateMsg)
                    }
                }
            }

            PICK_EXIT_DATE_REQUEST -> {
                visaSearchQuery.exitDate = mStartDate
                if (DateUtil.parseDateToMillisecond(visaSearchQuery.exitDate) <= DateUtil.parseDateToMillisecond(
                        visaSearchQuery.entryDate
                    )
                ) {
                    visaSearchQuery.exitDate = ""
                    dateOfExit.set(selectDateMsg)
                    showMessage(exitDateWarningMsg)
                    return
                }
                visaSearchQuery.exitDate = mStartDate
                dateOfExit.set(entry)
            }
        }
    }

    private fun handleTravelersCount(data: Intent) {
        val traveler = data.getIntExtra(EXTRA_NUMBER_OF_TRAVELLERS_FOR_VISA, 1)
        val travelers = "$traveler Traveller(s)"
        visaSearchQuery.travellerCount = traveler
        travellersCount.set(travelers)
    }

    fun onDestinationFieldClicked() {
        navigateWithArgument(ExtendedHomeNavigationKey.Destination.name, "")
    }

    fun onTravellerCountClicked() {
        navigateWithArgument(
            ExtendedHomeNavigationKey.TravellerCount.name,
            visaSearchQuery.travellerCount
        )

    }

    fun onClickedSearch() {
        navigateWithArgument(
            ExtendedHomeNavigationKey.Search.name,
            visaSearchQuery
        )

    }

    fun onClickEntryDateRange() {
        val startDateHintText = "Date of Entry"
        val calenderData = CalenderData(
            getStartDateInMillisecond(),
            mDateHintText = startDateHintText,
            serviceType = ServiceType.VISA.serviceName,
            visaPreparationMinimumDay = visaSearchQuery.visaPrepMinDays!!
        )
        navigateWithArgument(ExtendedHomeNavigationKey.EntryDate.name, calenderData)
    }

    fun onClickExitDateRequest() {
        val hintText = "Date of Exit"
        val calenderData = CalenderData(
            getEndDateInMillisecond(),
            mDateHintText = hintText,
            serviceType = ServiceType.VISA.serviceName,
            visaPreparationMinimumDay = visaSearchQuery.visaPrepMinDays!!,
            isVisaStartDateSelected = true,
            visaStartDate = entryDate
        )
        navigateWithArgument(ExtendedHomeNavigationKey.ExitDate.name, calenderData)
    }

    companion object {
        const val PICK_VISA_COUNTRY_REQUEST = 501
        const val PICK_TRAVELER_COUNT_REQUEST = 504
        const val PICK_ENTRY_DATE_REQUEST = 505
        const val PICK_EXIT_DATE_REQUEST = 506
    }
}
