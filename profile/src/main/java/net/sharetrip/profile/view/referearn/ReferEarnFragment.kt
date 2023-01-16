package net.sharetrip.profile.view.referearn

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentReferEarnBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle

class ReferEarnFragment : BaseFragment<FragmentReferEarnBinding>() {

    private val viewModel: ReferEarnViewModel by viewModels {
        ReferEarnViewModelFactory(DataManager.getSharedPref(requireContext()))
    }

    

    override fun layoutId() = R.layout.fragment_refer_earn

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.refer_amp_earn))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.intentLiveData.observe(viewLifecycleOwner) {
            if (it != null) startActivity(it)
        }

        bindingView.textFieldReferCode.setOnClickListener {
            val clipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Source Text", viewModel.code))
            val snackBar =
                Snackbar.make(requireView(), "Copied to clipboard.", Snackbar.LENGTH_LONG)
            snackBar.show()
        }

        bindingView.inviteFriend.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "\n\n")
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                requireContext().resources.getString(R.string.referral_msg, viewModel.code)
            )
            val intent = Intent.createChooser(
                sharingIntent,
                requireContext().resources.getString(R.string.app_name)
            )
            startActivity(intent)
        }
    }

    
}
