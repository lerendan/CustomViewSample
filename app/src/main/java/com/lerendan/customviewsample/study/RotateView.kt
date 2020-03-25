package com.lerendan.customviewsample.study

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.lerendan.customviewsample.R
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/24.
 */
class RotateView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val BITMAP_WIDTH = Utils.dp2px(200)

    var mCamera = Camera()
    var mBitmap: Bitmap
    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var mBitmapStartPoint = PointF()//绘制图片的起点（左上角）
    var mBitmapCenterPoint = PointF()//绘制图片的中心点

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.flip, BITMAP_WIDTH.toInt())
        mCamera.setLocation(0f, 0f, Utils.getZForCamera())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmapStartPoint.x = width / 2 - BITMAP_WIDTH / 2
        mBitmapStartPoint.y = height / 2 - BITMAP_WIDTH / 2
        mBitmapCenterPoint.x = width / 2f
        mBitmapCenterPoint.y = height / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //1 将图片绘制在中间
//        canvas.drawBitmap(mBitmap, mBitmapCenterPoint.x, mBitmapCenterPoint.y, mPaint)

        //2 将图片绘制切割成两部分，右边部分进行camera翻转投影
        canvas.save()

        mCamera.save()
        mCamera.rotateY(-30f)
        canvas.translate(mBitmapCenterPoint.x, mBitmapCenterPoint.y)
        mCamera.applyToCanvas(canvas)
        canvas.translate(-mBitmapCenterPoint.x, -mBitmapCenterPoint.y)
        mCamera.restore()

        canvas.clipRect(
            mBitmapCenterPoint.x.toInt(),
            mBitmapStartPoint.y.toInt(),
            (mBitmapStartPoint.x + BITMAP_WIDTH).toInt(),
            (mBitmapStartPoint.y + BITMAP_WIDTH).toInt()
        )
        canvas.drawBitmap(mBitmap, mBitmapStartPoint.x, mBitmapStartPoint.y, mPaint)
        canvas.restore()


//        canvas.save()
//
//        mCamera.save() // 保存 Camera 的状态
//        mCamera.rotateX(30f) // 旋转 Camera 的三维空间
//        canvas.translate(Utils.dp2px(150), Utils.dp2px(150)) // 旋转之后把投影移动回来
//        mCamera.applyToCanvas(canvas) // 把旋转投影到 Canvas
//        canvas.translate(-Utils.dp2px(150), -Utils.dp2px(150)) // 旋转之前把绘制内容移动到轴心（原点）
//        mCamera.restore() // 恢复 Camera 的状态

//        canvas.restore()
    }
}