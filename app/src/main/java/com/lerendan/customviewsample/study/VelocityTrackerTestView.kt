package com.lerendan.customviewsample.study

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration

/**
 * Created by danchao on 2020/4/4.
 */
class VelocityTrackerTestView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //1、创建实例
    private var mVelocityTracker = VelocityTracker.obtain()

    private var mViewConfiguration : ViewConfiguration = ViewConfiguration.get(context)
    private var mMaxFlingVelocity = 0
    //触发fling的速度
    private var mMinFlingVelocity = 0

    init {
        mMaxFlingVelocity = mViewConfiguration.scaledMaximumFlingVelocity
        mMinFlingVelocity = mViewConfiguration.scaledMinimumFlingVelocity
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //2、重置
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            mVelocityTracker.clear()
        }
        //3、开始追踪
        mVelocityTracker.addMovement(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //...
            }
            MotionEvent.ACTION_MOVE -> {
                //o...
            }
            MotionEvent.ACTION_UP -> {
                //速度 = （ 终点位置(px) - 起点位置(px) ）/ 时间段(ms)
                //4、设置时间段
                mVelocityTracker.computeCurrentVelocity(1000)
                //5、获取x方向、y方向的速度
                //其中getXVelocity、getYVelocity方法的参数是pointerId，用于多指触控。不考虑多指时，可以不用传参数
                var xVelocity = mVelocityTracker.getXVelocity(0)
                var yVelocity = mVelocityTracker.getYVelocity(0)
                //...
            }
        }
        return true
    }

    override fun onDetachedFromWindow() {
        //6、当不需要使用时，重置并回收内存
        mVelocityTracker.clear()
        mVelocityTracker.recycle()
        super.onDetachedFromWindow()
    }


}