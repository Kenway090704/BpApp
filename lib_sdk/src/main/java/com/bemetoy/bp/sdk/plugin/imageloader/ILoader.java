package com.bemetoy.bp.sdk.plugin.imageloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by Tom on 2016/3/17.
 */
public interface ILoader {

    /**
     * init the loader.
     * @param context
     * @return
     */
    ILoader init(Context context);

    /**
     * Load the image into the imgView.
     * @param url the url of the image
     * @param imgView
     */
    void load(String url,ImageView imgView);

}
