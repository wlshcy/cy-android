package com.shequcun.farm.data;

import java.io.Serializable;

/**
 * Created by cong on 15/8/17.
 */
public class ComboParam implements Serializable {
    private int id;
    private int weights;
    private String wimg;
    private int[] shipday;
    private float totalPrices;
    private String title;
    private int duration;
    private int comboIdx;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWeights() {
        return weights;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public String getWimg() {
        return wimg;
    }

    public void setWimg(String wimg) {
        this.wimg = wimg;
    }

    public int[] getShipday() {
        return shipday;
    }

    public void setShipday(int[] shipday) {
        this.shipday = shipday;
    }

    public float getTotalPrices() {
        return totalPrices;
    }

    public void setTotalPrices(float totalPrices) {
        this.totalPrices = totalPrices;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getComboIdx() {
        return comboIdx;
    }

    public void setComboIdx(int comboIdx) {
        this.comboIdx = comboIdx;
    }
}
