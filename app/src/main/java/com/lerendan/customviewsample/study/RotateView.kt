package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.lerendan.customviewsample.R
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/24.
 */
class RotateView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val BITMAP_WIDTH = Utils.dp2px(100)

    var camera = Camera()
    var mBitmap: Bitmap
    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.avatar, BITMAP_WIDTH.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        camera.save() // 保存 Camera 的状态
        camera.rotateX(30f) // 旋转 Camera 的三维空间
        canvas.translate(Utils.dp2px(150), Utils.dp2px(150)) // 旋转之后把投影移动回来
        camera.applyToCanvas(canvas) // 把旋转投影到 Canvas
        canvas.translate(-Utils.dp2px(150), -Utils.dp2px(150)) // 旋转之前把绘制内容移动到轴心（原点）
        camera.restore() // 恢复 Camera 的状态

        canvas.drawBitmap(mBitmap, Utils.dp2px(100), Utils.dp2px(100), mPaint)
        canvas.restore()
    }
}