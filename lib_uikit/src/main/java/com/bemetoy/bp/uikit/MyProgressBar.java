package com.bemetoy.bp.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tom on 2016/6/13.
 */
public class MyProgressBar extends View {

    private Drawable valueDrawable;
    private Drawable bgDrawable;
    private int max = 100;
    private int progress;
    private float textSize;

    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpProgressBar, defStyleAttr, 0);
        max = a.getInt(R.styleable.BpProgressBar_max, 100);
        progress = a.getInt(R.styleable.BpProgressBar_progress, 0);
        bgDrawable = a.getDrawable(R.styleable.BpProgressBar_backgroundDrawable);
        valueDrawable = a.getDrawable(R.styleable.BpProgressBar_progressDrawable);
        textSize = a.getDimensionPixelSize(R.styleable.BpProgressBar_txtSize, 24);
        a.recycle();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        float percent = progress / (float)max;

        Rect bgRect = new Rect(0, -12, getMeasuredWidth(), getMeasuredHeight() + 12);
        Rect valueRect = new Rect(0, 0, (int)(getMeasuredWidth() * percent), getMeasuredHeight());

        valueDrawable.setBounds(valueRect);
        bgDrawable.setBounds(bgRect);
        bgDrawable.draw(canvas);
        valueDrawable.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);

        String txt = progress + "/" + max;
        float txtWidth = paint.measureText(txt);
        float paddingX = (getMeasuredWidth() - txtWidth) / 2;

        canvas.drawText(txt, paddingX, getMeasuredHeight() / 4F * 3, paint);

    }
}
