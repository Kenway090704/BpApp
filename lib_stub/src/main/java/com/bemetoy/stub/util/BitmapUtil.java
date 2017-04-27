package com.bemetoy.stub.util;

import android.support.annotation.DrawableRes;
import android.util.LruCache;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

/**
 * Created by Tom on 2016/6/11.
 */
public class BitmapUtil {

    private static LruCache<Integer, BitmapDescriptor> descriptorCache = new LruCache<Integer, BitmapDescriptor>(24);

    /**
     * Get the BitmapDescriptor from the cache
     * @param drawableRes
     * @return
     */
    public static BitmapDescriptor getBitmapDescriptor(@DrawableRes int drawableRes) {
        if(descriptorCache.get(drawableRes) == null) {
           BitmapDescriptor descriptorReference = BitmapDescriptorFactory
                    .fromResource(drawableRes);
            descriptorCache.put(drawableRes, descriptorReference);
            return descriptorReference;
        } else {
            return descriptorCache.get(drawableRes);
        }
    }


}
