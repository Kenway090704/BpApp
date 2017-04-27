package com.bemetoy.stub.app;

import android.content.Context;

import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.widget.kankan.wheel.adaptes.AbstractWheelTextAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 2016/5/16.
 */
public class DistrictTextAdapter extends AbstractWheelTextAdapter {

    private static final String TAG = "APP.DistrictTextAdapter";

    private List<String> data = new ArrayList();

    public DistrictTextAdapter(Context context) {
        super(context);
    }

    public DistrictTextAdapter(Context context, int itemResource) {
        super(context, itemResource);
    }

    public DistrictTextAdapter(Context context, int itemResource, int itemTextResource) {
        super(context, itemResource, itemTextResource);
    }


    public void setData(List<String> data) {
        if(data == null) {
            Log.e(TAG, "data should not be null");
            return;
        }
        this.data = data;
    }

    @Override
    public CharSequence getItemText(int index) {
        if(index >= data.size() || index < 0) {
            return "";
        }

        return data.get(index);
    }

    @Override
    public int getItemsCount() {
        return data.size();
    }
}
