package com.shequcun.farm.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shequcun.farm.BaseFragmentActivity;
import com.shequcun.farm.R;
import com.shequcun.farm.ui.adapter.HomeViewPagerAdapter;

/**
 * farm home
 * Created by apple on 15/8/3.
 */
public class SqcFarmActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
    }

    private void initWidget() {
        hVpager = (HomeViewPager) findViewById(R.id.hVpager);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        buildAdapter();
        setWidgetLsn();
    }

    void setWidgetLsn() {
        radiogroup.setOnCheckedChangeListener(checkedChangeListener);
        // mHomeViewPager.clearAnimation();
        hVpager.setOnPageChangeListener(pageChangeLsn);
        buildRadioButtonStatus(0);
    }

    private void buildAdapter() {
        if (hAdapter == null)
            hAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        hVpager.setAdapter(hAdapter);
    }

    private ViewPager.OnPageChangeListener pageChangeLsn = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            buildRadioButtonStatus(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * @param index
     */
    private void buildRadioButtonStatus(int index) {
        RadioButton rb = ((RadioButton) radiogroup.getChildAt(index));
        rb.setChecked(true);
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId) {
                case R.id.combo_rb:// 套餐
                    onPageChanged(0);
                    break;
                case R.id.mine_rb:// 我的
                    onPageChanged(1);
                    break;
                default:
                    break;
            }

        }
    };

    public void onPageChanged(int pageIndex) {
        hVpager.setCurrentItem(pageIndex);
    }

    HomeViewPager hVpager;
    HomeViewPagerAdapter hAdapter;
    RadioGroup radiogroup;
}
