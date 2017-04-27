package com.bemetoy.bp.sdk.plugin.imageloader;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Tom on 2016/3/17.
 */
public class FrescoLoader implements ILoader {

    private static final String TAG = "ImageLoader.FrescoLoader";

    @Override
    public ILoader init(Context context) {
        Fresco.initialize(context);
        return this;
    }

    @Override
    public void load(String url, ImageView imgView) {

        if (Util.isNull(url) || Util.isNull(imgView)) {
            Log.e(TAG,"url or imgView is null");
            return;
        }

        if (imgView instanceof SimpleDraweeView) {
            SimpleDraweeView view = (SimpleDraweeView) imgView;
            view.setImageURI(Uri.parse(url));
        }
    }

}
