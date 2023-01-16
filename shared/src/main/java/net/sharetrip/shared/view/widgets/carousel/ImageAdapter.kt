package net.sharetrip.shared.view.widgets.carousel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.sharetrip.shared.view.adapter.SmartFragmentStatePagerAdapter

class ImageAdapter(fragmentManager: FragmentManager, private val imagesItemList: List<String>) : SmartFragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(mPosition: Int): Fragment {
        return ImageScreen(imagesItemList[mPosition]).fragment
    }

    override fun getCount(): Int {
        return imagesItemList.size
    }
}
