package com.shequcun.farm.dlg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.Utils;

/**
 * Created by mac on 15/9/30.
 */
public class AlertDialog {

    public void alertOutOfFixedRemains(Activity mAct, int remains) {
        String content = mAct.getResources().getString(R.string.out_of_fixed_remain);
        content = content.replace("A", Utils.unitConversion(remains));//mOrderController.getReqWeight()
        alertDialog(mAct, content);
    }

    public void alertOutOfReqWeight(Activity mAct, int weight) {
        String content = mAct.getResources().getString(R.string.out_of_required_weight);
        content = content.replace("A", Utils.unitConversion(weight));//mOrderController.getReqWeight()
        alertDialog(mAct, content);
    }

    public void alertOutOfReqWeight1(Activity mAct, int weight) {
        String content = mAct.getResources().getString(R.string.out_of_required_weight1);
        content = content.replace("A", Utils.unitConversion(weight));//mOrderController.getReqWeight()
        alertDialog(mAct, content);
    }

    public void alertOutOfMaxpacks(Activity mAct, int maxpacks) {
        String content = mAct.getResources().getString(R.string.out_of_maxpacks);
        content = content.replace("A", maxpacks + "");
        alertDialog(mAct, content);
    }

    public void alertOutOfRemains(Activity mAct) {
        String content = mAct.getResources().getString(R.string.out_of_remains);
        alertDialog(mAct, content);
    }

    public void alertDialog(Context mAct, String content) {
        final android.app.AlertDialog alert = new android.app.AlertDialog.Builder(mAct).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        alert.getWindow().setContentView(R.layout.alert_dialog);
        TextView tv = (TextView) alert.getWindow().findViewById(R.id.content_tv);
        tv.setText(content);
        alert.getWindow().findViewById(R.id.ok_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
    }

    public void alertDialog(Context mAct, int resId, DialogInterface.OnClickListener onClk) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mAct);
        builder.setTitle("提示");
        builder.setMessage(resId);
        builder.setNegativeButton("好的", onClk);
        builder.setNeutralButton("不去看了", null);
        builder.create().show();
    }


}
