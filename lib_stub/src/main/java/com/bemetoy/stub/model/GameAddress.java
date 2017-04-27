package com.bemetoy.stub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.protocols.ProtocolConstants;

/**
 * Created by Tom on 2016/5/6.
 */
public class GameAddress implements Parcelable {

    private int id;
    private String name;
    private String image1;
    private String image2;
    private String phone1;
    private String phone2;
    private int speedwayType;
    private String province;
    private String city;
    private String district;
    private String mark;
    private String detail;
    private double latitude;
    private double longitude;
    private long distance = ProtocolConstants.DEFAULT_DISTANCE;
    private int gamesState;
    private int lastGameId;

    public GameAddress(Racecar.Place place) {
        this.id = place.getId();
        this.name = place.getName();
        this.image1 = place.getImage1();
        this.image2 = place.getImage2();
        this.phone1 = place.getCall1();
        this.phone2 = place.getCall2();
        this.speedwayType = place.getTrack();
        this.province = place.getProvince();
        this.city = place.getCity();
        this.district = place.getDistrict();
        this.mark = place.getDetail();
        this.detail = place.getDetail();
        try {
            this.longitude =  Double.valueOf(place.getLongitude());
            this.latitude = Double.valueOf(place.getLatitude());
        } catch (Exception e) {

        }

        if(LocationLogic.getLastLocation() != null) {
            distance = (long)DistanceUtil.getDistance(new LatLng(latitude, longitude),
                    new LatLng(LocationLogic.getLastLocation().mLastLatitude, LocationLogic.getLastLocation().mLastLongitude));
        } else if(LocationLogic.isHasLocationed()) {
            distance = ProtocolConstants.ERROR_DISTANCE;
        }

        gamesState = place.getState();
        lastGameId = place.getGameId();
    }

    protected GameAddress(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image1 = in.readString();
        image2 = in.readString();
        phone1 = in.readString();
        phone2 = in.readString();
        speedwayType = in.readInt();
        province = in.readString();
        city = in.readString();
        district = in.readString();
        mark = in.readString();
        detail = in.readString();
        distance = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        gamesState = in.readInt();
        lastGameId = in.readInt();
    }

    public static final Creator<GameAddress> CREATOR = new Creator<GameAddress>() {
        @Override
        public GameAddress createFromParcel(Parcel in) {
            return new GameAddress(in);
        }

        @Override
        public GameAddress[] newArray(int size) {
            return new GameAddress[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public int getSpeedwayType() {
        return speedwayType;
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

    public String getMark() {
        return mark;
    }

    public String getDetail() {
        return detail;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getGamesState() {
        return gamesState;
    }

    public String getDisplayAddress() {
        final StringBuilder sb = new StringBuilder();
        if(distance > 0) {
            if(distance >= 1000) {
                return sb.append(name).append(" | ").append(detail).append(" | 距离:").append(String.format("%.1f", distance/1000f)).append("公里").toString();
            }
            return sb.append(name).append(" | ").append(detail).append(" | 距离:").append(distance).append("米").toString();
        } else if (distance == ProtocolConstants.DEFAULT_DISTANCE){
            return sb.append(name).append(" | ").append(detail).append(" | 距离:定位中...").toString();
        } else {
            return sb.append(name).append(" | ").append(detail).append(" | 距离:").append("定位失败").toString();
        }
    }

    public int getLastGameId() {
        return lastGameId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image1);
        dest.writeString(image2);
        dest.writeString(phone1);
        dest.writeString(phone2);
        dest.writeInt(speedwayType);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(mark);
        dest.writeString(detail);
        dest.writeLong(distance);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(gamesState);
        dest.writeInt(lastGameId);
    }
}
