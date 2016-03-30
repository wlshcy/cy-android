package com.lynp.ui.db;

/**
 * Created by niuminguo on 16/3/30.
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class ItemKey {

    public final static String ID = "id";
    public final static String TYPE = "type";
    public String id = "";// md5值
    public int type = -1;

    public String getKeyId() {
        if (id == null || id.length() == 0)
            generateKeyId();
        return id;
    }

    public abstract void generateKeyId();

    // 32 位 md5
    public static String createMD5(String str) {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (messageDigest == null)
            return "";

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

}