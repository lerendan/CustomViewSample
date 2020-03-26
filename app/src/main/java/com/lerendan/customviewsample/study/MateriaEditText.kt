package com.lerendan.customviewsample.study

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import com.lerendan.customviewsample.Utils

/**
 * Created by danchao on 2020/3/26.
 */
class MateriaEditText : AppCompatEditText {


    val TEXT_SIZE = Utils.dp2px(12)
    val TEXT_MARGIN = Utils.dp2px(8)
    val TEXT_VERTICAL_OFFSET = Utils.dp2px(20)
    val TEXT_HORIZENTAL_OFFSET = Utils.dp2px(4)

    var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var progress: Float = 1f
        set(value) {
            field = value
            invalidate()
        }

    var floatingLabelShown = true

    lateinit var objectAnimator: ObjectAnimator


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    }

    init {
        mPaint.textSize = Utils.dp2px(12)
        setPadding(paddingLeft, (paddingTop + TEXT_SIZE + TEXT_MARGIN).toInt(), paddingRight, paddingBottom)

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (floatingLabelShown && TextUtils.isEmpty(s)) {
                    floatingLabelShown = false
                    getAnimator().reverse()
                } else if(!floatingLabelShown && !TextUtils.isEmpty(s)){
                    floatingLabelShown = true
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
        canvas.drawText(hint.toString(), TEXT_HORIZENTAL_OFFSET, TEXT_VERTICAL_OFFSET, mPaint)


    }

}