package net.sharetrip.holiday.history.view.contact

import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayContactInfoBinding
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.model.PrimaryContact
import net.sharetrip.holiday.utils.ARG_HOLIDAY_HISTORY_PRIMARY_CONTACT
import net.sharetrip.holiday.utils.setTitleAndSubtitle

class HolidayContactFragment : BaseFragment<FragmentHolidayContactInfoBinding>() {

    private var primaryContact: PrimaryContact? = null

    override fun layoutId() = R.layout.fragment_holiday_contact_info

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.contact_information))

        primaryContact = arguments?.getParcelable(ARG_HOLIDAY_HISTORY_PRIMARY_CONTACT)
        bindingView.textFieldUserTitle.setText(primaryContact?.givenName)
        bindingView.emailInputEditText.setText(primaryContact?.email)
        bindingView.phoneNumberInputEditText.setText(primaryContact?.mobileNumber)
        bindingView.addressInputEditText.setText(primaryContact?.address1)
    }
}
