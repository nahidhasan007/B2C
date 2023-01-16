package net.sharetrip.profile.view.user

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
import net.sharetrip.shared.utils.imageCoprresion.ImageZipper
import net.sharetrip.shared.view.BaseFragment
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.profile.R
import net.sharetrip.profile.databinding.FragmentUserInfoNewBinding
import net.sharetrip.profile.network.DataManager
import net.sharetrip.profile.utils.MyClickableSpan
import net.sharetrip.profile.view.imagePreview.ImagePreviewFragment
import net.sharetrip.profile.view.list.CountryCurrencyFragment.Companion.ARG_COUNTRY_CURRENCY
import net.sharetrip.profile.view.list.CountryCurrencyFragment.Companion.EXTRA_COUNTRY_CODE
import net.sharetrip.profile.view.list.CountryCurrencyFragment.Companion.RESULT_COUNTRY_SELECTION
import net.sharetrip.profile.view.travelerShow.TravelerShowFragment.Companion.EXTRA_COUNTRY_NAME
import net.sharetrip.shared.utils.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserInfoFragment : BaseFragment<FragmentUserInfoNewBinding>() {

    private val viewModel: UserInfoViewModel by viewModels {
        UserInfoViewModelFactory(
            DataManager.getSharedPref(requireContext()),
            UserInfoRepository(DataManager.profileApiService)
        )
    }

    private var makeDirectory: File? = null
    private var cameraFile: File? = null
    private var phnMemoryBasePath: String? = null
    private var timeStamp: String? = null
    private var finalPath: String? = null
    private var outputFileUri: Uri? = null
    private var imageFileForProfile: File? = null
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

    override fun layoutId() = R.layout.fragment_user_info_new

    override fun getViewModel(): BaseViewModel = viewModel

    override fun initOnCreateView() {
        hideTripCoin()
        setTitleAndSubtitle(getString(R.string.edit_profile))

        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.viewModel = viewModel
        bindingView.executePendingBindings()

        viewModel.loadImage.observe(viewLifecycleOwner) {
            Glide.with(requireActivity())
                .load(it)
                .into(bindingView.userPhoto)
        }

        viewModel.userBirthDate.observe(viewLifecycleOwner) {
            bindingView.textFieldDateOfBirth.setExistingDate(it)
        }

        viewModel.isFirstNameValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.inputLayoutFirstName.error = null
            } else {
                bindingView.inputLayoutFirstName.error = getString(R.string.use_letter_only)
            }
        }
        viewModel.isLastNameValid.observe(viewLifecycleOwner) {
            if (it) {
                bindingView.inputLayoutLastName.error = null
            } else {
                bindingView.inputLayoutLastName.error = getString(R.string.use_letter_only)
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

        viewModel.imageUploadChosenNew.observe(viewLifecycleOwner, EventObserver {
            chooseImageOption(it, true)
        })

        viewModel.imageUploadChosenOld.observe(viewLifecycleOwner, EventObserver {
            chooseImageOption(it, false)
        })

        viewModel.navigateToCountryCurrency.observe(viewLifecycleOwner, EventObserver {
            val bundle = bundleOf(ARG_COUNTRY_CURRENCY to "country")
            findNavController().navigateSafe(
                R.id.action_userInfoFragment_to_countryCurrencyFragment,
                bundle
            )
        })

        getNavigationResultLiveData<Bundle>(RESULT_COUNTRY_SELECTION)?.observe(viewLifecycleOwner) {
            val code = it.getString(EXTRA_COUNTRY_CODE)
            val name = it.getString(EXTRA_COUNTRY_NAME)
            bindingView.textFieldNationality.setText(name)
            viewModel.setCountryCode(code!!, name!!)
            bindingView.textFieldNationality.setText(name)
        }

        viewModel.passportProgress.observe(viewLifecycleOwner) {
            bindingView.passportProgressBar.progress = it
        }
        viewModel.passportProgress.observe(viewLifecycleOwner) {
            bindingView.passportProgressBar.progress = it
        }

        viewModel.navigateToNameGuideLine.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigateSafe(R.id.action_userInfoFragment_to_nameGuidelineFragment)
        })

        val spannableContent = SpannableString(bindingView.textGuildLine.text.toString())
        val wordClick = object : MyClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.openNameGuildLine()
            }
        }
        spannableContent.setSpan(wordClick, 65, 84, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        bindingView.textGuildLine.linksClickable = true
        bindingView.textGuildLine.movementMethod = LinkMovementMethod.getInstance()
        bindingView.textGuildLine.setText(spannableContent, TextView.BufferType.SPANNABLE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.done, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.action_done == item.itemId) {
            viewModel.onClickSaveMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
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
                "profile" -> selectionCode = RESULT_PROFILE_CAMERA_CODE_SELECTION
                "passport" -> selectionCode = RESULT_PASSPORT_CAMERA_CODE_SELECTION
                "visa" -> selectionCode = RESULT_VISA_CAMERA_CODE_SELECTION
            }
            dialog.dismiss()
            writePermissionCheck(selectionCode)
        }
        galleryLayout.setOnClickListener {
            var selectionCode = 0
            when (tag) {
                "profile" -> selectionCode = RESULT_PROFILE_GALLERY_CODE_SELECTION
                "passport" -> selectionCode = RESULT_PASSPORT_GALLERY_CODE_SELECTION
                "visa" -> selectionCode = RESULT_VISA_GALLERY_CODE_SELECTION
            }
            dialog.dismiss()
            readPermissionCheck(selectionCode)
        }

        viewLayout.setOnClickListener {
            dialog.dismiss()
            openImagePreviewScreen(tag)
        }

        dialog.show()
    }

    private fun openImagePreviewScreen(tag: String) {
        val bundle = bundleOf()
        when (tag) {
            "passport" -> {
                bundle.putString(
                    ImagePreviewFragment.ARG_IMAGE_URL,
                    viewModel.userInfo.get()?.passportCopy
                )
            }
            else -> {
                bundle.putString(
                    ImagePreviewFragment.ARG_IMAGE_URL,
                    viewModel.userInfo.get()?.avatar
                )
            }
        }

        findNavController().navigateSafe(R.id.action_userInfoFragment_to_imagePreviewFragment, bundle)
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
                        makeDirectory = File(phnMemoryBasePath)
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
        cameraFile = File(finalPath)
        try {
            cameraFile!!.createNewFile()
        } catch (e: IOException) {
            e.stackTrace
        }
        outputFileUri = FileProvider.getUriForFile(
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
        chooseFile.type =
            if (selectionCode == RESULT_PROFILE_GALLERY_CODE_SELECTION) "image/*" else "*/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        viewModel.requestCode = selectionCode
        startActivityForResult.launch(chooseFile)
    }

    private fun getActivityResult(requestCode: Int, result: ActivityResult) {
        try {
            when (requestCode) {
                RESULT_PROFILE_CAMERA_CODE_SELECTION -> {
                    val mime = FileUtils.getMimeType(cameraFile.toString())
                    imageFileForProfile =
                        if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                            ImageZipper(requireContext()).compressToFile(cameraFile)
                        } else {
                            cameraFile
                        }
                    imageFileForProfile = cameraFile
                    Glide.with(requireActivity())
                        .load(imageFileForProfile)
                        .into(bindingView.userPhoto)
                    viewModel.updateImageFile(imageFileForProfile!!, "profile", mime)
                }

                RESULT_PROFILE_GALLERY_CODE_SELECTION -> {
                    val mime = requireActivity().contentResolver.getType(result.data?.data!!)
                    val file = File(FileUtils.getRealPath(requireActivity(), result.data?.data!!)!!)
                    imageFileForProfile =
                        if (Integer.parseInt((file.length() / 1024).toString()) > 1024) {
                            ImageZipper(requireContext()).compressToFile(file)
                        } else {
                            file
                        }
                    Glide.with(requireActivity())
                        .load(imageFileForProfile)
                        .into(bindingView.userPhoto)
                    viewModel.updateImageFile(imageFileForProfile!!, "profile", mime!!)

                }

                RESULT_PASSPORT_CAMERA_CODE_SELECTION -> {
                    val mime = FileUtils.getMimeType(cameraFile.toString())
                    if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                        viewModel.updateImageFile(
                            ImageZipper(requireContext()).compressToFile(
                                cameraFile
                            ), "passport", mime
                        )
                    } else {
                        viewModel.updateImageFile(cameraFile!!, "passport", mime)
                    }
                }

                RESULT_PASSPORT_GALLERY_CODE_SELECTION -> {
                    val mime = requireActivity().contentResolver.getType(result.data?.data!!)
                    if (mime!!.contains("image") || mime.contains("pdf")) {
                        val file =
                            File(FileUtils.getRealPath(requireActivity(), result.data?.data!!)!!)
                        if (Integer.parseInt((file.length() / 1024).toString()) > 1024 && mime.contains(
                                "image"
                            )
                        ) {
                            viewModel.updateImageFile(
                                ImageZipper(requireContext()).compressToFile(file), "passport",
                                mime
                            )
                        } else {
                            viewModel.updateImageFile(file, "passport", mime)
                        }
                    } else {
                        Toast.makeText(activity, "Wrong formatted document", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                RESULT_VISA_CAMERA_CODE_SELECTION -> {
                    val mime = FileUtils.getMimeType(cameraFile.toString())
                    if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                        viewModel.updateImageFile(
                            ImageZipper(requireContext()).compressToFile(cameraFile), "visa",
                            mime
                        )
                    } else {
                        viewModel.updateImageFile(cameraFile!!, "visa", mime)
                    }
                }

                RESULT_VISA_GALLERY_CODE_SELECTION -> {
                    val mime = requireActivity().contentResolver.getType(result.data?.data!!)
                    if (mime!!.contains("image") || mime.contains("pdf")) {
                        val file =
                            File(FileUtils.getRealPath(requireActivity(), result.data?.data!!)!!)
                        if (Integer.parseInt((file.length() / 1024).toString()) > 1024 && mime.contains(
                                "image"
                            )
                        ) {
                            viewModel.updateImageFile(
                                ImageZipper(requireContext()).compressToFile(file), "passport",
                                mime
                            )
                        } else {
                            viewModel.updateImageFile(file, "visa", mime)
                        }
                    } else {
                        Toast.makeText(activity, "Wrong formatted document", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                RESULT_COUNTRY_CODE_SELECTION -> {
                    val code = result.data?.getStringExtra(EXTRA_COUNTRY_CODE)
                    val name = result.data?.getStringExtra(EXTRA_COUNTRY_NAME)
                    bindingView.textFieldNationality.setText(name)
                    viewModel.setCountryCode(code!!, name!!)
                }
            }
        } catch (e: Exception) {
            e.stackTrace
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
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
        const val RESULT_COUNTRY_CODE_SELECTION = 205
        const val RESULT_PASSPORT_CAMERA_CODE_SELECTION = 206
        const val RESULT_PASSPORT_GALLERY_CODE_SELECTION = 207
        const val RESULT_VISA_CAMERA_CODE_SELECTION = 208
        const val RESULT_VISA_GALLERY_CODE_SELECTION = 209
        const val RESULT_PROFILE_CAMERA_CODE_SELECTION = 212
        const val RESULT_PROFILE_GALLERY_CODE_SELECTION = 213
        const val APPLICATION_ID = "net.sharetrip";
    }
}
