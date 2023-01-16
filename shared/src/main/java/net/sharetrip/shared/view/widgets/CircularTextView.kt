package net.sharetrip.shared.view.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import net.sharetrip.shared.R

class CircularTextView : AppCompatTextView {
    private lateinit var mFillPaint: Paint
    private var mViewSize = 0

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.CircularTextView, defStyle, 0)
        val mFillColor = a.getColor(R.styleable.CircularTextView_cv_fillColor, Color.TRANSPARENT)
        a.recycle()
        mFillPaint = Paint()
        mFillPaint.flags = Paint.ANTI_ALIAS_FLAG
        mFillPaint.style = Paint.Style.FILL
        mFillPaint.color = mFillColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        mViewSize = width.coerceAtLeast(height)
        setMeasuredDimension(mViewSize, mViewSize)
    }

    override fun onDraw(canvas: Canvas) {
        val mInnerRectF = RectF()
        mInnerRectF[0f, 0f, width.toFloat()] = height.toFloat()
        val centerX = mInnerRectF.centerX()
        val centerY = mInnerRectF.centerY()
        val radius = (mViewSize / 2).toFloat()
        canvas.drawCircle(centerX, centerY, radius, mFillPaint)
        super.onDraw(canvas)
    }
}
