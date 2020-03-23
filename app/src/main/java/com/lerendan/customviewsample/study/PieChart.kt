package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.lerendan.customviewsample.Utils
import java.util.jar.Attributes

/**
 * Created by danchao on 2020/3/21.
 */
class PieChart(context: Context, attributes: AttributeSet) : View(context, attributes) {
    val RADIUS = Utils.dp2px(150)
    val OUT_LENGTH = Utils.dp2px(20)
    val PULL_OUT_INDEX = 2

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mBounds = RectF();
    var angles = arrayOf(60f, 100f, 120f, 80f)
    var colors = arrayOf(
        Color.parseColor("#2979FF"), Color.parseColor("#C2185B"),
        Color.parseColor("#009688"), Color.parseColor("#FF8F00")
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBounds.set(width / 2 - RADIUS, height / 2 - RADIUS, width / 2 + RADIUS, height / 2 + RADIUS);
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        var curStartAngle = 0f
        for (i in angles.indices) {
            mPaint.color = colors[i]
            canvas.save()
            if(i == PULL_OUT_INDEX){
                canvas.translate(
                    (Math.cos(Math.toRadians((curStartAngle + angles[i]/2).toDouble()))* OUT_LENGTH).toFloat(),
                    (Math.sin(Math.toRadians((curStartAngle + angles[i]/2).toDouble()))* OUT_LENGTH).toFloat()
                )
            }
            canvas.drawArc(mBounds, curStartAngle, angles[i], true, mPaint)
            canvas.restore()
            curStartAngle += angles[i]
        }
    }

}