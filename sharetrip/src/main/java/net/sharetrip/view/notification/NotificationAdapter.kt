package net.sharetrip.view.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.model.UserNotification
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.utils.loadImageFromRes
import net.sharetrip.shared.utils.loadImageWithRoundCorner
import net.sharetrip.R
import net.sharetrip.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val detailForward: DetailForward
) :
    PagingDataAdapter<UserNotification, NotificationAdapter.NotificationViewHolder>(NotificationDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val viewHolder = DataBindingUtil.inflate<ItemNotificationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_notification, parent, false
        )
        return NotificationViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = getItem(position)
        holder.binding.notification = notification
        if (notification != null) {
            holder.binding.notificationDate.text =
                DateUtil.getDateFromMilliSecond(notification.timeStamp!!)
        }
        if (notification != null) {
            if (!notification.imageUrl.isNullOrEmpty()) {
                holder.binding.notificationIcon.loadImageWithRoundCorner(notification.imageUrl, 16)
            } else {
                holder.binding.notificationIcon.loadImageFromRes(R.drawable.ic_img_deals)
            }
        }
        holder.binding.itemView.setOnClickListener {
            if (notification != null) {
                detailForward.navigateToDetail(notification)
            }
        }
    }

    class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    class NotificationDiff: DiffUtil.ItemCallback<UserNotification>(){
        override fun areItemsTheSame(
            oldItem: UserNotification,
            newItem: UserNotification
        ): Boolean {
            return oldItem.timeStamp == newItem.timeStamp
        }

        override fun areContentsTheSame(
            oldItem: UserNotification,
            newItem: UserNotification
        ): Boolean {
            return oldItem == newItem
        }
    }
}
