package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.shequcun.farm.R;

/**
 * Created by apple on 15/8/3.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentMgrInterface fMgrInterface;

    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fMgrInterface = (FragmentMgrInterface) getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        setWidgetLsn();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (fMgrInterface != null)
            fMgrInterface.setSelectedFragment(this);
    }


    public void gotoFragment(int id, Fragment fragment, String tag) {
        FragmentManager fm = getFragmentManager();
        //getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(id, fragment);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public void gotoFragmentByAdd(int id, Fragment fragment, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(id, fragment);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public void gotoFragmentByAdd(Bundle bundle, int id, Fragment fragment,
                                  String tag) {
        fragment.setArguments(bundle);
        gotoFragmentByAdd(id, fragment, tag);
    }


    public void gotoFragment(Bundle bundle, int id, Fragment fragment,
                             String tag) {
        fragment.setArguments(bundle);
        gotoFragment(id, fragment, tag);
    }

    /**
     * 出栈
     */
    public void popBackStack() {
        getFragmentManager().popBackStack();
    }

    public void clearStack() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void gotoFragmentByAnimation(int id, Fragment fragment,
                                        String tag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top, R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom);
        ft.replace(id, fragment);
        ft.addToBackStack(tag);
        ft.commit();
    }

    /**
     * 初始化各个控件
     *
     * @param v
     */
    protected abstract void initWidget(View v);

    /**
     * 设置各个控制的监听
     */
    protected abstract void setWidgetLsn();
}
