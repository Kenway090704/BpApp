package com.bemetoy.stub.databinding;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bemetoy.bp.sdk.plugin.imageloader.ImageLoader;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.uikit.widget.BpImageTextView;

/**
 * Created by albieliang on 16/4/2.
 */
public class DataBindingAttrSetter {

    @BindingAdapter("imageUrl")
    public static void imageUrl(ImageView view, String url) {
        ImageLoader.load(url, view);
    }

    @BindingAdapter("imageValue")
    public static void imageUrl(BpImageTextView view, int value) {
        view.setValue(value);
    }


    @BindingAdapter("formatTime")
    public static void imageUrl(TextView view, long time) {
        // TODO: 16/4/2 albieliang
        view.setText(Util.getFormatTime((int) (time / 1000)));
    }

    @BindingAdapter({"format", "time"})
    public static void imageUrl(TextView view, String format, long time) {
        // TODO: 16/4/2 albieliang
        view.setText(Util.getDateFormat(format, time));
    }
}
