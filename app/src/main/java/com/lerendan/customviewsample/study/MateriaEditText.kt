package com.lerendan.customviewsample.study

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/26.
 */
class MateriaEditText(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet) {

    //字体大小
    val TEXT_SIZE = Utils.dp2px(12)
    //离输入框的距离
    val TEXT_MARGIN = Utils.dp2px(8)
    //垂直偏移
    val TEXT_VERTICAL_OFFSET = Utils.dp2px(20)
    //水平偏移
    val TEXT_HORIZENTAL_OFFSET = Utils.dp2px(4)
    //动画偏移
    val TEXT_ANIMATION_OFFSET = Utils.dp2px(16)

    //浮动文字是否显示
    var floatingTextShown = false
    //背景偏移
    var backgroundPadding = Rect()

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var objectAnimator: ObjectAnimator

    //动画进度
    var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        mPaint.textSize = Utils.dp2px(12)
        background.getPadding(backgroundPadding)
        setPadding(paddingLeft, (backgroundPadding.top + TEXT_SIZE + TEXT_MARGIN).toInt(), paddingRight, paddingBottom)

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (floatingTextShown && TextUtils.isEmpty(s)) {
                    floatingTextShown = false
                    getAnimator().reverse()
                } else if (!floatingTextShown && !TextUtils.isEmpty(s)) {
                    floatingTextShown = true
                    getAnimator().start()
                }
            }
        })
    }

    fun getAnimator(): ObjectAnimator {
        if (!this::objectAnimator.isInitialized) {
            objectAnimator = ObjectAnimator.ofFloat(this, "progress", 0f, 1f)
        }
        return objectAnimator
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mPaint.alpha = (0xff * progress).toInt()
        canvas.drawText(
            hint.toString(), TEXT_HORIZENTAL_OFFSET,
            TEXT_VERTICAL_OFFSET + TEXT_ANIMATION_OFFSET * (1 - progress), mPaint
        )
    }

}