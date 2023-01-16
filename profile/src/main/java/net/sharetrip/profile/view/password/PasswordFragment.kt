package net.sharetrip.profile.view.password

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentChangePasswordBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.signup.view.RegistrationActivity

class PasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {

    private val viewModel: PasswordViewModel by viewModels {
        PasswordViewModelFactory(
            DataManager.getSharedPref(requireContext()),
            PasswordRepository(DataManager.profileApiService)
        )
    }

    

    override fun layoutId(): Int = R.layout.fragment_change_password

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.change_password))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.showToast.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        viewModel.navigateLogin.observe(viewLifecycleOwner,EventObserver{
            val intent = Intent(requireContext(), RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
        })
    }

    
}
