package net.sharetrip.holiday.booking.view.contact

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.holiday.R
import com.example.holiday.databinding.FragmentClientContactBinding
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.*
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment

class ClientContactFragment : BaseFragment<FragmentClientContactBinding>() {

    private val sharedPrefsHelper: SharedPrefsHelper by lazy {
        DataManager.getSharedPref(requireContext())
    }

    private val viewModel: ClientContactViewModel by viewModels {
        val holidayParam: HolidayParam = arguments?.get(ARG_HOLIDAY_PARAM_MODEL) as HolidayParam
        ClientContactViewModelFactory(
            holidayParam, sharedPrefsHelper,
            ClientContactRepository(DataManager.holidayBookingApiService)
        )
    }

    override fun layoutId() = R.layout.fragment_client_contact

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.contact_information))
        hideTripCoin()

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.done, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_done == menuItem.itemId) {
                    val isParamsValid = viewModel.setSummaryParams(
                        bindingView.textFieldTitle.text.toString(),
                        bindingView.textFieldGivenName.text.toString(),
                        bindingView.textFieldSurName.text.toString(),
                        bindingView.textFieldPhoneNumber.text.toString(),
                        bindingView.textFieldEmail.text.toString(),
                        bindingView.textFieldAddress.text.toString()
                    )

                    if (isParamsValid) {
                        val bundle = bundleOf(
                            ARG_HOLIDAY_PARAM_MODEL to viewModel.param,
                            ARG_HOLIDAY_CONTACT_MODEL to viewModel.contact,
                            ARG_HOLIDAY_SUMMARY_MODEL to viewModel.summary
                        )
                        findNavController().navigateSafe(
                            R.id.action_clientContactFragment_to_holidaySummaryFragment,
                            bundle
                        )
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val title = requireContext().resources.getStringArray(R.array.person_title)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, title)
        bindingView.textFieldTitle.setAdapter(adapter)
        bindingView.textFieldTitle.setText(title[0])

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        viewModel.msg.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
}
