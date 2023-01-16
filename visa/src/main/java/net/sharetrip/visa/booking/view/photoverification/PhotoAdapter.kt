package net.sharetrip.visa.booking.view.photoverification

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.visa.R
import net.sharetrip.visa.databinding.ItemVisaPhotoBinding

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        context = parent.context

        val bindingItem = DataBindingUtil.inflate<ItemVisaPhotoBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_visa_photo, parent, false
        )

        return PhotoViewHolder(bindingItem)
    }

    override fun getItemCount() = PhotoVerificationFragment.photoList.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = PhotoVerificationFragment.photoList[position]

        holder.bindingView.tvTitle.text = photoItem.docTitle
        holder.bindingView.tvTitle.isSelected = true
        holder.bindingView.tvTitle.marqueeRepeatLimit = -1
        holder.bindingView.tvTitle.isSingleLine = true
        holder.bindingView.tvTitle.ellipsize = TextUtils.TruncateAt.MARQUEE

        photoItem.uri?.let {
            holder.bindingView.imgName.setImageURI(Uri.fromFile(photoItem.uri))
            holder.bindingView.tvPhotoStatus.text = context.getString(R.string.edit)
            holder.bindingView.tvPhotoStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_edit_mono,
                0,
                0,
                0
            )
        }

        holder.bindingView.tvPhotoStatus.text = context.getString(R.string.upload)
    }

    inner class PhotoViewHolder(val bindingView: ItemVisaPhotoBinding) :
        RecyclerView.ViewHolder(bindingView.root)
}
