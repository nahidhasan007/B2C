package net.sharetrip.wheel.library

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import net.sharetrip.wheel.R
import net.sharetrip.wheel.library.LuckyWheelUtils.convertDpToPixel
import net.sharetrip.wheel.library.PieView.PieRotateListener
import net.sharetrip.wheel.library.model.LuckyItem
import java.util.*

class LuckyWheelView : RelativeLayout, PieRotateListener {
    private var mBackgroundColor = 0
    private var mTextColor = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mBorderColor = 0
    private var mTopTextPadding = 0
    private var mSecondaryTextVerticalPadding = 0
    private var mSecondaryTextHorizontalPadding = 0
    private var mEdgeWidth = 0
    private var mCenterImage: Drawable? = null
    private var mCursorImage: Drawable? = null
    private lateinit var pieView: PieView
    private lateinit var ivCursorView: ImageView
    private var mLuckyRoundItemSelectedListener: LuckyRoundItemSelectedListener? = null

    override fun rotateDone(value: String?) {
        if (mLuckyRoundItemSelectedListener != null) {
            mLuckyRoundItemSelectedListener!!.LuckyRoundItemSelected(value)
        }
    }

    interface LuckyRoundItemSelectedListener {
        fun LuckyRoundItemSelected(value: String?)
    }

    fun setLuckyRoundItemSelectedListener(listener: LuckyRoundItemSelectedListener?) {
        mLuckyRoundItemSelectedListener = listener
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(ctx: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView)
            mBackgroundColor =
                typedArray.getColor(R.styleable.LuckyWheelView_lkwBackgroundColor, -0x340000)
            mTopTextSize = typedArray.getDimensionPixelSize(
                R.styleable.LuckyWheelView_lkwTopTextSize,
                convertDpToPixel(10f, context).toInt()
            )
            mSecondaryTextSize = typedArray.getDimensionPixelSize(
                R.styleable.LuckyWheelView_lkwSecondaryTextSize,
                convertDpToPixel(12f, context).toInt()
            )
            mTextColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwTopTextColor, 0)
            mTopTextPadding = typedArray.getDimensionPixelSize(
                R.styleable.LuckyWheelView_lkwTopTextPadding,
                convertDpToPixel(10f, context).toInt()
            ) + convertDpToPixel(10f, context).toInt()
            mSecondaryTextVerticalPadding = typedArray.getDimensionPixelSize(
                R.styleable.LuckyWheelView_lkwSecondaryTextVerticalPadding,
                convertDpToPixel(10f, context).toInt()
            )
            mSecondaryTextHorizontalPadding = typedArray.getDimensionPixelSize(
                R.styleable.LuckyWheelView_lkwSecondaryTextHorizontalPadding,
                convertDpToPixel(10f, context).toInt()
            )
            mCursorImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCursor)
            mCenterImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCenterImage)
            mEdgeWidth = typedArray.getInt(R.styleable.LuckyWheelView_lkwEdgeWidth, 10)
            mBorderColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwEdgeColor, 0)
            typedArray.recycle()
        }

        val inflater = LayoutInflater.from(context)
        val frameLayout = inflater.inflate(R.layout.lucky_wheel_layout, this, false) as FrameLayout
        pieView = frameLayout.findViewById(R.id.pieView)
        ivCursorView = frameLayout.findViewById(R.id.cursorView)
        pieView.setPieRotateListener(this)
        pieView.setPieBackgroundColor(mBackgroundColor)
        pieView.setTopTextPadding(mTopTextPadding)
        pieView.setSecondaryTextVerticalPadding(mSecondaryTextVerticalPadding)
        pieView.setSecondaryTextHorizontalPadding(mSecondaryTextHorizontalPadding)
        pieView.setTopTextSize(mTopTextSize)
        pieView.setSecondaryTextSizeSize(mSecondaryTextSize)
        pieView.setPieCenterImage(mCenterImage)
        pieView.setBorderColor(mBorderColor)
        pieView.setBorderWidth(mEdgeWidth)
        if (mTextColor != 0) pieView.setPieTextColor(mTextColor)
        ivCursorView.setImageDrawable(mCursorImage)
        addView(frameLayout)
    }

    var isTouchEnabled: Boolean
        get() = pieView.isTouchEnabled
        set(touchEnabled) {
            pieView.isTouchEnabled = touchEnabled
        }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        //This is to control that the touch events triggered are only going to the PieView
        for (i in 0 until childCount) {
            if (isPieView(getChildAt(i))) {
                return super.dispatchTouchEvent(ev)
            }
        }
        return false
    }

    private fun isPieView(view: View): Boolean {
        if (view is ViewGroup) {
            for (i in 0 until childCount) {
                if (isPieView(view.getChildAt(i))) {
                    return true
                }
            }
        }
        return view is PieView
    }

    fun setData(data: ArrayList<LuckyItem>) {
        pieView.setData(data)
    }

    fun clear() {
        pieView.clear()
    }

    fun setRound(numberOfRound: Int) {
        pieView.setRound(numberOfRound)
    }

    fun startLuckyWheelWithTargetIndex(index: Int) {
        pieView.rotateTo(index)
    }
}
