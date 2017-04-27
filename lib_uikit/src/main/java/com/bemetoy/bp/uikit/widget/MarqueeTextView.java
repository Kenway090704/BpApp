package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Tom on 2016/4/21.
 */
public class MarqueeTextView extends BpTextView {

    public MarqueeTextView(Context context) {
        super(context);
        setSingleLine();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSingleLine();
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
