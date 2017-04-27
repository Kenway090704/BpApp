package com.bemetoy.bp.plugin.personalcenter.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bemetoy.bp.autogen.protocol.Racecar;

/**
 * Created by Tom on 2016/5/30.
 */
public class AddressInfo implements Parcelable {

    private int id;
    private String contact;
    private String mobile;
    private String province;
    private String city;
    private String district;
    private String detail;
    private boolean isDefault;

    public AddressInfo(Racecar.Address address) {
        id = address.getId();
        mobile = address.getMobile();
        contact = address.getContact();
        province = address.getProvince();
        city = address.getCity();
        detail = address.getDetail();
        district = address.getDistrict();
        isDefault = address.getFlag() == 1;
    }

    protected AddressInfo(Parcel in) {
        id = in.readInt();
        contact = in.readString();
        mobile = in.readString();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        detail = in.readString();
        isDefault = in.readByte() != 0;
    }

    public static final Creator<AddressInfo> CREATOR = new Creator<AddressInfo>() {
        @Override
        public AddressInfo createFromParcel(Parcel in) {
            return new AddressInfo(in);
        }

        @Override
        public AddressInfo[] newArray(int size) {
            return new AddressInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getContact() {
        return contact;
    }

    public String getMobile() {
        return mobile;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isDefault() {
        return isDefault;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(contact);
        dest.writeString(mobile);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(detail);
        dest.writeByte((byte) (isDefault ? 1 : 0));
    }
}
