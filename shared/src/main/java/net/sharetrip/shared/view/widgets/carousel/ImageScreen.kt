package net.sharetrip.shared.view.widgets.carousel

import android.os.Bundle
import androidx.fragment.app.Fragment
import net.sharetrip.shared.navigation.FragmentScreen

class ImageScreen(private val images: String) : FragmentScreen() {

    override fun onAddArguments(arguments: Bundle?) {
        arguments!!.putString(ARG_TRANSFER_IMAGES, images)
        super.onAddArguments(arguments)
    }

    override fun createFragment(): Fragment {
        return ImageFragment()
    }

    override fun getName(): String? {
        return ImageScreen::class.java.simpleName
    }

    companion object {
        const val ARG_TRANSFER_IMAGES = "ImageScreen.Image"
    }
}
