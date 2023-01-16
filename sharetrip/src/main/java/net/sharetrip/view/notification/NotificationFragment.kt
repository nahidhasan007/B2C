package net.sharetrip.view.notification

import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.sharetrip.R
import net.sharetrip.databinding.FragmentNotificationBinding
import net.sharetrip.network.MainDataManager
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction

class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {

    private lateinit var notificationAdapter: NotificationAdapter

    private val viewModel by lazy {
        NotificationVMFactory(
            MainDataManager.getSharedPref(requireContext()),
            NotificationRepo(MainDataManager.mainApiService)
        ).create(NotificationViewModel::class.java)
    }

    

    override fun layoutId(): Int = R.layout.fragment_notification

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        notificationAdapter = NotificationAdapter(viewModel)
        bindingView.recyclerNotification.adapter = notificationAdapter
        bindingView.recyclerNotification.setHasFixedSize(true)
        bindingView.recyclerNotification.itemAnimator?.changeDuration = 0

        lifecycleScope.launch {
            notificationAdapter.loadStateFlow.collectLatest { loadState ->
                bindingView.progressBar.isVisible = loadState.refresh is LoadState.Loading
            }
        }

        viewModel.dealsLiveData.observe(viewLifecycleOwner) { deals ->
            deals?.let {
                notificationAdapter.submitData(this.lifecycle, it)
            }
        }

        viewModel.goToNotificationDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(
                ProfileActivity.ARG_PROFILE_ACTION,
                ProfileAction.ARG_NOTIFICATION_DETAIL
            )
            startActivity(intent)
        })

    }

    
}
