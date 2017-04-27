package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.R;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tomliu on 16-9-26.
 */
public class AutoScrollTextView extends LinearLayout {

    private Runnable action;
    private Timer timer;
    private String text;
    private TextView textView;
    private int currentOffset;
    private int currentTextLength;

    private static final int OFFSET_UNIT = 2;

    public AutoScrollTextView(Context context) {
        this(context, null);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,  attrs, defStyle);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {
        View view = LayoutInflater.from(context).inflate(R.layout.marquee_txtview, this, true);
        final HorizontalScrollView scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);
        //disable the scroll by the user.
        scrollView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        textView = (TextView) view.findViewById(R.id.actual_tv);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpMarqueeTextView, defStyle, 0);
        text = a.getString(R.styleable.BpMarqueeTextView_text);
        if(text == null) {
            text = "";
        }
        textView.setText(text);
        currentTextLength = (int) textView.getPaint().measureText(text);

        action = new Runnable() {
            @Override
            public void run() {
                textView.scrollBy(OFFSET_UNIT, 0);
                currentOffset += OFFSET_UNIT;
                if(currentOffset >= currentTextLength + textView.getWidth()) {
                    textView.scrollTo(currentTextLength * -1, 0);
                    currentOffset = 0;
                }
            }
        };
        timer = new Timer();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() > 1) {
            throw new IllegalArgumentException("MarqueeTextView not support add child.");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(action);
            }
        }, 1000 , 60);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = textView.getLayoutParams();
                params.width = findViewById(R.id.scrollView).getMeasuredWidth();
                textView.setLayoutParams(params);

            }
        }, 1000);
    }

    public void setText(String text) {
        if(text == null) {
            this.text = "";
        } else {
            this.text = text;
        }
        textView.setText(this.text);
        currentTextLength = (int) textView.getPaint().measureText(this.text);
        this.invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(action);
        timer.cancel();
    }
}