package net.sharetrip.flight.booking.view.travellers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.text.Html.fromHtml
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.flight.booking.model.ChildrenDOB
import net.sharetrip.flight.databinding.ItemBirthDateSelectionBinding
import net.sharetrip.shared.utils.formatToTwoDigit
import timber.log.Timber
import java.util.*

class ChildDOBAdapter(val viewModel: TravellerNumberViewModel) :
    RecyclerView.Adapter<ChildDOBAdapter.ChildDobHolder>() {

    private val dobList: ArrayList<ChildrenDOB> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildDobHolder {
        val viewHolder = ItemBirthDateSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChildDobHolder(viewHolder)
    }

    override fun getItemCount() = dobList.size

    override fun onBindViewHolder(holder: ChildDobHolder, position: Int) {
        holder.binding.childrenDob = dobList[position]

        holder.binding.inputLayoutBirthDate.hint =
            fromHtml(dobList[position].title + "<font color=\"#FF0000\">" + "*" + "</font>")

        holder.binding.textFieldBirthDate.setOnClickListener() {
            openDatePicker(holder, position)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openDatePicker(holder: ChildDobHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = viewModel.firstTravelDate!!
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        Timber.d(" ${calendar.timeInMillis}")

        val datePickerDialog = DatePickerDialog(
            holder.binding.textFieldBirthDate.context, { _, yearNow, monthOfYear, dayOfMonth ->
                val monthFormatted = formatToTwoDigit(monthOfYear + 1)
                val dayFormatted = formatToTwoDigit(dayOfMonth)

                val date = "$yearNow-$monthFormatted-$dayFormatted"

                holder.binding.textFieldBirthDate.setText("$dayFormatted-$monthFormatted-$yearNow")
                dobList[position] =
                    ChildrenDOB(dobList[position].title, date)

                viewModel.childDobList.clear()
                viewModel.childDobList.addAll(dobList)
            }, year, month, day
        )

        calendar.add(Calendar.YEAR, -2)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        calendar.add(Calendar.YEAR, -10)
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    fun updateData(list: ArrayList<ChildrenDOB>) {
        dobList.clear()
        dobList.addAll(list)
        notifyDataSetChanged()
    }

    class ChildDobHolder(val binding: ItemBirthDateSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)
}
