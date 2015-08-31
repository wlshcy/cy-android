package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitmap.cache.ImageCacheManager;
import com.shequcun.farm.R;
import com.shequcun.farm.data.RecommendEntry;
import com.shequcun.farm.util.AvoidDoubleClickListener;

/**
 * Created by apple on 15/8/19.
 */
public class RecommendGoodsDetailsFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recommended_product_details_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        goods_img = (ImageView) v.findViewById(R.id.goods_img);
        close_iv = (ImageView) v.findViewById(R.id.close_iv);
        goods_name = (TextView) v.findViewById(R.id.goods_name);
        goods_prce = (TextView) v.findViewById(R.id.goods_prce);
        goods_desc = (TextView) v.findViewById(R.id.goods_desc);
        pView = v.findViewById(R.id.pView);
    }

    @Override
    protected void setWidgetLsn() {
        close_iv.setOnClickListener(onClk);
        pView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popBackStack();
            }
        });
        setWidgetContent();
    }

    private void setWidgetContent() {
        RecommendEntry entry = buildRecommendEntry();
        if (entry != null) {
            ImageCacheManager.getInstance().displayImage(goods_img, entry.imgs[0]);
            goods_name.setText(entry.title);
            goods_prce.setText((double) entry.price / 100 + "元/份");
            goods_desc.setText(entry.descr);
        }
    }

    AvoidDoubleClickListener onClk = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == close_iv)
                popBackStack();
        }
    };

    RecommendEntry buildRecommendEntry() {
        Bundle bundle = getArguments();
        return bundle != null ? (RecommendEntry) bundle.getSerializable("RecommendEntry") : null;
    }

    ImageView goods_img;
    ImageView close_iv;
    TextView goods_name;
    TextView goods_prce;
    TextView goods_desc;
    View pView;
}
