package net.sharetrip.view.blog.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.R
import net.sharetrip.model.BlogPost
import net.sharetrip.view.blog.dashboard.BlogDashboardViewModel

class BlogTopSliderAdapter(val context: Context, private val blogPost: List<BlogPost>, val viewModel: BlogDashboardViewModel) : PagerAdapter() {

    override fun getCount() = blogPost.size

    override fun isViewFromObject(view: View, objectData: Any): Boolean {
        return view == objectData
    }

    override fun getPageWidth(position: Int): Float {
        return .9f
    }

    override fun destroyItem(container: View, position: Int, objectItem: Any) {
        val vp = container as ViewPager
        val view = objectItem as View?
        vp.removeView(view)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.layout_blog_slider, null)
        val imageView = view.findViewById<View>(R.id.imageViewBlog) as ImageView
        val tvBlogBigTitle = view.findViewById<View>(R.id.tvBlogBigTitle) as AppCompatTextView
        val tvBlogTopTitle = view.findViewById<View>(R.id.tvBlogTopTitle) as AppCompatTextView
        tvBlogBigTitle.text = blogPost[position].subTitle
        tvBlogTopTitle.text = blogPost[position].title
        imageView.loadImage(blogPost[position].featuredImage!!)
        val vp = container as ViewPager
        view.setOnClickListener {
            viewModel.navigateToBlogDetails(blogPost[position].slug)
        }
        vp.addView(view, 0)
        return view
    }
}