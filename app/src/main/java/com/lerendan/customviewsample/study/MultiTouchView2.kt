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
 * 多指触控：协作型
 * 忽略个体，只看整体的焦点
 * Created by danchao on 2020/4/3.
 */
class MultiTouchView2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

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

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, IMAGE_WIDTH.toInt())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        var sumX = 0f
        var sumY = 0f
        var pointerCount = event.pointerCount

        var isPointerUp = event.actionMasked == MotionEvent.ACTION_POINTER_UP

        for (i in 0 until pointerCount) {
            if (!(isPointerUp && i == event.actionIndex)) {
                sumX += event.getX(i)
                sumY += event.getY(i)
            }
        }

        if(isPointerUp){
            pointerCount -= 1
        }
        var focusX = sumX / pointerCount
        var focusY = sumY / pointerCount

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                mDownPoint.x = focusX
                mDownPoint.y = focusY
                mCanvasLastOffsetPoint.x = mCanvasOffsetPoint.x
                mCanvasLastOffsetPoint.y = mCanvasOffsetPoint.y
            }
            MotionEvent.ACTION_MOVE -> {
                mCanvasOffsetPoint.x = mCanvasLastOffsetPoint.x + focusX - mDownPoint.x
                mCanvasOffsetPoint.y = mCanvasLastOffsetPoint.y + focusY - mDownPoint.y
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap, mCanvasOffsetPoint.x, mCanvasOffsetPoint.y, mPaint)
    }
}