package com.shequcun.farm.util;

import android.os.CountDownTimer;
import android.widget.Button;

import com.shequcun.farm.R;

/**
 * 计时器
 */
public class TimeCount extends CountDownTimer {
    Button obtain_verification_code;

    public TimeCount(long millisInFuture, long countDownInterval, Button button) {
        super(millisInFuture, countDownInterval);
        obtain_verification_code = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        obtain_verification_code.setClickable(false);
        obtain_verification_code.setText(millisUntilFinished / 1000
                + "秒");
    }

    @Override
    public void onFinish() {
        obtain_verification_code.setText(R.string.re_get_sms_code);
        obtain_verification_code.setClickable(true);
        cancel();
    }
}