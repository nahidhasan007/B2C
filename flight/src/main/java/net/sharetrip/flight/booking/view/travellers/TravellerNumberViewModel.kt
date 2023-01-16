package net.sharetrip.flight.booking.view.travellers

import android.annotation.SuppressLint
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.sharetrip.base.viewmodel.BaseViewModel
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.booking.model.TravellersInfo
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class TravellerNumberViewModel(var travellersInfo: TravellersInfo?) : BaseViewModel() {
    var numberOfAdult: Int = 0
    var numberOfChildren: Int = 0
    var numberOfInfant: Int = 0
    var tripClassType: String
    val childDOBNumber = MutableLiveData<ArrayList<ChildrenDOB>>()
    var childDobList: ArrayList<ChildrenDOB> = arrayListOf()

    val adultNumber = ObservableInt()
    val childNumber = ObservableInt()
    val infantNumber = ObservableInt()
    var firstTravelDate: Long? = null

    init {
        numberOfAdult = travellersInfo!!.adult
        numberOfChildren = travellersInfo!!.child
        numberOfInfant = travellersInfo!!.infant
        tripClassType = travellersInfo!!.classType
        val childList: ArrayList<ChildrenDOB>? = travellersInfo?.childDateOfBirthList
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = travellersInfo?.tripDate?.let { sdf.parse(it) }
        if (date != null) {
            firstTravelDate = date.time
        }

        if (childList != null) {
            if (childList.size > 0) {
                for (aChildObj in childList) {
                    val aChild = ChildrenDOB(aChildObj.title, aChildObj.date)
                    childDobList.add(aChild)
                }
                childDOBNumber.value = childList!!
            }
        }

        adultNumber.set(numberOfAdult)
        childNumber.set(numberOfChildren)
        infantNumber.set(numberOfInfant)
    }

    fun checkDob(): Boolean {
        for (child in childDobList) {
            if (child.date.isEmpty()) {
                 showMessage("Please fill up all child Date Of Birth")
                return false
            }
        }
        return true
    }

    fun onClickedAdultRemove() {
        if (numberOfAdult > 1) {
            numberOfAdult--
            adultNumber.set(numberOfAdult)

            if (numberOfInfant > numberOfAdult)
                numberOfInfant = numberOfAdult

            infantNumber.set(numberOfInfant)
        }
    }

    fun onClickedAdultAdd() {
        if (numberOfAdult + numberOfChildren < 7) {
            numberOfAdult++
            adultNumber.set(numberOfAdult)
        }
    }

    fun onClickedChildrenRemove() {
        if (numberOfChildren > 0) {
            numberOfChildren--
            childNumber.set(numberOfChildren)

            if (childDobList.size > 0) {
                childDobList.removeAt(childDobList.size - 1)
                childDOBNumber.value = childDobList
            }
        }
    }

    fun onClickedChildrenAdd() {
        if (numberOfAdult + numberOfChildren < 7) {
            numberOfChildren++
            childNumber.set(numberOfChildren)
            childDobList.add(ChildrenDOB("Child $numberOfChildren Date of birth"))
            childDOBNumber.value = childDobList
        }
    }

    fun onClickedInfantsRemove() {
        if (numberOfInfant > 0) {
            numberOfInfant--
            infantNumber.set(numberOfInfant)
        }
    }

    fun onClickedInfantsAdd() {
        if (numberOfInfant < numberOfAdult) {
            numberOfInfant++
            infantNumber.set(numberOfInfant)
        }
    }
}
