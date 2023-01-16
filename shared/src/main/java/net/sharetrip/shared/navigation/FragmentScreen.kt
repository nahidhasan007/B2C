package net.sharetrip.shared.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class FragmentScreen : Screen {
    private var mFragment: Fragment? = null
    val fragment: Fragment
        get() {
            if (mFragment != null) {
                return mFragment!!
            }
            mFragment = createFragment()
            checkNotNull(mFragment) { "createFragment() returns null" }
            val mArguments = Bundle()
            onAddArguments(mArguments)
            mFragment!!.arguments = mArguments
            return mFragment!!
        }

    protected open fun onAddArguments(arguments: Bundle?) {
        arguments!!.putString(BF_NAME, getName())
    }

     abstract fun createFragment(): Fragment?
     abstract fun getName(): String?

    companion object {
        const val BF_NAME = "FragmentScreen.name"
    }
}