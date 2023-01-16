package net.sharetrip.shared.view.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

abstract class SmartFragmentStatePagerAdapter(fragmentManager: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManager) {
    private val mRegisteredFragments = SparseArray<Fragment>()

    override fun instantiateItem(mContainer: ViewGroup, mPosition: Int): Any {
        val mFragment = super.instantiateItem(mContainer, mPosition) as Fragment
        mRegisteredFragments.put(mPosition, mFragment)
        return mFragment
    }

    override fun destroyItem(mContainer: ViewGroup, mPosition: Int, mObject: Any) {
        mRegisteredFragments.remove(mPosition)
        super.destroyItem(mContainer, mPosition, mObject)
    }
}