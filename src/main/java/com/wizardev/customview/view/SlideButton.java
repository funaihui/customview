package com.wizardev.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideButton extends View {

    private Path mPath;
    private int mRadius = 100;
    private RectF mRectF;
    private Paint mPaint;
    private float cx = 100;
    private float cy = 100;
    private float mAfterX;


    public SlideButton(Context context) {
        this(context,null);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath = new Path();
        mRectF = new RectF(0,0,mRadius*2,mRadius*2);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
//        mPath.reset();
        canvas.drawColor(Color.YELLOW);
        mPath.addArc(mRectF,90,180);
        mPath.moveTo(mRadius,0);
        mPath.lineTo(300,0);
        mRectF = new RectF(300-mRadius , 0, mRadius  +300, mRadius * 2);
        mPath.addArc(mRectF,-90,180);
        mPath.lineTo(mRadius,2*mRadius);
        canvas.drawPath(mPath,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        if (mAfterX == 0) {
            canvas.drawCircle(cx , cy, mRadius, mPaint);

        } else {
            //防止超出边界
            if (mAfterX > 300) {
                mAfterX = 300;
            }

            if (mAfterX < mRadius) {
                mAfterX = mRadius;
            }
            canvas.drawCircle(mAfterX,cy,mRadius,mPaint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

         float mBeforeX = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*if (mBeforeX == 0) {
                }*/
                if (event.getX() > mAfterX + mRadius || event.getX()<mAfterX - mRadius) {
                    return false;
                }

                mBeforeX = event.getX();

                return true;
            case MotionEvent.ACTION_MOVE:
                mAfterX = event.getX() - mBeforeX;
                mBeforeX = mAfterX;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onTouchEvent(event);
    }
}
