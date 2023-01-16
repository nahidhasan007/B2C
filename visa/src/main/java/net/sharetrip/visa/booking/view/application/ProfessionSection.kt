package net.sharetrip.visa.booking.view.application

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.Document
import net.sharetrip.visa.booking.model.ReqDocsItem

class ProfessionSection(var reqDocsItem: ReqDocsItem) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.item_visa_required_docs)
        .headerResourceId(R.layout.item_visa_profession_header)
        .build()
) {
    var documentList: List<Document>? = reqDocsItem.documents

    override fun getContentItemsTotal(): Int {
        return documentList!!.size
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return RequiredDocsViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as RequiredDocsViewHolder
        itemHolder.tvDocsDetail.text = documentList!![position].description
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return ProfessionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        super.onBindHeaderViewHolder(holder)
        val headerViewHolder = holder as ProfessionViewHolder
        headerViewHolder.tvProfessionHead.text = reqDocsItem.name + " :"
    }
}
