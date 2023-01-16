package net.sharetrip.hotel.booking.view.nationality

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.FragmentNationalityCodeHotelBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport

class NationalityFragment : BaseFragment<FragmentNationalityCodeHotelBinding>() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            NationalityVMFactory(
                DataManager.hotelApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[NationalityViewModel::class.java]
    }
    private val adapter = NationalityAdapter()

    override fun layoutId(): Int = R.layout.fragment_nationality_code_hotel

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel

        bindingView.listCountryCode.layoutManager = LinearLayoutManager(context)
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
            .setOnItemClickListener { _, position, _ -> viewModel.onClickItem(position) }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            setNavigationResult(it, ARG_NATIONALITY_BACK_DATA)
            findNavController().popBackStack()
        }
    }

    companion object {
        const val ARG_NATIONALITY_BACK_DATA = "ARG_NATIONALITY_BACK_DATA"
    }
}
