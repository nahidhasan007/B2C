package net.sharetrip.view.blog.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImageWithRoundCorner
import net.sharetrip.R
import net.sharetrip.databinding.ItemBlogBookingBinding
import net.sharetrip.model.BlogBookingItem

class BlogBookingAdapter : RecyclerView.Adapter<BlogBookingAdapter.BlogViewHolder>() {

    private val blogBookingList = ArrayList<BlogBookingItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val countryView =
                DataBindingUtil.inflate<ItemBlogBookingBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_blog_booking,
                        parent,
                        false)

        return BlogViewHolder(countryView)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val aPostItem = blogBookingList[position]
        holder.bindingView.tvBlogTopTitle.text = aPostItem.title
        holder.bindingView.tvBlogBigTitle.text = aPostItem.subTitle
        holder.bindingView.tvBlogBookingName.text = aPostItem.buttonText
        aPostItem.imgSrc.let {
            holder.bindingView.imageViewBlog.loadImageWithRoundCorner(it, 16)
        }
    }

    override fun getItemCount() = blogBookingList.size

    fun updateBlogBookingList(list: List<BlogBookingItem>?) {
        if (list != null) {
            this.blogBookingList.clear()
            this.blogBookingList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): BlogBookingItem {
        return blogBookingList[position]
    }

    inner class BlogViewHolder(val bindingView: ItemBlogBookingBinding) :
            RecyclerView.ViewHolder(bindingView.root)
}