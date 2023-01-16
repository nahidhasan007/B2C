package net.sharetrip.view.blog.category

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentBlogCategoryBinding
import net.sharetrip.model.Category
import net.sharetrip.shared.utils.SLUG
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import net.sharetrip.shared.utils.hideKeyboard
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import java.text.NumberFormat
import java.util.*

class BlogCategoryFragment : BaseFragment<FragmentBlogCategoryBinding>() {

    private val blogCategoryAdapter = BlogCategoryAdapter()
    private val blogEventManager =
        AnalyticsProvider.blogEventManager(AnalyticsProvider.getFirebaseAnalytics())


    override fun layoutId(): Int = R.layout.fragment_blog_category

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.recyclerBlogCategory.adapter = blogCategoryAdapter
        blogCategoryAdapter.updateCategoryList(getBlogCategory())
        hideKeyboard()

        ItemClickSupport.addTo(bindingView.recyclerBlogCategory)
            .setOnItemClickListener { _, position, _ ->
                blogEventManager.handleBlogCategoryEvent(blogCategoryAdapter.getItem(position).name.toString())

                findNavController().navigateSafe(
                    R.id.action_blogCategory_to_searchBlogFragment,
                    bundleOf(SLUG to (blogCategoryAdapter.getItem(position).name))
                )
            }

        bindingView.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        var points = SharedPrefsHelper(requireContext())[PrefKey.USER_TRIP_COIN, ""]
        points = points.filter { it in '0'..'9' }
        if (points.isBlank()) {
            points = "0"
            SharedPrefsHelper(requireContext()).put(PrefKey.USER_TRIP_COIN, "0")
        }
        bindingView.textViewTripCoin.text =
            NumberFormat.getNumberInstance(Locale.US).format(points.toInt())
    }

    private fun getBlogCategory(): List<Category> {
        val categoryList = ArrayList<Category>()
        categoryList.add(Category(name = "Destinations"))
        categoryList.add(Category(name = "Discover Bangladesh"))
        categoryList.add(Category(name = "Travel Inspiration"))
        categoryList.add(Category(name = "Stories"))
        categoryList.add(Category(name = "Tips"))
        return categoryList
    }


}
