package net.sharetrip.payment.view.success

import android.app.Activity
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.payment.R
import net.sharetrip.payment.databinding.FragmentPaymentSuccessBinding

class PaymentSuccessFragment : BaseFragment<FragmentPaymentSuccessBinding>(){

    override fun layoutId(): Int = R.layout.fragment_payment_success

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.homeButton.setOnClickListener {
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }
    }

}
