package net.sharetrip.view.home.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.R
import net.sharetrip.databinding.ItemDashboardHeaderBinding
import net.sharetrip.holiday.booking.HolidayBookingActivity
import net.sharetrip.holiday.booking.model.HolidayCity
import net.sharetrip.holiday.booking.model.HolidayDiscountType
import net.sharetrip.holiday.booking.model.HolidayItem
import net.sharetrip.holiday.utils.ARG_HOLIDAY_CITY_CODE
import net.sharetrip.holiday.utils.ARG_HOLIDAY_CITY_NAME
import net.sharetrip.holiday.utils.HOLIDAY_NAVIGATION_ACTION
import net.sharetrip.holiday.utils.HolidayNavigationActions
import net.sharetrip.hotel.booking.HotelBookingActivity
import net.sharetrip.shared.databinding.ItemHolidayBinding
import net.sharetrip.view.home.HomeActionsViewModel
import net.sharetrip.view.home.HomeViewModel
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.wheel.WheelActivity
import java.text.NumberFormat
import java.util.*

class HolidayAdapter(
    private val dashBoardViewModel: HomeActionsViewModel,
    private val homeViewModel: HomeViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val holidayList = ArrayList<HolidayItem>()

    init {
        holidayList.add(HolidayItem())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val binding = DataBindingUtil.inflate<ItemHolidayBinding>(
                LayoutInflater.from(parent.context), R.layout.item_holiday, parent, false
            )
            return HolidayViewHolder(binding)
        }

        val binding = DataBindingUtil.inflate<ItemDashboardHeaderBinding>(
            LayoutInflater.from(parent.context), R.layout.item_dashboard_header, parent, false
        )
        return DashboardHeaderViewHolder(binding)
    }

    override fun getItemCount() = holidayList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as DashboardHeaderViewHolder).dataBind(dashBoardViewModel)
        } else {
            (holder as HolidayViewHolder).bind(holidayList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return VIEW_TYPE_HEADER
        }
        return VIEW_TYPE_ITEM
    }

    fun update(holidays: ArrayList<HolidayItem>) {
        holidayList.addAll(holidays)
        notifyDataSetChanged()
    }

    inner class HolidayViewHolder(private val holidayBinding: ItemHolidayBinding) :
        RecyclerView.ViewHolder(holidayBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: HolidayItem) {
            holidayBinding.textViewTime.text = item.duration.toString() + " Day"
            holidayBinding.textViewTripCoin.text =
                "" + NumberFormat.getNumberInstance(Locale.US).format(item.point.earningPoint)
            holidayBinding.textViewPrice.text =
                item.currency + " " + NumberFormat.getNumberInstance(Locale.US)
                    .format(item.discountPrice)
            holidayBinding.textViewDescription.text = item.title

            item.discount?.let {
                if (it > 0) {
                    val price = NumberFormat.getNumberInstance(Locale.US).format(item.lowestPrice)
                    val string = SpannableString("${item.currency} $price")
                    string.setSpan(
                        StrikethroughSpan(),
                        0,
                        string.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    holidayBinding.textViewPriceDiscounted.text = string

                    if (item.discountType.contentEquals(HolidayDiscountType.Percentage.toString())) {
                        val discountAmount = NumberFormat.getNumberInstance(Locale.US).format(it)
                        holidayBinding.textViewDiscount.text = "*${discountAmount}%"
                    }
                } else {
                    holidayBinding.textViewDiscount.visibility = GONE
                    holidayBinding.textViewPriceDiscounted.visibility = GONE
                }
            }

            var location = ""
            for (i in item.locations!!.indices) {
                location = if (i != item.locations!!.size - 1) {
                    "$location${item.locations!![i]} - "
                } else {
                    "$location${item.locations!![i]}"
                }
            }

            holidayBinding.textViewLocation.text = location
            if (item.withAirfare!!.endsWith("yes", true)) {
                holidayBinding.textViewWitAirFair.visibility = VISIBLE
            } else {
                holidayBinding.textViewWitAirFair.visibility = GONE
            }

            if (item.image.isNotEmpty()) {
                Glide.with(holidayBinding.imageViewTrip.context)
                    .load(item.image[0])
                    .apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holidayBinding.imageViewTrip)
            }
        }
    }

    inner class DashboardHeaderViewHolder(private val dashboardHeaderBinding: ItemDashboardHeaderBinding) :
        RecyclerView.ViewHolder(dashboardHeaderBinding.root) {

        private val cityAdapter = PopularCityAdapter()
        private val handler = Handler(Looper.getMainLooper())
        private val handlerWheel = Handler(Looper.getMainLooper())
        private lateinit var runnable: Runnable
        private lateinit var runnableWheel: Runnable

        init {
            runnable = Runnable {
                dashboardHeaderBinding.animationViewJerking.enableMergePathsForKitKatAndAbove(true)
                dashboardHeaderBinding.animationViewJerking.playAnimation()
                handler.postDelayed(runnable, 8000)
            }

            runnableWheel = Runnable {
                dashboardHeaderBinding.animationViewWheel.enableMergePathsForKitKatAndAbove(true)
                dashboardHeaderBinding.animationViewWheel.playAnimation()
                handlerWheel.postDelayed(runnableWheel, 12000)
            }

            dashBoardViewModel.showTimer()
        }

        fun dataBind(viewModel: HomeActionsViewModel) {
            dashboardHeaderBinding.viewModel = viewModel
            dashboardHeaderBinding.cities.setHasFixedSize(true)
            dashboardHeaderBinding.cities.adapter = cityAdapter

            viewModel.popularCity.observeForever {
                cityAdapter.update(it as ArrayList<HolidayCity>)
            }

            viewModel.openFlight.observeForever(EventObserver {
                homeViewModel.openFlight()
            })

            viewModel.gotoVisa.observeForever(EventObserver {
                val context = dashboardHeaderBinding.visaButton.context
                val intent = Intent(context, VisaBookingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            })

            viewModel.gotoHoliday.observeForever(EventObserver {
                val context = dashboardHeaderBinding.hotelsButton.context
                val intent = Intent(context, HolidayBookingActivity::class.java)
                when (it) {
                    HolidayNavigationActions.VISIT_HOLIDAY_SEARCH -> {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra(
                            HOLIDAY_NAVIGATION_ACTION,
                            HolidayNavigationActions.VISIT_HOLIDAY_SEARCH
                        )
                        context.startActivity(intent)
                    }
                    HolidayNavigationActions.VISIT_HOLIDAY_LIST -> {
                        intent.putExtra(
                            HOLIDAY_NAVIGATION_ACTION,
                            HolidayNavigationActions.VISIT_HOLIDAY_LIST
                        )
                        intent.putExtra(ARG_HOLIDAY_CITY_NAME, viewModel.holidayCityName)
                        intent.putExtra(ARG_HOLIDAY_CITY_CODE, viewModel.holidayCityCode)
                        context.startActivity(intent)
                    }
                    else -> {}
                }
            })

            viewModel.gotoHotel.observeForever(EventObserver {
                val context = dashboardHeaderBinding.hotelsButton.context
                val intent = Intent(context, HotelBookingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            })

            viewModel.gotoBusBooking.observeForever(EventObserver {
                val context = dashboardHeaderBinding.visaButton.context
//                val intent = Intent(context, BusBookingActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(intent)
            })

            viewModel.gotoWheelActivity.observeForever(EventObserver {
                val context = dashboardHeaderBinding.hotelsButton.context
                val intent = Intent(context, WheelActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(intent)
            })

//            viewModel.navigateToTour.observeForever {
//                val context = dashboardHeaderBinding.hotelsButton.context
//                val intent = Intent(context, TourBookingActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(intent)
//            }

            viewModel.particleAnimation.observeForever(EventObserver {
                if (it) {
                    viewModel.runParticleAnimation(
                        activity = dashboardHeaderBinding.animationViewJerking.context as Activity,
                        startView = dashboardHeaderBinding.animationViewJerking
                    )
                }
            })

            viewModel.isInternetAvailable.observeForever {
                if (!it)
                    Toast.makeText(
                        dashboardHeaderBinding.hotelsButton.context,
                        "No Internet",
                        Toast.LENGTH_LONG
                    ).show()
            }

            ItemClickSupport.addTo(dashboardHeaderBinding.cities)
                .setOnItemClickListener { _, position, _ ->
                    viewModel.navigateToHolidayList(position)
                }

            runnable.run()
            runnableWheel.run()

            dashboardHeaderBinding.animationViewJerking.setOnClickListener {
                dashBoardViewModel.showWinCoin(
                    it.context,
                    dashboardHeaderBinding.animationViewJerking
                )
            }

            dashboardHeaderBinding.layoutFreeCoin.setOnClickListener {
                dashBoardViewModel.showWinCoin(
                    it.context,
                    dashboardHeaderBinding.animationViewJerking
                )
            }
        }
    }

    fun onDestroy() {
        dashBoardViewModel.onDestroy()
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
