package net.sharetrip.visa.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerDecoration(context: Context, private var space: Int = 10) :
    RecyclerView.ItemDecoration() {

    private val spaceInDp = dpToPx(context, space)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) > 0) {
            outRect.left = spaceInDp
            outRect.right = spaceInDp
            outRect.bottom = spaceInDp
            outRect.top = spaceInDp
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int =
        (dp * context.resources.displayMetrics.density).toInt()
}
