package net.sharetrip.flight.booking.view.farerules

import android.content.Intent
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.databinding.FragmentFareRulesFlightBinding
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.model.ProfileAction

class RulesDetailsFragment(private val details: String, private val selectedPosition: Int) :
    BaseFragment<FragmentFareRulesFlightBinding>() {

    override fun layoutId(): Int = R.layout.fragment_fare_rules_flight

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.textViewRules.text = details
        if (selectedPosition == 1) {
            bindingView.isFareDetails = true
        }

        bindingView.textViewReadRule.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, ProfileAction.ARG_READ_RULES)
            startActivity(intent)
        }
    }
}
