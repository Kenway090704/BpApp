package com.bemetoy.bp.plugin.games.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher;

import com.bemetoy.bp.uikit.widget.FlowRadioGroup;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Tom on 2016/5/5.
 */
public class HeaderViewFactory implements ViewSwitcher.ViewFactory {

    private Context mContext;

    public HeaderViewFactory(Context context) {
        mContext = context;
    }

    @Override
    public View makeView() {
        final SimpleDraweeView i = new SimpleDraweeView(mContext);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(mContext.getResources());
        builder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        i.setHierarchy(builder.build());
        i.setLayoutParams(new ImageSwitcher.LayoutParams(FlowRadioGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return i ;
    }
}
