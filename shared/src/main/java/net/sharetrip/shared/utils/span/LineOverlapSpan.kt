package net.sharetrip.shared.utils.span

import android.graphics.Paint.FontMetricsInt
import android.text.style.LineHeightSpan

class LineOverlapSpan : LineHeightSpan {
    override fun chooseHeight(mText: CharSequence, mStart: Int, mEnd: Int,
                              mSpanStarTextView: Int, mLineHeight: Int,
                              mFontMetricsInt: FontMetricsInt) {
        mFontMetricsInt.bottom += mFontMetricsInt.top
        mFontMetricsInt.descent += mFontMetricsInt.top
    }
}