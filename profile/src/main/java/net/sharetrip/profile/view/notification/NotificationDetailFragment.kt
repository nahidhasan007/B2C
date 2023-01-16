package net.sharetrip.profile.view.notification

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import androidx.fragment.app.viewModels
import net.sharetrip.shared.utils.loadImageNormal
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentNotificationDetailBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle

class NotificationDetailFragment : BaseFragment<FragmentNotificationDetailBinding>() {

    private val viewModel: NotificationDetailViewModel by viewModels {
        NotificationDetailsVMFactory(DataManager.getSharedPref(requireContext()))
    }



    override fun layoutId() = R.layout.fragment_notification_detail

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.details))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.imageUrl.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                bindingView.imageFeature.loadImageNormal(it)
            }
        }

        bindingView.description.autoLinkMask = Linkify.ALL
        bindingView.description.movementMethod = LinkMovementMethod.getInstance()
        Linkify.addLinks(bindingView.description, Linkify.ALL)
    }

    

}
