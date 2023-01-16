package net.sharetrip.view.onbording.carousel

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.sharetrip.shared.view.adapter.SmartFragmentStatePagerAdapter
import net.sharetrip.R
import net.sharetrip.view.onbording.carousel.OnBoardingSliderFragment.Companion.ARG_IMAGE_RES_ID
import net.sharetrip.view.onbording.carousel.OnBoardingSliderFragment.Companion.ARG_SUBTITLE_RES_ID
import net.sharetrip.view.onbording.carousel.OnBoardingSliderFragment.Companion.ARG_TITLE_RES_ID

class OnBoardingSliderAdapter(fragmentManager: FragmentManager) :
    SmartFragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val arguments = Bundle()

        when (position) {
            0 -> {
                arguments.putInt(ARG_IMAGE_RES_ID, R.drawable.ic_trip_onboarding)
                arguments.putInt(ARG_TITLE_RES_ID, R.string.onboarding_trip_title)
                arguments.putInt(ARG_SUBTITLE_RES_ID, R.string.onboarding_trip_subtitle)
            }
            1 -> {
                arguments.putInt(ARG_IMAGE_RES_ID, R.drawable.ic_coin_onboarding)
                arguments.putInt(ARG_TITLE_RES_ID, R.string.onboarding_coin_title)
                arguments.putInt(ARG_SUBTITLE_RES_ID, R.string.onboarding_coin_subtitle)
            }
            else -> {
                arguments.putInt(ARG_IMAGE_RES_ID, R.drawable.ic_book_onboarding)
                arguments.putInt(ARG_TITLE_RES_ID, R.string.onboarding_book_title)
                arguments.putInt(ARG_SUBTITLE_RES_ID, R.string.onboarding_book_subtitle)
            }
        }

        val fragment = OnBoardingSliderFragment()
        fragment.arguments = arguments
        return fragment
    }

    override fun getCount(): Int = 3
}
