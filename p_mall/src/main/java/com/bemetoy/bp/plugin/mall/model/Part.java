package com.bemetoy.bp.plugin.mall.model;

import com.bemetoy.bp.autogen.protocol.Racecar;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Tom on 2016/4/25.
 */
public class Part implements Serializable {

    public static final int PART_STABILITY = 1;
    public static final int PART_WEIGHT = 2;
    public static final int PART_ROAD_HOLDING = 3;
    public static final int PART_OUTPUT_POWER = 4;
    public static final int PART_STAMINA = 5;
    public static final int PART_ROTATE_SPEED = 6;
    public static final int PART_FLEXIBILITY = 7;
    public static final int PART_TORSION = 8;

    private String name;
    private String imgUrl;
    private int score;
    private final HashMap<Integer,Integer> parameter = new HashMap();
    private long id;
    private String desc;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public HashMap<Integer, Integer> getParameter() {
        return parameter;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Part transform(Racecar.GoodsListResponse.Item item) {
        Part part = new Part();
        part.setName(item.getName());
        part.setDesc(item.getDescribe());
        part.setId(item.getGoodsId());
        part.setImgUrl(item.getImage());
        part.setScore(item.getScore());

        part.getParameter().put(Part.PART_FLEXIBILITY, item.getParameter().getFlexibility() * 20);
        part.getParameter().put(Part.PART_OUTPUT_POWER, item.getParameter().getOutputPower() * 20);
        part.getParameter().put(Part.PART_ROAD_HOLDING, item.getParameter().getRoadHolding() * 20);
        part.getParameter().put(Part.PART_ROTATE_SPEED, item.getParameter().getRotateSpeed() * 20);
        part.getParameter().put(Part.PART_STABILITY, item.getParameter().getStability() * 20);
        part.getParameter().put(Part.PART_STAMINA, item.getParameter().getStamina() * 20);
        part.getParameter().put(Part.PART_TORSION, item.getParameter().getTorsion() * 20);
        part.getParameter().put(Part.PART_WEIGHT, item.getParameter().getWeight() * 20);

        return part;
    }

}
