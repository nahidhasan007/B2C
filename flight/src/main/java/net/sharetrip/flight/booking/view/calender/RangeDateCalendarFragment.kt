package net.sharetrip.flight.booking.view.calender

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.AdvanceSearchResponse
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.Fare
import net.sharetrip.shared.model.ServiceType
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.shared.databinding.FragmentRangeCalendarBinding
import net.sharetrip.shared.utils.*
import org.threeten.bp.*
import java.util.*
import kotlin.math.round

internal class RangeDateCalendarFragment : BaseFragment<FragmentRangeCalendarBinding>() {
    private lateinit var bindView: FragmentRangeCalendarBinding
    private lateinit var advanceSearchResponse: AdvanceSearchResponse
    private lateinit var today: LocalDate
    private lateinit var lastDay: LocalDate
    private lateinit var visaPreparationMinimumDate: LocalDate

    private var mRangeStartDate: LocalDate? = null
    private var mRangeEndDate: LocalDate? = null
    private var cheapRangeDirect: String = "Cheap"
    private var mediumRangeDirect: String = "Medium"
    private var expansiveRangeDirect: String = "Expensive"
    private var cheapRangeNonDirect: String = "Cheap"
    private var mediumRangeNonDirect: String = "Medium"
    private var expansiveRangeNonDirect: String = "Expensive"
    private var oneThirdPriceDirect: Double? = null
    private var oneThirdPriceNonDirect: Double? = null
    private var fare = ArrayList<Fare>()
    private var isDirect: Boolean = false
    private var fromAirportCode: String = ""
    private var toAirportCode: String = ""
    private var serviceType: String = ""
    private var visaPreparationMinDay: Long = 0
    private var mStartDateHintText = ""
    private var mEndDateHintText = ""

    private var flightAdvanceSearchData: AdvanceSearchResponse? = null

    private val sharedPreferences: SharedPrefsHelper by lazy {
        SharedPrefsHelper(requireContext())
    }

    private val startBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.continuous_selected_bg_start) as GradientDrawable
    }
    private val endBackground: GradientDrawable by lazy {
        requireContext().getDrawableCompat(R.drawable.continuous_selected_bg_end) as GradientDrawable
    }

    private val viewModel: CalendarVM by viewModels()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun layoutId(): Int = R.layout.fragment_range_calendar

    override fun initOnCreateView() {
        bindView = bindingView

        bindView.applyButton.setOnClickListener {
            onApplyButtonClicked()
        }

        observeAdvanceSearchData()

        bindView.directFlightSwitch.setOnClickListener { view: View ->
            directNonDirectPriceRange((view as SwitchCompat).isChecked)
        }

        lastDay = LocalDate.now().plusMonths(12).minusDays(1)
        var mStartDateInMillisecond = Calendar.getInstance().timeInMillis
        var mEndDateInMillisecond = DateUtil.dayAfterTomorrowDateCalender.timeInMillis
        val mBundle = arguments
        if (mBundle != null) {
            val calenderData: CalenderData? =
                arguments?.getParcelable(ARG_CALENDER_DATA)

            flightAdvanceSearchData = calenderData?.searchResponse

            mStartDateInMillisecond = calenderData?.mDateInMillisecond!!
            mEndDateInMillisecond = calenderData.mEndDateInMillisecond
            mStartDateHintText = calenderData.mDateHintText
            mEndDateHintText = calenderData.mEndDateHintText
            fromAirportCode = calenderData.fromAirportCode
            toAirportCode = calenderData.toAirportCode
            serviceType = calenderData.serviceType
            visaPreparationMinDay = calenderData.visaPreparationMinimumDay.toLong()

            if (!Strings.isBlank(mStartDateHintText)) {
                bindView.startDateHintTextView.text = mStartDateHintText
            }
            if (!Strings.isBlank(mEndDateHintText)) {
                bindView.endDateHintTextView.text = mEndDateHintText
            }
        }
        mRangeStartDate =
            Instant.ofEpochMilli(mStartDateInMillisecond).atZone(ZoneId.systemDefault())
                .toLocalDate()
        mRangeEndDate =
            Instant.ofEpochMilli(mEndDateInMillisecond).atZone(ZoneId.systemDefault()).toLocalDate()
        today = when (serviceType) {
            ServiceType.FLIGHT.serviceName -> {
                LocalDate.now().plusDays(
                    sharedPreferences[PrefKey.FLIGHT_SEARCH_THRESHOLD_TIME, "20:00"].getProperFlightStartDate(
                        sharedPreferences[PrefKey.BOOK_TODAY_FLIGHT, false]
                    )
                )
            }
            ServiceType.VISA.serviceName -> {
                visaPreparationMinimumDate = LocalDate.now().plusDays(visaPreparationMinDay)
                LocalDate.now()
            }
            ServiceType.HOTEL.serviceName -> {
                LocalDate.now()
            }
            else -> LocalDate.now().plusDays(3)
        }
        initCalender()
        bindSummaryViews()
        when (serviceType) {
            ServiceType.FLIGHT.serviceName -> {
                viewModel.getFlightCalenderPrice(
                    fromAirportCode,
                    toAirportCode,
                    DateUtil.parseDisplayDateFromDateForNewCalendarDot(today),
                    DateUtil.parseDisplayDateFromDateForNewCalendarDot(today.plusDays(1))
                )
            }
            ServiceType.VISA.serviceName -> {
                initializeUIForVisa()
            }
            else -> {
                initializeUIForOtherService()
            }
        }
    }

    private fun observeAdvanceSearchData() {
        viewModel.advanceSearchResponse.observe(viewLifecycleOwner) {
            onGetCalendarPrice(it)
        }
    }

    private fun onGetCalendarPrice(response: AdvanceSearchResponse) {
        advanceSearchResponse = response
        fare = advanceSearchResponse.fare as ArrayList<Fare>
        oneThirdPriceDirect =
            round((advanceSearchResponse.max.direct - advanceSearchResponse.min.direct) / 3)
        oneThirdPriceNonDirect =
            round((advanceSearchResponse.max.nonDirect - advanceSearchResponse.min.nonDirect) / 3)

        cheapRangeDirect =
            advanceSearchResponse.min.direct.convertToK() + " - " + (oneThirdPriceDirect!! + advanceSearchResponse.min.direct - 1).convertToK()
        mediumRangeDirect =
            ((oneThirdPriceDirect!! + advanceSearchResponse.min.direct)).convertToK() + " - " + ((oneThirdPriceDirect!! * 2) + advanceSearchResponse.min.direct - 1).convertToK()
        expansiveRangeDirect =
            ((oneThirdPriceDirect!! * 2) + advanceSearchResponse.min.direct).convertToK() + " - " + advanceSearchResponse.max.direct.convertToK()

        cheapRangeNonDirect =
            advanceSearchResponse.min.nonDirect.convertToK() + " - " + (oneThirdPriceNonDirect!! + advanceSearchResponse.min.nonDirect - 1).convertToK()
        mediumRangeNonDirect =
            (oneThirdPriceNonDirect!! + advanceSearchResponse.min.nonDirect).convertToK() + " - " + ((oneThirdPriceNonDirect!! * 2) + advanceSearchResponse.min.nonDirect - 1).convertToK()
        expansiveRangeNonDirect =
            ((oneThirdPriceNonDirect!! * 2) + advanceSearchResponse.min.nonDirect).convertToK() + " - " + advanceSearchResponse.max.nonDirect.convertToK()
        directNonDirectPriceRange(false)
    }

    private fun directNonDirectPriceRange(isDirect: Boolean) {
        this.isDirect = isDirect
        bindView.cheapRate.text = if (isDirect) cheapRangeDirect else cheapRangeNonDirect
        bindView.mediumRate.text = if (isDirect) mediumRangeDirect else mediumRangeNonDirect
        bindView.expensiveRate.text =
            if (isDirect) expansiveRangeDirect else expansiveRangeNonDirect
        bindView.exFourCalendar.notifyCalendarChanged()
        bindSummaryViews()
    }

    private fun initCalender() {
        bindView.exFourCalendar.post {
            val radius = ((bindView.exFourCalendar.width / 7) / 2).toFloat()
            startBackground.setCornerRadius(topLeft = radius, bottomLeft = radius)
            endBackground.setCornerRadius(topRight = radius, bottomRight = radius)
        }
        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = if (serviceType == ServiceType.FLIGHT.serviceName)
            YearMonth.now()
        else
            YearMonth.from(today)
        bindView.exFourCalendar.setup(currentMonth, currentMonth.plusMonths(24), daysOfWeek.first())
        bindView.exFourCalendar.scrollToMonth(YearMonth.from(mRangeStartDate))

        bindView.exFourCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val textViewDummy = container.textViewDummy
                val roundBgView = container.roundBgView
                textView.text = null
                textView.background = null
                roundBgView.makeInVisible()
                textViewDummy.makeInVisible()

                when (serviceType) {
                    ServiceType.VISA.serviceName -> {
                        drawDateForVisa(day, textView, textViewDummy, roundBgView)
                    }
                    else -> {
                        drawDateForOtherServiceType(day, textView, textViewDummy, roundBgView)
                    }
                }
            }
        }

        bindView.exFourCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val monthTitle =
                        "${
                            month.yearMonth.month.name.lowercase()
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        } ${month.year}"
                    container.textView.text = monthTitle
                }
            }
    }

    private fun bindSummaryViews() {
        if (mRangeStartDate != null) {
            bindView.departureDateTextView.text =
                DateUtil.parseDisplayDateFromDateForNewCalendar(mRangeStartDate)
        }
        if (mRangeEndDate != null) {
            bindView.applyButton.isEnabled = true
            bindView.returnDateTextView.text =
                DateUtil.parseDisplayDateFromDateForNewCalendar(mRangeEndDate)
        } else {
            when (serviceType) {
                ServiceType.FLIGHT.serviceName -> {
                    bindView.applyButton.isEnabled = true
                    bindView.returnDateTextView.text =
                        DateUtil.parseDisplayDateFromDateForNewCalendar(mRangeStartDate)
                }
                ServiceType.VISA.serviceName -> {
                    bindView.returnDateTextView.text = ""
                    bindView.applyButton.isEnabled = true
                }
                else -> {
                    bindView.returnDateTextView.text = ""
                    bindView.applyButton.isEnabled = false
                }
            }
        }
    }

    private fun initializeUIForOtherService() {
        bindView.noneStopText.visibility = View.GONE
        bindView.directFlightSwitch.visibility = View.GONE
        bindView.priceBreakDownLayout.visibility = View.GONE
        bindView.startDateHintTextView.text = getString(R.string.check_in_date)
        bindView.endDateHintTextView.text = getString(R.string.check_out_date)
        Glide.with(requireContext()).load(R.drawable.ic_hotel_56dp_blue)
            .into(bindView.startFlightIcon)
        Glide.with(requireContext()).load(R.drawable.ic_hotel_56dp_blue)
            .into(bindView.endFlightIcon)
        bindView.startFlightIcon.rotation = 0F
        bindView.endFlightIcon.rotation = 0F
    }

    private fun initializeUIForVisa() {
        bindView.noneStopText.visibility = View.GONE
        bindView.directFlightSwitch.visibility = View.GONE
        bindView.priceBreakDownLayout.visibility = View.GONE
        bindView.startDateHintTextView.text = mStartDateHintText
        bindView.endDateHintTextView.text = mEndDateHintText
        Glide.with(requireContext()).load(R.drawable.ic_calendar_mono)
            .into(bindView.startFlightIcon)
        Glide.with(requireContext()).load(R.drawable.ic_calendar_mono).into(bindView.endFlightIcon)
        bindView.startFlightIcon.rotation = 0F
        bindView.endFlightIcon.rotation = 0F
    }

    private fun onApplyButtonClicked() {
        val endDate = if (mRangeEndDate != null) mRangeEndDate!! else mRangeStartDate!!
        val intent = Intent()
        intent.putExtra(
            EXTRA_START_DATE_IN_MILLISECOND,
            mRangeStartDate!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        intent.putExtra(
            EXTRA_END_DATE_IN_MILLISECOND,
            endDate.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        setNavigationResult(
            intent,
            ARG_RANGE_DATE
        )
        findNavController().navigateUp()
    }

    private fun drawDateForVisa(
        day: CalendarDay,
        textView: AppCompatTextView,
        textViewDummy: AppCompatTextView,
        roundBgView: View
    ) {
        if (day.owner == DayOwner.THIS_MONTH) {
            textView.text = day.day.toString()

            if (day.date.isBefore(visaPreparationMinimumDate.plusDays(1))) {
                textViewDummy.makeInVisible()
                textView.setTextColorRes(R.color.example_4_grey_past)
                if (day.date == LocalDate.now()) {
                    roundBgView.makeVisible()
                    roundBgView.setBackgroundResource(R.drawable.today_bg)
                }
            } else {

                when {
                    mRangeStartDate == day.date && mRangeEndDate == null -> {
                        textView.setTextColorRes(R.color.white)
                        roundBgView.makeVisible()
                        roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
                    }

                    day.date == mRangeStartDate -> {
                        textView.requestFocus()
                        textView.setTextColorRes(R.color.white)
                        textView.background = startBackground
                    }

                    mRangeStartDate != null && mRangeEndDate != null && (day.date > mRangeStartDate && day.date < mRangeEndDate) -> {
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                    }

                    day.date == mRangeEndDate -> {
                        textView.setTextColorRes(R.color.white)
                        textView.background = endBackground
                    }

                    else -> {
                        textViewDummy.makeVisible()
                        textView.setTextColorRes(R.color.black)
                        textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            R.drawable.ash_round
                        )
                        textViewDummy.makeInVisible()
                    }
                }
            }
        } else {

            if (day.date.isBefore(visaPreparationMinimumDate.plusDays(1))) {
                textViewDummy.makeInVisible()
                textView.setTextColorRes(R.color.example_4_grey_past)
            } else {
                val startDate = mRangeStartDate
                val endDate = mRangeEndDate
                if (startDate != null && endDate != null) {
                    // Mimic selection of inDates that are less than the startDate.
                    // Example: When 26 Feb 2019 is startDate and 5 Mar 2019 is endDate,
                    // this makes the inDates in Mar 2019 for 24 & 25 Feb 2019 look selected.
                    if ((day.owner == DayOwner.PREVIOUS_MONTH
                                && startDate.monthValue == day.date.monthValue
                                && endDate.monthValue != day.date.monthValue) ||
                        // Mimic selection of outDates that are greater than the endDate.
                        // Example: When 25 Apr 2019 is startDate and 2 May 2019 is endDate,
                        // this makes the outDates in Apr 2019 for 3 & 4 May 2019 look selected.
                        (day.owner == DayOwner.NEXT_MONTH
                                && startDate.monthValue != day.date.monthValue
                                && endDate.monthValue == day.date.monthValue) ||

                        // Mimic selection of in and out dates of intermediate
                        // months if the selection spans across multiple months.
                        (startDate < day.date && endDate > day.date
                                && startDate.monthValue != day.date.monthValue
                                && endDate.monthValue != day.date.monthValue)
                    ) {
                        textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                    }
                }
            }
        }
    }

    private fun drawDateForOtherServiceType(
        day: CalendarDay,
        textView: AppCompatTextView,
        textViewDummy: AppCompatTextView,
        roundBgView: View
    ) {
        if (day.owner == DayOwner.THIS_MONTH) {
            textView.text = day.day.toString()

            if (day.date.isBefore(today) || day.date.isAfter(lastDay)) {
                textViewDummy.makeInVisible()
                textView.setTextColorRes(R.color.example_4_grey_past)
                if (day.date == LocalDate.now()) {
                    roundBgView.makeVisible()
                    roundBgView.setBackgroundResource(R.drawable.today_bg)
                }
            } else {
                when {
                    mRangeStartDate == day.date && mRangeEndDate == null -> {
                        textView.setTextColorRes(R.color.white)
                        roundBgView.makeVisible()
                        roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
                    }

                    day.date == mRangeStartDate -> {
                        textView.requestFocus()
                        textView.setTextColorRes(R.color.white)
                        textView.background = startBackground
                    }

                    mRangeStartDate != null && mRangeEndDate != null && (day.date > mRangeStartDate && day.date < mRangeEndDate) -> {
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                    }

                    day.date == mRangeEndDate -> {
                        textView.setTextColorRes(R.color.white)
                        textView.background = endBackground
                    }

                    else -> {
                        textViewDummy.makeVisible()
                        textView.setTextColorRes(R.color.black)
                        textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            0,
                            R.drawable.ash_round
                        )
                        if (fare.isNotEmpty()) {
                            val myDate =
                                DateUtil.parseDisplayDateFromDateForNewCalendarDot(day.date)
                            val fareItem = fare.find { it.date == myDate }
                            if (fareItem != null) {
                                val datePrice =
                                    if (isDirect) fareItem.direct else fareItem.nonDirect
                                val oneThirdPrice =
                                    if (isDirect) oneThirdPriceDirect else oneThirdPriceNonDirect
                                val minPrice =
                                    if (isDirect) advanceSearchResponse.min.direct else advanceSearchResponse.min.nonDirect

                                if (datePrice > 0 && datePrice < (oneThirdPrice!! + minPrice)) {
                                    textView.setTextColorRes(R.color.green_700)
                                    textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        0,
                                        R.drawable.green_round
                                    )
                                } else if (datePrice >= (oneThirdPrice!! + advanceSearchResponse.min.nonDirect) && datePrice < ((oneThirdPrice * 2) + minPrice)) {
                                    textView.setTextColorRes(R.color.example_7_yellow)
                                    textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        0,
                                        R.drawable.yellow_round
                                    )
                                } else if (datePrice >= ((oneThirdPrice * 2) + minPrice)) {
                                    textView.setTextColorRes(R.color.red_800)
                                    textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        0,
                                        R.drawable.red_round
                                    )
                                }
                            }
                        } else {
                            textViewDummy.makeInVisible()
                        }
                    }
                }
            }
        } else {

            // This part is to make the coloured selection background continuous
            // on the blank in and out dates across various months and also on dates(months)
            // between the start and end dates if the selection spans across multiple months.

            val startDate = mRangeStartDate
            val endDate = mRangeEndDate
            if (startDate != null && endDate != null) {
                // Mimic selection of inDates that are less than the startDate.
                // Example: When 26 Feb 2019 is startDate and 5 Mar 2019 is endDate,
                // this makes the inDates in Mar 2019 for 24 & 25 Feb 2019 look selected.
                if ((day.owner == DayOwner.PREVIOUS_MONTH
                            && startDate.monthValue == day.date.monthValue
                            && endDate.monthValue != day.date.monthValue) ||
                    // Mimic selection of outDates that are greater than the endDate.
                    // Example: When 25 Apr 2019 is startDate and 2 May 2019 is endDate,
                    // this makes the outDates in Apr 2019 for 3 & 4 May 2019 look selected.
                    (day.owner == DayOwner.NEXT_MONTH
                            && startDate.monthValue != day.date.monthValue
                            && endDate.monthValue == day.date.monthValue) ||

                    // Mimic selection of in and out dates of intermediate
                    // months if the selection spans across multiple months.
                    (startDate < day.date && endDate > day.date
                            && startDate.monthValue != day.date.monthValue
                            && endDate.monthValue != day.date.monthValue)
                ) {
                    textView.setBackgroundResource(R.drawable.continuous_selected_bg_middle)
                }
            }
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        lateinit var day: CalendarDay // Will be set when this container is bound.
        val textView: AppCompatTextView = view.findViewById(R.id.exFourDayText)
        val textViewDummy: AppCompatTextView = view.findViewById(R.id.exFourDayTextDummy)
        val roundBgView: View = view.findViewById(R.id.exFourRoundBgView)

        init {
            view.setOnClickListener {

                when (serviceType) {
                    ServiceType.VISA.serviceName ->
                        checkDateRangeForVisa()

                    else ->
                        checkDateRangeForOtherServices()
                }
            }
        }

        private fun checkDateRangeForVisa() {
            if (day.date.isAfter(visaPreparationMinimumDate)) {
                val date = day.date

                if (mRangeStartDate != null) {
                    if (date < mRangeStartDate || mRangeEndDate != null) {
                        mRangeStartDate = date
                        mRangeEndDate = null
                    } else if (date != mRangeStartDate) {
                        mRangeEndDate = date
                    }
                } else {
                    mRangeStartDate = date
                }

                bindView.exFourCalendar.notifyCalendarChanged()
                bindSummaryViews()
            }
        }

        private fun checkDateRangeForOtherServices() {
            if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(today))) {
                val date = day.date
                if (mRangeStartDate != null) {
                    if (date < mRangeStartDate || mRangeEndDate != null) {
                        mRangeStartDate = date
                        mRangeEndDate = null
                    } else if (date != mRangeStartDate) {
                        mRangeEndDate = date
                    }
                } else {
                    mRangeStartDate = date
                }

                bindView.exFourCalendar.notifyCalendarChanged()
                bindSummaryViews()
            }
        }
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView: AppCompatTextView = view.findViewById(R.id.exFourHeaderText)
    }

}
