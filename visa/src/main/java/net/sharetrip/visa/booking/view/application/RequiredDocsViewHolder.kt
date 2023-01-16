package net.sharetrip.visa.booking.view.application

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R

class RequiredDocsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvDocsDetail: TextView = itemView.findViewById(R.id.tv_docs_detail)
}
