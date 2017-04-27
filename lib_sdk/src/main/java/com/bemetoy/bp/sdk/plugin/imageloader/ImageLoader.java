package com.bemetoy.bp.sdk.plugin.imageloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Tom on 2016/3/17.
 */
public class ImageLoader {

    private static ILoader loader;

    public static void initImageLoader(Context context) {
        loader = new FrescoLoader().init(context);
    }

    public static void load(String url,ImageView imageView) {
        loader.load(url,imageView);
    }

}
