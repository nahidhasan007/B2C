package net.sharetrip.profile.view.quickpick

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.ItemPickBinding
import net.sharetrip.profile.model.Traveler
import java.util.*

class QuickPickAdapter : RecyclerView.Adapter<QuickPickAdapter.QuickPickViewHolder>() {
    private val otherPassengersList = ArrayList<Traveler>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickPickViewHolder {
        val quickPickView = DataBindingUtil.inflate<ItemPickBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_pick,
            parent,
            false
        )
        return QuickPickViewHolder(quickPickView)
    }

    override fun onBindViewHolder(holder: QuickPickViewHolder, position: Int) {
        holder.bindingView.traveler = otherPassengersList[position]
    }

    override fun getItemCount() = otherPassengersList.size

    fun updateQuickPickList(otherPassengersList: List<Traveler>) {
        if (otherPassengersList != null) {
            this.otherPassengersList.clear()
            this.otherPassengersList.addAll(otherPassengersList)
            notifyDataSetChanged()
        }
    }

    inner class QuickPickViewHolder(val bindingView: ItemPickBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
