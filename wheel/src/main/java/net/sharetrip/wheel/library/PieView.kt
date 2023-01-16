package net.sharetrip.wheel.library

import android.animation.Animator
import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import net.sharetrip.wheel.library.LuckyWheelUtils.drawableToBitmap
import net.sharetrip.wheel.library.model.LuckyItem
import java.util.*

class PieView : View {
    private var mRange = RectF()
    private var mRadius = 0
    private lateinit var mArcPaint: Paint
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: TextPaint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTopTextPadding = 0
    private var mSecondaryTextVerticalPadding = 0
    private var mSecondaryTextHorizontalPadding = 0
    private var mTopTextSize = 0
    private var mSecondaryTextSize = 0
    private var mRoundOfNumber = 4
    private var mEdgeWidth = -1
    private var isRunning = false
    private var borderColor = 0
    private var defaultBackgroundColor = 0
    private var drawableCenterImage: Drawable? = null
    private var textColor = 0
    private lateinit var viewPropertyAnimator: ViewPropertyAnimator
    private var predeterminedNumber = -1
    private var mLuckyItemList: ArrayList<LuckyItem>? = ArrayList()
    private var mPieRotateListener: PieRotateListener? = null

    interface PieRotateListener {
        fun rotateDone(value: String?)
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setPieRotateListener(listener: PieRotateListener?) {
        mPieRotateListener = listener
    }

    private fun init() {
        viewPropertyAnimator = animate()
        mArcPaint = Paint()
        mArcPaint.isAntiAlias = true
        mArcPaint.isDither = true
        mTextPaint = TextPaint()
        mTextPaint!!.isAntiAlias = true
        if (textColor != 0) mTextPaint!!.color = textColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 14f,
            resources.displayMetrics
        )
        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    val luckyItemListSize: Int
        get() = mLuckyItemList!!.size

    fun setData(luckyItemList: ArrayList<LuckyItem>?) {
        mLuckyItemList = luckyItemList
        invalidate()
    }

    fun clear() {
        val path = Path()
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setBorderColor(color: Int) {
        borderColor = color
        invalidate()
    }

    fun setTopTextPadding(padding: Int) {
        mTopTextPadding = padding
        invalidate()
    }

    fun setSecondaryTextVerticalPadding(padding: Int) {
        mSecondaryTextVerticalPadding = padding
        invalidate()
    }

    fun setSecondaryTextHorizontalPadding(padding: Int) {
        mSecondaryTextHorizontalPadding = padding
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setTopTextSize(size: Int) {
        mTopTextSize = size
        invalidate()
    }

    fun setSecondaryTextSizeSize(size: Int) {
        mSecondaryTextSize = size
        invalidate()
    }

    fun setBorderWidth(width: Int) {
        mEdgeWidth = width
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2,
                measuredHeight - mPadding / 2
            ), null
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLuckyItemList == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mLuckyItemList!!.size
        val halfSweepAngle = sweepAngle / 2
        var startAngle = 90f

        for (i in mLuckyItemList!!.indices) {
            if (mLuckyItemList!![i].color != 0) {
                mArcPaint.style = Paint.Style.FILL
                mArcPaint.color = mLuckyItemList!![i].color
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint)
            }

            if (borderColor != 0 && mEdgeWidth > 0) {
                mArcPaint.style = Paint.Style.STROKE
                mArcPaint.color = borderColor
                mArcPaint.strokeWidth = mEdgeWidth.toFloat()
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint)
            }

            val sliceColor =
                if (mLuckyItemList!![i].color != 0) mLuckyItemList!![i].color else defaultBackgroundColor

            if (!TextUtils.isEmpty(mLuckyItemList!![i].topText)) drawTopText(
                canvas,
                tmpAngle,
                sweepAngle,
                mLuckyItemList!![i].topText,
                sliceColor
            )

            if (!TextUtils.isEmpty(mLuckyItemList!![i].secondaryText)) drawSecondaryText(
                canvas,
                tmpAngle,
                mLuckyItemList!![i].secondaryText,
                sliceColor
            )

            if (startAngle + sweepAngle > 360) {
                startAngle = 0f
            }

            startAngle += sweepAngle

            if (mLuckyItemList!![i].icon != 0) drawImage(
                canvas,
                tmpAngle,
                rotateBitmap(
                    getBitmapFromVectorDrawable(context, mLuckyItemList!![i].icon),
                    startAngle - halfSweepAngle
                )
            )

            tmpAngle += sweepAngle
        }

        if (drawableCenterImage != null) {
            drawCenterImage(canvas, drawableCenterImage!!)
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == 0) return
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            mCenter - 5.toFloat(),
            mBackgroundPaint!!
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = Math.min(measuredWidth, measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        var imgWidth = mRadius / mLuckyItemList!!.size
        imgWidth = (imgWidth / 1.2).toInt()
        val angle = ((tmpAngle + 360f / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 1.3 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 1.3 / 2 * Math.sin(angle.toDouble())).toInt()
        val rect = Rect(
            x - imgWidth, y - imgWidth,
            x + imgWidth, y + imgWidth
        )
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable) {
        var bitmap = drawableToBitmap(drawable)
        bitmap = Bitmap.createScaledBitmap(
            bitmap,
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            false
        )
        canvas.drawBitmap(
            bitmap, measuredWidth / 2 - bitmap.width / 2.toFloat(),
            measuredHeight / 2 - bitmap.height / 2.toFloat(), null
        )
    }

    private fun isColorDark(color: Int): Boolean {
        val colorValue = ColorUtils.calculateLuminance(color)
        val compareValue = 0.30
        return colorValue <= compareValue
    }

    private fun drawTopText(
        canvas: Canvas,
        tmpAngle: Float,
        sweepAngle: Float,
        mStr: String?,
        backgroundColor: Int
    ) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textAlign = Paint.Align.LEFT
        mTextPaint!!.textSize = mTopTextSize.toFloat()
        mTextPaint!!.color = Color.WHITE
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mTopTextPadding
        canvas.drawTextOnPath(mStr!!, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    private fun drawSecondaryText(
        canvas: Canvas,
        tmpAngle: Float,
        mStrq: String?,
        backgroundColor: Int
    ) {
        canvas.save()
        val arraySize = mLuckyItemList!!.size
        if (textColor == 0) mTextPaint!!.color =
            if (isColorDark(backgroundColor)) -0x1 else -0x1000000
        val typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mTextPaint!!.typeface = typeface
        mTextPaint!!.textSize = mSecondaryTextSize.toFloat()
        mTextPaint!!.textAlign = Paint.Align.RIGHT
        mTextPaint!!.color = Color.WHITE
        val initFloat = tmpAngle + 360f / arraySize / 2
        val angle = (initFloat * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * Math.cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * Math.sin(angle.toDouble())).toInt()
        val mTextLayout = StaticLayout(
            mStrq,
            mTextPaint,
            canvas.width,
            Layout.Alignment.ALIGN_NORMAL,
            1.0f,
            0.0f,
            false
        )
        canvas.rotate(initFloat + arraySize / 18f, x.toFloat(), y.toFloat())
        canvas.translate(
            x + mSecondaryTextVerticalPadding.toFloat(),
            y - mSecondaryTextHorizontalPadding.toFloat()
        )
        mTextLayout.draw(canvas)
        canvas.restore()
    }

    private fun getAngleOfIndexTarget(index: Int): Float {
        return 360f / mLuckyItemList!!.size * index
    }

    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    fun setPredeterminedNumber(predeterminedNumber: Int) {
        this.predeterminedNumber = predeterminedNumber
    }

    fun rotateTo(index: Int) {
        val rand = Random()
        rotateTo(index, rand.nextInt() * 3 % 2, true)
    }

    fun cancelRoting() {
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator!!.cancel()
        }
    }

    fun rotateInitial() {
        val targetAngle = 360 * 2.toFloat()
        viewPropertyAnimator
            .setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 1000 + 900L)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            .rotation(targetAngle)
            .start()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun rotateTo(index: Int, rotation: Int, startSlow: Boolean) {
        if (isRunning) {
            return
        }
        val rotationAssess = if (rotation <= 0) 1 else -1

        //If the staring position is already off 0 degrees, make an illusion that the rotation has smoothly been triggered.
        // But this inital animation will just reset the position of the circle to 0 degreees.
        if (getRotation() != 0.0f) {
            setRotation(getRotation() % 360f)
            val animationStart: TimeInterpolator =
                if (startSlow) AccelerateInterpolator() else LinearInterpolator()
            //The multiplier is to do a big rotation again if the position is already near 360.
            val multiplier = if (getRotation() > 200f) 2 else 1
            if (this::viewPropertyAnimator.isInitialized) {
                viewPropertyAnimator
                    .setInterpolator(animationStart)
                    .setDuration(500L)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            isRunning = true
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            isRunning = false
                            setRotation(0f)
                            rotateTo(index, rotation, false)
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                    .rotation(360f * multiplier * rotationAssess)
                    .start()
                return
            }
        }

        // This addition of another round count for counterclockwise is to simulate the perception of the same number of spin
        // if you still need to reach the same outcome of a positive degrees rotation with the number of rounds reversed.
        val targetAngle =
            360f * mRoundOfNumber * rotationAssess + 270f - getAngleOfIndexTarget(index) - 360f / mLuckyItemList!!.size / 2
        if (this::viewPropertyAnimator.isInitialized) {
            viewPropertyAnimator
                .setInterpolator(DecelerateInterpolator())
                .setDuration(mRoundOfNumber * 1050 + 900L)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        isRunning = true
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isRunning = false
                        setRotation(getRotation() % 360f)
                        if (mPieRotateListener != null) {
                            mLuckyItemList?.let {
                                if (index in 0 until it.size) {
                                    mPieRotateListener!!.rotateDone(it[index].secondaryText)
                                }
                            }
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
                .rotation(targetAngle)
                .start()
        }
    }

    var isTouchEnabled = true
}
