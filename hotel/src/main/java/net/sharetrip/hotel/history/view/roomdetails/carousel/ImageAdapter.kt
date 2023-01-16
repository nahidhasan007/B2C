package net.sharetrip.hotel.history.view.roomdetails.carousel

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.sharetrip.shared.view.adapter.SmartFragmentStatePagerAdapter
import net.sharetrip.hotel.history.view.roomdetails.carousel.ImageFragment.Companion.ARG_TRANSFER_IMAGES

class ImageAdapter(fragmentManager: FragmentManager, private val imagesItemList: List<String>) :
    SmartFragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(mPosition: Int): Fragment {
        val fragment = ImageFragment()
        val arguments = Bundle()
        arguments.putString(ARG_TRANSFER_IMAGES, imagesItemList[mPosition])
        fragment.arguments = arguments
        return fragment
    }

    override fun getCount(): Int {
        return imagesItemList.size
    }
}
