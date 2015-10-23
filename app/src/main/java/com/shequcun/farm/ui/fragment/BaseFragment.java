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
import com.shequcun.farm.util.HttpRequestUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by apple on 15/8/3.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentMgrInterface fMgrInterface;
    protected FragmentActivity mAct;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fMgrInterface = (FragmentMgrInterface) getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
        mAct = (FragmentActivity) activity;
    }


    public void gotoFragment(int id, Fragment fragment, String tag) {
        FragmentActivity mmAct = getActivity();
        if (mmAct == null) {
            mmAct = mAct;
        }
        FragmentManager fm = mmAct.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(id, fragment);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    public void gotoFragmentByAdd(int id, Fragment fragment, String tag) {
        FragmentActivity mmAct = getActivity();
        if (mmAct == null) {
            mmAct = mAct;
        }
        FragmentManager fm = mmAct.getSupportFragmentManager();
        if (fm != null) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_from_left, R.anim.slide_in_from_right, R.anim.slide_out_from_left);
            transaction.add(id, fragment);
            transaction.addToBackStack(tag);
            transaction.commitAllowingStateLoss();
        }

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

    /**
     * 出栈
     */
    public void popBackStack() {
        FragmentActivity mmAct = getActivity();
        if (mmAct == null) {
            mAct = mmAct;
        }
        FragmentManager fMgr = mAct.getSupportFragmentManager();
        if (fMgr != null)
            fMgr.popBackStack();

//        getActivity().getSupportFragmentManager().;
    }

    public void clearStack() {
        FragmentActivity mmAct = getActivity();
        if (mmAct == null) {
            mmAct = mAct;
        }
        FragmentManager fMgr = mmAct.getSupportFragmentManager();
        if (fMgr != null)
            fMgr.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


//    public void gotoFragmentByAnimation(Bundle bundle, int id, Fragment fragment,
//                                        String tag) {
//        fragment.setArguments(bundle);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.setCustomAnimations(R.anim.puff_in, R.anim.puff_in, R.anim.puff_in, R.anim.puff_out);
////        ft.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top, R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom);
//        ft.add(id, fragment);
//        ft.addToBackStack(tag);
////        ft.commit();
//        ft.commitAllowingStateLoss();
//    }

    /**
     * @param bundle
     * @param id
     * @param fragment
     * @param tag
     * @param enterAni 进入动画
     * @param exitAni  退出动画
     */
    public void gotoFragmentByAnimation(Bundle bundle, int id, Fragment fragment, String tag, int enterAni, int exitAni) {
        if (bundle != null)
            fragment.setArguments(bundle);
        FragmentManager fMgr = getFragmentManager();
        if (fMgr != null) {
            FragmentTransaction ft = fMgr.beginTransaction();
            ft.setCustomAnimations(enterAni, exitAni, enterAni, exitAni);
            ft.add(id, fragment);
            ft.addToBackStack(tag);
            ft.commitAllowingStateLoss();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HttpRequestUtil.cancelHttpRequest();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//umeng统计页面
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
//umeng统计页面
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    public FragmentActivity getBaseAct() {
        FragmentActivity mmAct = getActivity();
        if (mmAct == null) {
            mmAct = mAct;
        }
        return mmAct;
    }

    /**
     * 设置各个控制的监听
     */
    protected abstract void setWidgetLsn();

    protected abstract void initWidget(View v);

    public abstract boolean onBackPressed();
}
