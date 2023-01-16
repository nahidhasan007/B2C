package net.sharetrip.view.blog.details

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.WebSettings
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.SLUG
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentBlogDetailsBinding
import net.sharetrip.model.BlogPost
import net.sharetrip.model.TagsItem
import net.sharetrip.network.MainDataManager
import net.sharetrip.view.blog.dashboard.adapter.TrendingBlogAdapter

class BlogDetailsFragment : BaseFragment<FragmentBlogDetailsBinding>() {
    val viewModel by lazy {
        BlogDetailsVMFactory(MainDataManager.mainApiService, arguments?.getString(SLUG), MainDataManager.getSharedPref(requireContext())).create(
            BlogDetailsViewModel::class.java
        )
    }

    private val youMightLikeAdapter = TrendingBlogAdapter()



    override fun layoutId(): Int = R.layout.fragment_blog_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        bindingView.recyclerYouMightLike.adapter = youMightLikeAdapter
        bindingView.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.youMayLikeBlogList.observe(viewLifecycleOwner) {
            youMightLikeAdapter.updateTravelerList(it as List<BlogPost>)
        }

        ItemClickSupport.addTo(bindingView.recyclerYouMightLike)
            .setOnItemClickListener { _, position, _ ->
                findNavController().navigateSafe(
                    R.id.action_blogDetailsFragment_self,
                    bundleOf(SLUG to youMightLikeAdapter.getItem(position).slug)
                )
            }

        viewModel.blogTagList.observe(viewLifecycleOwner) {
            setWebData()
            addBlogTags(it)
        }

        viewModel.shareIntent.observe(viewLifecycleOwner, EventObserver{
            val shareIntent = Intent.createChooser(it, null)
            requireContext().startActivity(shareIntent)
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebData() {
        bindingView.webView.settings.javaScriptEnabled = true
        bindingView.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

        bindingView.webView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.pale_grey
            )
        )
        bindingView.webView.loadData(
            getHtmlData(viewModel.blogPost.get()!!.formatContentData()),
            "text/html",
            "UTF-8"
        )
    }

    private fun getHtmlData(bodyHTML: String): String {
        val head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>"
        return "<html>$head<body>$bodyHTML</body></html>"
    }

    @SuppressLint("RestrictedApi")
    private fun addBlogTags(list: List<TagsItem>) {

        list.forEach {
            val textView = TextView(requireContext())
            textView.text = it.blogTag.title
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.clear_blue))
            textView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.round_stoke_blog_tag__background
            )
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 16, 0)
            textView.layoutParams = params
            textView.setPadding(12, 8, 12, 8)
            textView.setOnClickListener {
                findNavController().navigateSafe(
                    R.id.action_blogDetailsFragment_to_searchBlogFragment,
                    bundleOf(SLUG to textView.text.toString())
                )
            }
            bindingView.linearTag.addView(textView)
        }
    }

    
}
