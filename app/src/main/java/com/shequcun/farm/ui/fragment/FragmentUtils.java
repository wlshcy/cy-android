package com.shequcun.farm.ui.fragment;

import android.os.Bundle;

import com.shequcun.farm.R;

/**
 * Created by cong on 15/10/26.
 */
public class FragmentUtils {
    public static void login(BaseFragment baseFragment) {
        baseFragment.gotoFragmentByAdd(R.id.mainpage_ly, new LoginAllFragment(), LoginAllFragment.class.getName());
    }

    public static void invalidRedPacketsList(BaseFragment baseFragment, Bundle bundle) {
        baseFragment.gotoFragmentByAdd(bundle, R.id.mainpage_ly, new RedPacketsInvalidListFragment(), RedPacketsInvalidListFragment.class.getName());
    }

    public static void changePwd(BaseFragment baseFragment) {
        baseFragment.gotoFragmentByAdd(R.id.mainpage_ly, new SettingUpdatePasswordFragment(), SettingUpdatePasswordFragment.class.getName());
    }
}
