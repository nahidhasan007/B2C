package net.sharetrip.view.blog.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.R
import net.sharetrip.databinding.ItemBlogCategoryBinding
import net.sharetrip.model.Category

class BlogCategoryAdapter : RecyclerView.Adapter<BlogCategoryAdapter.BlogCategoryViewHolder>() {

    private val postList = ArrayList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogCategoryViewHolder {
        val blogView =
                DataBindingUtil.inflate<ItemBlogCategoryBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_blog_category,
                        parent,
                        false)
        return BlogCategoryViewHolder(blogView)
    }

    override fun getItemCount() = postList.size

    inner class BlogCategoryViewHolder(val bindingView: ItemBlogCategoryBinding) :
            RecyclerView.ViewHolder(bindingView.root)

    override fun onBindViewHolder(holder: BlogCategoryViewHolder, position: Int) {
        val aPostItem = postList[position]
        holder.bindingView.textViewBlogCategory.text = aPostItem.name
        if (position == 0) {
            holder.bindingView.textViewBlogCategory.setBackgroundResource(R.drawable.top_round_shape)
        } else if (position + 1 == postList.size) {
            holder.bindingView.textViewBlogCategory.setBackgroundResource(R.drawable.bottom_round_shape_white)
        }
    }

    fun updateCategoryList(list: List<Category>?) {
        if (list != null) {
            postList.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): Category {
        return postList[position]
    }
}