package com.lerendan.customviewsample.thumbup.practice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.lerendan.customviewsample.R;

/**
 * Created by danchao on 2020/1/8.
 */
public class MyThumbView extends View implements View.OnClickListener {
    //缩放大小的比例
    private static final float SCALE_MIN = 0.9f;
    private static final float SCALE_MAX = 1f;
    //缩放动画的时间
    private static final int SCALE_DURING = 100;

    private boolean mIsThumb;
    private Bitmap mThumbBitmap, mNotThumbBitmap;
    private Paint mBitmapPaint, mCirclePaint;
    private Point mBitmapPoint, mCircleCenterPoint;

    private int mSelectDrawableId, mUnSelectDrawableId;

    public MyThumbView(Context context) {
        this(context, null);
    }

    public MyThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyThumbView);
//        mSelectDrawableId = typedArray.getResourceId(R.styleable.MyThumbView_select_img, 0);
//        mUnSelectDrawableId = typedArray.getResourceId(R.styleable.MyThumbView_unselect_img, 0);
//        typedArray.recycle();
        init();
    }

    private void init() {
        mThumbBitmap = BitmapFactory.decodeResource(getResources(), mSelectDrawableId);
        mNotThumbBitmap = BitmapFactory.decodeResource(getResources(), mUnSelectDrawableId);
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBitmapPoint = new Point();
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int bitmapWidth = mThumbBitmap.getWidth();
        int bitmapHeight = mThumbBitmap.getHeight();
        //增加对padding的支持
        int contentWidth = bitmapWidth + getPaddingLeft() + getPaddingRight();
        int contentHeight = bitmapHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(getSize(widthMeasureSpec, contentWidth), getSize(heightMeasureSpec, contentHeight));

        mBitmapPoint.x = (getPaddingLeft() + getPaddingRight()) / 2;
        mBitmapPoint.y = (getPaddingTop() + getPaddingBottom()) / 2;
    }

    private int getSize(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                result = Math.max(defaultSize, specSize);
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsThumb) {
            //选中
            canvas.drawBitmap(mThumbBitmap, mBitmapPoint.x, mBitmapPoint.y, mBitmapPaint);
        } else {
            //反选
            canvas.drawBitmap(mNotThumbBitmap, mBitmapPoint.x, mBitmapPoint.y, mBitmapPaint);
        }
    }

    @Override
    public void onClick(View v) {
        if (mIsThumb) {
            //当前是选中状态
            startDownAnim();
        } else {
            startUpAnim();
        }
    }

    /**
     * 选中时的动画
     */
    private void startUpAnim() {
        //反选的图由大变小
        ObjectAnimator notThumbUpScale = ObjectAnimator.ofFloat(this, "notThumbScale", SCALE_MAX, SCALE_MIN);
        notThumbUpScale.setDuration(SCALE_DURING);
        notThumbUpScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsThumb = true;
            }
        });
        //选中的图由小变大
        ObjectAnimator thumbAnimator = ObjectAnimator.ofFloat(this, "thumbScale", SCALE_MIN, SCALE_MAX);
        thumbAnimator.setDuration(SCALE_DURING);
        thumbAnimator.setInterpolator(new OvershootInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(notThumbUpScale).after(thumbAnimator);
        animatorSet.start();
    }

    /**
     * 反选时的动画
     */
    private void startDownAnim() {
        ObjectAnimator thumbUpScale = ObjectAnimator.ofFloat(this, "thumbScale", SCALE_MIN, SCALE_MAX);
        thumbUpScale.setDuration(SCALE_DURING);
        thumbUpScale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsThumb = false;
                setNotThumbScale(SCALE_MAX);
            }
        });
        thumbUpScale.start();
    }

    public void setThumbScale(float thumbScale) {
        Matrix matrix = new Matrix();
        matrix.postScale(thumbScale, thumbScale);
        mThumbBitmap = BitmapFactory.decodeResource(getResources(), mSelectDrawableId);
        mThumbBitmap = Bitmap.createBitmap(mThumbBitmap, 0, 0, mThumbBitmap.getWidth(), mThumbBitmap.getHeight(),
                matrix, true);
        postInvalidate();
    }

    public void setNotThumbScale(float notThumbScale) {
        Matrix matrix = new Matrix();
        matrix.postScale(notThumbScale, notThumbScale);
        mNotThumbBitmap = BitmapFactory.decodeResource(getResources(), mUnSelectDrawableId);
        mNotThumbBitmap = Bitmap.createBitmap(mNotThumbBitmap, 0, 0, mNotThumbBitmap.getWidth(), mNotThumbBitmap.getHeight(),
                matrix, true);
        postInvalidate();
    }
}
