package net.sharetrip.holiday.booking.view.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.holiday.R
import com.example.holiday.databinding.FragmentHolidayDetailBinding
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.shared.view.widgets.carousel.ImageAdapter
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.holiday.network.DataManager
import net.sharetrip.holiday.utils.*
import java.util.*

class HolidayDetailFragment : BaseFragment<FragmentHolidayDetailBinding>(),
    ViewPager.OnPageChangeListener {

    private val viewModel: HolidayDetailViewModel by viewModels {
        val productCode = arguments?.getString(ARG_HOLIDAY_PRODUCT_CODE)
        HolidayDetailsViewModelFactory(
            productCode!!,
            NetworkUtil.hasNetwork(requireContext()),
            SharedPrefsHelper(requireContext()),
            HolidayDetailsRepository(DataManager.holidayBookingApiService)
        )
    }
    private val offerAdapter = HolidayOfferAdapter()
    private var totalImageCount = 0
    private var currentPage = 0
    private var handler: Handler? = null
    private var update: Runnable? = null
    private var imageAdapter: ImageAdapter? = null

    override fun layoutId() = R.layout.fragment_holiday_detail

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.holiday))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.listHolidayOffer.adapter = offerAdapter

        viewModel.offers.observe(viewLifecycleOwner) {
            offerAdapter.update(it, viewModel.holidayDetail.value?.withAirfare!!)
        }

        viewModel.imageLink.observe(viewLifecycleOwner) {
            totalImageCount = it.size
            bindingView.viewPagerImage.addOnPageChangeListener(this)
            bindingView.textViewImageCount.text = "1/$totalImageCount"
            imageAdapter = ImageAdapter(childFragmentManager, it)
            if (imageAdapter != null) {
                bindingView.viewPagerImage.adapter = imageAdapter
                if (it.size > 1) {
                    autoScroll()
                }
            }
        }

        ItemClickSupport.addTo(bindingView.listHolidayOffer)
            .setOnItemClickListener { _, position, _ ->
                //this function sets values for HolidayParam class
                viewModel.setHolidayParam(position)

                val bundle = bundleOf(
                    ARG_HOLIDAY_PARAM_MODEL to viewModel.holidayParam,
                    ARG_HOLIDAY_OFFER_MODEL to viewModel.offers.value!![position]
                )
                findNavController().navigateSafe(
                    R.id.action_holidayDetailFragment_to_holidayReserveFragment,
                    bundle
                )
            }

        viewModel.shareUrl.observe(viewLifecycleOwner) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "\n\n")
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                requireActivity().resources.getString(R.string.referral_msg_other)
            )
            startActivity(
                Intent.createChooser(
                    sharingIntent,
                    resources.getString(R.string.app_name)
                )
            )
        }

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver{
            when (it.first) {
                HolidayDetailViewModel.HolidayDetailsDestinations.ITINERARY.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.PICKUP_NOTE.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.SPECIAL_NOTE.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.TAX.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.GENERAL_CONDITION.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.CANCELLATION_POLICY.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.INCLUDED_SERVICE.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
                HolidayDetailViewModel.HolidayDetailsDestinations.EXCLUDED_SERVICE.name -> {
                    findNavController().navigateSafe(
                        R.id.action_holidayDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        if (handler != null && update != null) {
            handler!!.removeCallbacks(update!!)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    @SuppressLint("SetTextI18n")
    override fun onPageSelected(position: Int) {
        bindingView.textViewImageCount.text = "${position + 1}/$totalImageCount"
    }

    private fun autoScroll() {
        handler = Handler()
        update = Runnable {
            if (currentPage == totalImageCount) {
                currentPage = 0
            }
            bindingView.viewPagerImage.setCurrentItem(currentPage++, true)
        }
        Timer().schedule(object : TimerTask() {

            override fun run() {
                handler!!.post(update!!)
            }
        }, 100, 3000)
    }
    }
