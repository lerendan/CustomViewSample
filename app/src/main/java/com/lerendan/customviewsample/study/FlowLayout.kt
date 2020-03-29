package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by danchao on 2020/3/27.
 */
class FlowLayout(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    //子view的rect
    var childrenBounds = ArrayList<Rect>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)

        //已使用宽度
        var widthUsed = 0
        //已使用高度
        var heightUsed = 0
        //当前行width已使用的宽度
        var lineWidthUsed = 0
        //当前行view的高度最大值
        var lineMaxHeight = 0

        for (i in 0 until childCount) {
            //获取子view
            val childView = getChildAt(i)
            //测量子view，因为要换行，所以这里的widthUsed不传入，我们下面自己计算
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0)

            //换行逻辑
            if (widthSpecMode != MeasureSpec.UNSPECIFIED && lineWidthUsed + childView.measuredWidth > widthSpecSize) {
                lineWidthUsed = 0
                heightUsed += lineMaxHeight
                lineMaxHeight = 0
                measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, heightUsed)
            }

            //避免重复创建
            var childBound: Rect
            if (childrenBounds.size <= i) {
                childBound = Rect()
                childrenBounds.add(childBound)
            } else {
                childBound = childrenBounds[i]
            }

            //设置rect边界
            childBound.set(
                lineWidthUsed, heightUsed, lineWidthUsed + childView.measuredWidth,
                heightUsed + childView.measuredHeight
            )

            //当前行使用宽度加上当前childView的测量宽度
            lineWidthUsed += childView.measuredWidth
            //计算最大的宽度
            widthUsed = Math.max(widthUsed, lineWidthUsed)
            //当前行childView的最大高度
            lineMaxHeight = Math.max(lineMaxHeight, childView.measuredHeight)

        }
        val width = widthUsed
        //viewGroup的使用高度要加上最后一行的最大高度
        val height = heightUsed + lineMaxHeight
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
