package com.shequcun.farm.dlg;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.shequcun.farm.R;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mac on 15/10/12.
 */
public class UserGuideDialog extends Dialog implements BaseSliderView.OnSliderClickListener {

    @Bind(R.id.slider)
    SliderLayout slider;

    public UserGuideDialog(Context context) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.ucai_guide_ly);
//        initViewPager();
        ButterKnife.bind(this, findViewById(R.id.guide_ly));
        initSlider();
    }

    private void initSlider() {
        for (int resId : pics) {
            addSliderUrl(resId);
        }
        slider.setPagerTransformer(false, new BaseTransformer() {
            @Override
            protected void onTransform(View view, float position) {
                //空是为了防止loop
            }
        });
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
    }

    private void addSliderUrl(int resId) {
        DefaultSliderView textSliderView = new DefaultSliderView(getContext());
        // initialize a SliderLayout
        textSliderView
                .description("")
                .image(resId)
                .setScaleType(BaseSliderView.ScaleType.Fit)
                .setOnSliderClickListener(this);
        if (resId == R.drawable.guide3)
            textSliderView.setParamObj(new Object());
        slider.addSlider(textSliderView);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        if (slider.getParamObj() != null)
            dismiss();
    }

//    private void initViewPager() {
//        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
//        views = new ArrayList<View>();
////        欢迎页面
//
//        for (int i = 0; i < pics.length; i++) {
//            ImageView iv = new ImageView(getContext());
//            LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
//            iv.setLayoutParams(mParams);
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//                iv.setBackground(getContext().getResources().getDrawable(pics[i]));
//            } else {
//                iv.setBackgroundDrawable(getContext().getResources().getDrawable(pics[i]));
//            }
//
//
//            views.add(iv);
//        }
//        vpAdapter = new ViewPagerAdapter(views);
//        viewPager.setAdapter(vpAdapter);
//        setOnclick();
//    }

    private void setOnclick() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 3) {
                    dismiss();
                    PersistanceManager.saveOnce(getContext(), true);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private int[] pics = {R.drawable.guide1, R.drawable.guide2,
            R.drawable.guide3};
    private ViewPagerAdapter vpAdapter;
    private ViewPager viewPager;
    private ArrayList<View> views;
}
