package net.sharetrip.payment.view.fail

import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.payment.R
import net.sharetrip.payment.databinding.FragmentPaymentFailBinding

class PaymentFailFragment : BaseFragment<FragmentPaymentFailBinding>() {

    override fun layoutId(): Int = R.layout.fragment_payment_fail

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        bindingView.tryAgainButton.setOnClickListener {
            activity?.finish()
        }

        bindingView.cancelButton.setOnClickListener {
            activity?.finish()
        }
    }
}
