package net.sharetrip.visa.history.model

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImage
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import net.sharetrip.visa.R

class TravelerSection(val it: TravellersItem, private val travelerNumber: Int) : Section(
    SectionParameters.builder()
        .headerResourceId(R.layout.item_visa_history_traveler)
        .itemResourceId(R.layout.item_visa_history_photo)
        .build()
) {

    var documentList: List<VisaBookingUploadedDocsItem>? = it.visaBookingUploadedDocs

    override fun getContentItemsTotal() = documentList!!.size

    override fun getHeaderViewHolder(view: View) = TravellerViewHolder(view)

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        super.onBindHeaderViewHolder(holder)
        val headerViewHolder = holder as TravellerViewHolder
        headerViewHolder.typeOfUser.text = "Traveller $travelerNumber"
        headerViewHolder.tvAdultName.text = it.givenName + " " + it.surName
        headerViewHolder.tvGenderType.text = it.gender
        headerViewHolder.tvDob.text = it.dateOfBirth
        headerViewHolder.tvPassportNumber.text = it.passportNumber
        headerViewHolder.tvPassportExp.text = it.passportExpireDate
        headerViewHolder.tvNationalityType.text = it.nationality
        headerViewHolder.tvProfession.text = it.profession
        headerViewHolder.tvCurrentAddress.text = it.address1
        headerViewHolder.tvDestinationAddress.text = it.address2
        headerViewHolder.tvPhone.text = it.mobileNumber
        headerViewHolder.tvEmail.text = it.email
    }

    inner class TravellerViewHolder(val bindingView: View) : RecyclerView.ViewHolder(bindingView) {
        val typeOfUser: AppCompatTextView = itemView.findViewById(R.id.type_of_user)
        val tvAdultName: AppCompatTextView = itemView.findViewById(R.id.tv_adult_name)
        val tvGenderType: AppCompatTextView = itemView.findViewById(R.id.tv_gender_type)
        val tvDob: AppCompatTextView = itemView.findViewById(R.id.tv_dob)
        val tvPassportNumber: AppCompatTextView = itemView.findViewById(R.id.tv_passport_number)
        val tvPassportExp: AppCompatTextView = itemView.findViewById(R.id.tv_passport_exp)
        val tvNationalityType: AppCompatTextView = itemView.findViewById(R.id.tv_nationality_type)
        val tvProfession: AppCompatTextView = itemView.findViewById(R.id.tvProfession)
        val tvCurrentAddress: AppCompatTextView = itemView.findViewById(R.id.tv_current_address)
        val tvDestinationAddress: AppCompatTextView =
            itemView.findViewById(R.id.tv_destination_address)
        val tvPhone: AppCompatTextView = itemView.findViewById(R.id.tv_phone)
        val tvEmail: AppCompatTextView = itemView.findViewById(R.id.tv_email)
    }

    override fun getItemViewHolder(view: View) = UploadedDocsViewHolder(view)

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as UploadedDocsViewHolder
        itemHolder.tvTitle.text = documentList?.get(position)?.docTitle!!
        itemHolder.imageView.loadImage(documentList?.get(position)?.docUrl!!)
    }

    inner class UploadedDocsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var tvTitle: AppCompatTextView = view.findViewById(R.id.tv_title)
        var imageView: AppCompatImageView = view.findViewById(R.id.img_name)
    }
}
