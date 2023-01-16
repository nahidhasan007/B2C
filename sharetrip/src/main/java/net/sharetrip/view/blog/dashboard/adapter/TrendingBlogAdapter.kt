package net.sharetrip.view.blog.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImageWithRoundCorner
import net.sharetrip.R
import net.sharetrip.databinding.ItemBlogTrendingTopicsBinding
import net.sharetrip.model.BlogPost

class TrendingBlogAdapter : RecyclerView.Adapter<TrendingBlogAdapter.BlogViewHolder>() {

    private val postList = ArrayList<BlogPost>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val countryView =
                DataBindingUtil.inflate<ItemBlogTrendingTopicsBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_blog_trending_topics,
                        parent,
                        false)

        return BlogViewHolder(countryView)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val aPostItem = postList[position]
        holder.bindingView.tvBlogTopTitle.text = aPostItem.title
        holder.bindingView.tvBlogBigTitle.text = aPostItem.subTitle
        holder.bindingView.imageViewBlog.loadImageWithRoundCorner(aPostItem.featuredImage, 8)
    }

    override fun getItemCount() = postList.size

    fun updateTravelerList(list: List<BlogPost>?) {
        if (list != null) {
            this.postList.clear()
            this.postList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): BlogPost {
        return postList[position]
    }

    inner class BlogViewHolder(val bindingView: ItemBlogTrendingTopicsBinding) :
            RecyclerView.ViewHolder(bindingView.root)
}