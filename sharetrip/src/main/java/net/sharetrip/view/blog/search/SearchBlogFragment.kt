package net.sharetrip.view.blog.search

import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentBlogSearchBinding
import net.sharetrip.network.MainDataManager
import net.sharetrip.shared.utils.SLUG
import net.sharetrip.shared.utils.hideKeyboard
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.openKeyboard
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.view.blog.dashboard.adapter.BlogAdapter

class SearchBlogFragment : BaseFragment<FragmentBlogSearchBinding>() {
    private val blogAdapter = BlogAdapter()
    private val viewModel by lazy {
        SearchBlogVMFactory(
            MainDataManager.mainApiService,
            arguments?.getString(SLUG),
            MainDataManager.getSharedPref(requireContext())
        ).create(
            SearchBlogViewModel::class.java
        )
    }

    override fun layoutId(): Int = R.layout.fragment_blog_search

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        bindingView.recyclerBlog.adapter = blogAdapter

        bindingView.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        ItemClickSupport.addTo(bindingView.recyclerBlog).setOnItemClickListener { _, position, _ ->
            findNavController().navigateSafe(
                R.id.action_searchBlogFragment_to_blogDetailsFragment,
                bundleOf(SLUG to blogAdapter.getItem(position).slug)
            )
        }

        viewModel.searchBlogPost.observe(viewLifecycleOwner) {
            blogAdapter.clearAndUpdateBlogList(it)
            hideKeyboard()
        }

        viewModel.goToCategory.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_searchBlogFragment_to_blogCategory)
        })

        bindingView.editTextSearching.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    viewModel.onSearchClicked()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bindingView.editTextSearching.requestFocus()
        openKeyboard(requireContext(), bindingView.editTextSearching)
    }
}