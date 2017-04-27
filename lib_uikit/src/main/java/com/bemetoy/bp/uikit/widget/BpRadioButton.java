package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Checkable;

import com.bemetoy.bp.uikit.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Tom on 2016/6/6.
 */
public class BpRadioButton extends SimpleDraweeView implements Checkable {

    private volatile boolean mIsChecked;

    private Drawable mChecked;
    private Drawable mUnChecked;
    private String mUri;

    private OnCheckedChangeListener onCheckedChangeListener;


    public BpRadioButton(Context context) {
        this(context, null);
    }

    public BpRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BpRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BpRadioButton, defStyle, 0);
        mChecked = a.getDrawable(R.styleable.BpRadioButton_bg_checked);
        mUnChecked = a.getDrawable(R.styleable.BpRadioButton_bg_uncheck);
        a.recycle();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int xOffset = getWidth() / 2;
        int yOffset = getHeight() / 4;

        int yCenter = getHeight() / 2;
        int xCenter = getWidth() / 2 ;

        Rect rect = new Rect(xCenter , yCenter +  yOffset , xCenter + xOffset, yCenter + 2 * yOffset);
        if(mChecked != null) {
            mChecked.setBounds(rect);
        }

        if(mUnChecked != null) {
            mUnChecked.setBounds(rect);
        }

        if(mIsChecked) {
            if(mChecked != null) {
                mChecked.draw(canvas);
            }

        } else {
            if(mUnChecked != null) {
                mUnChecked.draw(canvas);
            }
        }
    }

    @Override
    public void setChecked(boolean checked) {
        mIsChecked = checked;
        invalidate();
    }

    public String getImageUrl() {
        return mUri;
    }

    @Override
    public void setImageURI(Uri uri) {
        mUri = uri.toString();
        super.setImageURI(uri);
    }

    @Override
    public boolean performClick() {
        toggle();
        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }
        return handled;
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        if(!isChecked()) {
            setChecked(!mIsChecked);
            if(onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, mIsChecked);
            }
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setCheckedDrawable(Drawable mChecked) {
        this.mChecked = mChecked;
    }

    public void setUnCheckDrawable(Drawable mUnChecked) {
        this.mUnChecked = mUnChecked;
    }

    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(BpRadioButton buttonView, boolean isChecked);
    }
}
