package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 账户登录之后Serve端返回的数据体
 *
 * @author apple
 */
public class UserLoginEntry extends BaseEntry implements Serializable {
    @SerializedName("nickname")
    public String nickname;
    @SerializedName("headimg")
    public String headimg;
    @SerializedName("name")
    public String name;
    @SerializedName("bgimg")
    public String bgimg;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("address")
    public String address;
    @SerializedName("id")
    public int id;
    @SerializedName("zname")
    public String zname;
}
