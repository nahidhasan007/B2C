package net.sharetrip.hotel.history.view.travelerlist

import android.view.View
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.databinding.FragmentTravellersRoomContactBinding
import net.sharetrip.hotel.history.HotelHistoryActivity
import net.sharetrip.hotel.history.model.Guest

class TravelerListFragment : BaseFragment<FragmentTravellersRoomContactBinding>() {

    override fun layoutId(): Int = R.layout.fragment_travellers_room_contact

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        setTitle(getString(R.string.travellers_info))
        val guest =
            arguments?.getParcelable<Guest>(ARG_TRAVELER_LIST)

        guest?.let {
            bindingView.primaryContact = guest.primaryContact
        }
        bindingView.lifecycleOwner = viewLifecycleOwner

        guest?.rooms?.let {

            val size = guest.rooms!!.size

            bindingView.recyclerRoomInformationList.adapter =
                TravelerInfoAdapter(guest.rooms!![0], 1)

            if (size > 1) {
                bindingView.recyclerRoomInformationListTwo.adapter =
                    TravelerInfoAdapter(guest.rooms!![1], 2)
                bindingView.recyclerRoomInformationListTwo.visibility = View.VISIBLE
            }
            if (size > 2) {
                bindingView.recyclerRoomInformationListThree.adapter =
                    TravelerInfoAdapter(guest.rooms!![2], 3)
                bindingView.recyclerRoomInformationListThree.visibility = View.VISIBLE
            }
            if (size > 3) {
                bindingView.recyclerRoomInformationListFour.adapter =
                    TravelerInfoAdapter(guest.rooms!![3], 4)
                bindingView.recyclerRoomInformationListFour.visibility = View.VISIBLE
            }
        }
    }

    private fun setTitle(title: String) {
        (activity as HotelHistoryActivity).supportActionBar?.title = title
    }

    companion object {
        const val ARG_TRAVELER_LIST = "ARG_TRAVELER_LIST"
    }
}
