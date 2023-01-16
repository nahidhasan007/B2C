package net.sharetrip.visa.booking.view.summary

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.Strings
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.VisaBookingActivity
import net.sharetrip.visa.booking.model.PaymentGatewayType
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.model.payment.PaymentMethod
import net.sharetrip.visa.booking.model.payment.Series
import net.sharetrip.visa.databinding.FragmentVisaBookingSummaryBinding
import net.sharetrip.visa.network.DataManager
import java.text.NumberFormat
import java.util.*

class VisaBookingSummaryFragment : BaseFragment<FragmentVisaBookingSummaryBinding>() {
    private val viewModel by lazy {
        val visaSearchQuery = requireArguments().getParcelable<VisaSearchQuery>(
            ARG_VISA_SUMMARY_DATA_MODEL
        )!!

        ViewModelProvider(
            this,
            VisaBookingSummaryVMFactory(
                visaSearchQuery,
                DataManager.visaBookingApiService,
                DataManager.getSharedPref(requireContext())
            )
        ).get(
            VisaBookingSummaryViewModel::class.java
        )
    }

    private var mPaymentAdapter = PaymentAdapter()
    private var seriesArrayList: List<Series>? = ArrayList()
    private var mAdapter: ArrayAdapter<String>? = null
    private val paymentMethodWithEarn: MutableList<PaymentMethod> = ArrayList()
    private val paymentMethodWithRedeem: MutableList<PaymentMethod> = ArrayList()
    private val paymentMethodWithCoupon: MutableList<PaymentMethod> = ArrayList()
    private var mSelectionTracker: SelectionTracker<Long>? = null
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var dots: Array<ImageView?>
    private var isCountTotalVisible = true
    private var dotsCount = 0
    private var totalVisibleItem = 0
    private lateinit var itemDetails: PaymentMethod
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        alertDialog = AlertDialog.Builder(requireActivity()).create()
        alertDialog.setTitle(getString(R.string.data_error))
        alertDialog.setMessage(getString(R.string.data_error_occured))
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)
        ) { _, _ ->
            val intent = Intent(requireActivity(), VisaBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            requireActivity().finish()
            startActivity(intent)
        }
        alertDialog.setOnDismissListener {
            val intent = Intent(requireActivity(), VisaBookingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            requireActivity().finish()
            startActivity(intent)
        }
    }



    override fun layoutId() = R.layout.fragment_visa_booking_summary

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)
        bindingView.seekBar.isEnabled = false

        observeSeekbar()
        setClickableTermsAndCondition()
        getPaymentMethodListAndObserveItsType()
        setPaymentMethodRecyclerView()
        observeUIState()
        observeTripCoin()
        observeBookClick()
        observeProfileNavigation()
    }

    private fun observeProfileNavigation() {
        viewModel.navigateProfile.observe(viewLifecycleOwner){ action ->
            val intent = Intent(context, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, action)
            startActivity(intent)
        }
    }

    private fun observeSeekbar() {
        viewModel.seekBarMax.observe(viewLifecycleOwner) {
            bindingView.seekBar.max = it.toInt()
        }
    }

    private fun observeBookClick() {
        viewModel.navigateToPayment.observe(viewLifecycleOwner, EventObserver { bundle ->
            lifecycleScope.launchWhenResumed {
                findNavController().navigateSafe(R.id.navigate_to_payment, bundle)
            }
        })
    }

    

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    private fun setClickableTermsAndCondition() {
        val spannableString =
            SpannableString(requireContext().getString(R.string.i_agree_to_terms_condition))

        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                viewModel.onClickTermsAndCondition()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                if (bindingView.tvTermsCondition.isPressed) {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_blue)
                } else {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_dark)
                }
                ds.isUnderlineText = false
                bindingView.tvTermsCondition.invalidate()
            }
        }

        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                viewModel.onClickPrivacyPolicy()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                if (bindingView.tvTermsCondition.isPressed) {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_blue)
                } else {
                    ds.color = ContextCompat.getColor(requireContext(), R.color.blue_dark)
                }
                ds.isUnderlineText = false
                bindingView.tvTermsCondition.invalidate()

            }
        }

        spannableString.setSpan(termsClickableSpan, 15, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
            privacyPolicyClickableSpan,
            36,
            51,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        bindingView.tvTermsCondition.text = spannableString
        bindingView.tvTermsCondition.movementMethod = LinkMovementMethod.getInstance()
        bindingView.tvTermsCondition.highlightColor = Color.TRANSPARENT
    }

    private fun observeTripCoin() {
        viewModel.observableUserTripCoin.observe(viewLifecycleOwner) {
            (activity as VisaBookingActivity).findViewById<TextView>(R.id.text_view_trip_coin).text =
                it
        }
    }

    private fun getPaymentMethodListAndObserveItsType() {
        viewModel.paymentMethodList.observe(
            viewLifecycleOwner, EventObserver
            { paymentMethods: List<PaymentMethod> ->
                paymentMethodWithEarn.addAll(paymentMethods.filter { it.isEarnTripCoinApplicable })
                paymentMethodWithRedeem.addAll(paymentMethods.filter { it.isRedeemTripCoinApplicable })
                paymentMethodWithCoupon.addAll(paymentMethods.filter { it.isCouponAvailable })

                if (paymentMethodWithCoupon.isEmpty()) {
                    viewModel.noCardSeriesAvailable()
                }
                if (mPaymentAdapter.itemCount > 0) {
                    mSelectionTracker!!.select(0L)
                }
            })

        viewModel.cardPaymentChecked.observe(viewLifecycleOwner) {
            bindingView.textViewDiscountLabel.text =
                requireActivity().resources.getString(R.string.discount)
            bindingView.seekBar.isEnabled = false
            bindingView.redeemCheckBox.isChecked = false
            setGridSpanSizeAndUpdatePaymentList(paymentMethodWithEarn)
            viewModel.onCardChecked()

            if (mPaymentAdapter.itemCount > 0) {
                mSelectionTracker!!.select(0L)
            }
        }

        viewModel.redeemChecked.observe(viewLifecycleOwner) {
            bindingView.textViewDiscountLabel.text =
                requireActivity().resources.getString(R.string.redeem_coin)
            bindingView.seekBar.isEnabled = true
            bindingView.radioButtonCardPayment.isChecked = false
            setGridSpanSizeAndUpdatePaymentList(paymentMethodWithRedeem)
            viewModel.onRedeemChecked()

            if (mPaymentAdapter.itemCount > 0) {
                mSelectionTracker!!.select(0L)
            }
        }


        viewModel.availCoupon.observe(viewLifecycleOwner) {
            bindingView.textViewDiscountLabel.text =
                requireActivity().resources.getString(R.string.redeem_coupon)
            bindingView.seekBar.isEnabled = false
            bindingView.radioButtonCardPayment.isChecked = false
            setGridSpanSizeAndUpdatePaymentList(paymentMethodWithCoupon)
            viewModel.onCouponChecked()

            if (mPaymentAdapter.itemCount > 0) {
                mSelectionTracker!!.select(0L)
            }
        }
    }

    private fun setPaymentMethodRecyclerView() {
        layoutManager = GridLayoutManager(
            activity, 2,
            RecyclerView.HORIZONTAL, false
        )
        bindingView.listPaymentType.layoutManager = layoutManager
        bindingView.listPaymentType.adapter = mPaymentAdapter

        bindingView.listPaymentType.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        setIndicatorPosition()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isCountTotalVisible) {
                    isCountTotalVisible = false
                    totalVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    drawIndicator(mPaymentAdapter.itemCount)
                }
            }
        })

        setCardPrefixAdapter()

        mSelectionTracker = SelectionTracker.Builder(
            this.javaClass.simpleName,
            bindingView.listPaymentType,
            PaymentAdapter.KeyProvider(),
            PaymentAdapter.DetailsLookup(bindingView.listPaymentType),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
            .build()
        mPaymentAdapter.setSelectionTracker(mSelectionTracker!!)
        mSelectionTracker!!.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                try {
                    if (selected) {
                        itemDetails = mPaymentAdapter.getItemDetails(key.toInt())
                        viewModel.selectedPaymentMethod.value = itemDetails
                        viewModel.bookingCurrency.set(itemDetails.currency.code)
                        if (itemDetails.currency.conversion.rate != 0.0) {
                            viewModel.calculatePrice(itemDetails.currency.conversion.rate!!)
                        }
                        setCardPrefix(itemDetails.series)
                    } else if (mSelectionTracker!!.selection.isEmpty) {
                        hideCardPrefix()
                    }
                } catch (e: Exception) {
                    e.fillInStackTrace()
                }
            }
        })
    }

    private fun setCardPrefixAdapter() {
        mAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, ArrayList()
        )
        bindingView.cardPrefixAutoCompleteTextView.setAdapter<ArrayAdapter<String>>(mAdapter)
    }

    private fun observeUIState() {
        viewModel.showMessage.observe(viewLifecycleOwner,EventObserver {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.clickedBooking.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                var paymentId: String? = null

                if (mSelectionTracker!!.hasSelection()) {
                    val selection = mSelectionTracker!!.selection
                    val iterator: Iterator<Long> = selection.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        val details = mPaymentAdapter.getItemDetails(next.toInt())
                        paymentId = details.id
                        break
                    }
                }

                if (Strings.isBlank(paymentId)) {
                    showMessageDialog("Select payment method")
                } else if (!viewModel.isCheckboxActive.get()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.terms_condition_user_hints),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (itemDetails.currency.code == PaymentGatewayType.USD.toString()) {
                        viewModel.getTotalPayableAmount()
                            ?.let { it1 -> showUSDPaymentDialog(it1, paymentId) }
                    } else {
                        viewModel.makeABookingRequest(paymentId!!, bindingView.cardPrefixAutoCompleteTextView.text.toString())
                    }
                }
            }
        })

        viewModel.isExceptionOccurred.observe(viewLifecycleOwner) {
            if (it) {
                alertDialog.show()
            }
        }
    }

    private fun showUSDPaymentDialog(newPrice: Double, paymentID: String?) {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_payment_with_usd)
        dialog.setCancelable(true)

        val dialogContinueButton = dialog.findViewById<Button>(R.id.bt_continue)
        val tvUpdatePrice = dialog.findViewById<AppCompatTextView>(R.id.tvPrice)
        val price =
            PaymentGatewayType.USD.toString() + " " + NumberFormat.getNumberInstance(Locale.US)
                .format(newPrice)
        tvUpdatePrice.text = price

        dialogContinueButton.setOnClickListener {
            dialog.dismiss()
            viewModel.makeABookingRequest(paymentID!!, bindingView.cardPrefixAutoCompleteTextView.text.toString())
        }

        dialog.show()
    }

    private fun setGridSpanSizeAndUpdatePaymentList(list: List<PaymentMethod>) {
        when {
            list.size > 4 -> layoutManager.spanCount = 2
            else -> layoutManager.spanCount = 1
        }
        mPaymentAdapter.submitList(null)
        mPaymentAdapter.submitList(list)
        isCountTotalVisible = true
    }

    private fun drawIndicator(count: Int) {

        if (count <= 6) {
            bindingView.linearSliderDots.visibility = View.GONE
            return
        }

        bindingView.linearSliderDots.removeAllViews()
        bindingView.linearSliderDots.visibility = View.VISIBLE
        bindingView.linearSliderDots.post {
            bindingView.linearSliderDots.invalidate()
            bindingView.linearSliderDots.requestLayout()
        }

        dotsCount = ((count - totalVisibleItem) / 2)

        if (count % 2 == 0) {
            dotsCount++
        } else {
            dotsCount += 2
        }

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {
            dots[i] = ImageView(context)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(8, 8, 8, 8)
            bindingView.linearSliderDots.addView(dots[i], params)
        }

        dots[0]?.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.active_dot
            )
        )
    }

    private fun setIndicatorPosition() {
        val lastCompleteVisible = layoutManager.findLastCompletelyVisibleItemPosition() + 1

        val dotsSelected = if (lastCompleteVisible <= totalVisibleItem) {
            0
        } else {
            val valueOne = ((lastCompleteVisible - totalVisibleItem)) / 2
            val valueTwo = (lastCompleteVisible - totalVisibleItem) % 2
            valueOne + valueTwo
        }

        for (i in 0 until dotsCount) {
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.nonactive_dot
                )
            )
        }

        if (dotsSelected < dotsCount) {
            dots[dotsSelected]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.active_dot
                )
            )
        }
    }

    private fun showCardPrefix(list: List<String>) {
        bindingView.cardPrefixTextInputLayout.visibility = View.VISIBLE
        bindingView.cardPrefixImageView.visibility = View.VISIBLE
        bindingView.cardPrefixAutoCompleteTextView.setText(list[0])
    }

    private fun hideCardPrefix() {
        bindingView.cardPrefixAutoCompleteTextView.setText("")
        bindingView.cardPrefixTextInputLayout.visibility = View.GONE
        bindingView.cardPrefixImageView.visibility = View.INVISIBLE
    }

    private fun setCardPrefix(series: List<Series>?) {
        seriesArrayList = series
        val list: MutableList<String> = ArrayList()

        if (series != null)
            for (data in series)
                list.add(data.series)

        mAdapter!!.clear()
        mAdapter!!.addAll(list)

        if (list.size > 0)
            showCardPrefix(list)
        else
            hideCardPrefix()
    }

    private fun showMessageDialog(mMessage: String) {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setMessage(mMessage)
        mBuilder.setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int -> }
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    companion object {
        const val ARG_VISA_SUMMARY_DATA_MODEL = "ARG_VISA_SUMMARY_DATA_MODEL"
    }
}
