package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/6/23.
 */
public class BpImageTextView extends View {

    private static final String TAG = "Uikit.BpImageTextView";

    private static final int DEFAULT_VALUE = 0;
    private int mValue = DEFAULT_VALUE;

    private int singleTextWidth = 45;

    public BpImageTextView(Context context) {
        this(context, null);
    }

    public BpImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpImageTextView, defStyleAttr, 0);
        mValue = a.getInt(R.styleable.BpImageTextView_value, DEFAULT_VALUE);
        a.recycle();
    }

    public void setValue(int value) {
        mValue = value;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int w = singleTextWidth * String.valueOf(mValue).length();
        width = Math.min(w, width);

        Log.d(TAG, "measured width is %d, height is %d", width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String valueText = String.valueOf(mValue);
        for(int i = 0; i < valueText.length(); i++) {
            Bitmap bitmap = BitmapUtil.getBitmap(this.getContext(), String.valueOf(valueText.charAt(i)));
            RectF rectF = new RectF(i * singleTextWidth, 0, (i+1) * singleTextWidth , getMeasuredHeight());
            canvas.drawBitmap(bitmap, null, rectF , null);
        }
    }
}


