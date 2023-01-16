package net.sharetrip.flight.history.view.voidorrefund

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.ItemRefundVoidTravellarsNamesBinding
import net.sharetrip.flight.history.model.Traveller

class TravellerDetailsAdapter(private var dataList: List<Traveller>) :
    RecyclerView.Adapter<TravellerDetailsAdapter.TravellerDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravellerDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRefundVoidTravellarsNamesBinding>(
            layoutInflater,
            R.layout.item_refund_void_travellars_names,
            parent,
            false
        )
        return TravellerDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TravellerDetailsViewHolder, position: Int) {
        holder.itemViewBinding.passengerNameTextView.text =
            dataList[position].givenName + " " + dataList[position].surName
    }

    override fun getItemCount(): Int = dataList.size

    inner class TravellerDetailsViewHolder(val itemViewBinding: ItemRefundVoidTravellarsNamesBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root)
}
