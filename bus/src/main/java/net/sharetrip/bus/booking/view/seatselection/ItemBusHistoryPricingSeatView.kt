package net.sharetrip.bus.booking.view.seatselection

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.ItemBusSeatClassPriceBinding

class ItemBusHistoryPricingSeatView : ConstraintLayout {
    var binding: ItemBusSeatClassPriceBinding

    constructor(context: Context?) : super(context!!) {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.item_bus_seat_class_price, this, true)
    }
}
