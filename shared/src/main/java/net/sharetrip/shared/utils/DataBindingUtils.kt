package net.sharetrip.shared.utils

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("imageDrawableId")
fun loadImage(view: AppCompatImageView, drawableId: Int) {
    Glide.with(view.context)
            .load(drawableId)
            .into(view)
}

@BindingAdapter("imageDrawableId")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("isEmptyText")
fun checkEditText(inputLayout: TextInputLayout, text: String?) {
    if (text != null && text.trim { it <= ' ' }.isEmpty()) {
        inputLayout.isErrorEnabled = true
        inputLayout.error = " "
    } else {
        inputLayout.isErrorEnabled = false
    }
}


