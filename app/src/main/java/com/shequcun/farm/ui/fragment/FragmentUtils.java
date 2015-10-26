package com.shequcun.farm.ui.fragment;

import com.shequcun.farm.R;

/**
 * Created by cong on 15/10/26.
 */
public class FragmentUtils {
    public static void login(BaseFragment baseFragment) {
        baseFragment.gotoFragmentByAdd(R.id.mainpage_ly, new LoginAllFragment(), LoginAllFragment.class.getName());
    }
}
