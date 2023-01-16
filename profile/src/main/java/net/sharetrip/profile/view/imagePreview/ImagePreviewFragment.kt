package net.sharetrip.profile.view.imagePreview

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator
import com.github.piasy.biv.loader.glide.GlideImageLoader
import net.sharetrip.shared.utils.hideAppbar
import net.sharetrip.shared.utils.showAppbar
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentImagePreviewBinding

class ImagePreviewFragment : BaseFragment<FragmentImagePreviewBinding>() {

    override fun layoutId(): Int = R.layout.fragment_image_preview

    override fun getViewModel(): BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BigImageViewer.initialize(GlideImageLoader.with(context))
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initOnCreateView() {
        hideAppbar(R.id.app_bar_layout)

        val imageUrl = requireArguments().getString(ARG_IMAGE_URL)

        if (imageUrl!!.contains("pdf")) {
            bindingView.webView.visibility = View.VISIBLE
            bindingView.mBigImage.visibility = View.GONE
            bindingView.webView.settings.javaScriptEnabled = true
            bindingView.webView.loadUrl("https://docs.google.com/viewer?embedded%20=%20true&url=$imageUrl")
        } else {
            bindingView.mBigImage.setProgressIndicator(ProgressPieIndicator())
            bindingView.mBigImage.showImage(
                Uri.parse(imageUrl)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showAppbar(R.id.app_bar_layout)
    }

    companion object {
        const val ARG_IMAGE_URL = "ImagePreviewScreen.imageUrl"
    }
}
