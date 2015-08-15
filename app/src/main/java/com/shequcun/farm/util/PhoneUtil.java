package com.shequcun.farm.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.shequcun.farm.R;

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
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}