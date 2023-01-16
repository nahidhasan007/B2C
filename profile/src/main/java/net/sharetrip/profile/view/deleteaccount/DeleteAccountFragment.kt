package net.sharetrip.profile.view.deleteaccount

import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.facebook.login.LoginManager
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.DeleteAccountDialogBinding
import net.sharetrip.profile.databinding.FragmentDeleteAccountBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.signup.view.RegistrationActivity
import timber.log.Timber

class DeleteAccountFragment : BaseFragment<FragmentDeleteAccountBinding>() {

    val sharedPrefsHelper: SharedPrefsHelper by lazy {
        DataManager.getSharedPref(requireContext())
    }

    private val viewModel: DeleteAccountViewModel by viewModels {
        DeleteAccountVMF(
            DeleteAccountRepo(DataManager.profileApiService),
            DataManager.getSharedPref(requireContext()).get(
                PrefKey.ACCESS_TOKEN, ""
            )
        )
    }

    private lateinit var dialog: Dialog

    

    override fun layoutId() = R.layout.fragment_delete_account

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.delete_account))
        hideTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner

        viewModel.deletionReasons.observe(viewLifecycleOwner) { reasons ->
            bindingView.reasonList.removeAllViews()
            for ((index, option) in reasons.withIndex()) {
                createReasonOption(index, option.text, bindingView.reasonList)
            }
        }

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver{
            when(it.first){
                DeleteAccountViewModel.DeleteAccountDestinations.LOGIN.name ->{
                    showAlertDialog(it.second as String)
                }
            }
        })

        bindingView.accountDelete.setOnClickListener {
            if (bindingView.reasonList.checkedRadioButtonId < 0)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_a_reason),
                    Toast.LENGTH_SHORT
                )
                    .show()
            else {
                showConfirmationDialog()
            }
        }
    }

    private fun showAlertDialog(message: String){
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle(getString(R.string.notice))
            setMessage(message)
            setCancelable(false)
            setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                logoutUser()
                val intent = Intent(requireContext(), RegistrationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                (activity as ProfileActivity).finish()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun logoutUser(){
        LoginManager.getInstance().logOut()
        sharedPrefsHelper.put(PrefKey.ACCESS_TOKEN, "")
        sharedPrefsHelper.put(PrefKey.IS_LOGIN, false)
        sharedPrefsHelper.put(PrefKey.USER_TRIP_COIN, "0")
        sharedPrefsHelper.put(PrefKey.QUIZ_NEXT_DAY, 0L)
    }

    private fun createReasonOption(id: Int, option: String, radioGroup: RadioGroup) {
        val radioButton = RadioButton(requireContext())
        radioButton.id = id
        radioButton.text = option
        radioGroup.addView(radioButton)
    }


    private fun showConfirmationDialog() {
        try {
            dialog = Dialog(requireContext(), R.style.MyDialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context),
                R.layout.delete_account_dialog,
                null,
                false
            ) as DeleteAccountDialogBinding
            dialogBinding.confirmDeleteButton.setOnClickListener {
                viewModel.deleteAccount(
                    bindingView.reasonList.checkedRadioButtonId,
                    bindingView.commentText.text.toString().trim()
                )
                dialog.dismiss()
            }
            dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(dialogBinding.root)
            dialog.show()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    
}
