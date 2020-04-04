package com.lerendan.customviewsample.study

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lerendan.customviewsample.R
import com.lerendan.customviewsample.Utils

/**
 * 多指触控：接力形
 */
class MultiTouchView1(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        val IMAGE_WIDTH = Utils.dp2px(200)
    }

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mBitmap: Bitmap

    //手指按下时的坐标
    private var mDownPoint = PointF()
    //canvas偏移坐标
    private var mCanvasOffsetPoint = PointF()
    //canvas上一次偏移坐标
    private var mCanvasLastOffsetPoint = PointF()
    //当前正在监控的手指
    private var mTrackingPointerId = 0

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, IMAGE_WIDTH.toInt())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                setTrackingPointer(event.actionIndex, event)
            }
            MotionEvent.ACTION_MOVE -> {
                val index = event.findPointerIndex(mTrackingPointerId)
                mCanvasOffsetPoint.x = mCanvasLastOffsetPoint.x + event.getX(index) - mDownPoint.x
                mCanvasOffsetPoint.y = mCanvasLastOffsetPoint.y + event.getY(index) - mDownPoint.y
                invalidate()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                //抬起手指时切换监控的手指

                //当前抬起的手指index
                val actionIndex = event.actionIndex
                //当前抬起的手指id
                val pointerId = event.getPointerId(actionIndex)
                //看抬起的手指是否是当前正在监控的手指
                if (pointerId == mTrackingPointerId) {
                    //判断抬起的手指是否是最后一个,并将当前追踪点设置为最后一个index对应的point
                    val newIndex =
                        if (actionIndex == event.pointerCount - 1) event.pointerCount - 2 else event.pointerCount - 1
                    setTrackingPointer(newIndex, event)
                }
            }
        }
        return true
    }

    /**
     * 设置当前追踪的 Pointer
     */
    private fun setTrackingPointer(newPointIndex: Int, event: MotionEvent) {
        mTrackingPointerId = event.getPointerId(newPointIndex)
        mDownPoint.x = event.getX(newPointIndex)
        mDownPoint.y = event.getY(newPointIndex)
        mCanvasLastOffsetPoint.x = mCanvasOffsetPoint.x
        mCanvasLastOffsetPoint.y = mCanvasOffsetPoint.y
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap, mCanvasOffsetPoint.x, mCanvasOffsetPoint.y, mPaint)
    }
}