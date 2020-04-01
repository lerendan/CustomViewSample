package com.lerendan.customviewsample.study

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
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
class ScalableImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val IMAGE_WIDTH = Utils.dp2px(300)
    val OVER_SCALE_FACTOR = 2//放大系数

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mBitmap: Bitmap
    //图片绘制起点
    private var mBitmapStartPoint = PointF()
    //图片偏移
    private var mBitmapOffsetPoint = PointF()

    private var mIsBigScale = false//是否是大图
    private var mSmallScale = 0f//达到内贴边效果的放缩倍数，即小图
    private var mBigScale = 0f//达到外贴边效果的放缩倍数，即大图

    lateinit var mScaleAnimator: ObjectAnimator
    private var mScroller: OverScroller
    private var mGestureDetector: GestureDetectorCompat
    private var mGestureListener = MyGestureListener()

    var mScaleFraction = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, IMAGE_WIDTH.toInt())
        mGestureDetector = GestureDetectorCompat(context, mGestureListener)
        mScroller = OverScroller(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //使得图片初始是居中的
        mBitmapStartPoint.x = (width - mBitmap.width) / 2f
        mBitmapStartPoint.y = (height - mBitmap.height) / 2f

        //这里确定两个使原图达到内贴边、外贴边的放缩倍数，
        if (mBitmap.width / mBitmap.height.toFloat() > width / height.toFloat()) {
            mSmallScale = width / mBitmap.width.toFloat()
            mBigScale = height / mBitmap.height.toFloat()
        } else {
            mSmallScale = height / mBitmap.height.toFloat()
            mBigScale = width / mBitmap.width.toFloat()
        }
        //为了更好的展示拖动效果，这里再乘一个放大系数，使得上下左右都能够拖动
        mBigScale *= OVER_SCALE_FACTOR
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //乘以scaleFraction是为了在缩小时恢复到中点
        canvas.translate(mBitmapOffsetPoint.x * mScaleFraction, mBitmapOffsetPoint.y * mScaleFraction)
        //放缩
        val scale = mSmallScale + (mBigScale - mSmallScale) * mScaleFraction
        canvas.scale(scale, scale, width / 2f, height / 2f)
        canvas.drawBitmap(mBitmap, mBitmapStartPoint.x, mBitmapStartPoint.y, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun computeScroll() {
        //scroller用法
        if (mScroller.computeScrollOffset()) {
            mBitmapOffsetPoint.x = mScroller.currX.toFloat()
            mBitmapOffsetPoint.y = mScroller.currY.toFloat()
            invalidate()
        }
    }

    fun getScaleAnimator(): ObjectAnimator {
        if (!this::mScaleAnimator.isInitialized) {
            mScaleAnimator = ObjectAnimator.ofFloat(this, "mScaleFraction", 0f, 1f)
        }
        return mScaleAnimator
    }

    inner class MyGestureListener : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
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
            if (mIsBigScale) {
                mScroller.fling(
                    mBitmapOffsetPoint.x.toInt(), mBitmapOffsetPoint.y.toInt(), velocityX.toInt(), velocityY.toInt(),
                    (-(mBitmap.width * mBigScale - width) / 2).toInt(),
                    ((mBitmap.width * mBigScale - width) / 2).toInt(),
                    (-(mBitmap.height * mBigScale - height) / 2).toInt(),
                    ((mBitmap.height * mBigScale - height) / 2).toInt(), 100, 100
                )
                invalidate()
            }
            return false
        }

        override fun onScroll(down: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (mIsBigScale) {
                mBitmapOffsetPoint.x -= distanceX
                mBitmapOffsetPoint.x = Math.min(mBitmapOffsetPoint.x, (mBitmap.width * mBigScale - width) / 2)
                mBitmapOffsetPoint.x = Math.max(mBitmapOffsetPoint.x, -(mBitmap.width * mBigScale - width) / 2)
                mBitmapOffsetPoint.y -= distanceY
                mBitmapOffsetPoint.y = Math.min(mBitmapOffsetPoint.y, (mBitmap.height * mBigScale - height) / 2)
                mBitmapOffsetPoint.y = Math.max(mBitmapOffsetPoint.y, -(mBitmap.height * mBigScale - height) / 2)
                invalidate()
            }
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            //切换放大、缩小
            mIsBigScale = !mIsBigScale

            if (mIsBigScale) {
                //计算双击点偏移，使在双击的点在放大前后位于同一个点
                //双击点距离中心 - 放大后的这个点距离中心
                mBitmapOffsetPoint.x = (e.x - width / 2f) - (e.x - width / 2f) * mBigScale / mSmallScale
                mBitmapOffsetPoint.x = Math.min(mBitmapOffsetPoint.x, (mBitmap.width * mBigScale - width) / 2)
                mBitmapOffsetPoint.x = Math.max(mBitmapOffsetPoint.x, -(mBitmap.width * mBigScale - width) / 2)
                mBitmapOffsetPoint.y = (e.y - height / 2f) - (e.y - height / 2f) * mBigScale / mSmallScale
                mBitmapOffsetPoint.y = Math.min(mBitmapOffsetPoint.y, (mBitmap.height * mBigScale - height) / 2)
                mBitmapOffsetPoint.y = Math.max(mBitmapOffsetPoint.y, -(mBitmap.height * mBigScale - height) / 2)
                getScaleAnimator().start()
            } else {
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

    fun setBitmapOffsetPoint(x: Float, y: Float) {
        mBitmapOffsetPoint.x = x
        mBitmapOffsetPoint.x = Math.min(mBitmapOffsetPoint.x, (mBitmap.width * mBigScale - width) / 2)
        mBitmapOffsetPoint.x = Math.max(mBitmapOffsetPoint.x, -(mBitmap.width * mBigScale - width) / 2)
        mBitmapOffsetPoint.y = y
        mBitmapOffsetPoint.y = Math.min(mBitmapOffsetPoint.y, (mBitmap.height * mBigScale - height) / 2)
        mBitmapOffsetPoint.y = Math.max(mBitmapOffsetPoint.y, -(mBitmap.height * mBigScale - height) / 2)
    }

}