package com.bemetoy.bp.plugin.operatoins.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tom on 2016/5/11.
 */
public class WelfareItem implements Parcelable {

    private String name;
    private String imgUrl;
    private String date;
    private String detailUrl;

    public WelfareItem(){

    }

    protected WelfareItem(Parcel in) {
        name = in.readString();
        imgUrl = in.readString();
        date = in.readString();
        detailUrl = in.readString();
    }

    public static final Creator<WelfareItem> CREATOR = new Creator<WelfareItem>() {
        @Override
        public WelfareItem createFromParcel(Parcel in) {
            return new WelfareItem(in);
        }

        @Override
        public WelfareItem[] newArray(int size) {
            return new WelfareItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imgUrl);
        dest.writeString(date);
        dest.writeString(detailUrl);
    }
}
