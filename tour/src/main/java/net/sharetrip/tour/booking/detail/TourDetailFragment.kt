package net.sharetrip.tour.booking.detail

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.sharetrip.base.event.Event
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.network.NetworkUtil
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.shared.view.widgets.carousel.ImageAdapter
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.FragmentTourDetailBinding
import net.sharetrip.tour.model.PeriodX
import net.sharetrip.tour.network.TourDataManager
import net.sharetrip.tour.utils.setTitleAndSubtitle
import net.sharetrip.tour.utils.setTripCoin
import java.util.*

class TourDetailFragment : BaseFragment<FragmentTourDetailBinding>(),
    ViewPager.OnPageChangeListener {

    private val viewModel: TourDetailViewModel by viewModels {
        val code = arguments?.getString(ARG_TOUR_PRODUCT_CODE)
        TourDetailVMF(
            code!!,
            NetworkUtil.hasNetwork(requireContext()),
            TourDetailRepo(TourDataManager.tourBookingAPIService),
            TourDataManager.getSharedPref(requireContext())
        )
    }

    private val offerAdapter = TourOfferAdapter()
    private var totalImageCount = 0
    private var currentPage = 0
    private var handler: Handler? = null
    private var update: Runnable? = null
    private var imageAdapter: ImageAdapter? = null

    override fun layoutId(): Int = R.layout.fragment_tour_detail

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.tour_detail))
        setTripCoin()

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.listOffer.adapter = offerAdapter

        viewModel.offers.observe(viewLifecycleOwner) {
            offerAdapter.update(it as ArrayList<PeriodX>)
        }

        viewModel.pickUpNotes.observe(viewLifecycleOwner) {
            bindingView.textViewHotelLocation.text = Html.fromHtml(it)
        }

        ItemClickSupport.addTo(bindingView.listOffer)
            .setOnItemClickListener { recyclerView, position, v ->
                viewModel.navigateToReserve(position)
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

        viewModel.navigateToDestinations.observe(viewLifecycleOwner, EventObserver {
            stopAutoScroll()
            when (it.first) {
                TourDetailViewModel.TourDetailNavDestinations.TO_RESERVE -> {
                    findNavController().navigate(
                        R.id.action_tourDetailFragment_to_tourReserveFragment,
                        it.second as Bundle
                    )
                }
                TourDetailViewModel.TourDetailNavDestinations.TO_HIGHLIGHT -> {
                    findNavController().navigate(
                        R.id.action_tourDetailFragment_to_highLightFragment,
                        it.second as Bundle
                    )
                }
            }
        })

        bindingView.textViewShare.setOnClickListener { viewModel.fetchShareUrl() }
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

    }

    override fun onStop() {
        super.onStop()
        stopAutoScroll()
    }

    private fun stopAutoScroll() {
        if (handler != null && update != null) {
            handler!!.removeCallbacks(update!!)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

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

    companion object {
        const val ARG_TOUR_PRODUCT_CODE = "ARG_TOUR_PRODUCT_CODE"
    }
}
