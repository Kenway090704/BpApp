package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bemetoy.bp.sdk.utils.TypefaceUtil;
import com.bemetoy.bp.uikit.R;

/**
 * Created by Tom on 2016/4/6.
 *
 */
public class BpTextView extends TextView {

    private static final String TAG = "uikit.BpTextView";

    public static final int FONT_SYSTEM_SETTING = 0;
    public static final int FONT_HAN_YI = 1;
    public static final int FONT_YA_HEI = 2;

    private float mStrokeWidth = -1f;
    private int mStrokeColor = 0;
    private TextPaint strokePaint = null;


    public BpTextView(Context context) {
        this(context, null);
    }

    public BpTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpTextView, defStyleAttr, 0);
        mStrokeColor = a.getColor(R.styleable.BpTextView_stroke_color, Color.TRANSPARENT);
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.BpTextView_stroke_width, -1);

        a.recycle();

        Typeface tf = TypefaceUtil.get(context, "fonts/yahei.ttf"); //set YAHEI as the default font.
        if (tf != null && this.getTypeface() != null) {
            tf = Typeface.create(tf, this.getTypeface().getStyle()); // set the style
            this.setTypeface(tf);
        }
        strokePaint = new TextPaint(getPaint());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mStrokeWidth > 0) {
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setColor(mStrokeColor);
            strokePaint.setStrokeWidth(mStrokeWidth);

            String text = getText().toString();
            canvas.drawText(text, (getWidth() - strokePaint.measureText(text)) / 2 + getCompoundDrawablePadding(),
                    getBaseline(), strokePaint);
        }
        super.onDraw(canvas);
    }


    public boolean isTextOverflow(){
        return strokePaint.measureText(getText().toString()) > getWidth();
    }

}
