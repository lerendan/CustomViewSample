package com.lerendan.customviewsample.study

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.lerendan.customviewsample.Utils

/**
 * 多指触控：各自为战型
 */
class MultiTouchView3(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaths = SparseArray<Path>()

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = Utils.dp2px(4)
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = event.actionIndex
                val pointerId = event.getPointerId(actionIndex)
                val path = Path()
                path.moveTo(event.getX(actionIndex), event.getY(actionIndex))
                mPaths.append(pointerId, path)
            }
            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val path = mPaths.get(event.getPointerId(i))
                    path.lineTo(event.getX(i), event.getY(i))
                }
                invalidate()
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP -> {
                //抬起手指时删除绘制
                val pointerId = event.getPointerId(event.actionIndex)
                mPaths.remove(pointerId)
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until mPaths.size()) {
            canvas.drawPath(mPaths.valueAt(i), mPaint)
        }
    }
}