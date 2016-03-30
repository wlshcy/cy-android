package com.lynp.ui.fragment;

/**
 * Created by niuminguo on 16/3/30.
 */
import android.os.Bundle;

import com.lynp.R;

/**
 * Created by cong on 15/10/26.
 */
public class FragmentUtils {
    public static void login(BaseFragment baseFragment) {
        baseFragment.gotoFragmentByAdd(R.id.mainpage_ly, new LoginAllFragment(), LoginAllFragment.class.getName());
    }
}