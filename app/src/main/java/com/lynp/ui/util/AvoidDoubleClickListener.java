package com.lynp.ui.util;

/**
 * Created by niuminguo on 16/3/30.
 */
import android.view.View;
import android.view.View.OnClickListener;

/**
 *
 * @author apple
 *
 */
public abstract class AvoidDoubleClickListener implements OnClickListener {

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        } else {
            onViewClick(v);
        }
    }

    public abstract void onViewClick(View v);

}