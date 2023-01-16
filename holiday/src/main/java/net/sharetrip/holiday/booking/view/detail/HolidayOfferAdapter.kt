package net.sharetrip.holiday.booking.view.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.holiday.R
import com.example.holiday.databinding.ItemHolidayOfferBinding
import net.sharetrip.shared.utils.dateChangeToDDMMYYYY
import net.sharetrip.shared.utils.getStrikePriceStringWithCurrency
import net.sharetrip.holiday.booking.model.HolidayOffer
import java.text.NumberFormat
import java.util.*

class HolidayOfferAdapter : RecyclerView.Adapter<HolidayOfferAdapter.OfferViewHolder>() {
    private val offerList = ArrayList<HolidayOffer>()
    private lateinit var withAirfare: String
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemHolidayOfferBinding>(LayoutInflater.from(context),
                R.layout.item_holiday_offer, parent, false)
        return OfferViewHolder(binding)
    }

    override fun getItemCount() = offerList.size

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(offerList[position], withAirfare)
    }

    fun update(offers: ArrayList<HolidayOffer>, airFare: String) {
        withAirfare = airFare
        offerList.clear()
        offerList.addAll(offers)
        notifyDataSetChanged()
    }

    inner class OfferViewHolder(private val offerBinding: ItemHolidayOfferBinding) : RecyclerView.ViewHolder(offerBinding.root) {
        var count = 0
        private val perPaxList = ArrayList<PerPaxInfo>()
        private lateinit var holidayHotelAdapter: HolidayHotelAdapter

        init {
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle1, offerBinding.textViewPerPaxPrice1, offerBinding.textViewPerPaxPriceDiscount1))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle2, offerBinding.textViewPerPaxPrice2, offerBinding.textViewPerPaxPriceDiscount2))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle3, offerBinding.textViewPerPaxPrice3, offerBinding.textViewPerPaxPriceDiscount3))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle4, offerBinding.textViewPerPaxPrice4, offerBinding.textViewPerPaxPriceDiscount4))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle5, offerBinding.textViewPerPaxPrice5, offerBinding.textViewPerPaxPriceDiscount5))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle6, offerBinding.textViewPerPaxPrice6, offerBinding.textViewPerPaxPriceDiscount6))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle7, offerBinding.textViewPerPaxPrice7, offerBinding.textViewPerPaxPriceDiscount7))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle8, offerBinding.textViewPerPaxPrice8, offerBinding.textViewPerPaxPriceDiscount8))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle9, offerBinding.textViewPerPaxPrice9, offerBinding.textViewPerPaxPriceDiscount9))
            perPaxList.add(PerPaxInfo(offerBinding.textViewPaxTitle10, offerBinding.textViewPerPaxPrice10, offerBinding.textViewPerPaxPriceDiscount10))
        }

        fun bind(item: HolidayOffer, withAirfare: String) {
            if (withAirfare == "YES") {
                offerBinding.textLabelWithAirfare.visibility = View.VISIBLE
            } else {
                offerBinding.textLabelWithAirfare.visibility = View.GONE
            }

            offerBinding.textViewOfferTitle.text = item.category
            offerBinding.textViewValidFrom.text = item.periodFrom!!.dateChangeToDDMMYYYY()
            offerBinding.textViewValidDate.text = item.periodTo!!.dateChangeToDDMMYYYY()
            holidayHotelAdapter = HolidayHotelAdapter()
            offerBinding.recyclerViewHotel.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = holidayHotelAdapter
            }

            holidayHotelAdapter.update(item.hotels!!)

            if (item.singlePerPax != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_single_person)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.singlePerPaxDiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.singlePerPaxDiscountPrice < item.singlePerPax) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.singlePerPax)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.doublePerPax != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_double_person)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.doublePerPaxDiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.doublePerPaxDiscountPrice < item.doublePerPax) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.doublePerPax)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.twinPerPax != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_person_twin)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.twinPerPaxDiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.twinPerPaxDiscountPrice < item.twinPerPax) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.twinPerPax)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.triplePerPax != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_person_triple)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.triplePerPaxDiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.triplePerPaxDiscountPrice < item.triplePerPax) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.triplePerPax)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.quadPerPax != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_person_quad_)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.quadPerPaxDiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.quadPerPaxDiscountPrice <item.quadPerPax) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.quadPerPax)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.infant != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_infant)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.infantDiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.infantDiscountPrice < item.infant) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.infant)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.child3To6 != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_child_3_5)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.child3To6DiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.child3To6DiscountPrice < item.child3To6) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.child3To6)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }

            if (item.child7To12 != 0) {
                perPaxList[count].title.text = context.resources.getString(R.string.price_per_child_7_12_)
                perPaxList[count].price.text = "BDT " + NumberFormat.getNumberInstance(Locale.US).format(item.child7To12DiscountPrice)
                perPaxList[count].title.visibility = View.VISIBLE
                perPaxList[count].price.visibility = View.VISIBLE
                if (item.child7To12DiscountPrice < item.child7To12) {
                    perPaxList[count].originalPrice?.text = item.currency.getStrikePriceStringWithCurrency(item.child7To12)
                    perPaxList[count].originalPrice?.visibility = View.VISIBLE
                }
                count++
            }
        }
    }

    class PerPaxInfo(val title: AppCompatTextView, val price: AppCompatTextView, var originalPrice: AppCompatTextView? = null)
}
