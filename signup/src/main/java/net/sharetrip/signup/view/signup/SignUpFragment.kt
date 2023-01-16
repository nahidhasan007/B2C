package net.sharetrip.signup.view.signup

import android.app.AlertDialog
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.signup.R
import net.sharetrip.signup.databinding.FragmentSignUpBinding
import net.sharetrip.signup.network.SignUpDataManager

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {

    private val viewModel by lazy {
        SignUpVMFactory(SignUpDataManager.signupApiService).create(SignUpViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.fragment_sign_up

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        viewModel.snackBarMsg.observe(viewLifecycleOwner, EventObserver {
            val snackBar = Snackbar.make(bindingView.container, it, Snackbar.LENGTH_SHORT)
            val sbView: View = snackBar.view
            sbView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.error_color))
            val textView =
                sbView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            textView.typeface = Typeface.DEFAULT_BOLD
            snackBar.show()
        })

        viewModel.isSignUpSuccess.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                showDialogForSignUpSuccess()
            }
        })

        viewModel.goToLogin.observe(viewLifecycleOwner, EventObserver{
            if (it) findNavController().navigateSafe(R.id.action_signupFragment_to_emailLoginFragment)
        })
    }

    private fun showDialogForSignUpSuccess() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Please check your email to verify your email address to login. Thank you")
            builder.apply {
                setPositiveButton("Okay") { _, _ -> }
            }
            builder.create()
        }
        alertDialog?.show()
    }

    
}