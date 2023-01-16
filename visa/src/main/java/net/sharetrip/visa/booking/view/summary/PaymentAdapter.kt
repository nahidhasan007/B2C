package net.sharetrip.visa.booking.view.summary

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.payment.PaymentMethod

class PaymentAdapter :
    ListAdapter<PaymentMethod, PaymentAdapter.PaymentViewHolder>(PaymentDiffUtil()) {
    private lateinit var selectionTracker: SelectionTracker<Long>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val mContext = parent.context
        val mImageView = AppCompatImageView(mContext)

        val height = mContext.resources.getDimensionPixelSize(R.dimen.payment_logo_height)
        val width = mContext.resources.getDimensionPixelSize(R.dimen.payment_logo_width)
        val padding = mContext.resources.getDimensionPixelSize(R.dimen.spacing_small_tiny)
        val margin = mContext.resources.getDimensionPixelSize(R.dimen.spacing_small)

        mImageView.layoutParams = LinearLayout.LayoutParams(width, height).apply {
            setMargins(margin, margin, 0, 0)
        }

        mImageView.setPadding(padding, padding, padding, padding)
        mImageView.setBackgroundResource(R.drawable.payment_method_selector)

        return PaymentViewHolder(mImageView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind()
    }

    inner class PaymentViewHolder(val mImageView: AppCompatImageView) :
        RecyclerView.ViewHolder(mImageView) {
        val itemDetails: Details = Details()
        fun bind() {
            itemDetails.position = absoluteAdapterPosition.toLong()
            val item = getItem(absoluteAdapterPosition)
            val url = item!!.logo.large
            Glide.with(mImageView.context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(FitCenter()))
                .into(mImageView)
            if (selectionTracker != null) {
                mImageView.isActivated = selectionTracker.isSelected(itemDetails.selectionKey)
            }
        }
    }

    class PaymentDiffUtil : DiffUtil.ItemCallback<PaymentMethod>() {
        override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
            return oldItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean =
            oldItem == newItem
    }

    class KeyProvider : ItemKeyProvider<Long>(SCOPE_MAPPED) {
        override fun getKey(position: Int): Long {
            return position.toLong()
        }

        override fun getPosition(key: Long): Int {
            return key.toInt()
        }
    }

    class DetailsLookup(private val mRecyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = mRecyclerView.findChildViewUnder(e.x, e.y)

            if (view != null) {
                val viewHolder = mRecyclerView.getChildViewHolder(view)
                if (viewHolder is PaymentAdapter.PaymentViewHolder) {
                    return viewHolder.itemDetails
                }
            }

            return null
        }
    }

    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>) {
        this.selectionTracker = selectionTracker
    }

    fun getItemDetails(key: Int): PaymentMethod {
        return getItem(key)
    }

    inner class Details : ItemDetails<Long>() {
        var position: Long = 0

        override fun getPosition(): Int {
            return position.toInt()
        }

        override fun getSelectionKey(): Long {
            return position
        }

        override fun inSelectionHotspot(e: MotionEvent): Boolean {
            return true
        }
    }
}
