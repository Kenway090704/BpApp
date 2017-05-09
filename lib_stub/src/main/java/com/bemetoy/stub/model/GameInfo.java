package com.bemetoy.stub.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.bp.stub.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 比赛信息bean类
 * Created by Tom on 2016/4/22.
 */
public class GameInfo implements Parcelable {

    private String beginDate;
    private String endDate;
    private int participants;
    private int type;
    private int currentState;
    private boolean joined;
    private String title;
    private int id;
    private String rewardRules = "";
    private GameAddress gameAddress;
    private String detail;
    private int total;
    private String image1;
    private String image2;
    private String coachName;
    private String coachMobile;

    public GameInfo(Racecar.Game game) {
        participants = game.getJoin();
        type = game.getType();
        currentState  = game.getState();
        title = game.getTitle();
        id = game.getId();
        gameAddress = new GameAddress(game.getPlace());
        beginDate =  game.getBegin();
        endDate = game.getEnd();
        detail = game.getDetail();
        total = game.getTotal();
        joined = game.getJoined();
        image1 = game.getImage1();
        image2 = game.getImage2();
        coachName = game.getCoachName();
        coachMobile = game.getCoachMobile();
    }


    protected GameInfo(Parcel in) {
        beginDate = in.readString();
        endDate = in.readString();
        participants = in.readInt();
        type = in.readInt();
        currentState = in.readInt();
        joined = in.readByte() != 0;
        title = in.readString();
        id = in.readInt();
        rewardRules = in.readString();
        gameAddress = in.readParcelable(GameAddress.class.getClassLoader());
        detail = in.readString();
        total = in.readInt();
        image1 = in.readString();
        image2 = in.readString();
        coachName = in.readString();
        coachMobile = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(beginDate);
        dest.writeString(endDate);
        dest.writeInt(participants);
        dest.writeInt(type);
        dest.writeInt(currentState);
        dest.writeByte((byte) (joined ? 1 : 0));
        dest.writeString(title);
        dest.writeInt(id);
        dest.writeString(rewardRules);
        dest.writeParcelable(gameAddress, flags);
        dest.writeString(detail);
        dest.writeInt(total);
        dest.writeString(image1);
        dest.writeString(image2);
        dest.writeString(coachName);
        dest.writeString(coachMobile);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameInfo> CREATOR = new Creator<GameInfo>() {
        @Override
        public GameInfo createFromParcel(Parcel in) {
            return new GameInfo(in);
        }

        @Override
        public GameInfo[] newArray(int size) {
            return new GameInfo[size];
        }
    };

    public void updateStatus(int currentState) {
        this.currentState = currentState;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getParticipants() {
        return participants;
    }

    public int getType() {
        return type;
    }

    public int getCurrentState() {
        return currentState;
    }

    public boolean isJoined() {
        return joined;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getRewardRules() {
        return rewardRules;
    }

    public GameAddress getGameAddress() {
        return gameAddress;
    }

    public String getDetail() {
        return detail;
    }

    public int getTotal() {
        return total;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public void updateParticipants() {
        ++participants;
    }

    public String getCoachMobile() {
        return coachMobile;
    }

    public String getGameImg() {
        String res = "res://img/";
        switch (type) {
            case ProtocolConstants.GameType.CITY_GAME:
                return res + R.drawable.game_type_city;
            case ProtocolConstants.GameType.PROVINCE_GAME:
                return res + R.drawable.game_type_province;
            case ProtocolConstants.GameType.STATE_GAME:
                return res + R.drawable.game_type_state;
            case ProtocolConstants.GameType.THEME_GAME:
                return res + R.drawable.game_type_theme;
            case ProtocolConstants.GameType.WEEK_GAME:
                return res + R.drawable.game_type_week;
        }
        return null;
    }

    public String getDisplayDate() {
        StringBuilder stringBuilder = new StringBuilder();
        Calendar beginCalendar = Calendar.getInstance();
        if(Util.isNullOrBlank(beginDate) || Util.isNullOrBlank(endDate)) {
            return "";
        }
        beginCalendar.setTime(Util.getDate("yyyy-MM-dd hh:mm:ss",beginDate));
        stringBuilder.append(beginCalendar.get(Calendar.MONTH) + 1);
        stringBuilder.append("月");
        stringBuilder.append(beginCalendar.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append("日");

        stringBuilder.append("-");

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(Util.getDate("yyyy-MM-dd hh:mm:ss",endDate));
        stringBuilder.append(endCalendar.get(Calendar.MONTH) + 1);
        stringBuilder.append("月");
        stringBuilder.append(endCalendar.get(Calendar.DAY_OF_MONTH));
        stringBuilder.append("日");

        stringBuilder.append("   ");

        stringBuilder.append(String.format("%02d",beginCalendar.get(Calendar.HOUR_OF_DAY)));
        stringBuilder.append(":");
        stringBuilder.append(String.format("%02d",beginCalendar.get(Calendar.MINUTE)));
        stringBuilder.append("-");
        stringBuilder.append(String.format("%02d",endCalendar.get(Calendar.HOUR_OF_DAY)));
        stringBuilder.append(":");
        stringBuilder.append(String.format("%02d",endCalendar.get(Calendar.MINUTE)));
        return stringBuilder.toString();
    }

    public static List<GameInfo> transformList(List<Racecar.Game> list) {
        Log.v("Stub.GameInfo", "games size is %d", list.size());
        List<GameInfo> gameInfoList = new ArrayList<>();
        for (Racecar.Game game : list) {
            // update the distance base on the last gps location.
            GameInfo info = new GameInfo(game);
            if(LocationLogic.getLastLocation() == null) {
                if(LocationLogic.isHasLocationed()) {
                    info.getGameAddress().setDistance(ProtocolConstants.ERROR_DISTANCE);
                } else {
                    info.getGameAddress().setDistance(ProtocolConstants.DEFAULT_DISTANCE);
                }
            } else {
                int distance = (int) DistanceUtil.getDistance(new LatLng(info.getGameAddress().getLatitude(), info.getGameAddress().getLongitude())
                        ,new LatLng(LocationLogic.getLastLocation().mLastLatitude,  LocationLogic.getLastLocation().mLastLongitude));
                info.getGameAddress().setDistance(distance);

            }
            gameInfoList.add(info);
        }
        return gameInfoList;
    }

}
