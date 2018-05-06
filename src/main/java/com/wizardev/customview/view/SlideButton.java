package com.wizardev.customview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.wizardev.customview.R;

public class SlideButton extends View {

    private String mSnakeBarBottomText;
    private int mSnakeBarTextColor;
    private int mSnakeBarTextSize;
    private String mSnakeBarTopText;
    private int mInnerTextColor;
    private int mInnerTextSize;
    private int mSnackColor;
    private int mSnakeRadius;
    private int mRingLineSize;
    private Path mPath;
    private RectF mRightRectF;
    private Paint mRoundPaint;
    private float mAfterX;
    private Paint mTextPaint;
    private int mResultWidth;
    private int mResultHeight;
    private Context mContext;
    private RectF mLeftRectF;
    private int mRingLineColor;
    private Paint mRingPaint;
    private int mResultRadius;
    private OnSlideCallback mSlideCallback;
    private String mInnerText;
    private Paint mInnerTextPaint;
    private Bitmap mSuccessedDrawable;

    private Paint mBitPaint;

    private SlideState mState;


    public SlideButton(Context context) {
        this(context, null);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideButton, 0, defStyleAttr);

        mSnakeRadius = typedArray.getDimensionPixelSize(R.styleable.SlideButton_snakeRadius, 15);
        mRingLineSize = typedArray.getDimensionPixelSize(R.styleable.SlideButton_ringLineSize, 1);
        mRingLineColor = typedArray.getColor(R.styleable.SlideButton_ringLineColor, context.getResources().getColor(R.color.colorPrimary));
        mSnackColor = typedArray.getColor(R.styleable.SlideButton_snakeColor, context.getResources().getColor(R.color.colorAccent));
        mInnerText = typedArray.getString(R.styleable.SlideButton_innerText);
        mInnerTextSize = typedArray.getDimensionPixelSize(R.styleable.SlideButton_innerTextSize, 14);

        mInnerTextColor = typedArray.getColor(R.styleable.SlideButton_innerTextColor, context.getResources().getColor(R.color.colorAccent));

        mSnakeBarTopText = typedArray.getString(R.styleable.SlideButton_snakeBarTopText);
        mSnakeBarBottomText = typedArray.getString(R.styleable.SlideButton_snakeBarBottomText);
        mSnakeBarTextSize = typedArray.getDimensionPixelSize(R.styleable.SlideButton_snakeBarTextSize, 14);

        mSnakeBarTextColor = typedArray.getColor(R.styleable.SlideButton_snakeBarTextColor, context.getResources().getColor(R.color.colorAccent));


        typedArray.recycle();

        mContext = context;
        init();
    }

    private void init() {
        mPath = new Path();
        mTextPaint = new Paint();
        mRoundPaint = new Paint();
        mRingPaint = new Paint();
        mInnerTextPaint = new Paint();

        mInnerTextPaint.setColor(mInnerTextColor);
        mInnerTextPaint.setTextAlign(Paint.Align.CENTER);
        mInnerTextPaint.setTextSize(mInnerTextSize);


        mTextPaint.setColor(mSnakeBarTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mSnakeBarTextSize);

        mRingPaint.setColor(mRingLineColor);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mRingLineSize);


        mRoundPaint.setAntiAlias(true);
        mRoundPaint.setColor(mSnackColor);
        mRoundPaint.setStyle(Paint.Style.FILL);

        mResultRadius = mSnakeRadius - mRingLineSize;

        mSuccessedDrawable = ((BitmapDrawable) (mContext.getResources().getDrawable(R.mipmap.grey))).getBitmap();

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);

        mState = SlideState.UN_FINISH;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLeftRectF = new RectF(mRingLineSize, mRingLineSize, mSnakeRadius * 2, mSnakeRadius * 2);
        mRightRectF = new RectF(mResultWidth - 2 * mSnakeRadius - mRingLineSize, mRingLineSize, mResultWidth - mRingLineSize, mSnakeRadius * 2);
//        mPath.reset();
//        canvas.drawColor(Color.YELLOW);
        //画左边圆环
        mPath.addArc(mLeftRectF, 90, 180);
        //画上方的直线
        mPath.moveTo(mSnakeRadius, mRingLineSize);
        mPath.lineTo(mResultWidth - mSnakeRadius, mRingLineSize);
        //画右边圆环
        mPath.addArc(mRightRectF, -90, 180);
        //画下方的直线
        mPath.lineTo(mSnakeRadius, 2 * mSnakeRadius);
        canvas.drawPath(mPath, mRingPaint);

        Paint.FontMetrics fontMetrics = mInnerTextPaint.getFontMetrics();
        //画圆环内的文字
        float baseline = mResultHeight / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        canvas.drawText(mInnerText, mResultWidth / 2, baseline, mInnerTextPaint);
        Paint.FontMetrics snakeFontMetrics;
        float snakeBaselineTop;
        float snakeBaselineBottom;
        snakeFontMetrics = mTextPaint.getFontMetrics();
        snakeBaselineTop = mResultHeight / 2 - snakeFontMetrics.descent;
        snakeBaselineBottom = mResultHeight / 2 - snakeFontMetrics.ascent;
        if (mAfterX == 0) {
            canvas.drawCircle(mSnakeRadius + mRingLineSize / 2, mResultHeight / 2, mResultRadius, mRoundPaint);
            canvas.drawText(mSnakeBarTopText, mSnakeRadius, snakeBaselineTop, mTextPaint);
            canvas.drawText(mSnakeBarBottomText, mSnakeRadius, snakeBaselineBottom, mTextPaint);
        } else {
            //防止超出右边界
            if (mAfterX > mResultWidth - mResultRadius - mRingLineSize - mRingLineSize / 2) {
                mAfterX = mResultWidth - mResultRadius - mRingLineSize - mRingLineSize / 2;
            }
            //防止超出左边界
            if (mAfterX < mResultRadius + mRingLineSize) {
                mAfterX = mSnakeRadius + mRingLineSize / 2;
            }
            canvas.drawCircle(mAfterX, mResultHeight / 2, mResultRadius, mRoundPaint);
            if (mState == SlideState.FINISH) {
                mTextPaint.setColor(Color.TRANSPARENT);
                Rect rect = new Rect(0, 0, mResultRadius * 2, mResultRadius * 2);
                Rect sRect = new Rect((int) (mAfterX - mResultRadius / 2), mRingLineSize + mResultRadius / 2, (int) (mAfterX + mResultRadius / 2), mResultRadius * 2 - mResultRadius / 2 + mRingLineSize);

                canvas.drawBitmap(mSuccessedDrawable, rect, sRect, mBitPaint);
                return;
            }
            canvas.drawText(mSnakeBarTopText, mAfterX, snakeBaselineTop, mTextPaint);
            canvas.drawText(mSnakeBarBottomText, mAfterX, snakeBaselineBottom, mTextPaint);
        }


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mResultWidth = widthSize;
        mResultHeight = heightSize;
        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth = mSnakeRadius * 2 + getPaddingLeft() + getPaddingRight() + mRingLineSize * 2;
            mResultWidth = (contentWidth < widthSize) ? contentWidth : mResultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = mSnakeRadius * 2 + getPaddingTop() + getPaddingBottom() + mRingLineSize;
            mResultHeight = (contentHeight < heightSize) ? contentHeight : mResultHeight;
        }

        setMeasuredDimension(mResultWidth, mResultHeight);
    }


    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float mBeforeX = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mState == SlideState.FINISH) {
                    return false;
                }
                if (event.getX() > mAfterX + mSnakeRadius || event.getX() < mAfterX - mSnakeRadius) {
                    return false;
                }

                mBeforeX = event.getX();

                return true;
            case MotionEvent.ACTION_MOVE:
                mAfterX = event.getX() - mBeforeX;
                mBeforeX = mAfterX;
                if (mAfterX > mResultWidth - mResultRadius - mRingLineSize - mRingLineSize / 2) {
                    mState = SlideState.FINISH;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                //没有滑动到终点则返回原点
                if (mAfterX < mResultWidth - mResultRadius - mRingLineSize - mRingLineSize / 2) {
                    mState = SlideState.UN_FINISH;

                    //回到远点
                    ValueAnimator valueAnimator = ValueAnimator.ofInt((int) mAfterX, mSnakeRadius + mRingLineSize / 2);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int animatedValue = (int) animation.getAnimatedValue();
                            mAfterX = animatedValue;
                            postInvalidate();
                        }
                    });
                    valueAnimator.start();
                } else {
                    mState = SlideState.FINISH;
                    if (mSlideCallback != null) {
                        mSlideCallback.onComplete();
                    }
                    postInvalidate();
                }

                break;
        }

        return super.onTouchEvent(event);
    }


    public void setOnSlideCallback(OnSlideCallback slideCallback) {
        mSlideCallback = slideCallback;
    }


    public interface OnSlideCallback {
        void onComplete();
    }

    enum SlideState {
        UN_FINISH,
        FINISH
    }

}
