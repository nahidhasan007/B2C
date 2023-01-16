package net.sharetrip.profile.view.manageyourdata

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import net.sharetrip.shared.model.GuestLoginListener
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentManageYourDataBinding
import net.sharetrip.profile.databinding.UserConfirmationLayoutBinding
import net.sharetrip.profile.model.UserConfirmPopUpData
import net.sharetrip.profile.network.DataManager
import net.sharetrip.signup.view.RegistrationActivity
import timber.log.Timber

class ManageYourDataFragment : BaseFragment<FragmentManageYourDataBinding>(), GuestLoginListener {

    val sharedPrefsHelper : SharedPrefsHelper by lazy {
        DataManager.getSharedPref(requireContext())
    }

    val popupData: UserConfirmPopUpData by lazy {
        UserConfirmPopUpData("Dear " + sharedPrefsHelper[PrefKey.USER_LAST_NAME,""],
            getString(R.string.please_login_to_verify_your_identity),
            sharedPrefsHelper[PrefKey.USER_AVATER,""], this
        )
    }

    val registerResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                dialog.dismiss()
                Toast.makeText(requireContext(), "User Logged in", Toast.LENGTH_SHORT).show()
                findNavController().navigateSafe(R.id.action_manageYourDataFragment_to_deleteAccountFragment)
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    requireContext(),
                    "Cannot proceed without login!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private lateinit var dialog: Dialog

    

    override fun layoutId() = R.layout.fragment_manage_your_data

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.mange_your_data))
        hideTripCoin()

        bindingView.deleteAccountButton.setOnClickListener {
            showGuestDialog()
        }
    }

    private fun showGuestDialog() {
        try {
            dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(context),
                R.layout.user_confirmation_layout,
                null,
                false
            ) as UserConfirmationLayoutBinding
            dialog.setContentView(dialogBinding.root)
            dialogBinding.data = popupData
            dialog.show()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onClickLogin() {
        val intent = Intent(requireContext(), RegistrationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        registerResult.launch(intent)
    }
}
