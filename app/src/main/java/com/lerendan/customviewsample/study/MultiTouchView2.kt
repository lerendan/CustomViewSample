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
    //所有pointer的焦点(中心点)
    private var mPointerFocusPoint = PointF()
    //pointer数量
    private var mPointerCount = 0

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, IMAGE_WIDTH.toInt())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //pointer数量
        mPointerCount = event.pointerCount
        //所有pointer的x、y总和
        var sumX = 0f
        var sumY = 0f
        //是否是pointer_up事件
        val isPointerUp = event.actionMasked == MotionEvent.ACTION_POINTER_UP

        for (i in 0 until mPointerCount) {
            //抬起的那个pointer不用计算
            if (!(isPointerUp && i == event.actionIndex)) {
                sumX += event.getX(i)
                sumY += event.getY(i)
            }
        }

        if (isPointerUp) {
            //如果是pointer_up抬起事件，则pointer总数量-1
            mPointerCount -= 1
        }
        //计算焦点
        mPointerFocusPoint.x = sumX / mPointerCount
        mPointerFocusPoint.y = sumY / mPointerCount

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                mDownPoint.x = mPointerFocusPoint.x
                mDownPoint.y = mPointerFocusPoint.y
                mCanvasLastOffsetPoint.x = mCanvasOffsetPoint.x
                mCanvasLastOffsetPoint.y = mCanvasOffsetPoint.y
            }
            MotionEvent.ACTION_MOVE -> {
                mCanvasOffsetPoint.x = mCanvasLastOffsetPoint.x + mPointerFocusPoint.x - mDownPoint.x
                mCanvasOffsetPoint.y = mCanvasLastOffsetPoint.y + mPointerFocusPoint.y - mDownPoint.y
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(mBitmap, mCanvasOffsetPoint.x, mCanvasOffsetPoint.y, mPaint)
    }
}