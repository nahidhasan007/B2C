package net.sharetrip.tracker.view.cirium.view.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImageBinder
import net.sharetrip.tracker.databinding.ItemFlightStatusBinding
import net.sharetrip.tracker.view.cirium.model.StatusInfo

class StatusAdapter(
    private val statusList: List<StatusInfo>,
    private val viewModel: FlightStatusListViewModel
) : RecyclerView.Adapter<StatusAdapter.FlightStatusHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightStatusHolder {
        val bindingView =
            ItemFlightStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlightStatusHolder(bindingView)
    }

    override fun onBindViewHolder(holder: FlightStatusHolder, position: Int) {
        holder.itemFlightStatusBinding.status = statusList[position]
        holder.itemFlightStatusBinding.imageCarrier.loadImageBinder(statusList[position].airlineIcon)
        holder.itemFlightStatusBinding.cardViewFlightStatus.setOnClickListener {
            viewModel.onItemClicked(position)
        }
    }

    override fun getItemCount() = statusList.size

    inner class FlightStatusHolder(val itemFlightStatusBinding: ItemFlightStatusBinding) :
        RecyclerView.ViewHolder(itemFlightStatusBinding.root)
}
