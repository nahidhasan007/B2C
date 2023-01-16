package net.sharetrip.shared.utils

import android.graphics.Paint
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import net.sharetrip.shared.R

@BindingAdapter("android:strikeText")
fun AppCompatTextView.strikeText(text: String?) {
    this.apply {
        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        setText(text)
    }
}

@BindingAdapter("imageUrl")
fun loadImage(view: AppCompatImageView, url: String) {
    Glide.with(view.context)
        .load(url)
        .into(view)
}

@BindingAdapter("loadImageWithGlide")
fun loadImage(view: ImageView, imageUrl: String?) {
    view.loadImageWithRoundCorner(imageUrl, 8)
}

@BindingAdapter(value = ["loadImageCornerRound", "radius"])
fun ImageView.loadImageWithRoundCornerXML(url: String?, radius: Int) {
    this.loadImageWithRoundCorner(url, radius)
}

@BindingAdapter("android:loadImageBinder")
fun ImageView.loadImageBinder(url: String?) {
    this.loadImageNormal(url)
}

@BindingAdapter("android:setCompoundDrawable")
fun AppCompatTextView.setCompoundDrawable(isActive: Boolean) {
    if (isActive) {
        this.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_radio_button_checked_24,
            0,
            0,
            0
        );
    } else {
        this.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_radio_button_unchecked_24,
            0,
            0,
            0
        );
    }
}

/*@BindingAdapter("android:setFilterType")
fun AppCompatTextView.setFilterType(filterType: Int) {
    when (filterType) {
        PRICE -> {
            this.text = this.context.getString(R.string.price_range)
        }
        CLASS_FILTER -> {
            this.text = this.context.getString(R.string.title_class)
        }
        SCHEDULE_FILTER -> {
            this.text = this.context.getString(R.string.title_schedule)
        }
        OPERATOR_FILTER -> {
            this.text = this.context.getString(R.string.title_operators)
        }
    }
}*/
