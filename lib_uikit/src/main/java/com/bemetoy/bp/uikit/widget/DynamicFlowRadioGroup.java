package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/6/6.
 */
public class DynamicFlowRadioGroup extends LinearLayout {

    public static final int MAX_VIEW_COUNT_IN_SINGLE_LINE = 5;

    private int mCheckId = -1;
    private int maxChildInLine = MAX_VIEW_COUNT_IN_SINGLE_LINE;

    public DynamicFlowRadioGroup(Context context) {
        this(context, null);
    }

    public DynamicFlowRadioGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicFlowRadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        setOrientation(VERTICAL);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpTextView, defStyleAttr, 0);
        maxChildInLine = a.getInt(R.styleable.DynamicFlowRadioGroup_maxViewCountInOneLine, MAX_VIEW_COUNT_IN_SINGLE_LINE);
        a.recycle();

        if(maxChildInLine <= 0) {
            throw new IllegalArgumentException("the count of child view in a line ");
        }

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }



}
