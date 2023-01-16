package net.sharetrip.profile.view.tripCoin

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentUserTripCoinBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle

class TripCoinFragment : BaseFragment<FragmentUserTripCoinBinding>() {

    private val viewModel: TripCoinViewModel by viewModels {
        TripCoinVMFactory(DataManager.getSharedPref(requireContext()), TripCoinRepository(DataManager.profileApiService))
    }

    private val tripCoinAdapter = TripCoinAdapter()

    

    override fun layoutId() = R.layout.fragment_user_trip_coin

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.trip_coin))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.listTripCoin.adapter = tripCoinAdapter

        viewModel.tripCoinItems.observe(viewLifecycleOwner) {
            tripCoinAdapter.updateTripCoinList(it)
        }

        viewModel.showToast.observe(viewLifecycleOwner, EventObserver{
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

    
}
