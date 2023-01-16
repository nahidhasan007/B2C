package net.sharetrip.profile.view.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import net.sharetrip.shared.utils.setNavigationResult
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentCountryCurrencyBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.shared.utils.setTitleAndSubtitle

class CountryCurrencyFragment : BaseFragment<FragmentCountryCurrencyBinding>() {

    private val viewModel: CountryCurrencyViewModel by viewModels {
        val checkingCode = requireArguments().getString(ARG_COUNTRY_CURRENCY)
        CountryCurrencyVMFactory(
            checkingCode!!,
            DataManager.getSharedPref(requireContext()),
            CountryCurrencyRepo(DataManager.profileApiService)
        )
    }

    private val adapter = CountryCurrencyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun layoutId(): Int = R.layout.fragment_country_currency

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.select_country))

        bindingView.lifecycleOwner = viewLifecycleOwner
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
            adapter.updateCountryCurrencyList(it)
        }

        ItemClickSupport.addTo(bindingView.listCountryCode)
            .setOnItemClickListener { _, position, _ ->
                val bundle = bundleOf(
                    EXTRA_COUNTRY_CODE to viewModel.codeList.value!![position].code,
                    EXTRA_COUNTRY_NAME to viewModel.codeList.value!![position].name
                )
                setNavigationResult(key = RESULT_COUNTRY_SELECTION, result = bundle)
                findNavController().navigateUp()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        const val EXTRA_COUNTRY_CODE = "extra_country_code"
        const val EXTRA_COUNTRY_NAME = "extra_country_name"
        const val RESULT_COUNTRY_SELECTION = "RESULT_COUNTRY_SELECTION"
        const val ARG_COUNTRY_CURRENCY = "settings"
    }
}
