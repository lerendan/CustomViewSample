package com.lerendan.customviewsample.study

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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

    var mCenterPoint = PointF()//绘制图片的中心点

    var leftFlip = 0f
        set(value) {
            field = value
            invalidate()
        }
    var rightFlip = 0f
        set(value) {
            field = value
            invalidate()
        }
    var flipRotation = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        mBitmap = Utils.decodeBitmap(resources, R.drawable.flip, BITMAP_WIDTH.toInt())
        mCamera.setLocation(0f, 0f, Utils.getZForCamera())

        //动画一：右边翻转
        val rightFlipAnimator = ObjectAnimator.ofFloat(this, "rightFlip", -45f)
        rightFlipAnimator.duration = 1000

        //动画二：旋转
        val flipRotationAnimator = ObjectAnimator.ofFloat(this, "flipRotation", 270f)
        flipRotationAnimator.duration = 1000

        //动画三：左边翻转
        val leftFlitAnimator = ObjectAnimator.ofFloat(this, "leftFlip", 45f)
        leftFlitAnimator.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(rightFlipAnimator, flipRotationAnimator, leftFlitAnimator)
        animatorSet.startDelay = 1000
        animatorSet.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterPoint.x = width / 2f
        mCenterPoint.y = height / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制左边
        canvas.save()
        canvas.translate(mCenterPoint.x, mCenterPoint.y)
        canvas.rotate(-flipRotation)
        mCamera.save()
        mCamera.rotateY(leftFlip)
        mCamera.applyToCanvas(canvas)
        mCamera.restore()
        canvas.clipRect(-BITMAP_WIDTH.toInt(), -BITMAP_WIDTH.toInt(), 0, BITMAP_WIDTH.toInt())
        canvas.rotate(flipRotation)
        canvas.translate(-mCenterPoint.x, -mCenterPoint.y)
        canvas.drawBitmap(mBitmap, mCenterPoint.x - BITMAP_WIDTH / 2, mCenterPoint.y - BITMAP_WIDTH / 2, mPaint)
        canvas.restore()

        //绘制右边
        canvas.save()
        canvas.translate(mCenterPoint.x, mCenterPoint.y)
        canvas.rotate(-flipRotation)
        mCamera.save()
        mCamera.rotateY(rightFlip)
        mCamera.applyToCanvas(canvas)
        mCamera.restore()
        canvas.clipRect(0, BITMAP_WIDTH.toInt(), BITMAP_WIDTH.toInt(), -BITMAP_WIDTH.toInt())
        canvas.rotate(flipRotation)
        canvas.translate(-mCenterPoint.x, -mCenterPoint.y)
        canvas.drawBitmap(mBitmap, mCenterPoint.x - BITMAP_WIDTH / 2, mCenterPoint.y - BITMAP_WIDTH / 2, mPaint)
        canvas.restore()
    }
}