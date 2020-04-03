package com.lerendan.customviewsample.study

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
    companion object {
        //原图大小
        val IMAGE_WIDTH = Utils.dp2px(300)
        //放大系数
        const val OVER_SCALE_FACTOR = 2
    }

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBitmap: Bitmap
    //图片绘制起点
    private var mBitmapStartPoint = PointF()
    //canvas当前偏移
    private var mCanvasOffsetPoint = PointF()
    //canvas负向偏移最小值
    private var mCanvasMinOffsetPoint = PointF()
    //canvas正向偏移最大值
    private var mCanvasMaxOffsetPoint = PointF()
    //是否是大图
    private var mIsBigScale = false
    //达到内贴边效果的放缩倍数，即小图
    private var mSmallScale = 0f
    //达到外贴边效果的放缩倍数，即大图
    private var mBigScale = 0f
    private var mScroller: OverScroller
    private var mGestureDetector: GestureDetectorCompat
    private var mGestureListener = MyGestureListener()
    //属性动画
    private lateinit var mScaleAnimator: ObjectAnimator

    private var mScaleFraction = 0f
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
        //canvas负向偏移最小值
        mCanvasMinOffsetPoint.x = -(mBitmap.width * mBigScale - width) / 2
        mCanvasMinOffsetPoint.y = -(mBitmap.height * mBigScale - height) / 2
        //canvas正向偏移最大值
        mCanvasMaxOffsetPoint.x = (mBitmap.width * mBigScale - width) / 2
        mCanvasMaxOffsetPoint.y = (mBitmap.height * mBigScale - height) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //乘以scaleFraction是为了在缩小时恢复到中点
        canvas.translate(mCanvasOffsetPoint.x * mScaleFraction, mCanvasOffsetPoint.y * mScaleFraction)
        //放缩倍数
        val scale = mSmallScale + (mBigScale - mSmallScale) * mScaleFraction
        canvas.scale(scale, scale, width / 2f, height / 2f)
        canvas.drawBitmap(mBitmap, mBitmapStartPoint.x, mBitmapStartPoint.y, mPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun computeScroll() {
        //scroller用法
        if (mScroller.computeScrollOffset()) {
            mCanvasOffsetPoint.x = mScroller.currX.toFloat()
            mCanvasOffsetPoint.y = mScroller.currY.toFloat()
            invalidate()
        }
    }

    /**
     * 获取动画
     */
    fun getScaleAnimator(): ObjectAnimator {
        if (!this::mScaleAnimator.isInitialized) {
            mScaleAnimator = ObjectAnimator.ofFloat(this, "mScaleFraction", 0f, 1f)
        }
        return mScaleAnimator
    }

    /**
     * 设置图片偏移并检查边界
     */
    fun setBitmapOffsetPoint(x: Float, y: Float) {
        mCanvasOffsetPoint.x = x
        mCanvasOffsetPoint.x = Math.min(mCanvasOffsetPoint.x, mCanvasMaxOffsetPoint.x)
        mCanvasOffsetPoint.x = Math.max(mCanvasOffsetPoint.x, mCanvasMinOffsetPoint.x)
        mCanvasOffsetPoint.y = y
        mCanvasOffsetPoint.y = Math.min(mCanvasOffsetPoint.y, mCanvasMaxOffsetPoint.y)
        mCanvasOffsetPoint.y = Math.max(mCanvasOffsetPoint.y, mCanvasMinOffsetPoint.y)
    }

    inner class MyGestureListener : GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        override fun onShowPress(e: MotionEvent?) {
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            //手指按下时如果还在滚动则停止滚动
            if (!mScroller.isFinished) {
                mScroller.abortAnimation()
            }
            //down事件，必须返回true，否则后面的事件就收不到了
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            //大图模式才可以弹性滑动
            if (mIsBigScale) {
                mScroller.fling(
                    mCanvasOffsetPoint.x.toInt(), mCanvasOffsetPoint.y.toInt(), velocityX.toInt(), velocityY.toInt(),
                    mCanvasMinOffsetPoint.x.toInt(), mCanvasMaxOffsetPoint.x.toInt(),
                    mCanvasMinOffsetPoint.y.toInt(), mCanvasMaxOffsetPoint.y.toInt(),
                    100, 100
                )
                invalidate()
            }
            return false
        }

        override fun onScroll(down: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            //大图模式时才可以滑动
            if (mIsBigScale) {
                //限制滚动边界
                setBitmapOffsetPoint(mCanvasOffsetPoint.x - distanceX, mCanvasOffsetPoint.y - distanceY)
                invalidate()
            }
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            //双击切换放大、缩小
            mIsBigScale = !mIsBigScale
            if (mIsBigScale) {
                //计算双击点偏移，使双击的点在放大前后位于同一个点
                //双击点距离中心 - 放大后的这个点距离中心
                setBitmapOffsetPoint(
                    (e.x - width / 2f) - (e.x - width / 2f) * mBigScale / mSmallScale,
                    (e.y - height / 2f) - (e.y - height / 2f) * mBigScale / mSmallScale
                )
                //正向动画
                getScaleAnimator().start()
            } else {
                //反向动画
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

}