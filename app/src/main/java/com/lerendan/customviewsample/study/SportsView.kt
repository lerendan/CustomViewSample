package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/22.
 */
class SportsView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val RADIUS = Utils.dp2px(150)
    val RING_WIDTH = Utils.dp2px(20)
    val CIRCLE_COLOR = Color.parseColor("#9C9C9C")
    val HIGHT_LIGHT_COLOE = Color.parseColor("#D81B60")
    val CENTER_TEXT = "top1"

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mCenterTextBound:Rect = Rect()
    var mFontMetrics : Paint.FontMetrics

    init {

        mPaint.strokeWidth = RING_WIDTH
        mPaint.textSize = Utils.dp2px(100)
        //文字横向居中
        mPaint.textAlign = Paint.Align.CENTER
        mFontMetrics = mPaint.fontMetrics
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //圆环
        mPaint.style = Paint.Style.STROKE
        mPaint.color = CIRCLE_COLOR
        canvas.drawCircle(width / 2f, height / 2f, RADIUS, mPaint)
        //圆弧
        mPaint.color = HIGHT_LIGHT_COLOE
        mPaint.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(
            width / 2f - RADIUS, height / 2f - RADIUS, width / 2f + RADIUS, height / 2f + RADIUS,
            -90f, 225f, false, mPaint
        )
        mPaint.style = Paint.Style.FILL

        //文字纵向居中
        //方式1：getTextBounds,缺点是文字动态变化时，文字会跳动
//        mPaint.getTextBounds(CENTER_TEXT,0,CENTER_TEXT.length,mCenterTextBound)
//        var offset = (mCenterTextBound.bottom + mCenterTextBound.top)/2
//        canvas.drawText(CENTER_TEXT,width/2f,height/2f - offset,mPaint)
        //方式2
        var offset = (mFontMetrics.ascent + mFontMetrics.descent)/2
        canvas.drawText(CENTER_TEXT,width/2f,height/2f - offset,mPaint)
    }
}