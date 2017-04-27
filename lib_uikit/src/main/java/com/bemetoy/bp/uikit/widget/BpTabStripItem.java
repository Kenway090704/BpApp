package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/8/18.
 */
public class BpTabStripItem extends BpTextView implements BpTabStrip.OnItemChecked {

    private Drawable drawableChecked;
    private Drawable drawableUnchecked;
    private static int DEFAULT_CHECKED_TEXT_COLOR = Color.WHITE;
    private static int DEFAULT_UNCHECK_TEXT_COLOR = Color.WHITE;

    private int textColorUnCheck;
    private int textColorChecked;

    public BpTabStripItem(Context context) {
        this(context, null);
    }
    public BpTabStripItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpTabStripItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpTabStripItem, defStyleAttr, 0);
        drawableChecked = a.getDrawable(R.styleable.BpTabStripItem_drawableChecked);
        drawableUnchecked = a.getDrawable(R.styleable.BpTabStripItem_drawableUnchecked);
        textColorUnCheck = a.getColor(R.styleable.BpTabStripItem_item_text_uncheck,DEFAULT_UNCHECK_TEXT_COLOR);
        textColorChecked = a.getColor(R.styleable.BpTabStripItem_item_text_checked,DEFAULT_CHECKED_TEXT_COLOR);
        a.recycle();
    }

    @Override
    public void onCheckedChange(boolean isCheck) {
        if (isCheck) {
            this.setBackgroundDrawable(drawableChecked);
            this.setTextColor(textColorChecked);
        } else {
            this.setBackgroundDrawable(drawableUnchecked);
            this.setTextColor(textColorUnCheck);
        }
    }
}
