package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.lerendan.customviewsample.R
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/22.
 */
class ImageTextView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val BITMAP_WIDTH = Utils.dp2px(100)
    val TEXT = "广东省广州市深圳市 Android123广东省广州市深圳市 Android123广东省广州市深圳市 Android123广东省广州市深圳市 Android123" +
            "广东省广州市深圳市 Android123广东省广州市深圳市 Android123广东省广州市深圳市 Android123广东省广州市深圳市 Android123" +
            "广东省广州市深圳市 Android123广东省广州市深圳市 Android123广东省广州市深圳市 Android123广东省广州市深圳市 Android123"

    var mPaint = TextPaint()
    var mBitmap: Bitmap
    var cutWidth = FloatArray(1)

    init {
        mPaint.textSize = Utils.dp2px(16)
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, BITMAP_WIDTH.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(mBitmap, width - BITMAP_WIDTH, 100f, mPaint)
        //第一行
        var index = mPaint.breakText(TEXT, true, width.toFloat(), cutWidth)
        canvas.drawText(TEXT, 0, index, 0f, 50f, mPaint)
        //第二行
        var oldIndex = index
        index = mPaint.breakText(TEXT, index, TEXT.length, true, width.toFloat(), cutWidth)
        canvas.drawText(TEXT, oldIndex, oldIndex + index, 0f, 50f + mPaint.fontSpacing, mPaint)
        //第三行
        oldIndex = index + oldIndex
        index = mPaint.breakText(TEXT, index, TEXT.length, true, width - BITMAP_WIDTH, cutWidth)
        canvas.drawText(TEXT, oldIndex, oldIndex + index, 0f, 50f + mPaint.fontSpacing * 2, mPaint)
    }

}