package net.sharetrip.profile.view.addtraveler

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.sharetrip.base.event.EventObserver
import net.sharetrip.shared.utils.FileUtils
import net.sharetrip.shared.utils.getNavigationResultLiveData
import net.sharetrip.shared.utils.imageCoprresion.ImageZipper
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.utils.setTitleAndSubtitle
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentAddTravelerBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.profile.view.addtraveler.AddTravelerViewModel.Companion.GOTO_BACK
import net.sharetrip.profile.view.addtraveler.AddTravelerViewModel.Companion.GOTO_COUNTRY_CURRENCY
import net.sharetrip.profile.view.addtraveler.AddTravelerViewModel.Companion.GOTO_IMAGE_PREVIEW
import net.sharetrip.profile.view.imagePreview.ImagePreviewFragment
import net.sharetrip.profile.view.list.CountryCurrencyFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddTravelerFragment : BaseFragment<FragmentAddTravelerBinding>() {

    private val viewModel: AddTravelerViewModel by viewModels {
        AddTravelerVMFactory(
            DataManager.getSharedPref(requireContext()),
            AddTravelerRepository(DataManager.profileApiService)
        )
    }

    private var makeDirectory: File? = null
    private var cameraFile: File? = null
    private var phnMemoryBasePath: String? = null
    private var timeStamp: String? = null
    private var finalPath: String? = null
    lateinit var startActivityForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && viewModel.requestCode > 0) {
                    getActivityResult(viewModel.requestCode, result)
                }
            }
    }

    override fun layoutId(): Int = R.layout.fragment_add_traveler

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        setTitleAndSubtitle(getString(R.string.add_traveler))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel

        bindingView.textFieldNationality.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewModel.onClickNationality()
            }
        }

        viewModel.imageUploadChosserNew.observe(viewLifecycleOwner, EventObserver {
            chooseImageOption(it, true)
        })

        viewModel.imageUploadChosserOld.observe(viewLifecycleOwner, EventObserver {
            chooseImageOption(it, false)
        })

        viewModel.passportProgress.observe(viewLifecycleOwner) {
            bindingView.passportProgressBar.progress = it
        }

        viewModel.visaProgress.observe(viewLifecycleOwner) {
            bindingView.visaProgressBar.progress = it
        }

        viewModel.isFirstNameValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.inputLayoutFirstName.helperText = "  "
            } else {
                bindingView.inputLayoutFirstName.error = "Only Use Letter"
            }
        }

        viewModel.isLastNameValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.inputLayoutLastName.helperText = "*Required"
            } else {
                bindingView.inputLayoutLastName.error = "Only Use Letter"
            }
        }

        viewModel.isPassportNumberValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.inputLayoutPassportNumber.error = null
            } else {
                bindingView.inputLayoutPassportNumber.error = " "
            }
        }

        viewModel.isPassportExpiryDateValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.inputLayoutPassportExpiry.error = null
            } else {
                bindingView.inputLayoutPassportExpiry.error = " "
            }
        }

        viewModel.showToastMessage.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireActivity().baseContext, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.navigateToDestination.observe(viewLifecycleOwner, EventObserver {
            when (it.first) {
                GOTO_IMAGE_PREVIEW -> {
                    val bundle = bundleOf()
                    when (it.second as String) {
                        "passport" -> {
                            bundle.putString(
                                ImagePreviewFragment.ARG_IMAGE_URL,
                                viewModel.traveler.get()?.passportCopy
                            )
                        }
                        "visa" -> {
                            bundle.putString(
                                ImagePreviewFragment.ARG_IMAGE_URL,
                                viewModel.traveler.get()?.visaCopy
                            )
                        }
                    }

                    findNavController().navigateSafe(
                        R.id.action_addTravelerFragment_to_imagePreviewFragment,
                        bundle
                    )
                }

                GOTO_COUNTRY_CURRENCY -> {
                    val bundle = bundleOf(CountryCurrencyFragment.ARG_COUNTRY_CURRENCY to "country")
                    findNavController().navigateSafe(
                        R.id.action_addTravelerFragment_to_countryCurrencyFragment,
                        bundle
                    )
                }

                GOTO_BACK->{
                    findNavController().navigateUp()
                }
            }
        })

        getNavigationResultLiveData<Bundle>(CountryCurrencyFragment.RESULT_COUNTRY_SELECTION)?.observe(
            viewLifecycleOwner
        ) {
            val code = it.getString(CountryCurrencyFragment.EXTRA_COUNTRY_CODE)
            val name = it.getString(CountryCurrencyFragment.EXTRA_COUNTRY_NAME)
            bindingView.textFieldNationality.setText(name)
            viewModel.setCountryCode(code!!, name!!)
            bindingView.textFieldNationality.setText(name)
        }

        bindingView.textFieldNationality.keyListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.done, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
                            val file =
                                File(
                                    FileUtils.getRealPath(
                                        requireActivity(),
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
                                viewModel.updateImageFile(file, "visa", mime)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.action_done == item.itemId) {
            viewModel.onClickSaveMenu()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun chooseImageOption(tag: String, isAlreadyUploaded: Boolean) {
        val viewLayout: LinearLayout
        val cameraLayout: LinearLayout
        val galleryLayout: LinearLayout
        val view = layoutInflater.inflate(R.layout.profile_module_bottom_sheet_picker, null)
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
            viewModel.gotoImagePreview(tag)
        }

        dialog.show()
    }

    private fun writePermissionCheck(selectionCode: Int) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        phnMemoryBasePath =
                            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                .toString() + "/shareTrip/"
                        makeDirectory = File(phnMemoryBasePath!!)
                        makeDirectory!!.mkdir()
                        cameraIntent(selectionCode)
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            activity,
                            "You have denied the permission",
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
        Dexter.withActivity(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    galleryIntent(selectionCode)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(activity, "You have denied the permission", Toast.LENGTH_SHORT)
                        .show()
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
            "net.sharetrip" + ".provider",
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
        bindingView.textFieldDateOfBirth.setBirthdayRange(sdf.format(Calendar.getInstance().time))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 6)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        bindingView.textFieldPassportExpiry.setPassportRange(sdf.format(calendar.time))
    }

    companion object {
        const val RESULT_PASSPORT_CAMERA_CODE_SELECTION = 206
        const val RESULT_PASSPORT_GALLERY_CODE_SELECTION = 207
        const val RESULT_VISA_CAMERA_CODE_SELECTION = 208
        const val RESULT_VISA_GALLERY_CODE_SELECTION = 209
    }
}
