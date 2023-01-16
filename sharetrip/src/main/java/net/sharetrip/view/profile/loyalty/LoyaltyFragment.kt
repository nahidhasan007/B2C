package net.sharetrip.view.profile.loyalty

import android.content.Intent
import androidx.core.widget.NestedScrollView
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentLoyaltyBinding
import net.sharetrip.network.MainDataManager
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.signup.view.RegistrationActivity

class LoyaltyFragment : BaseFragment<FragmentLoyaltyBinding>() {
    private val viewModel by lazy {
        LoyaltyVMFactory(
            MainDataManager.mainApiService,
            MainDataManager.getSharedPref(requireContext())
        ).create(LoyaltyViewModel::class.java)
    }



    override fun layoutId(): Int = R.layout.fragment_loyalty

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.tabLayout.viewModel = viewModel
        bindingView.guestLayout.data = viewModel.popupData

        bindingView.loyaltyScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY)
                viewModel.onScrollView()
        }

        viewModel.userCard.observe(viewLifecycleOwner) {
            setTypeOfLoyaltyCard(it)
        }

        viewModel.goToProfileDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, viewModel.profileAction)
            startActivity(intent)
        })

        viewModel.goToLogin.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })

    }

    private fun setTypeOfLoyaltyCard(userType: String) {
        when (userType) {
            UserLoyaltyLevel.SILVER.levelName -> bindingView.ivLoyaltyCard.setImageResource(R.drawable.ic_carddesign_silver)
            UserLoyaltyLevel.GOLD.levelName -> bindingView.ivLoyaltyCard.setImageResource(R.drawable.ic_carddesign_gold)
            UserLoyaltyLevel.PLATINUM.levelName -> bindingView.ivLoyaltyCard.setImageResource(R.drawable.ic_carddesign_platinum)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkLoginInformation()
    }

    
}