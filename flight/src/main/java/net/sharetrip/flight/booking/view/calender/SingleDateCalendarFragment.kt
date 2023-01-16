package net.sharetrip.flight.booking.view.calender

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
import com.kizitonwose.calendarview.utils.yearMonth
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.AdvanceSearchResponse
import net.sharetrip.shared.model.CalenderData
import net.sharetrip.shared.model.Fare
import net.sharetrip.shared.model.ServiceType
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.shared.databinding.FragmentSingleCalendarBinding
import net.sharetrip.shared.utils.*
import org.threeten.bp.*
import java.util.*
import kotlin.math.round

internal class SingleDateCalendarFragment : BaseFragment<FragmentSingleCalendarBinding>() {
    private lateinit var visaApplicationStartingDate: LocalDate
    private lateinit var holidayBookingStartedDate: LocalDate
    private lateinit var endMonthHoliday: YearMonth
    private lateinit var endDate: LocalDate
    private lateinit var dateHintText: String
    private lateinit var advanceSearchResponse: AdvanceSearchResponse
    private lateinit var lastSevenDays: LocalDate
    private lateinit var today: LocalDate
    private lateinit var lastDay: LocalDate

    private var inActiveDays: ArrayList<Int> = ArrayList<Int>()
    private var holidayOffersValidTo: String = ""
    private var mSelectDate: LocalDate? = null
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
    private var visaPreparationMinimumDay: Int = 0

    private val sharedPreferences: SharedPrefsHelper by lazy {
        SharedPrefsHelper(requireContext())
    }
    private val viewModel: CalendarVM by viewModels()

    override fun layoutId(): Int = R.layout.fragment_single_calendar

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.applyButton.setOnClickListener {
            onApplyButtonClicked()
        }

        bindingView.directFlightSwitch.setOnClickListener { view: View ->
            directNonDirectPriceRange((view as SwitchCompat).isChecked)
        }

        observeAdvanceSearchData()

        lastDay = LocalDate.now().plusMonths(12)
        var mStartDateInMillisecond = Calendar.getInstance().timeInMillis
        val mBundle = arguments

        if (mBundle != null) {
            val calenderData: CalenderData? = arguments?.getParcelable(ARG_CALENDER_DATA)
            mStartDateInMillisecond = calenderData?.mDateInMillisecond!!

            dateHintText = calenderData.mDateHintText
            if (Strings.isBlank(dateHintText)) {
                bindingView.startDateHintTextView.text = dateHintText
            }

            fromAirportCode = calenderData.fromAirportCode
            toAirportCode = calenderData.toAirportCode
            serviceType = calenderData.serviceType
            inActiveDays = calenderData.inActiveDays
            holidayOffersValidTo = calenderData.holidayOfferValidTo
            visaPreparationMinimumDay = calenderData.visaPreparationMinimumDay
        }

        mSelectDate = Instant.ofEpochMilli(mStartDateInMillisecond).atZone(ZoneId.systemDefault())
            .toLocalDate()
        today =
            LocalDate.now().plusDays(
                sharedPreferences[PrefKey.FLIGHT_SEARCH_THRESHOLD_TIME, "20:00"].getProperFlightStartDate(
                    sharedPreferences[PrefKey.BOOK_TODAY_FLIGHT, false]
                )
            )
        lastSevenDays = LocalDate.now().minusDays(7)
        visaApplicationStartingDate =
            LocalDate.now().plusDays(visaPreparationMinimumDay.toLong() + 1)
        holidayBookingStartedDate = mSelectDate!!
        initCalender()
        bindSummaryViews()

        when (serviceType) {
            ServiceType.HOLIDAY.serviceName -> {
                bindingView.noneStopText.visibility = View.GONE
                bindingView.directFlightSwitch.visibility = View.GONE
                bindingView.priceBreakDownLayout.visibility = View.GONE
                bindingView.startDateHintTextView.text = dateHintText
                Glide.with(requireContext()).load(R.drawable.ic_calendar_mono)
                    .into(bindingView.startFlightIcon)
                bindingView.startFlightIcon.rotation = 0F
            }

            ServiceType.VISA.serviceName -> {
                bindingView.noneStopText.visibility = View.GONE
                bindingView.directFlightSwitch.visibility = View.GONE
                bindingView.priceBreakDownLayout.visibility = View.GONE
                bindingView.startDateHintTextView.text = dateHintText
                Glide.with(requireContext()).load(R.drawable.ic_calendar_mono)
                    .into(bindingView.startFlightIcon)
                bindingView.startFlightIcon.rotation = 0F
            }

            ServiceType.TRACKER.serviceName -> {
                bindingView.noneStopText.visibility = View.GONE
                bindingView.directFlightSwitch.visibility = View.GONE
                bindingView.priceBreakDownLayout.visibility = View.GONE
                bindingView.startDateHintTextView.text = dateHintText
                Glide.with(requireContext()).load(R.drawable.ic_calendar_mono)
                    .into(bindingView.startFlightIcon)
                bindingView.startFlightIcon.rotation = 0F
            }

            else -> {
                viewModel.getFlightCalenderPrice(
                    fromAirportCode,
                    toAirportCode,
                    DateUtil.parseDisplayDateFromDateForNewCalendarDot(today),
                    DateUtil.parseDisplayDateFromDateForNewCalendarDot(today.plusDays(1))
                )
            }
        }
    }

    private fun observeAdvanceSearchData() {
        viewModel.advanceSearchResponse.observe(viewLifecycleOwner){
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

    private fun onApplyButtonClicked() {
        setNavigationResult(
            mSelectDate!!.atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli(),
            ARG_SINGLE_DATE
        )
        findNavController().navigateUp()
    }

    private fun initCalender() {
        val firstDayOfWeek = daysOfWeekFromLocale().first()

        when (serviceType) {
            ServiceType.HOLIDAY.serviceName ->
                setCalenderForHoliday(firstDayOfWeek)

            ServiceType.TRACKER.serviceName ->
                setCalenderForFlightTracker(firstDayOfWeek)

            ServiceType.VISA.serviceName ->
                setCalenderForVisa(firstDayOfWeek)

            else ->
                setCalenderForOtherServiceType(firstDayOfWeek)
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.findViewById<AppCompatTextView>(R.id.exFourDayText)
            val textViewDummy = view.findViewById<AppCompatTextView>(R.id.exFourDayTextDummy)
            val roundBgView = view.findViewById<View>(R.id.exFourRoundBgView)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH && (day.date == today || day.date.isAfter(
                            today
                        ))
                    ) {
                        when (serviceType) {
                            ServiceType.HOLIDAY.serviceName -> {
                                if (!day.date.isBefore(holidayBookingStartedDate) && !checkForSpecificDay(
                                        day
                                    ) && !day.date.isAfter(endDate)
                                ) {
                                    val date = day.date
                                    mSelectDate = date
                                    bindingView.exFourCalendar.notifyCalendarChanged()
                                    bindSummaryViews()
                                }
                            }

                            ServiceType.TRACKER.serviceName -> {
                                if (!day.date.isAfter(endDate)) {
                                    val date = day.date
                                    mSelectDate = date
                                    bindingView.exFourCalendar.notifyCalendarChanged()
                                    bindSummaryViews()
                                }
                            }

                            ServiceType.VISA.serviceName -> {
                                if (!day.date.isBefore(visaApplicationStartingDate) && !day.date.isAfter(
                                        endDate
                                    )
                                ) {
                                    val date = day.date
                                    mSelectDate = date
                                    bindingView.exFourCalendar.notifyCalendarChanged()
                                    bindSummaryViews()
                                }
                            }

                            else -> {
                                if (day.owner == DayOwner.THIS_MONTH &&
                                    (day.date == today || day.date.isAfter(today))
                                ) {
                                    val date = day.date
                                    mSelectDate = date
                                    bindingView.exFourCalendar.notifyCalendarChanged()
                                    bindSummaryViews()
                                }
                            }
                        }
                    } else if (!day.date.isBefore(lastSevenDays)) {
                        when (serviceType) {
                            ServiceType.TRACKER.serviceName -> {
                                if (!day.date.isAfter(endDate)) {
                                    val date = day.date
                                    mSelectDate = date
                                    bindingView.exFourCalendar.notifyCalendarChanged()
                                    bindSummaryViews()
                                }
                            }
                        }
                    }
                }
            }
        }

        bindingView.exFourCalendar.dayBinder = object : DayBinder<DayViewContainer> {

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
                textView.text = day.day.toString()
                textView.setTextColorRes(R.color.example_4_grey_past)

                if (day.owner == DayOwner.THIS_MONTH) {
                    when (serviceType) {
                        ServiceType.HOLIDAY.serviceName ->
                            drawDateForHolidaySingleDay(day, textViewDummy, textView, roundBgView)

                        ServiceType.VISA.serviceName ->
                            drawDateForVisa(day, textViewDummy, textView, roundBgView)

                        ServiceType.TRACKER.serviceName ->
                            drawDateForFlightTracker(day, textViewDummy, textView, roundBgView)

                        else ->
                            drawDateForOtherServiceType(day, textViewDummy, textView, roundBgView)
                    }
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.findViewById<AppCompatTextView>(R.id.exFourHeaderText)
        }

        bindingView.exFourCalendar.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val monthTitle =
                        "${
                            month.yearMonth.month.name.lowercase(Locale.ROOT)
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                        } ${month.year}"
                    container.textView.text = monthTitle
                }
            }
    }

    private fun setCalenderForOtherServiceType(firstDayOfWeek: DayOfWeek) {
        val currentMonth = YearMonth.now()
        val endMonth = currentMonth.plusMonths(24)
        bindingView.exFourCalendar.setup(currentMonth, endMonth, firstDayOfWeek)
        bindingView.exFourCalendar.scrollToMonth(YearMonth.from(mSelectDate))
    }

    private fun setCalenderForHoliday(dayOfWeek: DayOfWeek) {
        val currentMonthHoliday: YearMonth = YearMonth.now()
        when {
            inActiveDays.isNotEmpty() || holidayOffersValidTo.isNotEmpty() -> {
                endMonthHoliday = LocalDate.parse(holidayOffersValidTo).yearMonth
                endDate = LocalDate.parse(holidayOffersValidTo)
            }
            else -> {
                endMonthHoliday = currentMonthHoliday.plusMonths(12)
                endDate = endMonthHoliday.atEndOfMonth()
            }
        }

        bindingView.exFourCalendar.setup(currentMonthHoliday, endMonthHoliday, dayOfWeek)
        bindingView.exFourCalendar.scrollToMonth(YearMonth.from(mSelectDate))
    }

    private fun setCalenderForFlightTracker(dayOfWeek: DayOfWeek) {
        endDate = LocalDate.now().plusDays(2)

        bindingView.exFourCalendar.setup(lastSevenDays.yearMonth, endDate.yearMonth, dayOfWeek)
        bindingView.exFourCalendar.scrollToMonth(YearMonth.from(mSelectDate))
    }

    private fun setCalenderForVisa(firstDayOfWeek: DayOfWeek) {
        endDate = LocalDate.now().plusMonths(24)

        bindingView.exFourCalendar.setup(lastSevenDays.yearMonth, endDate.yearMonth, firstDayOfWeek)
        bindingView.exFourCalendar.scrollToMonth(YearMonth.from(mSelectDate))
    }

    private fun checkForSpecificDay(day: CalendarDay): Boolean {
        when {
            inActiveDays.isEmpty() ->
                return false
            else -> {
                val array = inActiveDays
                val dayOfTheWeek = day.date.dayOfWeek
                for (i in array) {
                    if (dayOfTheWeek == DayOfWeek.of(i)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun drawDateForHolidaySingleDay(
        day: CalendarDay,
        textViewDummy: AppCompatTextView,
        textView: AppCompatTextView,
        roundBgView: View
    ) {
        if (day.date.isBefore(holidayBookingStartedDate)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)

            if (day.date == LocalDate.now()) {
                roundBgView.makeVisible()
                roundBgView.setBackgroundResource(R.drawable.today_bg)
            }
        } else if (day.date.isAfter(endDate) || checkForSpecificDay(day)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)
        } else {
            when (mSelectDate) {
                day.date -> {
                    textView.setTextColorRes(R.color.white)
                    roundBgView.makeVisible()
                    roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
                }
                else -> {
                    textView.setTextColorRes(R.color.black)
                }
            }
        }
    }

    private fun drawDateForVisa(
        day: CalendarDay,
        textViewDummy: AppCompatTextView,
        textView: AppCompatTextView,
        roundBgView: View
    ) {
        if (day.date.isBefore(visaApplicationStartingDate)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)
            if (day.date == LocalDate.now()) {
                roundBgView.makeVisible()
                roundBgView.setBackgroundResource(R.drawable.today_bg)
            }
        } else if (day.date.isAfter(endDate)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)
        } else {
            when (mSelectDate) {
                day.date -> {
                    textView.setTextColorRes(R.color.white)
                    roundBgView.makeVisible()
                    roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
                }
                else -> {
                    textView.setTextColorRes(R.color.black)
                }
            }
        }
    }

    private fun drawDateForFlightTracker(
        day: CalendarDay,
        textViewDummy: AppCompatTextView,
        textView: AppCompatTextView,
        roundBgView: View
    ) {
        if (day.date.isBefore(lastSevenDays)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)
            if (day.date == LocalDate.now()) {
                roundBgView.makeVisible()
                roundBgView.setBackgroundResource(R.drawable.today_bg)
            }
        } else if (day.date.isAfter(endDate)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)
        } else {
            when (mSelectDate) {
                day.date -> {
                    textView.setTextColorRes(R.color.white)
                    roundBgView.makeVisible()
                    roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
                }
                else -> {
                    textView.setTextColorRes(R.color.black)
                }
            }
        }
    }

    private fun drawDateForOtherServiceType(
        day: CalendarDay,
        textViewDummy: AppCompatTextView,
        textView: AppCompatTextView,
        roundBgView: View
    ) {
        if (day.date.isBefore(today)||day.date.isAfter(lastDay)) {
            textViewDummy.makeInVisible()
            textView.setTextColorRes(R.color.example_4_grey_past)
            if (day.date == LocalDate.now()) {
                roundBgView.makeVisible()
                roundBgView.setBackgroundResource(R.drawable.today_bg)
            }
        } else {
            when (mSelectDate) {
                day.date -> {
                    textView.setTextColorRes(R.color.white)
                    roundBgView.makeVisible()
                    roundBgView.setBackgroundResource(R.drawable.single_selected_bg)
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
                        val myDate = DateUtil.parseDisplayDateFromDateForNewCalendarDot(day.date)
                        val fareItem = fare.find { it.date == myDate }
                        if (fareItem != null) {
                            val datePrice = if (isDirect) fareItem.direct else fareItem.nonDirect
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
                                );
                            } else if (datePrice >= (oneThirdPrice!! + advanceSearchResponse.min.nonDirect) && datePrice < ((oneThirdPrice!! * 2) + minPrice)) {
                                textView.setTextColorRes(R.color.example_7_yellow)
                                textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    0,
                                    R.drawable.yellow_round
                                );
                            } else if (datePrice >= ((oneThirdPrice!! * 2) + minPrice)) {
                                textView.setTextColorRes(R.color.red_800)
                                textViewDummy.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    0,
                                    R.drawable.red_round
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bindSummaryViews() {
        bindingView.departureDateTextView.text =
            DateUtil.parseDisplayDateFromDateForNewCalendar(mSelectDate)
    }

    private fun directNonDirectPriceRange(isDirect: Boolean) {
        this.isDirect = isDirect
        bindingView.cheapRate.text = if (isDirect) cheapRangeDirect else cheapRangeNonDirect
        bindingView.mediumRate.text = if (isDirect) mediumRangeDirect else mediumRangeNonDirect
        bindingView.expensiveRate.text =
            if (isDirect) expansiveRangeDirect else expansiveRangeNonDirect
        bindingView.exFourCalendar.notifyCalendarChanged()
        bindSummaryViews()
    }

    companion object {
        const val EXTRA_DATE_IN_MILLISECOND =
            "BaseCalenderSingleDateSelectFragment.StartDateInMillisecond"
    }
}
