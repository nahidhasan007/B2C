package net.sharetrip.shared.view.widgets.carousel

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.R
import net.sharetrip.shared.databinding.FragmentImagesBinding


class ImageFragment : BaseFragment<FragmentImagesBinding>() {

    override fun layoutId() = R.layout.fragment_images

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        val image = arguments?.getString(ImageScreen.ARG_TRANSFER_IMAGES)
        Glide.with(this)
            .load(image)
            .apply(RequestOptions().placeholder(R.drawable.ic_placeholder))
            .into(bindingView.imageViewDetails)
    }
}
