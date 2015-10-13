package com.shequcun.farm.dlg;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.util.Utils;

/**
 * Created by mac on 15/9/30.
 */
public class AlertDialog {

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


}
