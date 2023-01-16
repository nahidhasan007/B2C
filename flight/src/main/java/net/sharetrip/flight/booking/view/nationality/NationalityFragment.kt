package net.sharetrip.flight.booking.view.nationality

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.NationalityCode
import net.sharetrip.flight.databinding.FragmentNationalityCodeFlightBinding
import net.sharetrip.flight.utils.setTitleSubTitle
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class NationalityFragment : BaseFragment<FragmentNationalityCodeFlightBinding>() {
    private lateinit var viewModel: NationalityViewModel

    private val adapter = NationalityAdapter()

    override fun layoutId(): Int {
        return R.layout.fragment_nationality_code_flight
    }

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleSubTitle(getString(R.string.select_nationality))

        viewModel = NationalityViewModelFactory(SharedPrefsHelper(requireContext())).create(
            NationalityViewModel::class.java
        )
        bindingView.viewModel = viewModel

        bindingView.listCountryCode.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        bindingView.listCountryCode.itemAnimator = DefaultItemAnimator()

        bindingView.listCountryCode.adapter = adapter

        viewModel.codeList.observe(viewLifecycleOwner) {
            adapter.updateCodeList(it)
        }

        ItemClickSupport.addTo(bindingView.listCountryCode)
            .setOnItemClickListener { _, position, _ ->
                setNavigationResult(
                    viewModel.codeList.value?.get(position) as NationalityCode,
                    "NationalityCode"
                )

                findNavController().navigateUp()
            }
    }

    override fun onStart() {
        super.onStart()
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }
}
