package net.sharetrip.profile.view.nahid
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentMyUserInfoBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment


class MyUserInfoFragment : BaseFragment<FragmentMyUserInfoBinding>() {

    private val viewModel : MyUserInfoViewModel by viewModels {
        MyUserInfoVMF(DataManager.getSharedPref(requireContext()),
        MyUserInfoRepository(DataManager.profileApiService)
        )
    }

    override fun layoutId(): Int = R.layout.fragment_my_user_info

    override fun getViewModel(): BaseViewModel? = viewModel

    override fun initOnCreateView() {
        hideTripCoin()
        setTitleAndSubtitle("My Profile")
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.myinfoViewModel = viewModel
        bindingView.executePendingBindings()


    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.saveinfo, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.actionSave == item.itemId) {
            viewModel.onclickSaveInfo()
        }
        return super.onOptionsItemSelected(item)
    }


}