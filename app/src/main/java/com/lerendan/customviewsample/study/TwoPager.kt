package com.lerendan.customviewsample.study

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.OverScroller

/**
 * Created by danchao on 2020/4/5.
 */
class TwoPager(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private var mDownX = 0f
    private var mDownY = 0f
    private var mDownScrollX = 0
    private var mScrolling = false

    private var mMinVelocity = 0
    private var mMaxVelocity = 0

    private var mOverScroller: OverScroller = OverScroller(context)
    private var mViewConfiguration: ViewConfiguration = ViewConfiguration.get(context)
    private var mVelocityTracker = VelocityTracker.obtain()

    init {
        mMaxVelocity = mViewConfiguration.scaledMaximumFlingVelocity
        mMinVelocity = mViewConfiguration.scaledMinimumFlingVelocity
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = 0
        val childTop = 0
        var childRight = width
        val childBottom = height
        for (i in 0 until childCount) {
            //从左至右排列
            val child = getChildAt(i)
            child.layout(childLeft, childTop, childRight, childBottom)
            childLeft += width
            childRight += width
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            mVelocityTracker.clear()
        }
        mVelocityTracker.addMovement(ev)

        var result = false
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mScrolling = false
                mDownX = ev.x
                mDownY = ev.y
                mDownScrollX = scrollX
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = mDownX - ev.x
                if (!mScrolling) {
                    if (Math.abs(dx) > mViewConfiguration.scaledPagingTouchSlop) {
                        mScrolling = true
                        parent.requestDisallowInterceptTouchEvent(true)
                        result = true
                    }
                }

            }
        }
        return result
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            mVelocityTracker.clear()
        }
        mVelocityTracker.addMovement(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                mDownScrollX = scrollX
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = (mDownX - event.x + mDownScrollX).toInt()
                if (dx > width) {
                    dx = width
                } else if (dx < 0) {
                    dx = 0
                }
                scrollTo(dx, 0)


            }
            MotionEvent.ACTION_UP -> {
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity.toFloat())
                var vx = mVelocityTracker.xVelocity
                var scrollX = scrollX

                val targetPage = if (Math.abs(vx) < mMinVelocity) {
                    if (scrollX > width / 2) 1 else 0
                } else {
                    if (vx < 0) 1 else 0
                }

                var scrollDistance = if (targetPage == 1) width - scrollX else -scrollX
                mOverScroller.startScroll(getScrollX(), 0, scrollDistance, 0)
                invalidate()

            }

        }
        return true
    }

    override fun computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            scrollTo(mOverScroller.currX, mOverScroller.currY)
            invalidate()
        }
    }


}