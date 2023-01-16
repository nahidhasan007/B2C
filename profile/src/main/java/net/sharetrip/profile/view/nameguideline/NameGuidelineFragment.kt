package net.sharetrip.profile.view.nameguideline

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentNameGuidelineBinding
import net.sharetrip.shared.utils.setTitleAndSubtitle

class NameGuidelineFragment : BaseFragment<FragmentNameGuidelineBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }



    override fun layoutId() = R.layout.fragment_name_guideline

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.name_guideline))
    }

    

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}
