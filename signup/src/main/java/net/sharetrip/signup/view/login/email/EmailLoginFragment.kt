package net.sharetrip.signup.view.login.email

import android.app.Activity
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.signup.R
import net.sharetrip.signup.databinding.FragmentEmailLoginBinding
import net.sharetrip.signup.network.SignUpDataManager

class EmailLoginFragment : BaseFragment<FragmentEmailLoginBinding>() {

    private val viewModel by lazy {
        EmailLoginVMFactory(
            SignUpDataManager.signupApiService,
            SignUpDataManager.signUpSharedPref(requireContext())
        ).create(EmailLoginViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.fragment_email_login

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        viewModel.goToForgetPassword.observe(viewLifecycleOwner, EventObserver {
            if (it) findNavController().navigateSafe(R.id.action_emailLoginFragment_to_forgetPasswordFragment)
        })

        viewModel.goToSignup.observe(viewLifecycleOwner, EventObserver {
            if (it) findNavController().navigateSafe(R.id.action_emailLoginFragment_to_signupFragment)
        })

        viewModel.setActivityResult.observe(viewLifecycleOwner) {
            if (it) {
                activity?.setResult(Activity.RESULT_OK)
                activity?.finish()
            }
        }

        viewModel.navigateBack.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                findNavController().navigateUp()
            }
        })
    }

    
}