package com.shequcun.farm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

//import com.shequcun.farm.R;
import com.lynp.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {
    // 直接拨打电话
    public static void gotoCall(Context activity, String telNum) {
        Intent intent = null;
        TelephonyManager tm = (TelephonyManager) activity
                .getSystemService(Activity.TELEPHONY_SERVICE);
        int state = tm.getSimState();

        if (state == TelephonyManager.SIM_STATE_UNKNOWN) {
            Toast.makeText(activity, R.string.call_message_unknow,
                    Toast.LENGTH_SHORT).show();
        } else if (state == TelephonyManager.SIM_STATE_ABSENT) {
            Toast.makeText(activity, R.string.tel_message_absent,
                    Toast.LENGTH_SHORT).show();
        } else if (state == TelephonyManager.SIM_STATE_READY) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNum));
            activity.startActivity(intent);
        }
    }

    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean isPhone(String str) {
        // Pattern p =
        // Pattern.compile("(\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$");
        Pattern p = Pattern
                .compile("^1(1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9]|6[0-9]|7[0-9]|8[0-9]|9[0-9])\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }
}