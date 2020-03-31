package com.lerendan.customviewsample.study

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import com.lerendan.customviewsample.R
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/30.
 */
class ScalableImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, Runnable {



    val IMAGE_WIDTH = Utils.dp2px(300)
    val OVER_SCALE_FACTOR = 2//放大系数

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mBitmap: Bitmap
    var offsetX = 0f
    var offsetY = 0f
    var originalOffsetX = 0f
    var originalOffsetY = 0f

    var smallScale = 0f//内切边
    var bigScale = 0f//外切边

    var mDetector: GestureDetectorCompat

    var isBig = false//是否是大图

    var scaleFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    lateinit var mScaleAnimator: ObjectAnimator

    var scroller: OverScroller

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, IMAGE_WIDTH.toInt())
        mDetector = GestureDetectorCompat(context, this)
        scroller = OverScroller(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        originalOffsetX = (width - mBitmap.width) / 2f
        originalOffsetY = (height - mBitmap.height) / 2f

        if (mBitmap.width / mBitmap.height.toFloat() > width / height.toFloat()) {
            smallScale = width / mBitmap.width.toFloat()
            bigScale = height / mBitmap.height.toFloat() * OVER_SCALE_FACTOR
        } else {
            smallScale = height / mBitmap.height.toFloat()
            bigScale = width / mBitmap.width.toFloat() * OVER_SCALE_FACTOR
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(offsetX, offsetY)

        //放缩
        val scale = smallScale + (bigScale - smallScale) * scaleFraction

        canvas.scale(scale, scale, width / 2f, height / 2f)
        canvas.drawBitmap(mBitmap, originalOffsetX, originalOffsetY, mPaint)

    }

    private fun getScaleAnimator(): ObjectAnimator {
        if (!this::mScaleAnimator.isInitialized) {
            mScaleAnimator = ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f)
        }
        return mScaleAnimator
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mDetector.onTouchEvent(event)
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        //down事件，必须返回true，否则后面的事件就收不到了
        return true
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if (isBig) {
            scroller.fling(
                offsetX.toInt(), offsetY.toInt(), velocityX.toInt(), velocityY.toInt(),
                (-(mBitmap.width * bigScale - width) / 2).toInt(),
                ((mBitmap.width * bigScale - width) / 2).toInt(),
                (-(mBitmap.height * bigScale - height) / 2).toInt(),
                ((mBitmap.height * bigScale - height) / 2).toInt(),100,100
            )

            postOnAnimation(this)

        }
        return false
    }

    override fun run() {
        if(scroller.computeScrollOffset()) {
            offsetX = scroller.currX.toFloat()
            offsetY = scroller.currY.toFloat()
            invalidate()
            postOnAnimation(this)
        }
    }

    override fun onScroll(down: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (isBig) {
            offsetX -= distanceX
            offsetX = Math.min(offsetX, (mBitmap.width * bigScale - width) / 2)
            offsetX = Math.max(offsetX, -(mBitmap.width * bigScale - width) / 2)
            offsetY -= distanceY
            offsetY = Math.min(offsetY, (mBitmap.height * bigScale - height) / 2)
            offsetY = Math.max(offsetY, -(mBitmap.height * bigScale - height) / 2)
            invalidate()
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        isBig = !isBig
        if (isBig) {
            getScaleAnimator().start()
        } else {
                offsetX = 0f
                offsetY = 0f
            getScaleAnimator().reverse()
        }
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

}