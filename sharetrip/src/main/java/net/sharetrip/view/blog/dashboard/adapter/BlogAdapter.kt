package net.sharetrip.view.blog.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.loadImageWithRoundCorner
import net.sharetrip.R
import net.sharetrip.databinding.ItemBlogBinding
import net.sharetrip.model.BlogPost
import java.util.*

class BlogAdapter : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val postList = ArrayList<BlogPost>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val blogView =
                DataBindingUtil.inflate<ItemBlogBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_blog,
                        parent,
                        false)
        return BlogViewHolder(blogView)
    }

    override fun getItemCount() = postList.size

    inner class BlogViewHolder(val bindingView: ItemBlogBinding) :
            RecyclerView.ViewHolder(bindingView.root)

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val aPostItem = postList[position]
        holder.bindingView.tvBlogTopTitle.text = aPostItem.title
        holder.bindingView.tvBlogBigTitle.text = aPostItem.subTitle
        holder.bindingView.imageViewBlog.loadImageWithRoundCorner(aPostItem.featuredImage, 16)
    }

    fun updateBlogList(list: List<BlogPost>?) {
        if (list != null) {
            postList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun clearAndUpdateBlogList(list: List<BlogPost>?) {
        if (list != null) {
            postList.clear()
            postList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): BlogPost {
        return postList[position]
    }
}