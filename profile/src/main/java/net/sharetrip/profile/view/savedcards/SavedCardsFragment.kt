package net.sharetrip.profile.view.savedcards

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentSaveCradsBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle

class SavedCardsFragment : BaseFragment<FragmentSaveCradsBinding>() {

    private val viewModel: SavedCardsViewModel by viewModels {
        SavedCardsVMFactory(
            DataManager.getSharedPref(requireContext()),
            SavedCardsRepository(DataManager.profileApiService)
        )
    }

    private lateinit var adapter: SavedCardsAdapter

    

    override fun layoutId() = R.layout.fragment_save_crads

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.saved_cards))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        adapter = SavedCardsAdapter(viewModel)

        bindingView.rvSavedCards.adapter = adapter

        viewModel.savedCardList.observe(viewLifecycleOwner) {
            adapter.updateQuickPickList(it)
        }

        viewModel.showToast.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    
}