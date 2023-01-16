package net.sharetrip.bus.history.view.policydetails

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.bus.R
import net.sharetrip.bus.databinding.FragmentBusHistoryPoliciesBinding

class BusHistoryPoliciesFragment : BaseFragment<FragmentBusHistoryPoliciesBinding>() {

    override fun layoutId(): Int = R.layout.fragment_bus_history_policies

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
    }
}
