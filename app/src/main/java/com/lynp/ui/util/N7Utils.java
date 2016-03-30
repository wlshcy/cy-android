package com.lynp.ui.util;

/**
 * Created by niuminguo on 16/3/30.
 */
public class N7Utils {
    public static String filter22UrlParams(String url, int w) {
        if (url.indexOf("?") > -1)
            url = url + "&imageView2/2/w/" + w;
        else
            url = url + "?imageView2/2/w/" + w;
        return url;
    }
}
