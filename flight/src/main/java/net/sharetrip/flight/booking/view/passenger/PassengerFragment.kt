package net.sharetrip.flight.booking.view.passenger

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.EventObserver
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.R
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.booking.view.flightdetails.FlightDetailsFragment.Companion.ARG_FLIGHTS
import net.sharetrip.flight.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_FOR_CONFIRMATION
import net.sharetrip.flight.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_TAG
import net.sharetrip.flight.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_URL
import net.sharetrip.flight.booking.view.passenger.baggageinsurance.BaggageInsuranceBottomSheet
import net.sharetrip.flight.booking.view.passenger.covidTest.CovidInfoBottomSheet
import net.sharetrip.flight.booking.view.passenger.mealWheelchair.MealWheelchairBottomSheet
import net.sharetrip.flight.booking.view.passenger.travelinsurance.TravelInsuranceBottomSheet
import net.sharetrip.flight.booking.view.travellerdetails.TravelerDetailsFragment
import net.sharetrip.flight.booking.view.travellerdetails.TravelerDetailsFragment.Companion.ARG_ADAPTER_POSITION
import net.sharetrip.flight.booking.view.travellerdetails.TravelerDetailsFragment.Companion.ARG_TRAVELLER_DATA
import net.sharetrip.flight.databinding.BottomSheetTravelAdviceTermsFlightBinding
import net.sharetrip.flight.databinding.FragmentFlightPassengerDetailsBinding
import net.sharetrip.flight.utils.FileUtils
import net.sharetrip.flight.utils.ShearedViewModel
import net.sharetrip.flight.utils.imageCoprresion.ImageZipper
import net.sharetrip.flight.utils.setTitleSubTitle
import net.sharetrip.shared.model.PassengerTypeTitle
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.view.BaseFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PassengerFragment : BaseFragment<FragmentFlightPassengerDetailsBinding>() {
    private val itemTraveller by lazy {
        requireArguments().getBundle(ARG_TRAVELLER_DATA)
            ?.getParcelable<ItemTraveler>(ARG_TRAVELLER)!!
    }
    private val flights by lazy {
        requireArguments().getBundle(ARG_TRAVELLER_DATA)?.getParcelable<Flights>(ARG_FLIGHTS)!!
    }
    private val flightSearch by lazy {
        requireArguments().get(TravelerDetailsFragment.ARG_FLIGHT_SEARCH) as FlightSearch
    }

    private val viewModel: PassengerViewModel by lazy {
        PassengerViewModelFactory(
            SharedPrefsHelper(requireContext()),
            flights,
            flightSearch,
            itemTraveller,
            dateOfFlight
        ).create(PassengerViewModel::class.java)
    }

    private lateinit var dateOfFlight: String
    private lateinit var shearedViewModel: ShearedViewModel
    private lateinit var profDialog: AlertDialog
    private var makeDirectory: File? = null
    private var cameraFile: File? = null
    private var phnMemoryBasePath: String? = null
    private var timeStamp: String? = null
    private var finalPath: String? = null
    private var passengerTypeString = ""
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private var adaptersPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loader = layoutInflater.inflate(R.layout.layout_please_wait, null, false)
        loader.layout(0, 0, 0, 0)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("")
            .setView(loader)
            .setCancelable(true)
        profDialog = builder.create()

        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && viewModel.requestCode > 0) {
                    getActivityResult(viewModel.requestCode, result)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (itemTraveller.givenName.isNullOrEmpty())
            setTitleSubTitle(itemTraveller.passengerTypeTitle)
        else
            setTitleSubTitle(itemTraveller.givenName)
        hideTripCoin()
    }

    override fun onStop() {
        super.onStop()
        showTripCoin()
    }

    override fun layoutId(): Int = R.layout.fragment_flight_passenger_details

    override fun getViewModel(): BaseViewModel? = null

    override fun initOnCreateView() {
        shearedViewModel = ViewModelProvider(requireActivity())[ShearedViewModel::class.java]
        adaptersPosition =
            requireArguments().getBundle(ARG_TRAVELLER_DATA)?.getInt(ARG_ADAPTER_POSITION)!!
        dateOfFlight =
            requireArguments().getBundle(ARG_TRAVELLER_DATA)?.getString(FLIGHT_DATE_PASSENGERS)!!

        bindingView.viewModel = viewModel

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

        viewModel.userBirthDate.observe(viewLifecycleOwner) {
            bindingView.birthDayTextInputEditText.setExistingDate(it)
        }

        viewModel.travelInsuranceList.observe(viewLifecycleOwner) {
            val isOptionEmpty = viewModel.travelInsuranceOption.optionCode.isEmpty()
            if (isOptionEmpty) {
                it.forEach { item ->
                    item.options.forEach {
                        if (it.default) {
                            viewModel.travelInsuranceOption = TravelInsuranceOption(
                                code = item.code, optionCode = it.optionCode, it.price,
                                item.name + ", " + it.name,
                                it.discountPrice,
                                it.isSelected,
                                item.logo,
                                default = it.default
                            )
                            viewModel.setTravelInsuranceAddOnName()

                            if (item.self) {
                                viewModel.travelInsurance = null
                            } else {
                                viewModel.travelInsurance = TravelInsurance(
                                    code = item.code,
                                    optionsCode = it.optionCode
                                )
                            }
                        }
                    }
                }
            }
        }

        viewModel.baggageInsuranceList.observe(viewLifecycleOwner) {
            val isOptionEmpty = viewModel.baggageInsuranceOption.optionCode.isEmpty()
            if (isOptionEmpty) {
                it.forEach { item ->
                    item.options.forEach {
                        if (it.default) {
                            viewModel.baggageInsuranceOption = BaggageInsuranceOption(
                                code = item.code, optionCode = it.optionCode, it.price,
                                item.name + ", " + it.name,
                                it.discountPrice,
                                it.isSelected,
                                item.logo,
                                default = it.default
                            )
                            viewModel.setBaggageInsuranceAddOnName()

                            if (item.self) {
                                viewModel.baggageInsurance = null
                            } else {
                                viewModel.baggageInsurance = BaggageInsuranceBody(
                                    code = item.code,
                                    optionsCode = it.optionCode
                                )
                            }
                        }
                    }
                }
            }
        }

        viewModel.dataInvalid.observe(viewLifecycleOwner) {
            if (it) {
                val traveller = viewModel.passenger.get()

                if (!traveller?.surName.isNameValid())
                    showWrongTextErrorMsg(bindingView.surNameTextInputLayout)
                else
                    showEmptyErrorMsg(bindingView.surNameTextInputLayout, traveller?.surName)

                showEmptyErrorMsg(bindingView.birthDayTextInputLayout, traveller?.dateOfBirth)
                showEmptyErrorMsg(
                    bindingView.nationalityTextInputLayout,
                    traveller?.nationality
                )
                showEmptyErrorMsg(
                    bindingView.passportNumberTextInputLayout,
                    traveller?.passportNumber
                )
                showEmptyErrorMsg(
                    bindingView.passportExpireDateTextInputLayout,
                    traveller?.passportExpireDate
                )
            }
        }

        viewModel.showMessage.observe(viewLifecycleOwner, EventObserver {
            showToastMessage(it)
        })

        viewModel.flightAddOnsDetails.observe(viewLifecycleOwner) {
            val mBottomSheetDialog =
                BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)

            val sheetView = DataBindingUtil.inflate<BottomSheetTravelAdviceTermsFlightBinding>(
                layoutInflater, R.layout.bottom_sheet_travel_advice_terms_flight, null, false
            )

            mBottomSheetDialog.setContentView(sheetView.root)
            sheetView.tvHead.text = it.name
            sheetView.tvHead.setOnClickListener {
                mBottomSheetDialog.dismiss()
            }

            sheetView.webView.loadDataWithBaseURL(
                null,
                it.description,
                "text/html",
                "utf-8",
                null
            )

            mBottomSheetDialog.show()
        }

        shearedViewModel.covidServiceDetails.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.fetchAddOnsDetails(it, "covid")
                shearedViewModel.covidServiceDetails.value = ""
            }
        }

        shearedViewModel.travelInsuranceDetails.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.fetchAddOnsDetails(it, "travelInsurance")
                shearedViewModel.travelInsuranceDetails.value = ""
            }
        }

        shearedViewModel.baggageInsuranceDetails.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.fetchAddOnsDetails(it, "baggageInsurance")
                shearedViewModel.baggageInsuranceDetails.value = ""
            }
        }

        viewModel.passportValid.observe(viewLifecycleOwner) {
            if (!it)
                Toast.makeText(
                    requireActivity(),
                    "Passport Expiry date is not valid",
                    Toast.LENGTH_LONG
                ).show()
        }

        viewModel.passportValid.observe(viewLifecycleOwner) {
            if (!it)
                Toast.makeText(
                    requireActivity(),
                    "Passport Expiry date is not valid",
                    Toast.LENGTH_LONG
                )
                    .show()
        }

        viewModel.isPassportNumberValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.passportNumberTextInputLayout.error = null
            } else {
                bindingView.passportNumberTextInputLayout.error = " "
            }
        }

        viewModel.isPassportExpiryDateValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.passportExpireDateTextInputLayout.error = null
            } else {
                bindingView.passportExpireDateTextInputLayout.error =
                    getString(R.string.pasport_must_have_atleast_6_month_validity)
            }
        }

        viewModel.passengerType.observe(viewLifecycleOwner) {
            passengerTypeString = it
        }

        viewModel.passengerList.observe(viewLifecycleOwner) {
            val givenNameList = ArrayList<String>()
            for (passenger in it) {
                givenNameList.add("${passenger.givenName} ${passenger.surName}")
            }
            val adapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    givenNameList
                )
            bindingView.quickPickAutoCompleteTextView.setAdapter(adapter)
            bindingView.quickPickAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                viewModel.setQuickPickerData(position)
            }
        }

        viewModel.imageUploadChoiceNew.observe(viewLifecycleOwner, EventObserver {
            chooseImageOption(it, true)
        })

        viewModel.imageUploadChoiceOld.observe(viewLifecycleOwner, EventObserver {
            chooseImageOption(it, false)
        })

        viewModel.passportProgress.observe(viewLifecycleOwner) {
            bindingView.passportProgressBar.progress = it
        }

        viewModel.visaProgress.observe(viewLifecycleOwner) {
            bindingView.visaProgressBar.progress = it
        }

        viewModel.isLoaderShow.observe(viewLifecycleOwner) {
            if (it) {
                if (!profDialog.isShowing) {
                    profDialog.show()
                }
            } else {
                profDialog.dismiss()
            }
        }

        viewModel.isFirstNameValid.observe(viewLifecycleOwner) {
            bindingView.givenNameTextInputLayout.helperText = getString(R.string.only_use_letter)
        }

        viewModel.isLastNameValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.surNameTextInputLayout.helperText = getString(R.string.required)
            } else {
                bindingView.surNameTextInputLayout.error = getString(R.string.only_use_letter)
            }
        }

        viewModel.requestWheelchairLiveData.observe(viewLifecycleOwner) { pairItem ->
            val bundle = Bundle()
            bundle.putString(PrefKey.TITLE, SsrEnum.WHEELCHAIR.toString())
            bundle.putString(PrefKey.SELECTED_SSR, pairItem.second)
            bundle.putParcelableArrayList(PrefKey.SSR_LIST, pairItem.first)
            val mealWheelchairFragment = MealWheelchairBottomSheet()
            mealWheelchairFragment.arguments = bundle

            parentFragmentManager.let {
                mealWheelchairFragment.show(
                    it,
                    PassengerFragment::class.java.simpleName
                )
            }
        }

        viewModel.mealTypeLiveData.observe(viewLifecycleOwner) { pairItem ->
            val bundle = Bundle()
            bundle.putString(PrefKey.TITLE, SsrEnum.MEAL.toString())
            bundle.putString(PrefKey.SELECTED_SSR, pairItem.second)
            bundle.putParcelableArrayList(PrefKey.SSR_LIST, pairItem.first)
            val mealWheelchairFragment = MealWheelchairBottomSheet()
            mealWheelchairFragment.arguments = bundle

            parentFragmentManager.let {
                mealWheelchairFragment.show(
                    it,
                    PassengerFragment::class.java.simpleName
                )
            }
        }

        viewModel.covidInfoClicked.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    ARGS_COVID_ADD_ON_LIST,
                    it as ArrayList<out Parcelable>
                )
                bundle.putParcelable(ARGS_SELECTED_COVID_ADD_ON, viewModel.covidTestOption)
                val covidAddOnFragment: CovidInfoBottomSheet =
                    CovidInfoBottomSheet.newInstance()
                covidAddOnFragment.arguments = bundle
                parentFragmentManager.let { fragmentManager ->
                    covidAddOnFragment.show(
                        fragmentManager,
                        PassengerFragment::class.java.simpleName
                    )
                }
            } else {
                Toast.makeText(requireContext(), "No Data Available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.travelInsuranceClicked.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    ARGS_TRAVEL_INSURANCE_LIST,
                    it as ArrayList<out Parcelable>
                )
                bundle.putParcelable(
                    ARGS_SELECTED_TRAVEL_INSURANCE_OPTION,
                    viewModel.travelInsuranceOption
                )
                val travelInsuranceAddOnFragment: TravelInsuranceBottomSheet =
                    TravelInsuranceBottomSheet.newInstance()
                travelInsuranceAddOnFragment.arguments = bundle
                parentFragmentManager.let { fragmentManager ->
                    travelInsuranceAddOnFragment.show(
                        fragmentManager,
                        PassengerFragment::class.java.simpleName
                    )
                }
            } else {
                Toast.makeText(requireContext(), "No Data Available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.baggageInsuranceClicked.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                val bundle = Bundle()
                bundle.putParcelableArrayList(
                    ARGS_BAGGAGE_INSURANCE_LIST,
                    it as ArrayList<out Parcelable>
                )
                bundle.putParcelable(
                    ARGS_SELECTED_BAGGAGE_INSURANCE_OPTION,
                    viewModel.baggageInsuranceOption
                )
                val baggageInsuranceAddOnFragment: BaggageInsuranceBottomSheet =
                    BaggageInsuranceBottomSheet.newInstance()
                baggageInsuranceAddOnFragment.arguments = bundle
                parentFragmentManager.let { fragmentManager ->
                    baggageInsuranceAddOnFragment.show(
                        fragmentManager,
                        PassengerFragment::class.java.simpleName
                    )
                }
            } else {
                Toast.makeText(requireContext(), "No Data Available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.saveInfoClicked.observe(viewLifecycleOwner, EventObserver {
            setNavigationResult(viewModel.itemTraveler, "itemTraveler")
            findNavController().navigateUp()
        })

        viewModel.gotoNationality.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_passenger_to_nationality)
        })

        viewModel.goToImageConfirmation.observe(viewLifecycleOwner, EventObserver {
            val bundle = Bundle()
            when (it) {
                "passport" -> {
                    bundle.putString(ARG_IMAGE_URL, finalPath)
                    bundle.putString(ARG_IMAGE_TAG, getString(R.string.passport_copy_confirm))
                    bundle.putBoolean(ARG_FOR_CONFIRMATION, true)
                    findNavController().navigateSafe(
                        R.id.action_passenger_to_imagePreview,
                        bundleOf(ARG_IMAGE_DATA to bundle)
                    )
                    profDialog.dismiss()
                }
                "visa" -> {
                    bundle.putString(ARG_IMAGE_TAG, getString(R.string.visa_copy_preview))
                    bundle.putString(ARG_IMAGE_URL, viewModel.passenger.get()?.visaCopy)
                    bundle.putBoolean(ARG_FOR_CONFIRMATION, true)
                    findNavController().navigateSafe(
                        R.id.action_passenger_to_imagePreview,
                        bundleOf(ARG_IMAGE_DATA to bundle)
                    )
                    profDialog.dismiss()
                }
            }
        })

        shearedViewModel.mealTypeViewModel.observe(viewLifecycleOwner) {
            viewModel.setMealValue(it)
        }

        shearedViewModel.covidTestOption.observe(viewLifecycleOwner) {
            viewModel.setCovidInfo(it)
        }


        shearedViewModel.travelInsuranceOption.observe(viewLifecycleOwner, EventObserver {
            viewModel.setTravelInsuranceInfo(it)
        })

        shearedViewModel.baggageInsuranceOption.observe(viewLifecycleOwner, EventObserver {
            viewModel.setBaggageInsuranceInfo(it)
        })

        shearedViewModel.requestWheelchairViewModel.observe(viewLifecycleOwner) {
            viewModel.setWheelchairValue(it)
        }

        getNavigationResultLiveData<NationalityCode>("NationalityCode")?.observe(viewLifecycleOwner) {
            viewModel.setCountryCode(it.code, it.name)
            bindingView.nationalityInputEditText.setText(it.code)
        }

        getNavigationResultLiveData<Boolean>("confirmation")?.observe(viewLifecycleOwner) {
            if (it) {
                profDialog.show()
                val mime = FileUtils.getMimeType(cameraFile.toString())
                if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                    viewModel.updateImageFile(
                        ImageZipper(requireContext()).compressToFile(cameraFile),
                        "passport",
                        mime
                    )
                } else {
                    viewModel.updateImageFile(cameraFile!!, "passport", mime)
                }
            }
        }
    }

    private fun getActivityResult(requestCode: Int, result: ActivityResult) {
        try {
            when (requestCode) {
                RESULT_PASSPORT_CAMERA_CODE_SELECTION -> {
                    val mime = FileUtils.getMimeType(cameraFile.toString())
                    if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                        viewModel.updateImageFile(
                            ImageZipper(requireContext()).compressToFile(cameraFile),
                            "passport",
                            mime
                        )
                    } else {
                        viewModel.updateImageFile(cameraFile!!, "passport", mime)
                    }
                }

                RESULT_PASSPORT_GALLERY_CODE_SELECTION -> {
                    if (result.data?.data != null) {
                        val mime =
                            result.data?.data?.let { requireActivity().contentResolver.getType(it) }
                        if (mime?.contains("image") == true || mime?.contains("pdf") == true) {
                            val file =
                                File(
                                    FileUtils.getRealPath(
                                        requireContext(),
                                        result.data?.data!!
                                    )!!
                                )
                            if (Integer.parseInt((file.length() / 1024).toString()) > 1024 && mime.contains(
                                    "image"
                                )
                            ) {
                                viewModel.updateImageFile(
                                    ImageZipper(requireContext()).compressToFile(file),
                                    "passport",
                                    mime
                                )
                            } else {
                                viewModel.updateImageFile(file, "passport", mime)
                            }
                        } else
                            showToastMessage("Wrong formatted document")
                    } else
                        showToastMessage("Wrong formatted document")
                }

                RESULT_VISA_CAMERA_CODE_SELECTION -> {
                    val mime = FileUtils.getMimeType(cameraFile.toString())
                    if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                        viewModel.updateImageFile(
                            ImageZipper(requireContext()).compressToFile(
                                cameraFile
                            ), "visa", mime
                        )
                    } else {
                        viewModel.updateImageFile(cameraFile!!, "visa", mime)
                    }
                }

                RESULT_VISA_GALLERY_CODE_SELECTION -> {
                    if (result.data?.data != null) {
                        val mime =
                            requireActivity().contentResolver.getType(result.data?.data!!)
                        if (mime?.contains("image") == true || mime?.contains("pdf") == true) {
                            val gallerySelectedFile =
                                File(
                                    FileUtils.getRealPath(
                                        requireActivity(),
                                        result.data?.data!!
                                    )!!
                                )
                            if (Integer.parseInt((gallerySelectedFile.length() / 1024).toString()) > 1024 && mime.contains(
                                    "image"
                                )
                            ) {
                                viewModel.updateImageFile(
                                    ImageZipper(requireContext()).compressToFile(gallerySelectedFile),
                                    "passport",
                                    mime
                                )
                            } else {
                                viewModel.updateImageFile(gallerySelectedFile, "visa", mime)
                            }
                        } else
                            showToastMessage("Wrong formatted document")
                    } else
                        showToastMessage("Wrong formatted document")
                }
            }
        } catch (e: Exception) {
            e.stackTrace
            showToastMessage("Something went wrong")
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showEmptyErrorMsg(inputLayout: TextInputLayout, text: String?) {
        if (text != null && text.trim().isEmpty()) {
            inputLayout.isErrorEnabled = true
            inputLayout.error = "*Required"
        } else {
            inputLayout.isErrorEnabled = false
        }
    }

    private fun showWrongTextErrorMsg(inputLayout: TextInputLayout) {
        inputLayout.isErrorEnabled = true
        inputLayout.error = "Only use Letter"
    }

    @SuppressLint("InflateParams")
    private fun chooseImageOption(tag: String, isAlreadyUploaded: Boolean) {
        val viewLayout: LinearLayout
        val cameraLayout: LinearLayout
        val galleryLayout: LinearLayout
        val view = layoutInflater.inflate(R.layout.bottom_sheet_picker, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        viewLayout = view.findViewById(R.id.viewLayout)
        cameraLayout = view.findViewById(R.id.cameraLayout)
        galleryLayout = view.findViewById(R.id.galleryLayout)
        if (isAlreadyUploaded) {
            viewLayout.visibility = View.GONE
        } else {
            viewLayout.visibility = View.VISIBLE
        }
        cameraLayout.setOnClickListener {
            var selectionCode = 0
            when (tag) {
                "passport" -> selectionCode = RESULT_PASSPORT_CAMERA_CODE_SELECTION
                "visa" -> selectionCode = RESULT_VISA_CAMERA_CODE_SELECTION
            }
            dialog.dismiss()
            writePermissionCheck(selectionCode)
        }
        galleryLayout.setOnClickListener {
            var selectionCode = 0
            when (tag) {
                "passport" -> selectionCode = RESULT_PASSPORT_GALLERY_CODE_SELECTION
                "visa" -> selectionCode = RESULT_VISA_GALLERY_CODE_SELECTION
            }
            dialog.dismiss()
            readPermissionCheck(selectionCode)
        }

        viewLayout.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle()
            when (tag) {
                "passport" -> {
                    bundle.putString(ARG_IMAGE_URL, viewModel.passenger.get()?.passportCopy)
                    bundle.putString(ARG_IMAGE_TAG, getString(R.string.passport_copy_preview))
                    findNavController().navigateSafe(
                        R.id.action_passenger_to_imagePreview,
                        bundleOf(ARG_IMAGE_DATA to bundle)
                    )
                }
                "visa" -> {
                    bundle.putString(ARG_IMAGE_TAG, getString(R.string.visa_copy_preview))
                    bundle.putString(ARG_IMAGE_URL, viewModel.passenger.get()?.visaCopy)
                    findNavController().navigateSafe(
                        R.id.action_passenger_to_imagePreview,
                        bundleOf(ARG_IMAGE_DATA to bundle)
                    )
                }
            }
        }
        dialog.show()
    }

    private fun writePermissionCheck(selectionCode: Int) {
        Dexter.withContext(requireActivity())
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        phnMemoryBasePath =
                            requireContext().getExternalFilesDir(null)?.absolutePath + "/shareTrip/"
                        makeDirectory = File(phnMemoryBasePath!!)
                        makeDirectory!!.mkdir()
                        cameraIntent(selectionCode)
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.permission_deined),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }

    private fun readPermissionCheck(selectionCode: Int) {
        Dexter.withContext(requireActivity())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    galleryIntent(selectionCode)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.permission_deined),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    @SuppressLint("SimpleDateFormat")
    private fun cameraIntent(selectionCode: Int) {
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        finalPath = "$phnMemoryBasePath$timeStamp.jpeg"
        cameraFile = File(finalPath!!)
        try {
            cameraFile!!.createNewFile()
        } catch (e: IOException) {
            e.stackTrace
        }

        val outputFileUri = FileProvider.getUriForFile(
            requireActivity(),
            "$APPLICATION_ID.provider",
            cameraFile!!
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        viewModel.requestCode = selectionCode
        startActivityForResult.launch(cameraIntent)
    }

    private fun galleryIntent(selectionCode: Int) {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        viewModel.requestCode = selectionCode
        startActivityForResult.launch(chooseFile)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onResume() {
        super.onResume()

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date: Date = sdf.parse(dateOfFlight)!!
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MONTH, 6)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        bindingView.passportExpireDateInputEditText.setPassportRange(sdf.format(calendar.time))

        val calendarForBirthday = Calendar.getInstance()
        calendarForBirthday.time = date
        when (passengerTypeString) {
            PassengerTypeTitle.Adult.name -> {
                calendarForBirthday.add(Calendar.YEAR, -12)
                bindingView.birthDayTextInputEditText.setBirthdayRange(
                    sdf.format(
                        calendarForBirthday.time
                    )
                )
            }
            PassengerTypeTitle.Child.name -> {
                calendarForBirthday.add(Calendar.YEAR, -12)
                calendarForBirthday.add(Calendar.DAY_OF_MONTH, 2)
                val calMaxDate = Calendar.getInstance()
                calMaxDate.time = date
                calMaxDate.add(Calendar.YEAR, -2)
                bindingView.birthDayTextInputEditText.setRange(
                    sdf.format(calendarForBirthday.time),
                    sdf.format(calMaxDate.time)
                )
            }
            else -> {
                calendarForBirthday.add(Calendar.YEAR, -2)
                calendarForBirthday.add(Calendar.DAY_OF_MONTH, 2)
                bindingView.birthDayTextInputEditText.setRange(
                    sdf.format(calendarForBirthday.time),
                    sdf.format(Calendar.getInstance().time)
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.travelInsuranceOption = TravelInsuranceOption()
    }

    companion object {
        const val RESULT_PASSPORT_CAMERA_CODE_SELECTION = 206
        const val RESULT_PASSPORT_GALLERY_CODE_SELECTION = 207
        const val RESULT_VISA_CAMERA_CODE_SELECTION = 208
        const val RESULT_VISA_GALLERY_CODE_SELECTION = 209
        const val ARGS_COVID_ADD_ON_LIST = "COVID_ADD_ON_LIST"
        const val ARGS_SELECTED_COVID_ADD_ON = "ARGS_SELECTED_COVID_ADD_ONT"
        const val ARGS_TRAVEL_INSURANCE_LIST = "ARGS_TRAVEL_INSURANCE_LIST"
        const val ARGS_SELECTED_TRAVEL_INSURANCE_OPTION = "ARGS_SELECTED_TRAVEL_INSURANCE_OPTION"
        const val ARGS_BAGGAGE_INSURANCE_LIST = "ARGS_BAGGAGE_INSURANCE_LIST"
        const val ARGS_SELECTED_BAGGAGE_INSURANCE_OPTION = "ARGS_SELECTED_BAGGAGE_INSURANCE_OPTION"
        const val APPLICATION_ID = "net.sharetrip"
        const val FLIGHT_DATE_PASSENGERS = "FLIGHT_DATE_PASSENGERS"
        const val ARG_IMAGE_DATA = "ARG_IMAGE_DATA"
    }
}
