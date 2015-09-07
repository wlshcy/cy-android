package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mac on 15/9/6.
 */
public class RecommendDetailEntry implements Serializable {
    //    "content": "营养滋补",
//            "storage":"阴凉干燥处",
//            "image":"http://f.hiphotos.baidu.com/image/pic/item/9213b07eca80653879a8611594dda144ad348272.jpg"
    @SerializedName("content")
    public String content;
    @SerializedName("storage")
    public String storage;
    @SerializedName("image")
    public String image;
}
