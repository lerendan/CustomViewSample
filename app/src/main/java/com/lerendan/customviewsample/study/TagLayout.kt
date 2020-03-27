package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by danchao on 2020/3/27.
 */
class TagLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    var childrenBounds = ArrayList<Rect>()

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var widthUsed = 0
        var heightUsed = 0
        var lineWidthUsed = 0
        var lineMaxHeight = 0

        var specMode = MeasureSpec.getMode(widthMeasureSpec)
        var specWidth = MeasureSpec.getSize(widthMeasureSpec)

        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)

            if (specMode != MeasureSpec.UNSPECIFIED &&
                lineWidthUsed + childView.measuredWidth > specWidth) {
                lineWidthUsed = 0
                heightUsed += lineMaxHeight
                lineMaxHeight = 0
                measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)
            }

            var childBound: Rect
            if (childrenBounds.size <= i) {
                childBound = Rect()
                childrenBounds.add(childBound)
            } else {
                childBound = childrenBounds[i]
            }

            childBound.set(
                lineWidthUsed, heightUsed,
                lineWidthUsed + childView.measuredWidth, heightUsed + childView.measuredHeight
            )

            lineWidthUsed += childView.measuredWidth
            widthUsed = Math.max(widthUsed, lineWidthUsed)
            lineMaxHeight = Math.max(lineMaxHeight, childView.measuredHeight)

        }
        var width = widthUsed
        var height = heightUsed + lineMaxHeight
        setMeasuredDimension(width, height)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)

            val childBounds = childrenBounds[i]
            childView.layout(childBounds.left, childBounds.top, childBounds.right, childBounds.bottom)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}


//            var widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
//            var widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
//            //子view自己定义的宽高
//            var layoutParams = childView.layoutParams;
//
//            var childWidthSpecMode: Int
//            var chileWidthSpecSize: Int
//            when (layoutParams.width) {
//                LayoutParams.MATCH_PARENT -> {
//
//                    when (widthSpecMode) {
//                        MeasureSpec.EXACTLY, MeasureSpec.AT_MOST -> {
//                            childWidthSpecMode = MeasureSpec.EXACTLY
//                            chileWidthSpecSize = widthSpecSize - usedWidth
//                        }
//                        MeasureSpec.UNSPECIFIED -> {
//                            childWidthSpecMode = MeasureSpec.UNSPECIFIED
//                            chileWidthSpecSize = 0
//                        }
//                    }
//
//
//                }
//                LayoutParams.WRAP_CONTENT -> {
//
//                }
//
//                else -> {
//                }
//            }
