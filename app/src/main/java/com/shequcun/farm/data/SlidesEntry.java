package com.shequcun.farm.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by apple check_turn_on 15/7/23.
 */
public class SlidesEntry extends BaseEntry{
    @SerializedName("title")
    public String title;
    @SerializedName("img")
    public String img;
    @SerializedName("url")
    public String url;

//    id	int	幻灯片id	自增主键	10
//    cid	int	城市id		1
//    title	string	标题		轮播图
//    img	string	轮播图片url	默认为	'http://f.hiphotos.baidu.com/image/1.jpg'
//    url	string	轮播图片对应链接	默认为	'https://market.shequcun.com'
//    status	int	轮播图状态	0.停用, 1.启用	1
}