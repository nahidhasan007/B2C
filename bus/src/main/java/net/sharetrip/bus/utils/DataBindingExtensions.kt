package net.sharetrip.bus.utils

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import net.sharetrip.bus.R
import net.sharetrip.bus.booking.model.CLASS_FILTER
import net.sharetrip.bus.booking.model.OPERATOR_FILTER
import net.sharetrip.bus.booking.model.PRICE
import net.sharetrip.bus.booking.model.SCHEDULE_FILTER

@BindingAdapter("android:setFilterType")
fun AppCompatTextView.setFilterType(filterType: Int) {
    when (filterType) {
        PRICE -> {
            this.text = this.context.getString(R.string.price_range)
        }
        CLASS_FILTER -> {
            this.text = this.context.getString(R.string.title_class)
        }
        SCHEDULE_FILTER -> {
            this.text = this.context.getString(R.string.title_schedule)
        }
        OPERATOR_FILTER -> {
            this.text = this.context.getString(R.string.title_operators)
        }
    }
}