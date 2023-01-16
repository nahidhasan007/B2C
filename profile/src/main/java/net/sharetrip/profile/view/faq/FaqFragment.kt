package net.sharetrip.profile.view.faq

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTitleAndSubtitle
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentAllFaqBinding
import net.sharetrip.profile.view.content.ContentFragment
import net.sharetrip.shared.view.BaseFragment

class FaqFragment : BaseFragment<FragmentAllFaqBinding>() {

    private val viewModel: FaqViewModel by viewModels()

    override fun layoutId() = R.layout.fragment_all_faq

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.faq))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.navigateToContentScreen.observe(viewLifecycleOwner, EventObserver{
            val bundle = bundleOf(ContentFragment.ARG_FAQ_NUMBER to it)
            findNavController().navigateSafe(R.id.action_faqFragment_to_contentFragment, bundle)
        })

    }

    
}
