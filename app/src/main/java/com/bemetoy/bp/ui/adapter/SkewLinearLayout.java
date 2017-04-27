package com.bemetoy.bp.ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;

import com.bemetoy.bp.R;
import com.bemetoy.bp.sdk.tools.Log;

/**
 * Created by albieliang on 16/6/14.
 */
public class SkewLinearLayout extends LinearLayout {

    private static final String TAG = "SkewLinearLayout";

    public SkewLinearLayout(Context context) {
        super(context);
    }

    public SkewLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkewLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.home_page_game_list_skew_radius, outValue, true);
        float xtan = outValue.getFloat();
        float xtran = -xtan * getMeasuredHeight();
        super.onLayout(changed, l, t, r, b);
        setPadding(getPaddingLeft(), getPaddingTop(), (int) (xtran), getPaddingBottom());
        Log.i(TAG, "onLayout");
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        float xtan = -0.25f;
//        float xtran = -xtan * heightMeasureSpec;
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        super.onMeasure((int) (width - xtran), heightMeasureSpec);
//        Log.i(TAG, "onMeasure(%d, %d) -> (%d, %d)", (int) (width - xtran), heightMeasureSpec, getMeasuredWidth(), getMeasuredHeight());
//    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.home_page_game_list_skew_radius, outValue, true);
        float xtan = outValue.getFloat();
        float xtran = -xtan * getMeasuredHeight();
        int width = getMeasuredWidth();
        float xscale = (width - xtran) / width;
        canvas.scale(xscale, 1f);
        canvas.skew(xtan, 0f);
        canvas.translate(xtran, 0f);
        Log.d(TAG, "dispatchDraw skew(xscale : %s, width : %s, xtran : %s, cutW : %s)", xscale, width, xtran, (1 - xscale) * width);
        super.dispatchDraw(canvas);
    }
}
