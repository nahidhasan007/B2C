package net.sharetrip.view.profile2.mainprofile2

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentMainProfileBinding
import net.sharetrip.databinding.FragmentProfileMain2Binding
import net.sharetrip.network.MainDataManager
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.ProfileActivity.Companion.ARG_PROFILE_ACTION
import net.sharetrip.signup.view.RegistrationActivity

import net.sharetrip.view.profile.mainprofile.ProfileMainViewModel2

class ProfileMainFragment2 : BaseFragment<FragmentProfileMain2Binding>() {

    private val viewModel by lazy {
        ProfileMainVMV2(MainDataManager.getSharedPref(requireContext())).create(
            ProfileMainViewModel2::class.java
        )
    }


    override fun layoutId(): Int = R.layout.fragment_profile_main2

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        viewModel.sendMail.observe(viewLifecycleOwner) {
            try {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data =
                    Uri.parse("mailto:ask@sharetrip.net") // only email apps should handle this
                activity?.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_apps_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.goToProfileDetails.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ARG_PROFILE_ACTION, viewModel.profileAction)
            startActivity(intent)
        })

        viewModel.goToLogin.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        })
    }



    override fun onResume() {
        super.onResume()
        viewModel.checkLoginInformation()
    }
}