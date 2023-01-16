package net.sharetrip.view.onbording

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.sharetrip.base.data.PrefKey
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentOnBoardingBinding
import net.sharetrip.network.MainDataManager
import net.sharetrip.view.onbording.carousel.OnBoardingSliderAdapter

class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>(),
    OnPageChangeListener, View.OnTouchListener {
    private lateinit var onBoardingSliderAdapter: OnBoardingSliderAdapter
    private lateinit var pageChangeHandler: Handler



    override fun layoutId(): Int = R.layout.fragment_on_boarding

    override fun getViewModel(): BaseViewModel? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun initOnCreateView() {
        activity?.supportFragmentManager?.let {
            onBoardingSliderAdapter = OnBoardingSliderAdapter(it)
        }

        pageChangeHandler = Handler(Looper.getMainLooper()) {
            val currentPage = bindingView.onboardViewPager.currentItem
            val nextPage = currentPage + 1
            if (::onBoardingSliderAdapter.isInitialized && nextPage < onBoardingSliderAdapter.count) {
                bindingView.onboardViewPager.currentItem = nextPage
                pageChangeHandler.sendEmptyMessageDelayed(
                    0,
                    ON_BOARDING_PAGE_CHANGE_INTERVAL_TIME.toLong()
                )
            }
            false
        }

        bindingView.onboardViewPager.addOnPageChangeListener(this)
        bindingView.onboardViewPager.setOnTouchListener(this)
        bindingView.onboardViewPager.adapter = onBoardingSliderAdapter
        bindingView.indicator.setViewPager(bindingView.onboardViewPager)
        pageChangeHandler.sendEmptyMessageDelayed(
            0,
            ON_BOARDING_PAGE_CHANGE_INTERVAL_TIME.toLong()
        )

        bindingView.getStartButton.setOnClickListener { onGetStartedButtonClicked() }
    }

    

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (position == 2) saveOnBoardingStatus()
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        pageChangeHandler.removeCallbacksAndMessages(null)
        return false
    }

    override fun onStop() {
        pageChangeHandler.removeCallbacksAndMessages(null)
        super.onStop()
    }

    override fun onStart() {
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN )
        super.onStart()
    }

    private fun onGetStartedButtonClicked() {
        saveOnBoardingStatus()
        findNavController().popBackStack()
    }

    private fun saveOnBoardingStatus() {
        MainDataManager.getSharedPref(requireContext()).put(PrefKey.IS_ON_BOARDING_ONCE, true)
    }

    override fun onDetach() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onDetach()
    }

    companion object {
        private const val ON_BOARDING_PAGE_CHANGE_INTERVAL_TIME = 3000
    }
}
