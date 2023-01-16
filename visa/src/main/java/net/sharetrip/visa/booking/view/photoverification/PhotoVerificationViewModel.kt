package net.sharetrip.visa.booking.view.photoverification

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.visa.booking.model.*
import net.sharetrip.visa.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_FOR_CONFIRMATION
import net.sharetrip.visa.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_TAG
import net.sharetrip.visa.booking.view.imagepreview.ImagePreviewFragment.Companion.ARG_IMAGE_URL
import net.sharetrip.visa.network.VisaBookingApiService
import net.sharetrip.visa.utils.SingleLiveEvent
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PhotoVerificationViewModel(
    private val visaSearchQuery: VisaSearchQuery,
    private val apiService: VisaBookingApiService,
    sharedPrefsHelper: SharedPrefsHelper
) : BaseOperationalViewModel() {
    private var token = ""
    private val mapList = ArrayList<HashMap<String, String>>()
    var travellerInfo = ObservableField<String>()
    var photoList = MutableLiveData<List<PhotoItem>>()
    var photoUploaded = MutableLiveData<String>()
    var requestCode: Int = 0
    val goToImageConfirmation = SingleLiveEvent<Bundle>()
    val navigateToCheckout = SingleLiveEvent<VisaSearchQuery>()

    init {
        token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        travellerInfo.set(visaSearchQuery.travellerLabelInfo())
        fillPhotoAdapter()
    }

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        dataLoading.set(false)
        val response = (data.body as RestResponse<*>).response as VisaPhotoUploadRespone
        photoUploaded.postValue(response.fileName)
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        dataLoading.set(false)
        showMessage(errorMessage)
    }

    private fun fillPhotoAdapter() {
        val selectedVisaType = visaSearchQuery.selectedVisaType
        val selectedProfession = selectedVisaType?.let {
            visaSearchQuery.visaSelection?.visaProducts?.get(it)?.professionSelection
        }
        val reqDocs: ReqDocsItem? =
            selectedVisaType?.let {
                selectedProfession?.let { prof ->
                    visaSearchQuery.visaSelection?.visaProducts?.get(it)?.requiredDocList?.get(
                        prof
                    )
                }
            }

        val list = ArrayList<PhotoItem>()

        if (reqDocs != null) {
            reqDocs.documents?.forEachIndexed { index, it ->
                if (it.required == true) {
                    val photoItem =
                        PhotoItem(index, it.name.toString(), it.docTitle.toString(), null)
                    list.add(photoItem)
                }
                photoList.postValue(list)
            }
        }
    }

    fun onNext() {
        PhotoVerificationFragment.photoList.forEachIndexed { _, photoItem ->
            val map = HashMap<String, String>()
            map[photoItem.name] = photoItem.url
            map["docTitle"] = photoItem.docTitle
            mapList.add(map)
        }

        visaSearchQuery.travellers[visaSearchQuery.currentTravellerPosition!!].requireDoc?.addAll(
            mapList
        )

        navigateToCheckout.value = visaSearchQuery
    }

    fun checkList(): Boolean {
        val status = true

        PhotoVerificationFragment.photoList.forEach {
            if (it.url.isEmpty()) {
                return false
            }
        }

        return status
    }

    fun updateImageFile(compressToFile: File, name: String, mime: String) {
        dataLoading.set(true)
        val requestPhotoFile = compressToFile.asRequestBody(mime.toMediaTypeOrNull())
        val userPhoto =
            MultipartBody.Part.createFormData(name, compressToFile.name, requestPhotoFile)

        executeSuspendedCodeBlock { apiService.uploadVisaDocuments(token, userPhoto) }
    }

    fun gotoImagePreview(position: Int) {
        val photo: PhotoItem = PhotoVerificationFragment.photoList[position]
        val bundle = Bundle()

        photo.url.let {
            bundle.putString(ARG_IMAGE_TAG, photo.docTitle)
            bundle.putString(ARG_IMAGE_URL, it)
            bundle.putBoolean(ARG_FOR_CONFIRMATION, false)
            goToImageConfirmation.value = bundle
        }
    }

    fun makeJson(value: MutableList<VisaItemTraveler>): String {
        val gson = Gson().newBuilder().setPrettyPrinting().create()
        val data = gson.toJson(value)
        println("Traveler :> $data")
        return data
    }
}
