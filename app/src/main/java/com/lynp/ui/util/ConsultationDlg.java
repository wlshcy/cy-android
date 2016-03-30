package com.lynp.ui.util;

/**
 * Created by niuminguo on 16/3/30.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.lynp.ui.util.Constrants;
import com.lynp.ui.util.PhoneUtil;

/**
 * 咨询
 * Created by apple on 15/8/10.
 */
public class ConsultationDlg {
    public static void showCallTelDlg(final Activity act) {
        final String phone = Constrants.Customer_Service_Phone;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
//        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("提示");
        builder.setMessage("是否拨打客服电话" + phone);
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PhoneUtil.gotoCall(act, phone);
            }
        });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }

}