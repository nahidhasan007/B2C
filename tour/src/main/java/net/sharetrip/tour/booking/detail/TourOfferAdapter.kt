package net.sharetrip.tour.booking.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.sharetrip.shared.utils.dateChangeToDDMMYYYYFromZ
import net.sharetrip.tour.R
import net.sharetrip.tour.databinding.ItemTourOffersBinding
import net.sharetrip.tour.model.PeriodX
import java.text.NumberFormat
import java.util.*

class TourOfferAdapter : RecyclerView.Adapter<TourOfferAdapter.OfferViewHolder>() {
    private val offerList = ArrayList<PeriodX>()
    private var offerNo = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = DataBindingUtil.inflate<ItemTourOffersBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tour_offers, parent, false
        )
        return OfferViewHolder(binding)
    }

    override fun getItemCount() = offerList.size

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(offerList[position])
    }

    fun update(offers: ArrayList<PeriodX>) {
        offerNo = 0
        offerList.clear()
        offerList.addAll(offers)
        notifyDataSetChanged()
    }

    inner class OfferViewHolder(private val offerBinding: ItemTourOffersBinding) :
        RecyclerView.ViewHolder(offerBinding.root) {
        var count = 0
        private val perPaxList = ArrayList<PerPaxInfo>()

        init {
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle1,
                    offerBinding.textViewPerPaxPrice1
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle2,
                    offerBinding.textViewPerPaxPrice2
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle3,
                    offerBinding.textViewPerPaxPrice3
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle4,
                    offerBinding.textViewPerPaxPrice4
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle5,
                    offerBinding.textViewPerPaxPrice5
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle6,
                    offerBinding.textViewPerPaxPrice6
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle7,
                    offerBinding.textViewPerPaxPrice7
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle8,
                    offerBinding.textViewPerPaxPrice8
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle9,
                    offerBinding.textViewPerPaxPrice9
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle10,
                    offerBinding.textViewPerPaxPrice10
                )
            )
            perPaxList.add(
                PerPaxInfo(
                    offerBinding.textViewPaxTitle11,
                    offerBinding.textViewPerPaxPrice11
                )
            )

        }

        fun bind(item: PeriodX) {
            offerNo++
            offerBinding.textViewOfferTitle.text = "Rates"
            offerBinding.textViewValidFromDate.text = item.periodTo?.dateChangeToDDMMYYYYFromZ()
            offerBinding.textViewValidTillDate.text = item.periodTo?.dateChangeToDDMMYYYYFromZ()

            if (item.infant0To2 != 0) {
                perPaxList[count].title.text = "Price per infant"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.infant0To2)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.child3To6 != 0) {
                perPaxList[count].title.text = "Price per child 3-6"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.child3To6)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.child7To12 != 0) {
                perPaxList[count].title.text = "Price per child 7-12"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.child7To12)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.perPersonPax != 0) {
                perPaxList[count].title.text = "Price Per Adult"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.perPersonPax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price2Pax != 0) {
                perPaxList[count].title.text = "Price Per 2 PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price2Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price3Pax != 0) {
                perPaxList[count].title.text = "Price Per 3PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price3Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price4Pax != 0) {
                perPaxList[count].title.text = "Price Per 4PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price4Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price5Pax != 0) {
                perPaxList[count].title.text = "Price Per 5PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price5Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price6Pax != 0) {
                perPaxList[count].title.text = "Price Per 6PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price6Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price7Pax != 0) {
                perPaxList[count].title.text = "Price Per 7PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price7Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }

            if (item.price8Pax != 0) {
                perPaxList[count].title.text = "Price Per 8PAX"
                perPaxList[count].price.text =
                    "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.price8Pax)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                count++
            }
        }
    }

    class PerPaxInfo(val title: AppCompatTextView, val price: AppCompatTextView)
}
