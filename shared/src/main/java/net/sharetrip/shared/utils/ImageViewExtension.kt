package net.sharetrip.shared.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import net.sharetrip.shared.R

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
            .load(url)
            .apply(RequestOptions().centerInside())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(this)
}

fun ImageView.loadImageNormal(url: Any?) {
    Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(this)
}

fun ImageView.loadImageFromRes(url: Int) {
    Glide.with(this.context)
            .load(url)
            .apply(RequestOptions().centerCrop())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(this)
}

fun ImageView.loadImageWithRoundCorner(url: String?, radius: Int) {
    val options = RequestOptions()
            .centerCrop()
            .transform(CenterCrop(), RoundedCorners(radius))
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)

    Glide.with(this.context)
            .load(url)
            .apply(options)
            .into(this)
}

fun ImageView.loadImageWithCenterCrop(url: Any?) {
    Glide.with(this.context)
            .load(url)
            .apply(RequestOptions().centerCrop())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(this)
}

fun getBitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor? {

    val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
    val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun TextView.setDrawableTintColor(@ColorRes color: Int) {
    compoundDrawables.filterNotNull().forEach {
        it.colorFilter = PorterDuffColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

fun ImageView.setTint(@ColorInt color: Int?) {
    if (color == null) {
        ImageViewCompat.setImageTintList(this, null)
        return
    }
    ImageViewCompat.setImageTintMode(this, PorterDuff.Mode.SRC_ATOP)
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}