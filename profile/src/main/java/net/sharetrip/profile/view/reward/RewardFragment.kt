package net.sharetrip.profile.view.reward

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.viewModels
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.DateUtil
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentRewardBinding
import net.sharetrip.profile.model.RewardWinner
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle
import java.util.*

class RewardFragment : BaseFragment<FragmentRewardBinding>() {

    private val viewModel: RewardViewModel by viewModels {
        RewardViewModelFactory(
            DataManager.getSharedPref(requireContext()),
            RewardRepository(DataManager.profileApiService)
        )
    }

    private val tripCoinAdapter = TripCoinRewardAdapter()
    private val specialRewardAdapter = SpecialRewardBoardAdapter()
    private var specialRewardList = ArrayList<RewardWinner>()
    private var dialogSpecialRewardsDetail: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    

    override fun layoutId(): Int = R.layout.fragment_reward

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.reward))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        bindingView.listTripCoinReward.adapter = tripCoinAdapter
        bindingView.listSpecialReward.adapter = specialRewardAdapter

        viewModel.tripCoinWinnerList.observe(viewLifecycleOwner) {
            tripCoinAdapter.updateLeaderBoardList(it)
        }

        viewModel.rewardWinnerList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()){
                specialRewardList.clear()
                specialRewardList = it as ArrayList<RewardWinner>
                specialRewardAdapter.updateRewardWinnerList(it)
            }
        }

        ItemClickSupport.addTo(bindingView.listSpecialReward)
            .setOnItemClickListener { _, position, _ ->
                dialogSpecialRewardsDetail(specialRewardList[position])
            }

        viewModel.openShareDialog.observe(viewLifecycleOwner, EventObserver {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "\n\n")
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                requireContext().resources.getString(
                    R.string.referral_msg,
                    DataManager.getSharedPref(requireContext())[PrefKey.USER_REFERRAL_CODE, ""]
                )
            )
            val intent =
                Intent.createChooser(
                    sharingIntent,
                    requireContext().resources.getString(R.string.app_name)
                )
            startActivity(intent)
        })
    }

    

    @SuppressLint("SetTextI18n")
    private fun dialogSpecialRewardsDetail(rewardWinner: RewardWinner) {
        if (dialogSpecialRewardsDetail != null) {
            dialogSpecialRewardsDetail = null
        }
        dialogSpecialRewardsDetail = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialogSpecialRewardsDetail?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogSpecialRewardsDetail?.setContentView(R.layout.dialog_share_rewards)
        dialogSpecialRewardsDetail?.setCancelable(false)

        val title =
            dialogSpecialRewardsDetail?.findViewById<AppCompatTextView>(R.id.text_view_title)
        val body = dialogSpecialRewardsDetail?.findViewById<AppCompatTextView>(R.id.text_view_body)
        val buttonShare =
            dialogSpecialRewardsDetail?.findViewById<AppCompatTextView>(R.id.button_share)
        val imageViewClose =
            dialogSpecialRewardsDetail?.findViewById<AppCompatImageView>(R.id.image_view_close)

        title?.text = rewardWinner.activity
        body?.text =
            requireContext().getString(R.string.expiring_in) + " " + DateUtil.parseDisplayDateMonthFormatFromApiDateFormat(
                rewardWinner.expireDate
            )

        buttonShare?.setOnClickListener {
            dialogSpecialRewardsDetail?.dismiss()
            viewModel.onClickInviteFriend()
        }

        imageViewClose?.setOnClickListener {
            dialogSpecialRewardsDetail?.dismiss()
        }

        dialogSpecialRewardsDetail?.show()
    }
}
