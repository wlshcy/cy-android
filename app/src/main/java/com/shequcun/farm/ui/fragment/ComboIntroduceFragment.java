package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.ViewPagerAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;

import java.util.ArrayList;

/**
 * 套餐介绍
 * Created by apple on 15/8/17.
 */
public class ComboIntroduceFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_introduce_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.combo_introduce);
        right = v.findViewById(R.id.title_right_text);
        ((TextView) v.findViewById(R.id.title_right_text)).setText(R.string.consultation);
        m_PointLLayout = (LinearLayout) v.findViewById(R.id.point_ayout);
        // 实例化ViewPager
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        float density = metrics.density;
        mPadding = (int) (5 * density);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        right.setOnClickListener(onClick);

        viewPager.setOnPageChangeListener(this);
        // 设置适配器数据
        viewPager.setAdapter(vpAdapter);
        addView();
//        // 将要分页显示的View装入数组中
//        views.add(view1);
//        views.add(view2);
//        views.add(view3);
//        views.add(view4);
        vpAdapter.notifyDataSetChanged();

        initPointLayout();
    }


    void addView() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        ComboEntry entry = (ComboEntry) bundle.getSerializable("ComboEntry");
        if (entry == null)
            return;
        String tiles[] = entry.tiles;
        if (tiles == null || tiles.length <= 0) {
            tiles = new String[3];
            tiles[0] = "https://img.shequcun.com/1508/14171/6b5be25cd32741ee9745ee568bdc2136.png";
            tiles[1] = "https://img.shequcun.com/1508/14171/59cccec1a8e4491b937f5838d638f5bf.png";
            tiles[2] = "https://img.shequcun.com/1508/14171/a4cb59ed78d0404bb60519ca68611927.png";
        }


        for (int i = 0; i < tiles.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.combo_introduce_item_ly, null);
            ImageCacheManager.getInstance().displayImage(((ImageView) view.findViewById(R.id.img)), tiles[i]);
            if (i == tiles.length - 1) {
                view.findViewById(R.id.shopping_comobo).setVisibility(View.VISIBLE);
                view.findViewById(R.id.shopping_comobo).setOnClickListener(new AvoidDoubleClickListener() {
                    @Override
                    public void onViewClick(View v) {
                        popBackStack();
                    }
                });
            }
            views.add(view);
        }

    }


    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == right) {
                ConsultationDlg.showCallTelDlg(getActivity());
            }
        }
    };

    View back;
    View right;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setcurrentPoint(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initPointLayout() {
        try {
            m_Count = vpAdapter.getCount();
            m_PointImgs = new ImageView[m_Count];
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            for (int i = 0; i < m_Count; i++) {
                m_PointImgs[i] = new ImageView(getActivity());
                m_PointImgs[i].setLayoutParams(lp);
                m_PointImgs[i].setPadding(mPadding, mPadding, mPadding,
                        mPadding);
                m_PointImgs[i].setImageResource(R.drawable.icon_dot);
                m_PointImgs[i].setEnabled(true);
                m_PointImgs[i].setTag(i);
                m_PointLLayout.addView(m_PointImgs[i], i);
            }
            m_CurrentItem = 0;
            m_PointImgs[m_CurrentItem].setEnabled(false);
            m_PointImgs[m_CurrentItem]
                    .setImageResource(R.drawable.icon_dot_selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setcurrentPoint(int position) {
        try {

            if (position < 0 || position > m_Count - 1
                    || m_CurrentItem == position) {
                return;
            }
            for (int i = 0; i < m_Count; i++) {
                m_PointImgs[i].setImageResource(R.drawable.icon_dot);
            }
            m_PointImgs[m_CurrentItem].setEnabled(true);
            m_PointImgs[position]
                    .setImageResource(R.drawable.icon_dot_selected);
            m_PointImgs[position].setEnabled(false);
            m_CurrentItem = position;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ViewPagerAdapter vpAdapter;
    ArrayList views;
    ViewPager viewPager;

    private LinearLayout m_PointLLayout;
    private ImageView[] m_PointImgs;
    private int m_Count;
    private int m_CurrentItem;
    private int mPadding;
}
