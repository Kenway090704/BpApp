package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/8/23.
 */
public class BpTabStripImageViewItem extends ImageView implements BpTabStrip.OnItemChecked {

    private Drawable drawableChecked;
    private Drawable drawableUnchecked;
    private Drawable bgChecked;
    private Drawable bgUnCheck;


    public BpTabStripImageViewItem(Context context) {
        this(context, null);
    }

    public BpTabStripImageViewItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpTabStripImageViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpTabStripImageViewItem, defStyleAttr, 0);
        drawableChecked = a.getDrawable(R.styleable.BpTabStripImageViewItem_drawableChecked);
        drawableUnchecked = a.getDrawable(R.styleable.BpTabStripImageViewItem_drawableUnchecked);
        bgChecked = a.getDrawable(R.styleable.BpTabStripImageViewItem_bgChecked);
        bgUnCheck = a.getDrawable(R.styleable.BpTabStripImageViewItem_bgUnchecked);
        a.recycle();
    }

    @Override
    public void onCheckedChange(boolean isCheck) {
        if(isCheck) {
            this.setBackgroundDrawable(bgChecked);
            this.setImageDrawable(drawableChecked);
        } else {
            this.setBackgroundDrawable(bgUnCheck);
            this.setImageDrawable(drawableUnchecked);
        }
    }
}
