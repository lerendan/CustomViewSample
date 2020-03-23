package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lerendan.customviewsample.R
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/22.
 */
class AvaterView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val WIDTH = Utils.dp2px(300)
    val PADDING = Utils.dp2px(50)
    val EDGE_WIDTH = Utils.dp2px(10)

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mBitmap: Bitmap
    var mXfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, WIDTH.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制外圆
        canvas.drawOval(PADDING, PADDING, PADDING + WIDTH, PADDING + WIDTH, mPaint)
        //离屏缓冲
        val saved = canvas.saveLayer(PADDING, PADDING, PADDING + WIDTH, PADDING + WIDTH, mPaint)
        canvas.drawOval(
            PADDING + EDGE_WIDTH, PADDING + EDGE_WIDTH,
            PADDING + WIDTH - EDGE_WIDTH, PADDING + WIDTH - EDGE_WIDTH, mPaint
        )
        mPaint.xfermode = mXfermode
        canvas.drawBitmap(mBitmap, PADDING, PADDING, mPaint)
        mPaint.xfermode = null
        canvas.restoreToCount(saved)
    }

}