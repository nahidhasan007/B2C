package net.sharetrip.visa.booking.view.photoverification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.shared.utils.hideTripCoin
import net.sharetrip.shared.utils.navigateSafe
import net.sharetrip.shared.view.BaseFragment
import net.sharetrip.shared.view.adapter.ItemClickSupport
import net.sharetrip.visa.R
import net.sharetrip.visa.booking.model.PhotoItem
import net.sharetrip.visa.booking.model.VisaSearchQuery
import net.sharetrip.visa.booking.view.checkout.VisaCheckoutFragment
import net.sharetrip.visa.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_DATA
import net.sharetrip.visa.databinding.BottomSheetVisaConfirmationBinding
import net.sharetrip.visa.databinding.FragmentVisaPhotoVerificationBinding
import net.sharetrip.visa.network.DataManager
import net.sharetrip.visa.utils.FileUtils
import net.sharetrip.visa.utils.GridSpacingItemDecoration
import net.sharetrip.visa.utils.MsgUtils.unKnownErrorMsg
import net.sharetrip.visa.utils.imageCoprresion.ImageZipper
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoVerificationFragment : BaseFragment<FragmentVisaPhotoVerificationBinding>() {
    private val viewModel by lazy {
        val visaSearchQuery = requireArguments().getParcelable<VisaSearchQuery>(
            ARG_PHOTO_VERIFICATION_DATA_MODEL
        )!!

        ViewModelProvider(
            this,
            PhotoVerificationVMFactory(
                visaSearchQuery,
                DataManager.visaBookingApiService,
                DataManager.getSharedPref(requireContext())
            )
        )[PhotoVerificationViewModel::class.java]
    }

    private val adapter = PhotoAdapter()
    private var makeDirectory: File? = null
    private var cameraFile: File? = null
    private var phnMemoryBasePath: String? = null
    private var timeStamp: String? = null
    private var finalPath: String? = null
    private var itemPosition = -1
    lateinit var startActivityForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && viewModel.requestCode > 0) {
                    getActivityResult(viewModel.requestCode, result)
                }
            }
    }


    override fun layoutId() = R.layout.fragment_visa_photo_verification

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun initOnCreateView() {
        hideTripCoin()
        bindingView.viewModel = viewModel
        bindingView.lifecycleOwner = viewLifecycleOwner
        bindingView.recyclerPhotoDetails.layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_visa_photo_verification, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_photo_verification_done_text == menuItem.itemId) {
                    if (viewModel.checkList()) {
                        confirmDialog()
                    } else {
                        showToastMessage(getString(R.string.upload_all_photo))
                    }
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bindingView.recyclerPhotoDetails.addItemDecoration(
            GridSpacingItemDecoration(
                requireContext(),
                GRID_SPAN_COUNT, GRID_CELL_MARGIN, false
            )
        )

        viewModel.photoList.observe(viewLifecycleOwner) {
            if (photoList.isNullOrEmpty()) {
                photoList.addAll(it)
            }
            bindingView.recyclerPhotoDetails.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        ItemClickSupport.addTo(bindingView.recyclerPhotoDetails)
            .setOnItemClickListener { _, position, _ ->
                chooseImageOption(position)
            }

        viewModel.photoUploaded.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                photoList[itemPosition].url = it
                showToastMessage(getString(R.string.successfully_uploaded))
            } else {
                showToastMessage(getString(R.string.photo_not_uploaded))
            }
        }

        viewModel.goToImageConfirmation.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_photoVerificationFragment_to_imagePreviewFragment,
                bundleOf(ARG_IMAGE_DATA to it)
            )
        }

        viewModel.navigateToCheckout.observe(viewLifecycleOwner) {
            findNavController().navigateSafe(
                R.id.action_photoVerificationFragment_to_visaCheckoutFragment,
                bundleOf(VisaCheckoutFragment.ARG_VISA_CHECKOUT_DATA_MODEL to it)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        photoList.clear()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getActivityResult(requestCode: Int, result: ActivityResult) {
        try {
            when (requestCode) {
                RESULT_VISA_CAMERA_CODE_SELECTION -> {
                    val mime = FileUtils.getMimeType(cameraFile.toString())
                    if (Integer.parseInt((cameraFile!!.length() / 1024).toString()) > 1024) {
                        photoList[itemPosition].uri =
                            ImageZipper(requireContext()).compressToFile(cameraFile)
                        photoList[itemPosition].uri?.let {
                            viewModel.updateImageFile(
                                it,
                                photoList[itemPosition].name,
                                mime
                            )
                        }
                    } else {
                        photoList[itemPosition].uri = cameraFile
                        photoList[itemPosition].uri?.let {
                            viewModel.updateImageFile(
                                it,
                                photoList[itemPosition].name,
                                mime
                            )
                        }
                    }
                    bindingView.recyclerPhotoDetails.adapter!!.notifyDataSetChanged()
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
                            photoList[itemPosition].uri =
                                ImageZipper(requireContext()).compressToFile(file)
                            photoList[itemPosition].uri?.let {
                                viewModel.updateImageFile(
                                    it,
                                    photoList[itemPosition].name,
                                    mime
                                )
                            }
                        } else {
                            photoList[itemPosition].uri = file
                            photoList[itemPosition].uri?.let {
                                viewModel.updateImageFile(
                                    it,
                                    photoList[itemPosition].name,
                                    mime
                                )
                            }
                        }

                        bindingView.recyclerPhotoDetails.adapter!!.notifyDataSetChanged()
                    } else {
                        showToastMessage(getString(R.string.wrong_format))
                    }
                }
            }
        } catch (e: Exception) {
            e.stackTrace
            showToastMessage(unKnownErrorMsg)
        }
    }

    private fun confirmDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val bindingView = DataBindingUtil.inflate<BottomSheetVisaConfirmationBinding>(
            layoutInflater,
            R.layout.bottom_sheet_visa_confirmation,
            null,
            false
        )

        dialog.setContentView(bindingView.root)

        bindingView.buttonProceed.setOnClickListener {
            dialog.dismiss()
            viewModel.onNext()
        }

        bindingView.buttonNotNow.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun chooseImageOption(position: Int) {
        val viewLayout: LinearLayout
        val cameraLayout: LinearLayout
        val galleryLayout: LinearLayout
        val view = layoutInflater.inflate(R.layout.bottom_sheet_visa_photo_picker, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        viewLayout = view.findViewById(R.id.viewLayout)
        cameraLayout = view.findViewById(R.id.cameraLayout)
        galleryLayout = view.findViewById(R.id.galleryLayout)

        itemPosition = position
        photoList[position].uri?.let {
            viewLayout.visibility = View.VISIBLE
        }

        cameraLayout.setOnClickListener {
            dialog.dismiss()
            writePermissionCheck(RESULT_VISA_CAMERA_CODE_SELECTION)
        }

        galleryLayout.setOnClickListener {
            dialog.dismiss()
            readPermissionCheck(RESULT_VISA_GALLERY_CODE_SELECTION)
        }

        viewLayout.setOnClickListener {
            dialog.dismiss()
            viewModel.gotoImagePreview(itemPosition)
        }

        dialog.show()
    }

    private fun readPermissionCheck(selectionCode: Int) {
        Dexter.withContext(activity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    galleryIntent(selectionCode)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showToastMessage(getString(R.string.permission_denied))
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun writePermissionCheck(selectionCode: Int) {
        Dexter.withContext(activity)
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
                        showToastMessage(getString(R.string.permission_denied))
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
        chooseFile = Intent.createChooser(chooseFile, getString(R.string.choose_a_file))
        viewModel.requestCode = selectionCode
        startActivityForResult.launch(chooseFile)
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ARG_PHOTO_VERIFICATION_DATA_MODEL = "ARG_PHOTO_VERIFICATION_DATA_MODEL"
        const val APPLICATION_ID = "net.sharetrip"
        const val RESULT_VISA_CAMERA_CODE_SELECTION = 208
        const val RESULT_VISA_GALLERY_CODE_SELECTION = 209
        var photoList: ArrayList<PhotoItem> = ArrayList()
        const val GRID_CELL_MARGIN = 16
        const val GRID_SPAN_COUNT = 2
    }
}
