package net.sharetrip.holiday.booking.view.summary

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidaySummaryBinding
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.booking.model.HolidayParam
import net.sharetrip.holiday.booking.model.HolidaySummary
import net.sharetrip.holiday.booking.model.PrimaryContact
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.ARG_HOLIDAY_CONTACT_MODEL
import net.sharetrip.holiday.utils.ARG_HOLIDAY_PARAM_MODEL
import net.sharetrip.holiday.utils.ARG_HOLIDAY_SUMMARY_MODEL
import net.sharetrip.payment.model.PaymentGatewayType
import net.sharetrip.payment.model.PaymentMethod
import net.sharetrip.profile.ProfileActivity
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTripCoin
import net.sharetrip.shared.utils.showTripCoin
import net.sharetrip.shared.view.BaseFragment
import java.text.NumberFormat
import java.util.*

class HolidaySummaryFragment : BaseFragment<FragmentHolidaySummaryBinding>() {

    private val viewModel: HolidaySummaryViewModel by viewModels {
        val holidayParam = arguments?.getParcelable<HolidayParam>(ARG_HOLIDAY_PARAM_MODEL)
        val contact = arguments?.getParcelable<PrimaryContact>(ARG_HOLIDAY_CONTACT_MODEL)
        val summary = arguments?.getParcelable<HolidaySummary>(ARG_HOLIDAY_SUMMARY_MODEL)
        var tripCoin = DataManager.getSharedPref(requireContext())[PrefKey.USER_TRIP_COIN, "0"]
        tripCoin = tripCoin.filter { it in '0'..'9' }

        HolidaySummaryViewModelFactory(
            holidayParam!!, contact!!, summary!!, tripCoin,
            DataManager.getSharedPref(requireContext()),
            HolidaySummaryRepository(DataManager.holidayBookingApiService)
        )
    }

    private lateinit var dots: Array<ImageView?>
    private lateinit var layoutManager: GridLayoutManager

    private val adapter = PaymentAdapter()
    private var isCountTotalVisible = true
    private var dotsCount = 0
    private var totalVisibleItem = 0

    override fun layoutId() = R.layout.fragment_holiday_summary

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        showTripCoin()
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.bottomSheet.viewModel = viewModel
        bindingView.seekBar.isEnabled = false

        viewModel.seekBarMax.observe(viewLifecycleOwner) {
            bindingView.seekBar.max = it.toInt()
        }

        viewModel.tripCoinText.observe(viewLifecycleOwner) {
            setTripCoin(it)
        }

        layoutManager = GridLayoutManager(
            activity, 2,
            GridLayoutManager.HORIZONTAL, false
        )

        bindingView.listPaymentType.layoutManager = layoutManager
        bindingView.listPaymentType.adapter = adapter

        viewModel.paymentList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                when {
                    it.size > 4 -> layoutManager.spanCount = 2
                    else -> layoutManager.spanCount = 1
                }

                adapter.update(it as ArrayList<PaymentMethod>)
                if (it.isNotEmpty())
                    adapter.selectedItem(it[0])

                setImageSliderDots(it.size)
            }
        }

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
                    totalVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                    isCountTotalVisible = false
                    setImageSliderDots(recyclerView.layoutManager!!.itemCount)
                }
            }
        })

        viewModel.repository.showMsg.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        bindingView.seekBar.setOnSeekBarChangeListener(viewModel.seekBarListener)

        adapter.selectedPaymentMethod.observe(viewLifecycleOwner) {
            viewModel.selectedPaymentMethod.value = it
            viewModel.setPaymentGateWayId(it.id)
            bindingView.bottomSheet.btnBookNow.isEnabled = true
            if (it.currency.code == PaymentGatewayType.USD.toString()) {
                viewModel.updateUSDPayment(it)
            } else {
                viewModel.updateWithBDTPayment()
            }
            if (adapter.series != null) {
                val seriesList = ArrayList<String>()

                for (series in adapter.series!!) {
                    seriesList.add(series.series)
                }

                val cardAdapter =
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        seriesList
                    )

                if (seriesList.isNotEmpty()) {
                    bindingView.cardPrefixImageView.visibility = View.VISIBLE
                    bindingView.cardPrefixTextInputLayout.visibility = View.VISIBLE
                    viewModel.cardSeries = adapter.series!![0].id
                    bindingView.cardPrefixAutoCompleteTextView.setText(seriesList[0])
                    bindingView.cardPrefixAutoCompleteTextView.setAdapter(cardAdapter)
                } else {
                    bindingView.cardPrefixImageView.visibility = View.GONE
                    bindingView.cardPrefixTextInputLayout.visibility = View.GONE
                }
            }
        }

        bindingView.cardPrefixAutoCompleteTextView.setOnItemClickListener { _: AdapterView<*>, _: View, pos: Int, _: Long ->
            val id = adapter.series?.get(pos)?.id
            viewModel.cardSeries = id!!
        }

        viewModel.redeemChecked.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.seekBar.progress = 0
                bindingView.seekBar.isEnabled = true
            } else {
                bindingView.seekBar.progress = 0
                bindingView.seekBar.isEnabled = false
            }
        }

        setClickableTermsAndCondition()

        viewModel.usdPayment.observe(viewLifecycleOwner) {
            showUSDPaymentDialog(it)
        }

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                HolidaySummaryViewModel.HolidaySummaryNavDestinations.TO_PROFILE_CONTENT_PRIVACY.name -> {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, it.second as String)
                    startActivity(intent)
                }
                HolidaySummaryViewModel.HolidaySummaryNavDestinations.TO_PROFILE_CONTENT_TERM.name -> {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.putExtra(ProfileActivity.ARG_PROFILE_ACTION, it.second as String)
                    startActivity(intent)
                }
                HolidaySummaryViewModel.HolidaySummaryNavDestinations.TO_PAYMENT.name -> {
                    lifecycleScope.launchWhenResumed {
                        findNavController().navigateSafe(
                            R.id.action_holidaySummaryFragment_to_payment_nav_graph,
                            it.second as Bundle
                        )
                    }
                }
            }
        })
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

    private fun setImageSliderDots(count: Int) {
        if (count <= 6) {
            bindingView.linearSliderDots.visibility = View.GONE
            return
        }

        bindingView.linearSliderDots.removeAllViews()
        bindingView.linearSliderDots.visibility = View.VISIBLE

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
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
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

    @SuppressLint("SetTextI18n")
    private fun showUSDPaymentDialog(newPrice: Double) {
        val dialog = Dialog(requireContext(), R.style.MyDynamicDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_payment_with_usd)
        dialog.setCancelable(true)

        val dialogContinueButton = dialog.findViewById<Button>(R.id.bt_continue)
        val tvUpdatePrice = dialog.findViewById<AppCompatTextView>(R.id.tvPrice)
        tvUpdatePrice.text =
            PaymentGatewayType.USD.toString() + " " + NumberFormat.getNumberInstance(Locale.US)
                .format(newPrice)

        dialogContinueButton.setOnClickListener {
            dialog.dismiss()
            viewModel.bookAHoliday()
        }

        dialog.show()
    }
}
