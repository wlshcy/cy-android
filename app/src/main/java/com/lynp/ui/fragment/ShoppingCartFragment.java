package com.lynp.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.ExpandableHeightListView;

import com.lynp.R;
import com.lynp.ui.util.CacheManager;
import com.lynp.ui.util.PhotoModel;
import com.lynp.ui.adapter.ShoppingCartAdapter;
//import com.shequcun.farm.ui.fragment.BrowseImageFragment;
//import com.shequcun.farm.ui.fragment.RemarkFragment;
import com.lynp.ui.util.IntentUtil;
import com.lynp.ui.util.ResUtil;
import com.lynp.ui.util.Utils;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.lynp.ui.db.DataBase;
import com.lynp.ui.data.ItemDetailEntry;
import com.lynp.ui.data.OrderEnsureEntry;

import butterknife.Bind;

/**
 * 原味购物车
 * Created by nmg on 16/1/28.
 */
public class ShoppingCartFragment extends BaseFragment implements RemarkFragment.CallBackLsn {
//public class ShoppingCartFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shopping_cart_ui, container, false);
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        v.findViewById(R.id.back).setVisibility(View.GONE);
        title_center_text.setText(R.string.shop_cart);
//        Log.i("init", "init widget");
    }

    @Override
    protected void setWidgetLsn() {
        addWidgetToView();
        doRegisterRefreshBrodcast();
    }

    void addWidgetToView() {
        removeWidgetFromView();
        List<ItemDetailEntry> items = new DataBase(getActivity()).getItems();
        if(items != null && items.size()>0){
            buildAdapter();
            addDataToAdapter(items);
        }
        else {
            resetWidgetStatus();
        }
    }


    void resetWidgetStatus() {
        if (adapter != null) {
            adapter.clear();
        }
        if (footerView != null) {
            mLv.removeFooterView(footerView);
            footerView = null;
        }

        if (headView != null) {
            mLv.removeHeaderView(headView);
            headView = null;
        }
        if (footerViewRemark != null) {
            mLv.removeFooterView(footerViewRemark);
            footerViewRemark = null;
        }
        if (bottomView != null) {
            pView.removeView(bottomView);
            bottomView = null;
        }
        memo = null;
    }

    void removeWidgetFromView() {
        if (shopCartView != null) {
            pView.removeView(shopCartView);
            shopCartView = null;
        }

        if (noLoginView != null) {
            pView.removeView(noLoginView);
            noLoginView = null;
        }
    }

    /**
     * 是否登录成功
     *
     * @return
     */
    boolean isLogin() {
        return new CacheManager(getBaseAct()).getUserLoginEntry() != null;
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new ShoppingCartAdapter(getBaseAct());
        adapter.clear();
        adapter.buildOnClickLsn(onLookDtlLsn, onGoodsImgLsn, onAddGoodsLsn, onSubGoodsLsn);
        mLv.setAdapter(adapter);
        mLv.setExpanded(true);
    }

    View.OnClickListener onLookDtlLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (adapter == null || position >= adapter.getCount() || adapter.getItem(position) == null)
                return;
            ItemDetailEntry entry = adapter.getItem(position);
//
        }
    };

//    Bundle buildBundle(RecommendEntry entry) {
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("RecommentEntry", entry);
//        return bundle;
//    }

    void addDataToAdapter(List<ItemDetailEntry> items) {

//        Log.i("******", items.toString());

        for (int i = 0; i < items.size(); ++i) {
            totalPrice += items.get(i).count * items.get(i).price;
        }

        adapter.addAll(items);
        adapter.notifyDataSetChanged();
//        addFreightTip();
        addBottomView();
        updateWidget(totalPrice);
        pScrollView.fullScroll(View.FOCUS_UP);
    }

    View.OnClickListener onGoodsImgLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            ArrayList<PhotoModel> photos = new ArrayList<PhotoModel>();
            Bundle bundle = new Bundle();
//            bundle.putSerializable(BrowseImageFragment.KEY_PHOTOS, photos);
//            bundle.putInt(BrowseImageFragment.KEY_INDEX, position);
//            gotoFragmentByAnimation(bundle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName(), R.anim.puff_in, R.anim.puff_out);
        }
    };


    View.OnClickListener onAddGoodsLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (position >= adapter.getCount() || adapter.getItem(position) == null)
                return;
            View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount() - mLv.getFirstVisiblePosition());
            if (pView == null)
                return;
            TextView countView = (TextView) pView.findViewById(R.id.goods_count);
            if (countView.getVisibility() == View.GONE) {
                countView.setVisibility(View.VISIBLE);
            }
            ImageView subView = (ImageView) pView.findViewById(R.id.goods_sub);
            if (subView.getVisibility() == View.GONE) {
                subView.setVisibility(View.VISIBLE);
            }
            int count = Integer.parseInt(countView.getText().toString()) + 1;
            ItemDetailEntry item = adapter.getItem(position);
            totalPrice += item.price;
            updateWidget(totalPrice);
            countView.setText(String.valueOf(count));
            new DataBase(getActivity()).updateCount(item.id, count);
            }
    };
    View.OnClickListener onSubGoodsLsn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            if (position >= adapter.getCount() || adapter.getItem(position) == null)
                return;
            View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount() - mLv.getFirstVisiblePosition());
            if (pView == null)
                return;
            TextView countView = (TextView) pView.findViewById(R.id.goods_count);
            if (countView.getVisibility() == View.GONE) {
                countView.setVisibility(View.VISIBLE);
            }
            ImageView subView = (ImageView) pView.findViewById(R.id.goods_sub);
            if (subView.getVisibility() == View.GONE) {
                subView.setVisibility(View.VISIBLE);
            }
            int count = Integer.parseInt(countView.getText().toString()) - 1;

            ItemDetailEntry item = adapter.getItem(position);

            if (count == 0) {
                adapter.remove(item);
                adapter.notifyDataSetChanged();
                new DataBase(getActivity()).deleteItem(item.id);

                if (adapter.getCount() < 1) {
                    addWidgetToView();
                }
            } else {
//                new CacheManager(getBaseAct()).saveRecommendToDisk(zItem);
            }
            totalPrice -= item.price;
            updateWidget(totalPrice);
            new DataBase(getActivity()).updateCount(item.id, count);
            countView.setText(String.valueOf(count));
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnRegisterReceiver();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.i("*******", "visible");
        } else {
            Log.i("*******", "invisible");
        }
    }

    void updateWidget(float totalPrice) {
        Spannable spannable = Utils.getSpanableSpan("商品总价:", Utils.unitPeneyToYuan(totalPrice), ResUtil.dip2px(getBaseAct(), 18), ResUtil.dip2px(getBaseAct(), 18), getResources().getColor(R.color.gray_a9a9a9), getResources().getColor(R.color.red_f36043));
        total_price.setText(spannable);
    }

    void doRegisterRefreshBrodcast() {
        if (!mIsBind) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(IntentUtil.UPDATE_SHOPPING_CART_MSG);
            getBaseAct().registerReceiver(mUpdateReceiver, intentFilter);
            mIsBind = true;
        }
    }

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    return;
                }

                if (action.equals(IntentUtil.UPDATE_SHOPPING_CART_MSG)) {
                    addWidgetToView();
                }
            } catch (Exception e) {

            }

        }
    };

    private void doUnRegisterReceiver() {
        if (mIsBind) {
            getBaseAct().unregisterReceiver(mUpdateReceiver);
            mIsBind = false;
        }
    }

    void addBottomView() {
        if (bottomView == null) {
            bottomView = LayoutInflater.from(getBaseAct()).inflate(R.layout.bottom_widget_ui, null);
            total_price = (TextView) bottomView.findViewById(R.id.total_price);
            bottomView.findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<ItemDetailEntry> items = new DataBase(getActivity()).getItems();
                    OrderEnsureEntry entry = new OrderEnsureEntry();
                    entry.items = items;
                    entry.price = totalPrice;
                    entry.freight = (totalPrice >=5) ? 0 : 5;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("entry", entry);
                    gotoFragmentByAdd(bundle, R.id.mainpage_ly, new OrderEnsureFragment(), OrderEnsureFragment.class.getName());
                }
            });

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.bottomMargin = ResUtil.dipToPixel(getBaseAct(), 0);
            pView.addView(bottomView, params);
        }
    }

    void addFreightTip() {
        if (FreightTip == null) {
            FreightTip = LayoutInflater.from(getBaseAct()).inflate(R.layout.freight_tip, null);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.topMargin = ResUtil.dipToPixel(getBaseAct(), 51);
            pView.addView(FreightTip, params);
        }
    }
    @Override
    public void updateRemarkWidget(String remark) {
        memo = remark;
        if (remark_tv != null)
            remark_tv.setText(remark);
    }

    boolean mIsBind = false;
    ShoppingCartAdapter adapter;
    View shopCartView;
    @Bind(R.id.pView)
    RelativeLayout pView;

    TextView total_price;
    @Bind(R.id.mLv)
    ExpandableHeightListView mLv;
    @Bind(R.id.title_center_text)
    TextView title_center_text;
    TextView number_copies;
    TextView remark_tv;
    TextView freight_money_tv;
    View footerView;

    View footerViewRemark;
//    //总份数
//    private int allPart = 0;
    //总价格
    private float totalPrice = 0;

    @Bind(R.id.pScrollView)
    ScrollView pScrollView;
    String memo;
//    CouponEntry cEntry;
    View noLoginView;
    View headView;
    View bottomView;
    View FreightTip;
}
