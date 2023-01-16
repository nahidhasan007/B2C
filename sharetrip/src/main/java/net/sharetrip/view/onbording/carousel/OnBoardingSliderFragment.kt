package net.sharetrip.view.onbording.carousel

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.R
import net.sharetrip.databinding.FragmentOnBoardingSliderBinding

class OnBoardingSliderFragment : BaseFragment<FragmentOnBoardingSliderBinding>() {

    override fun layoutId() = R.layout.fragment_on_boarding_slider



    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        @DrawableRes
        val mDemographicImageResId = arguments?.getInt(ARG_IMAGE_RES_ID)!!

        @StringRes
        val mTitleResId = arguments?.getInt(ARG_TITLE_RES_ID)!!

        @StringRes
        val mSubTitleResId = arguments?.getInt(ARG_SUBTITLE_RES_ID)!!

        bindingView.demographicImageView.setImageResource(mDemographicImageResId)
        bindingView.titleTextView.setText(mTitleResId)
        bindingView.subtitleTextView.setText(mSubTitleResId)
    }

    

    companion object {
        const val ARG_IMAGE_RES_ID = "ARG_IMAGE_RES_ID"
        const val ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID"
        const val ARG_SUBTITLE_RES_ID = "ARG_SUBTITLE_RES_ID"
    }
}
