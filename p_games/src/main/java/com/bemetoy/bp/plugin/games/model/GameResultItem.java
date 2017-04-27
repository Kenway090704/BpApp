package com.bemetoy.bp.plugin.games.model;

/**
 * Created by Tom on 2016/5/5.
 */
public class GameResultItem {

    private String mUsername;
    private String mReward;
    private String mRankImg;
    private String mTime;

    public GameResultItem(String username, String reward, String rankImg, String time) {
        this.mUsername = username;
        this.mReward = reward;
        this.mRankImg = rankImg;
        this.mTime = time;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getReward() {
        return mReward;
    }

    public String getRankImg() {
        return mRankImg;
    }

    public String getTime() {
        return mTime;
    }

    public static GameResultItem generateEmptyItem(String rankimg, String reward) {
        return new GameResultItem("", reward, rankimg, "00''00'");
    }
}
