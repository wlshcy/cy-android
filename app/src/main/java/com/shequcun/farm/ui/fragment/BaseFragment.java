package com.shequcun.farm.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;

import com.shequcun.farm.R;

/**
 * Created by apple on 15/8/3.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentMgrInterface fMgrInterface;
//    protected FragmentActivity mfragmentActivity;

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
        view.setOnTouchListener(onRootViewTouchListener);
    }

    protected View.OnTouchListener onRootViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (fMgrInterface != null)
            fMgrInterface.setSelectedFragment(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        防止activity被系统回收掉
//        mfragmentActivity = (FragmentActivity)activity;
    }


    public void gotoFragment(int id, Fragment fragment, String tag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(id, fragment);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public void gotoFragmentByAdd(int id, Fragment fragment, String tag) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(id, fragment);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    protected void gotoFragmentByAdd(BaseFragment baseFragment, Class cls) {
        gotoFragmentByAdd(R.id.mainpage_ly, baseFragment, cls.getName());
    }

    protected void gotoFragmentByAdd(Bundle bundle, BaseFragment baseFragment, Class cls) {
        gotoFragmentByAdd(bundle, R.id.mainpage_ly, baseFragment, cls.getName());
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

//    protected FragmentActivity getFragmentActivity() {
//        FragmentActivity baseFragmentActivity = (FragmentActivity) getActivity();
//        if (baseFragmentActivity == null) {
////            activity被回收
//            return mfragmentActivity;
//        }
//        return baseFragmentActivity;
//    }

    /**
     * 出栈
     */
    public void popBackStack() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void clearStack() {
        FragmentActivity mAct = getActivity();
        if (mAct != null) {
            FragmentManager fMgr = mAct.getSupportFragmentManager();
            if (fMgr != null)
                fMgr.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }


    public void gotoFragmentByAnimation(Bundle bundle, int id, Fragment fragment,
                                        String tag) {
        fragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top, R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom);
//        ft.replace(id, fragment);
        ft.add(id, fragment);
        ft.addToBackStack(tag);
//        ft.commit();
        ft.commitAllowingStateLoss();
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
