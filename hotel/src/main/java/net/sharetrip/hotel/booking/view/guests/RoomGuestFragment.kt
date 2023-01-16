package net.sharetrip.hotel.booking.view.guests

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.hotel.R
import net.sharetrip.hotel.booking.model.GuestInfo
import net.sharetrip.hotel.booking.view.nationality.NationalityFragment.Companion.ARG_NATIONALITY_BACK_DATA
import net.sharetrip.hotel.booking.view.nationality.NationalityViewModel.Companion.EXTRA_COUNTRY_NAME
import net.sharetrip.hotel.databinding.FragmentRoomGuestBinding
import net.sharetrip.hotel.network.DataManager
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment

class RoomGuestFragment : BaseFragment<FragmentRoomGuestBinding>() {
    private val viewModel by lazy {
        val guestList = roomGuestBundle.getParcelableArrayList<GuestInfo>(ARG_ROOM_GUEST_LIST)!!

        ViewModelProvider(
            this,
            RoomGuestVMFactory(
                guestList, roomNo,
                DataManager.hotelApiService, DataManager.getSharedPref(requireContext())
            )
        )[RoomGuestViewModel::class.java]
    }

    private lateinit var adapter: GuestAdapter
    private val roomGuestBundle by lazy { arguments?.getBundle(ARG_ROOM_GUEST_BUNDLE)!! }
    private val roomNo by lazy { roomGuestBundle.getInt(ARG_GUEST_ROOM_NO) }

    override fun layoutId(): Int = R.layout.fragment_room_guest

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle("Guests of Room $roomNo")
        hideTripCoin()

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.done, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_done == menuItem.itemId) {
                    viewModel.onClickSaveMenu()
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.passengerList.observe(viewLifecycleOwner) {
            adapter = GuestAdapter(viewModel)
            adapter.update(viewModel.guestList, it)
            bindingView.listGuest.adapter = adapter
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            setNavigationResult(it, ARG_TRAVELLER)
            findNavController().popBackStack()
        }

        viewModel.navigateToNationality.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(R.id.action_roomGuestFragment_to_nationalityFragment)
        }

        getNavigationResultLiveData<Intent>(ARG_NATIONALITY_BACK_DATA)?.observe(viewLifecycleOwner) { data ->
            val name = data.getStringExtra(EXTRA_COUNTRY_NAME)
            viewModel.guestList[viewModel.guestPosition].nationality = name
            adapter.update(viewModel.guestList, viewModel.otherPassengerList)
        }
    }

    companion object {
        const val ARG_ROOM_GUEST_BUNDLE = "ARG_ROOM_GUEST_BUNDLE"
        const val ARG_ROOM_GUEST_LIST = "ARG_ROOM_GUEST_LIST"
        const val ARG_GUEST_ROOM_NO = "ARG_GUEST_ROOM_NO"
    }
}
