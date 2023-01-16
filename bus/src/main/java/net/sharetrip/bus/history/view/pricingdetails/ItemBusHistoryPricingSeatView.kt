package net.sharetrip.bus.history.view.pricingdetails

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.ItemBusSeatClassPriceBinding

class ItemBusHistoryPricingSeatView(context: Context) : ConstraintLayout(context) {
    var binding: ItemBusSeatClassPriceBinding

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.item_bus_seat_class_price, this, true)
    }
}
