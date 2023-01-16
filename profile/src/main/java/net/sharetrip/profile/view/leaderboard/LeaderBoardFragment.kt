package net.sharetrip.profile.view.leaderboard

import android.os.Bundle
import androidx.fragment.app.viewModels
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentLeaderboardBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle

class LeaderBoardFragment : BaseFragment<FragmentLeaderboardBinding>() {

    private val viewModel: LeaderBoardViewModel by viewModels{
        LeaderBoardVMFactory(
            DataManager.getSharedPref(requireContext()),
            LeaderBoardRepository(DataManager.profileApiService)
        )
    }

    private val tripCoinAdapter = TripCoinAdapter()
    private val rewardAdapter = SpecialRewardAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId(): Int = R.layout.fragment_leaderboard

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.leader_board))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        bindingView.listTripCoinReward.adapter = tripCoinAdapter
        bindingView.listSpecialReward.adapter = rewardAdapter

        viewModel.tripCoinWinnerList.observe(viewLifecycleOwner) {
            tripCoinAdapter.updateLeaderBoardList(it)
        }

        viewModel.rewardWinnerList.observe(viewLifecycleOwner) {
            rewardAdapter.updateRewardWinnerList(it)
        }
    }
}
