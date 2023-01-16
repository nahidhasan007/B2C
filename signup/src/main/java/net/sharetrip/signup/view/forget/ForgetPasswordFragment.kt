package net.sharetrip.signup.view.forget

import androidx.navigation.fragment.findNavController
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.signup.R
import net.sharetrip.signup.databinding.FragmentForgotPasswordBinding
import net.sharetrip.signup.network.SignUpDataManager

class ForgetPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    private val viewModel by lazy {
        ForgetPasswordVMFactory(SignUpDataManager.signupApiService).create(ForgetPasswordViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.fragment_forgot_password

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        viewModel.goBack.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

}