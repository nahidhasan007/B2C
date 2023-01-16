package net.sharetrip.flight.booking.view.flightlist

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.shared.utils.Strings
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemFlightBinding
import net.sharetrip.flight.booking.model.flightresponse.flights.flight.Flight

class ItemFlightView : RelativeLayout {

    lateinit var bindingView: ItemFlightBinding

    constructor(context: Context?) : super(context) {
        initUi()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initUi()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initUi()
    }

    private fun initUi() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        bindingView = DataBindingUtil.inflate(inflater, R.layout.item_flight, this, true)
    }

    fun setItemFlight(mFlight: Flight) {
        bindingView.departureAirlineCodeTextView.text = mFlight.originName.code
        bindingView.departureTimeTextView.text = mFlight.departureDateTime.time
        bindingView.durationTextView.text = mFlight.duration

        val mStopsCount = mFlight.stop
        bindingView.stopTextView.text = mStopsCount.toString()

        if (mStopsCount <= 0) {
            bindingView.flightStopCount.text = "NonStop"
        } else {
            bindingView.flightStopCount.text = "$mStopsCount Stop(s)"
        }
        bindingView.airlineNameTextView.text = mFlight.airlines.shortName
        bindingView.arrivalAirlineCodeTextView.text = mFlight.destinationName.code
        bindingView.arrivalTimeTextView.text = mFlight.arrivalDateTime.time

        if (Strings.isBlank(mFlight.dayCount) || mFlight.dayCount == "0") {
            bindingView.extraDayTextView.visibility = View.INVISIBLE
        } else {
            bindingView.extraDayTextView.visibility = View.VISIBLE
            bindingView.extraDayTextView.text = "+" + mFlight.dayCount.toString()
        }

        Glide.with(bindingView.flightLogo.context)
                .load(mFlight.logo)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(bindingView.flightLogo)
        if (mFlight.hiddenStops) {
            bindingView.relativeTechnicalStoppage.visibility = View.VISIBLE
        }
    }
}