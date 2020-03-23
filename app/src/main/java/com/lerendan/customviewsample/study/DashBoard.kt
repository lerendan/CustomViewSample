package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.lerendan.customviewsample.Utils

/**
 * 仪表盘
 * Created by danchao on 2020/3/20.
 */
class DashBoard(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    val ANGLE = 120
    val RADIUS = Utils.dp2px(150)
    val LENGTH = Utils.dp2px(138)

    var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mDashPath: Path = Path()
    var mArcPath: Path = Path()
    lateinit var mPathEffect: PathDashPathEffect

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(this.javaClass.name, "onSizeChanged")

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = Utils.dp2px(2)
        mArcPath.addArc(
            width / 2 - RADIUS, getHeight() / 2 - RADIUS, getWidth() / 2 + RADIUS,
            getHeight() / 2 + RADIUS, 90 + ANGLE / 2f, 360f - ANGLE
        )
        mDashPath.addRect(0f, 0f, Utils.dp2px(2), Utils.dp2px(10), Path.Direction.CW)
        //Path测量工具
        val pathMeasure = PathMeasure(mArcPath, false)
        //pathMeasure.length - 2dp 是减去最后一个刻度所占宽度
        mPathEffect = PathDashPathEffect(
            mDashPath, (pathMeasure.length - Utils.dp2px(2)) / 20, 0f,
            PathDashPathEffect.Style.ROTATE
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //1、绘制弧线
        canvas.drawPath(mArcPath, mPaint)
        //2、绘制刻度
        mPaint.pathEffect = mPathEffect
        canvas.drawPath(mArcPath, mPaint)
        mPaint.pathEffect = null
        //3、绘制指针
        canvas.drawLine(
            width / 2f, height / 2f,
            (width / 2 + Math.cos(Math.toRadians(getAngleFromMark(5.5f))) * LENGTH).toFloat(),
            (height / 2 + Math.sin(Math.toRadians(getAngleFromMark(5.5f))) * LENGTH).toFloat(),
            mPaint
        )
    }

    fun getAngleFromMark(mark: Float): Double {
        return (90 + ANGLE / 2 + (360 - ANGLE) / 20 * mark).toDouble()
    }

}