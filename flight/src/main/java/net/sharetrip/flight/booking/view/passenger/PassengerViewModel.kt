package net.sharetrip.flight.booking.view.passenger

import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.data.PrefKey
import com.sharetrip.base.data.SharedPrefsHelper
import com.sharetrip.base.event.Event
import com.sharetrip.base.network.model.BaseResponse
import com.sharetrip.base.network.model.ErrorType
import com.sharetrip.base.network.model.RestResponse
import com.sharetrip.base.viewmodel.BaseOperationalViewModel
import net.sharetrip.flight.booking.model.*
import net.sharetrip.flight.booking.model.flightresponse.flights.Flights
import net.sharetrip.flight.network.DataManager
import net.sharetrip.profile.view.user.UserInfoViewModel.Companion.ENTER_PASSPORT_EXPIRY_DATE
import net.sharetrip.profile.view.user.UserInfoViewModel.Companion.ENTER_PASSPORT_NUMBER
import net.sharetrip.profile.view.user.UserInfoViewModel.Companion.INVALID_PASSPORT
import net.sharetrip.shared.model.PassengerTypeTitle
import net.sharetrip.shared.utils.*
import net.sharetrip.shared.utils.analytics.AnalyticsProvider
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class PassengerViewModel(
    private val sharedPrefsHelper: SharedPrefsHelper,
    private var flights: Flights,
    private val flightSearch: FlightSearch,
    val itemTraveler: ItemTraveler,
    private val flightDate: String
) : BaseOperationalViewModel() {
    private val numberOfDaysMinimumRequired: Int = 180
    val passengerList = MutableLiveData<List<Traveler>>()
    val passenger = ObservableField<ItemTraveler>()
    var isMaleSelected = ObservableBoolean()
    var isDomesticFlight = ObservableBoolean()
    var isQuickPickEnable = ObservableBoolean()
    val dataInvalid = MutableLiveData<Boolean>()
    val mealTypeObservable = ObservableField<String>()
    val wheelchairRequestObservable = ObservableField<String>()
    val covidTestObservable = ObservableField("I'll test myself")
    val travelInsuranceObservable = ObservableField("None")
    val baggageInsuranceObservable = ObservableField("None")
    var imageUploadChoiceNew = MutableLiveData<Event<String>>()
    var imageUploadChoiceOld = MutableLiveData<Event<String>>()
    var passportProgress = MutableLiveData<Int>()
    var visaProgress = MutableLiveData<Int>()
    var isLoaderShow = MutableLiveData<Boolean>()
    val passportValid = MutableLiveData<Boolean>()
    val passengerType = MutableLiveData<String>()
    var isFirstNameValid = MutableLiveData<Boolean>()
    var isLastNameValid = MutableLiveData<Boolean>()
    var isSaudiaAirlines = ObservableBoolean(false)
    var userBirthDate = MutableLiveData<String>()
    var isBaggageInsuranceAllowed = true

    var mealTypeLiveData = MutableLiveData<Pair<ArrayList<Ssr>?, String?>>()
    var requestWheelchairLiveData = MutableLiveData<Pair<ArrayList<Ssr>?, String?>>()
    var covidAddOnList: ArrayList<CovidAddOnResponseItem> = ArrayList()
    var covidInfoClicked = MutableLiveData<ArrayList<CovidAddOnResponseItem>>()

    var travelInsuranceClicked = MutableLiveData<ArrayList<TravelInsuranceItem>>()
    var travelInsuranceList = MutableLiveData<ArrayList<TravelInsuranceItem>>()

    var baggageInsuranceClicked = MutableLiveData<ArrayList<BaggageInsurance>>()
    var baggageInsuranceList = MutableLiveData<ArrayList<BaggageInsurance>>()

    var flightAddOns = MutableLiveData<FlightAddOns>()
    var flightAddOnsDetails = MutableLiveData<FlightAddOnsDetails>()

    var ssrList: List<MealWheelchairResponse>? = null
    var wheelChairCode = ""
    private var mealTypeCode = ""
    var covid: Covid = Covid()
    var travelInsurance: TravelInsurance? = TravelInsurance()
    var baggageInsurance: BaggageInsuranceBody? = BaggageInsuranceBody()
    var covidTestOption: CovidTestOption = CovidTestOption(name = "I'll test myself")
    var travelInsuranceOption: TravelInsuranceOption = TravelInsuranceOption(name = "")
    var baggageInsuranceOption: BaggageInsuranceOption = BaggageInsuranceOption(name = "")
    var requestCode = 0
    val saveInfoClicked = MutableLiveData<Event<Boolean>>()
    val gotoNationality = MutableLiveData<Event<Boolean>>()
    val goToImageConfirmation = MutableLiveData<Event<String>>()
    private var tag = ""
    private val flightEventManager =
        AnalyticsProvider.flightEventManager(AnalyticsProvider.getFirebaseAnalytics())
    var isPassportNumberValid = MutableLiveData<Boolean>()
    var isPassportExpiryDateValid = MutableLiveData<Boolean>()

    override fun onSuccessResponse(operationTag: String, data: BaseResponse.Success<Any>) {
        when (operationTag) {
            ApiCallingKey.UserProfileRX.name -> {
                val listOfOtherPassengers =
                    ((data.body as RestResponse<*>).response as UserProfile).otherPassengers.apply {
                        val userInfo = (data.body as RestResponse<*>).response as UserProfile
                        val traveler = Traveler().apply {
                            titleName = userInfo.titleName
                            givenName = userInfo.givenName
                            surName = userInfo.surName
                            gender = userInfo.gender
                            nationality = userInfo.nationality
                            dateOfBirth = userInfo.dateOfBirth
                            passportNumber = userInfo.passportNumber
                            passportCopy = userInfo.passportCopy
                            passportExpireDate = userInfo.passportExpireDate
                            visaCopy = userInfo.visaCopy
                            frequentFlyerNumber = userInfo.frequentFlyerNumber
                        }
                        this.add(traveler)
                    }.filter {
                        (it.dateOfBirth != "" && itemTraveler.passengerTypeTitle!!.contains(
                            PassengerTypeTitle.Adult.name
                        )
                                && DateUtil.getAgeForFlight(it.dateOfBirth, flightDate) >= 11) ||
                                (it.dateOfBirth != "" && itemTraveler.passengerTypeTitle!!.contains(
                                    PassengerTypeTitle.Child.name
                                )
                                        && DateUtil.getAgeForFlight(it.dateOfBirth, flightDate) < 11
                                        && DateUtil.getAgeForFlight(
                                    it.dateOfBirth,
                                    flightDate
                                ) >= 2) ||
                                (it.dateOfBirth != "" && itemTraveler.passengerTypeTitle!!.contains(
                                    PassengerTypeTitle.Infant.name
                                )
                                        && DateUtil.getAgeForFlight(it.dateOfBirth, flightDate) < 2)
                    }.toList()
                isQuickPickEnable.set(listOfOtherPassengers.isNotEmpty())
                passengerList.value = listOfOtherPassengers
                isLoaderShow.value = false
            }

            ApiCallingKey.UpdateImage.name -> {
                when (tag) {
                    "passport" -> {
                        isLoaderShow.value = false
                        passenger.get()!!.passportCopy =
                            (data.body as ImageUploadResponse).response.path
                        passportProgress.value = 100
                    }
                    "visa" -> {
                        isLoaderShow.value = false
                        passenger.get()!!.visaCopy =
                            (data.body as ImageUploadResponse).response.path
                        visaProgress.value = 100
                    }
                }
            }

            ApiCallingKey.GetSsrCodes.name -> {
                ssrList = (data.body as RestResponse<*>).response as List<MealWheelchairResponse>
                itemTraveler.wheelChairText?.let {
                    wheelchairRequestObservable.set(it)
                }
                itemTraveler.mealPreferenceText?.let {
                    mealTypeObservable.set(it)
                }
            }

            ApiCallingKey.GetFlightAddOns.name -> {
                isLoaderShow.value = false
                flightAddOns.value =
                    (data.body as RestResponse<*>).response!! as FlightAddOns
                travelInsuranceList.value =
                    flightAddOns.value!!.travelInsurance as ArrayList
                covidAddOnList =
                    flightAddOns.value!!.covid as ArrayList
                baggageInsuranceList.value = flightAddOns.value!!.baggageInsurance as ArrayList
            }

            ApiCallingKey.GetFlightAddOnDetails.name -> {
                isLoaderShow.value = false
                flightAddOnsDetails.value =
                    (data.body as RestResponse<*>).response!! as FlightAddOnsDetails
            }
        }
    }

    override fun onAnyError(operationTag: String, errorMessage: String, type: ErrorType?) {
        when (operationTag) {
            ApiCallingKey.UserProfileRX.name -> {
                isLoaderShow.value = false
            }

            ApiCallingKey.UpdateImage.name -> {
                isLoaderShow.value = false
            }

            ApiCallingKey.GetSsrCodes.name -> {
                isLoaderShow.value = false
            }

            ApiCallingKey.CovidAddOn.name -> {
                isLoaderShow.value = false
            }

            ApiCallingKey.CovidService.name -> {
                isLoaderShow.value = false
            }

            ApiCallingKey.GetFlightAddOns.name -> {
                isLoaderShow.value = false
            }

            ApiCallingKey.GetFlightAddOnDetails.name -> {
                isLoaderShow.value = false
            }
        }
    }

    private fun isInputValid(): Boolean {
        val traveller = passenger.get()
        return (!TextUtils.isEmpty(traveller?.surName)
                && !TextUtils.isEmpty(traveller?.dateOfBirth)
                && !TextUtils.isEmpty(traveller?.nationality)
                && !TextUtils.isEmpty(traveller?.passportNumber)
                && !TextUtils.isEmpty(traveller?.passportExpireDate)
                && traveller?.surName.isNameValid()
                && checkAttachment())
    }

    init {
        isMaleSelected.set(true)
        isQuickPickEnable.set(true)
        val passengers = itemTraveler
        try {
            if (!DateUtil.isDateIsValid(itemTraveler.dateOfBirth)) {
                userBirthDate.value = itemTraveler.dateOfBirth
                passengers.dateOfBirth = itemTraveler.dateOfBirth!!.dateChangeToDDMMYYYY()
            }
            if (!DateUtil.isDateIsValid(itemTraveler.passportExpireDate)) {
                passengers.passportExpireDate =
                    itemTraveler.passportExpireDate!!.dateChangeToDDMMYYYY()
            }

            if (passengers.passportCopy == null) {
                passportProgress.value = 0
            } else {
                passportProgress.value = 100
            }

            if (passengers.visaCopy == null) {
                visaProgress.value = 0
            } else {
                visaProgress.value = 100
            }

            if (passengers.gender == "Female") {
                isMaleSelected.set(false)
            }
        } catch (e: Exception) {
            passengers.dateOfBirth = ""
            passengers.passportExpireDate = ""
        }

        isDomesticFlight.set(flights.domestic)

        if (flights.domestic && passengers.passportExpireDate!!.isEmpty()) {
            passengers.passportExpireDate = DateUtil.dayAfterOneYearApiDateFormat
        }

        if (flights.domestic && (itemTraveler.passportNumber == null || itemTraveler.passportNumber!!.isEmpty())) {
            passengers.passportNumber = "Domestic"
        }

        if (flights.domestic) {
            if (itemTraveler.travelInsuranceOption == null) {
                travelInsuranceOption = TravelInsuranceOption()
            } else {
                travelInsuranceOption = itemTraveler.travelInsuranceOption!!
                travelInsurance = itemTraveler.travelInsurance
                setTravelInsuranceAddOnName()
            }
        }

        if (itemTraveler.baggageInsuranceOption == null) {
            baggageInsuranceOption = BaggageInsuranceOption()
        } else {
            baggageInsuranceOption = itemTraveler.baggageInsuranceOption!!
            baggageInsurance = itemTraveler.baggageInsurance
            setBaggageInsuranceAddOnName()
        }

        when {
            itemTraveler.passengerTypeTitle!!.contains(PassengerTypeTitle.Adult.name) -> passengerType.value =
                PassengerTypeTitle.Adult.name
            itemTraveler.passengerTypeTitle!!.contains(PassengerTypeTitle.Child.name) -> passengerType.value =
                PassengerTypeTitle.Child.name
            itemTraveler.passengerTypeTitle!!.contains(PassengerTypeTitle.Infant.name) -> passengerType.value =
                PassengerTypeTitle.Infant.name
        }

        if(itemTraveler.passengerType == PassengerType.INFANT)
            isBaggageInsuranceAllowed = false

        passenger.set(passengers)
        onGetSsrCodesResponse()
        fetchProfileInfoRX()
        fetchFlightAddOns()
        flights.flight.map {
            if (it.airlines.code == "SV") {
                isSaudiaAirlines.set(true)
            }
        }
    }

    private fun fetchFlightAddOns() {
        isLoaderShow.value = true
        executeSuspendedCodeBlock(ApiCallingKey.GetFlightAddOns.name) {
            DataManager.flightApiService.getAddOns(flightSearch.searchId, flightSearch.sequence)
        }
    }

    fun fetchAddOnsDetails(code: String, service: String) {
        isLoaderShow.value = true
        executeSuspendedCodeBlock(ApiCallingKey.GetFlightAddOnDetails.name) {
            DataManager.flightApiService.getAddOnsDetails(code, service)
        }
    }

    private fun checkAttachment(): Boolean {
        if (flights.attachment) {
            return when {
                passenger.get()!!.passportCopy.isNullOrEmpty() -> {
                    showMessage("Please upload your passport copy")
                    false
                }
                passenger.get()!!.visaCopy.isNullOrEmpty() -> {
                    showMessage("Please upload your visa copy")
                    false
                }
                else -> {
                    true
                }
            }
        }
        return true
    }

    private fun fetchProfileInfoRX() {
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        executeSuspendedCodeBlock(ApiCallingKey.UserProfileRX.name) {
            DataManager.flightApiService.getProfileResponseRX(token)
        }
    }

    fun onClickSaveMenu() {
        val traveller = passenger.get()

        if (!flights.domestic) {
            if (traveller?.passportNumber.isNullOrEmpty()) {
                isLoaderShow.value = false
                showMessage(ENTER_PASSPORT_NUMBER)
                return
            }
            if (!traveller?.isValidPassportOrEmpty()!!) {
                showMessage(INVALID_PASSPORT)
                return
            }
            if (traveller.passportExpireDate.isNullOrEmpty()) {
                isLoaderShow.value = false
                showMessage(ENTER_PASSPORT_EXPIRY_DATE)
                return
            }
        }

        if (isInputValid()) {
            itemTraveler.givenName = traveller?.givenName ?: ""
            itemTraveler.surName = traveller?.surName
            itemTraveler.passportCopy = traveller?.passportCopy
            itemTraveler.visaCopy = traveller?.visaCopy
            itemTraveler.dateOfBirth =
                DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(traveller?.dateOfBirth)
            itemTraveler.nationality = traveller?.nationality
            itemTraveler.passportNumber = traveller?.passportNumber
            itemTraveler.passportExpireDate =
                DateUtil.parseApiDateFormatFromDisplayCommonDateFormat(traveller?.passportExpireDate)
            itemTraveler.isValidData = true

            if (isMaleSelected.get()) {
                itemTraveler.gender = "Male"
            } else {
                itemTraveler.gender = "Female"
            }

            itemTraveler.titleName =
                itemTraveler.gender.getUserTitleForFlight(itemTraveler.dateOfBirth!!, flightDate)
            itemTraveler.wheelChair = wheelChairCode
            itemTraveler.mealPreference = mealTypeCode

            if (!isDomesticFlight.get()) {
                itemTraveler.covid = covid
                itemTraveler.covidTestOption = covidTestOption
            }

            if (isDomesticFlight.get()) {
                itemTraveler.travelInsurance = travelInsurance
                itemTraveler.travelInsuranceOption = travelInsuranceOption
            }

            itemTraveler.wheelChairText = wheelchairRequestObservable.get()
            itemTraveler.mealPreferenceText = mealTypeObservable.get()
            itemTraveler.baggageInsurance = baggageInsurance
            itemTraveler.baggageInsuranceOption = baggageInsuranceOption
            saveInfoClicked.value = Event(true)
        } else {
            dataInvalid.value = true
        }
    }

    fun setQuickPickerData(position: Int) {
        flightEventManager.selectPassengerFromQuickPick()
        val info = passengerList.value?.get(position)
        val guest = ItemTraveler()
        guest.titleName = info?.titleName
        guest.givenName = info?.givenName
        guest.surName = info?.surName
        guest.gender = info?.gender
        try {
            userBirthDate.value = info!!.dateOfBirth
            guest.dateOfBirth = info.dateOfBirth.dateChangeToDDMMYYYY()
            guest.passportExpireDate = info.passportExpireDate.dateChangeToDDMMYYYY()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            if (info.passportExpireDate != null && info.passportExpireDate.isNotEmpty()) {
                val firstDate = sdf.parse(info.passportExpireDate)
                val secondDate = sdf.parse(flightDate)
                val diffInMillis: Long = abs(secondDate.time - firstDate.time)
                val diff: Long = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
                if (diff >= numberOfDaysMinimumRequired) {
                    guest.passportExpireDate = info.passportExpireDate.dateChangeToDDMMYYYY()
                    passportValid.value = true
                } else {
                    guest.passportExpireDate = ""
                    passportValid.value = false
                }
            }
        } catch (e: Exception) {
            guest.dateOfBirth = ""
            guest.passportExpireDate = ""
        }

        guest.nationality = info?.nationality
        guest.passportNumber = info?.passportNumber
        guest.passportCopy = info?.passportCopy
        guest.visaCopy = info?.visaCopy

        if (info?.passportCopy == "") {
            passportProgress.value = 0
        } else {
            passportProgress.value = 100
        }

        if (info?.visaCopy == "") {
            visaProgress.value = 0
        } else {
            visaProgress.value = 100
        }

        if (info?.gender == "Female") {
            isMaleSelected.set(false)
        } else {
            isMaleSelected.set(true)
        }

        guest.let {
            if (flights.domestic && it.passportExpireDate!!.isEmpty()) {
                it.passportExpireDate = DateUtil.dayAfterOneYearApiDateFormat
            }
            if (flights.domestic && it.passportNumber!!.isEmpty()) {
                it.passportNumber = "Domestic"
            }
        }
        passenger.set(guest)
    }

    fun onClickNationality() {
        gotoNationality.value = Event(true)
    }

    fun setCountryCode(code: String, name: String) {
        val traveller = passenger.get()
        traveller!!.nationality = code
        passenger.set(traveller)
    }

    fun onClickPassportCopy() {
        flightEventManager.uploadPassportCopy()
        if (passportProgress.value == 100) {
            imageUploadChoiceOld.value = Event("passport")
        } else {
            imageUploadChoiceNew.value = Event("passport")
        }
    }

    fun onClickMealType(view: View) {
        ssrList?.let {
            mealTypeLiveData.value = Pair(
                ArrayList(it.find { item -> item.type.uppercase(Locale.getDefault()) == SsrEnum.MEAL.name }?.ssr!!),
                mealTypeObservable.get()
            )
        }
    }

    fun onClickRequestWheelchair(view: View) {
        ssrList?.let {
            requestWheelchairLiveData.value = Pair(
                ArrayList(it.find { item -> item.type.uppercase(Locale.getDefault()) == SsrEnum.WHEELCHAIR.name }?.ssr!!),
                wheelchairRequestObservable.get()
            )
        }
    }

    fun onClickCovidTest(view: View) {
        covidInfoClicked.value = covidAddOnList
    }

    fun onClickTravelInsurance(view: View) {
        travelInsuranceClicked.value = travelInsuranceList.value
    }

    fun onClickBaggageInsurance(view: View) {
        baggageInsuranceClicked.value = baggageInsuranceList.value
    }

    fun onClickVisaCopy() {
        flightEventManager.uploadVisaCopy()
        if (visaProgress.value == 100) {
            imageUploadChoiceOld.value = Event("visa")
        } else {
            imageUploadChoiceNew.value = Event("visa")
        }
    }

    fun updateImageFile(photoFile: File, tag: String, mime: String) {
        isLoaderShow.value = true
        val token = sharedPrefsHelper[PrefKey.ACCESS_TOKEN, ""]
        val requestPhotoFile = photoFile.asRequestBody(mime.toMediaTypeOrNull())
        val userPhoto =
            MultipartBody.Part.createFormData("uploadFile", photoFile.name, requestPhotoFile)
        if (tag == "passport") {
            passportProgress.value = 50
        } else if (tag == "visa") {
            visaProgress.value = 50
        }
        this.tag = tag

        executeSuspendedCodeBlock(ApiCallingKey.UpdateImage.name) {
            DataManager.flightApiService.sendFile(token, userPhoto)
        }
    }

    fun onClickFemale(view: View) {
        isMaleSelected.set(false)
    }

    fun onClickMale(view: View) {
        isMaleSelected.set(true)
    }

    fun onTextChangedForFirstName(s: CharSequence, start: Int, before: Int, count: Int) {
        isFirstNameValid.value = if (s.toString().isEmpty()) true else s.toString().isNameValid()
    }

    fun onTextChangedForLastName(s: CharSequence, start: Int, before: Int, count: Int) {
        isLastNameValid.value = s.toString().isNameValid()
    }

    fun onTextChangedForPassportNumber(s: CharSequence, start: Int, before: Int, count: Int) {
        isPassportNumberValid.value = s.toString().isPassportNumberValid()
    }

    fun onTextChangedForPassportExpiryDate(s: CharSequence, start: Int, before: Int, count: Int) {
        isPassportExpiryDateValid.value =
            s.toString().isPassportExpiryDateValid() && DateUtil.getDuration(
                null,
                s.toString(),
                "mon"
            ) >= 6
    }

    private fun onGetSsrCodesResponse() {
        executeSuspendedCodeBlock(ApiCallingKey.GetSsrCodes.name) {
            DataManager.flightApiService.getSsrCodeResponse()
        }
    }

    fun setCovidInfo(covidTestOption: CovidTestOption) {
        this.covidTestOption = covidTestOption
        covid = Covid(
            covidTestOption.testCode,
            covidTestOption.code,
            covidTestOption.addressDetails,
            covidTestOption.self
        )
        setSelectedAddonName()
    }

    fun setTravelInsuranceInfo(travelInsuranceOption: TravelInsuranceOption) {
        this.travelInsuranceOption = travelInsuranceOption
        travelInsuranceList.value?.forEach { item ->
            item.options.forEach {
                if (it.code == travelInsuranceOption.optionCode) {
                    travelInsurance = if (item.self) {
                        null
                    } else {
                        TravelInsurance(
                            code = travelInsuranceOption.code,
                            optionsCode = travelInsuranceOption.optionCode
                        )
                    }
                }
            }
        }
        setTravelInsuranceAddOnName()
    }

    fun setBaggageInsuranceInfo(baggageInsuranceOption: BaggageInsuranceOption) {
        this.baggageInsuranceOption = baggageInsuranceOption
        baggageInsuranceList.value?.forEach { item ->
            item.options.forEach {
                if (it.code == baggageInsuranceOption.optionCode) {
                    baggageInsurance = if (item.self) {
                        null
                    } else {
                        BaggageInsuranceBody(
                            code = baggageInsuranceOption.code,
                            optionsCode = baggageInsuranceOption.optionCode
                        )
                    }
                }
            }
        }
        setBaggageInsuranceAddOnName()
    }

    fun setMealValue(value: Pair<String, String>) {
        mealTypeCode = if (value.first == "None") "" else value.second
        mealTypeObservable.set(value.first)
    }

    fun setWheelchairValue(value: Pair<String, String>) {
        wheelChairCode = if (value.first == "None") "" else value.second
        wheelchairRequestObservable.set(value.first)
    }

    private fun setSelectedAddonName() {
        covidTestOption.let {
            covidTestObservable.set(it.name + ", " + it.addressDetails)
        }
    }

    fun setTravelInsuranceAddOnName() {
        travelInsuranceOption.let {
            travelInsuranceObservable.set(it.name)
        }
    }

    fun setBaggageInsuranceAddOnName() {
        baggageInsuranceOption.let {
            baggageInsuranceObservable.set(it.name)
        }
    }
}
