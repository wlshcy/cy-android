package com.shequcun.farm.dlg;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.common.widget.FlipViewController;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.shequcun.farm.R;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.UserGuideAdapter;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;
import com.shequcun.farm.util.ToastHelper;

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
                ToastHelper.show(getContext(), position + "", Toast.LENGTH_LONG);
            }
        });
        slider.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
        slider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                ToastHelper.show(getContext(),"onPageScrolled"+position+"",Toast.LENGTH_LONG);
            }

            @Override
            public void onPageSelected(int position) {
//                ToastHelper.show(getContext(),"onPageSelected"+position+"",Toast.LENGTH_LONG);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                ToastHelper.show(getContext(),"onPageScrollStateChanged"+state+"",Toast.LENGTH_LONG);
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
//        if (resId == R.drawable.guide3)
//            textSliderView.setParamObj(null);
        slider.addSlider(textSliderView);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
//        if (slider.getParamObj() != null) {
        if (dismissDialog != null) dismissDialog.dismiss();
//            dismiss();
//        }
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

    public interface DismissDialog {
        void dismiss();
    }

    public void setDismissDialog(DismissDialog dismissDialog) {
        this.dismissDialog = dismissDialog;
    }

    private DismissDialog dismissDialog;

    private int[] pics = {R.drawable.guide1, R.drawable.guide2,
            R.drawable.guide3};
    private ViewPagerAdapter vpAdapter;
    private ViewPager viewPager;
    private ArrayList<View> views;
}
