package net.sharetrip.tracker.view.cirium.view.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sharetrip.base.utils.ShareTripAppConstants
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.utils.getBitmapDescriptorFromVector
import net.sharetrip.shared.utils.loadImage
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.tracker.R
import net.sharetrip.tracker.databinding.FragmentFlightStatusDetailsBinding
import net.sharetrip.tracker.network.DataManager
import net.sharetrip.tracker.view.cirium.model.FlightPosition
import net.sharetrip.tracker.view.cirium.model.FlightTrackingDataHolder
import net.sharetrip.tracker.view.cirium.view.search.FlightTrackingSearchFragment

class FlightStatusDetailsFragment : BaseFragment<FragmentFlightStatusDetailsBinding>(),
    OnMapReadyCallback {

    private val viewModel: FlightStatusDetailsViewModel by viewModels {
        val trackingData =
            requireArguments().get(FlightTrackingSearchFragment.ARG_FLIGHT_TRACKING_DATA) as FlightTrackingDataHolder
        FlightStatusDetailsVMFactory(
            trackingData,
            FlightStatusDetailsRepository(DataManager.flightTrackerApiService)
        )
    }

    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null


    override fun layoutId() = R.layout.fragment_flight_status_details

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        initGoogleMap()

        viewModel.statusInfo.observe(viewLifecycleOwner) {
            bindingView.data = it
            bindingView.executePendingBindings()
        }

        viewModel.airlineIcon.observe(viewLifecycleOwner) {
            bindingView.iconAirlineLogo.loadImage(it)
        }

        viewModel.position.observe(viewLifecycleOwner) {
            if (checkGooglePlayServices())
                updateGoogleMapView(it)
        }
    }


    private fun initGoogleMap() {
        if (checkGooglePlayServices()) {
            mapFragment =
                childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
            mapFragment.getMapAsync(this)
            mapFragment.view?.visibility = View.GONE
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(requireContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            val dialog = availability.getErrorDialog(requireActivity(), resultCode, 0)
            dialog?.show()
            return false
        }
        return true
    }

    private fun updateGoogleMapView(flightPosition: FlightPosition) {
        mapFragment.view?.visibility = View.VISIBLE
        googleMap?.addMarker(
            MarkerOptions().position(LatLng(flightPosition.latitude, flightPosition.longitude))
                .title(flightPosition.airLineDetails)
        )?.setIcon(
            getBitmapDescriptorFromVector(requireContext(), R.drawable.ic_flight_blue)
        )
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(flightPosition.latitude, flightPosition.longitude),
                ShareTripAppConstants.FLIGHT_TRACKER_GOOGLE_MAP_ZOOM_LEVEL
            )
        )
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap!!.uiSettings.isZoomControlsEnabled = true
        googleMap!!.uiSettings.isCompassEnabled = true
        googleMap!!.uiSettings.isZoomGesturesEnabled = true
    }

    override fun onSaveInstanceState(outState: Bundle) {}
}
