package com.bemetoy.bp.uikit.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Tom on 2016/3/11.
 */
public class BpTabStrip extends LinearLayout {

    private ViewPager viewPager;


    private int currentIndex = 0;
    public BpTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private OnTabChangeListener mTabChangeListener;

    public BpTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
    }

    private int getTabIndex(View view) {
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == view) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if(!(child instanceof OnItemChecked)) {
                throw new IllegalArgumentException("Only OnItemChecked class is supported here");
            }

            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = getTabIndex(view);
                    changeTabStyle(index);
                    currentIndex = index;

                    if(viewPager != null) {
                        viewPager.setCurrentItem(index, false);
                    }

                    if(mTabChangeListener != null) {
                        mTabChangeListener.onTabChange(currentIndex);
                    }
                }
            });
        }
        changeTabStyle(currentIndex);
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                changeTabStyle(currentIndex);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeTabStyle(int currentItemIndex){
        int childCount = this.getChildCount();
        for (int i = 0; i < childCount; i++) {
            OnItemChecked child = (OnItemChecked) getChildAt(i);
            if (currentItemIndex == i) {
                child.onCheckedChange(true);
                //child.setTextColor(textColorChecked);
            } else {
                child.onCheckedChange(false);
                //child.setTextColor(textColorUnCheck);
            }
        }
    }

    public int getCurrentIndex(){
        return currentIndex;
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.mTabChangeListener = listener;
    }



    public interface OnTabChangeListener {
        public void onTabChange(int currentIndex);
    }

    public interface OnItemChecked{
        void onCheckedChange(boolean isCheck);
    }

}

