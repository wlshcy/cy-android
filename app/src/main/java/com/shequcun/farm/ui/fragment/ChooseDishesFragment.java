package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.BadgeView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.anim.ArcTranslateAnimation;
import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.data.goods.DishesListItemEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.model.PhotoModel;
import com.shequcun.farm.ui.adapter.ChooseDishesAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择菜品页
 * Created by apple on 15/8/10.
 */
public class ChooseDishesFragment extends BaseFragment {
    private int deviceWidth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_dishes_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.choose_dishes);
        back = v.findViewById(R.id.back);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        rightTv.setText(R.string.consultation);
        rootView = (FrameLayout) v.findViewById(R.id.root_view);
        footShopCartLl = (LinearLayout) v.findViewById(R.id.foot_shop_cart_ll);
        mShopCartClearTv = (TextView) v.findViewById(R.id.shop_cart_clear_tv);
        emptyView = v.findViewById(R.id.empty_view);
        mOrderController = DisheDataCenter.getInstance();
        mBuyOrderTv = (TextView) v.findViewById(R.id.bug_order_tv);
        mShopCartIv = (ImageView) v.findViewById(R.id.shop_cart_iv);
        mBadgeViewShopCart = new BadgeView(getActivity(), mShopCartIv);
        mBadgeViewShopCart.setWidth(ResUtil.dip2px(getActivity(), 20));
        mBadgeViewShopCart.setHeight(ResUtil.dip2px(getActivity(), 20));
        mBadgeViewShopCart.setBackgroundResource(R.drawable.red_oval);
        int height = mBadgeViewShopCart.getHeight();
        mBadgeViewShopCart.setCornerPx(height * 2);
        mBadgeViewShopCart.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                ResUtil.dip2px(getActivity(), 10));
        mBadgeViewShopCart.setBadgeMargin(ResUtil.dip2px(getActivity(), 0));
        mShopCartPriceTv = (TextView) v
                .findViewById(R.id.shop_cart_total_price_tv);
        buildAdapter();
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        rightTv.setOnClickListener(onClick);
        mShopCartClearTv.setOnClickListener(onClick);
        mShopCartIv.setOnClickListener(onClick);
        mBuyOrderTv.setOnClickListener(onClick);
        emptyView.setOnClickListener(onClick);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requsetDishesList();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back) {
                popBackStack();
            } else if (v == rightTv) {
                ConsultationDlg.showCallTelDlg(getActivity());
            } else if (v == mShopCartClearTv) {//清空购物车
                hideShopCart();
                clearBadeView(mOrderController.getItemsCount());
                mOrderController.clear();
                updateShopCartDataToView();
            } else if (v == mShopCartIv) {
                if (mShopCartClearTv.getVisibility() == View.GONE) {
                    popupShoppingCart();
                } else {
                    hideShopCart();
                }
            } else if (v == mBuyOrderTv) {//
                gotoFragmentByAdd();
//                gotoFragmentByAdd(R.id.mainpage_ly,new OrderDetailsFragment(),OrderDetailsFragment.class.getName());
            } else if (v == emptyView) {
                hideShopCart();
            }
        }
    };

    int buildRequestID() {//需要完善
        mOrderController.setReqWeight(0);
        mBuyOrderTv.setText("选择" + mOrderController.getReqWeight() + "g菜");
        return 0;
    }

    void requsetDishesList() {
        int id = buildRequestID();
        RequestParams params = new RequestParams();
        params.add("combo_id", id + "");
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "cai/itemlist", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    DishesListItemEntry entry = JsonUtilsParser.fromJson(new String(data), DishesListItemEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            doAddDataToAdapter(entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "错误码 " + sCode);
            }

            @Override
            public void onStart() {
                super.onStart();
                if (pDlg != null)
                    pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (pDlg != null)
                    pDlg.dismiss();
            }
        });
    }

    private void doAddDataToAdapter(List<DishesItemEntry> aList) {
        if (aList != null && aList.size() > 0) {
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }

    }

    void gotoFragmentByAdd() {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data == null || data.length <= 0) {
            gotoFragmentByAdd(R.id.mainpage_ly, new LoginFragment(), LoginFragment.class.getName());
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("comboIdx", 0);//需要完善
            gotoFragmentByAdd(R.id.mainpage_ly, new OrderDetailsFragment(), OrderDetailsFragment.class.getName());
        }
    }


    void buildAdapter() {
        if (adapter == null)
            adapter = new ChooseDishesAdapter(getActivity());
        adapter.buildOnClickLsn(onGoodsImgLsn, mUpOnClickListener, mDownOnClickListener);
        mLv.setAdapter(adapter);
    }

    AvoidDoubleClickListener onGoodsImgLsn = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (adapter == null)
                return;
            int position = (int) v.getTag();
            ArrayList<PhotoModel> photos = new ArrayList<PhotoModel>();
            for (int i = 0; i < adapter.getItem(position).imgs.length; ++i) {
                photos.add(new PhotoModel(true, adapter.getItem(position).imgs[i]));
            }

            Bundle budle = new Bundle();
            budle.putSerializable(BrowseImageFragment.KEY_PHOTOS, photos);
            budle.putInt(BrowseImageFragment.KEY_INDEX, position);
            gotoFragmentByAdd(budle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName());
        }
    };

//    AvoidDoubleClickListener onAddGoodsLsn = new AvoidDoubleClickListener() {
//        @Override
//        public void onViewClick(View v) {
//            int position = (int) v.getTag();
//        }
//    };
//
//    AvoidDoubleClickListener onSubGoodsLsn = new AvoidDoubleClickListener() {
//        @Override
//        public void onViewClick(View v) {
//            int position = (int) v.getTag();
//        }
//    };


    //    /**
//     * 更新Adapter的某一项条目
//     *
//     * @param position
//     */
//    private void updateAdapterItem(final int position) {
//        if (position >= adapter.getCount() || adapter.getItem(position) == null)
//            return;
//        View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount()
//                - mLv.getFirstVisiblePosition());
//        if (pView == null)
//            return;
//        TextView goods_count = (TextView) pView.findViewById(R.id.goods_count);
//        if (goods_count == null)
//            return;
//    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mOrderController != null) {
            mOrderController.release();
            mOrderController = null;
        }
    }

    /**
     * 弹出购物车
     */
    private void popupShoppingCart() {
        emptyView.setVisibility(View.VISIBLE);
//        显示清空全部
        mShopCartClearTv.setVisibility(View.VISIBLE);
//        设定弹出的购物车容器底边距为底部栏高度
        LinearLayout popupShopCartLl = (LinearLayout) rootView
                .findViewById(R.id.shop_cart_container_ll);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) popupShopCartLl
                .getLayoutParams();
        lp.bottomMargin = footShopCartLl.getHeight();
        popupShopCartLl.setLayoutParams(lp);
//        添加滑动view到购物车容器
        ScrollView scrollView = (ScrollView) LayoutInflater.from(getActivity())
                .inflate(R.layout.shop_cart_popup, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        popupShopCartLl.addView(scrollView, lp1);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
//        滑动容器中的linearLayout添加item
        containerLl = (LinearLayout) scrollView.findViewById(R.id.container_ll);
        for (DishesItemEntry it : mOrderController.getItems()) {
            LinearLayout view = (LinearLayout) LayoutInflater
                    .from(getActivity()).inflate(R.layout.good_item_popup, null);
            TextView priceTv = (TextView) view.findViewById(R.id.good_price_tv);
            priceTv.setVisibility(View.GONE);
//            priceTv.setText(String.valueOf(it.getTotalPrice()));
            TextView countTv = (TextView) view.findViewById(R.id.good_count_tv);
            countTv.setText(String.valueOf(it.getCount()));
            TextView nameTv = (TextView) view.findViewById(R.id.good_name_tv);
            ImageView downIv = (ImageView) view
                    .findViewById(R.id.good_count_down_iv);
            ImageView upIv = (ImageView) view
                    .findViewById(R.id.good_count_up_iv);
            nameTv.setText(it.title);
            upIv.setTag(it.id);
            upIv.setOnClickListener(mUpOnClickListenerInShopCart);
            downIv.setTag(it.id);
            downIv.setOnClickListener(mDownOnClickListenerInShopCart);
            containerLl.addView(view);
//            View lineView = LayoutInflater
//                    .from(getActivity()).inflate(R.layout.common_line, null);
//            containerLl.addView(lineView, lp2);
        }
    }

    private AvoidDoubleClickListener mUpOnClickListener = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v.getTag() instanceof Integer) {
                int position = (int) v.getTag();
                DishesItemEntry goodItem = adapter.getItem(position);
                if (checkReqWeight(goodItem.packw)) {
                    return;
                }
                if (checkMaxpacks(goodItem.id)) {
                    return;
                }
            }
            animationFly(v);
        }
    };

    /**
     * 红点飞的动画
     */
    private void animationFly(final View v) {
//      获取开始点为增加图位置
        int sXY[] = new int[]{0, 0};
        v.getLocationOnScreen(sXY);
//        结束点为购物车图位置
        int fXY[] = new int[]{0, 0};
        mShopCartIv.getLocationOnScreen(fXY);
        // logger.error("x1:" + fXY[0] + "y1:" + fXY[1]);
//        新创建一个红色小球
        final TextView flyTv = new TextView(getActivity());
        int flyWidth = ResUtil.dip2px(getActivity(), 20);
        int flyHeight = ResUtil.dip2px(getActivity(), 20);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(flyWidth,
                flyHeight);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        // Log.e(TAG,
        // "deviceWidth:"+getDeviceWidth()+"deviceHeight:"+getDeviceHeight());
//        红色小球左边距＝设备宽度－开始点
        int rmargin = getDeviceWidth() - sXY[0];
        int bmargin = getDeviceHeight() - sXY[1];
        flyTv.setBackgroundColor(Color.RED);
        flyTv.setTextColor(Color.WHITE);
        flyTv.setGravity(Gravity.CENTER);
        flyTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        flyTv.setBackgroundResource(R.drawable.red_oval);
        // logger.error("rmargin:"+rmargin+"bmargin:"+bmargin);
        lp.rightMargin = rmargin;
        lp.bottomMargin = bmargin;
        flyTv.setText("1");
//        添加红色小球到根视图
        rootView.addView(flyTv, lp);
        // logger.error("getTitleHeight:" + getTitleHeight());
//动画开始点横坐标为：结束点－开始点+购物车图一半+小球一半
//动画开始点纵坐标为：容器高度－开始点＋购物车图一半+小球一半
        ArcTranslateAnimation arcAnim = new ArcTranslateAnimation(0, fXY[0]
                - sXY[0] + mShopCartIv.getWidth() / 2 + flyWidth / 2, 0,
                rootView.getHeight() - sXY[1] + mShopCartIv.getHeight() / 2
                        + flyHeight / 2);
        // logger.error("落点x:" + (sXY[0] - getDeviceWidth()));
        // logger.error("落点y:" + (getDeviceHeight() - sXY[1]));
//        贝赛尔曲线
        arcAnim.setControl(new PointF(0, ResUtil.dip2px(getActivity(), -200)));
        arcAnim.setDuration(700);
        arcAnim.setFillAfter(true);
        arcAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
//                移除飞红球
                rootView.removeView(flyTv);
                shopChartIconScaleAnimation(v);
            }
        });
        flyTv.startAnimation(arcAnim);
    }


    private void hideShopCart() {
        mShopCartClearTv.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        LinearLayout popupShopCartLl = (LinearLayout) rootView
                .findViewById(R.id.shop_cart_container_ll);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) popupShopCartLl
                .getLayoutParams();
        lp.bottomMargin = 0;
        popupShopCartLl.setLayoutParams(lp);
        popupShopCartLl.removeAllViews();
    }

    /**
     * 隐藏数量红点
     */
    private void clearBadeView(int num) {
        mBadgeViewShopCart.decrement(num);
        mBadgeViewShopCart.hide();
    }

    /**
     * 根据购物车数据更新界面
     */
    public void updateShopCartDataToView() {
//        for (List<Item> list : childList) {
//            for (Item goodItem : list) {
//                Item item = mOrderController.getItemById(goodItem.getId());
//                if (item != null) {
//                    goodItem.setCount(item.getCount());
//                } else {
//                    goodItem.setCount(0);
//                }
//            }
//        }
        adapter.notifyDataSetChanged();
        updateShopCartWidgetStatus();
    }

    public int getDeviceWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public int getDeviceHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 更新购物车相关控件状态
     */
    private void updateShopCartWidgetStatus() {
        int count = mOrderController.getItemsCount();
        if (count > 0) {
            mBadgeViewShopCart.setText(null);
            mBadgeViewShopCart.increment(count);
            mBadgeViewShopCart.show();
        } else {
            mBadgeViewShopCart.setText("0");
            mBadgeViewShopCart.hide();
        }
        updateBuyOrderStatus();
        toggleTotalPrice();
    }

    /**
     * 更新下单按钮状态和数量红点的可见性
     */
    private void updateBuyOrderStatus() {
        if (mOrderController.getItemsCount() <= 0) {
            mBadgeViewShopCart.hide();
        }
        if (mOrderController.reachReqWeight()) {
            toggleBuyOrder(true);
        } else {
            toggleBuyOrder(false);
        }
    }


    /**
     * 转换下单按钮的可见性
     */
    private void toggleBuyOrder(boolean buy) {
        if (buy) {
            mBuyOrderTv
                    .setBackgroundResource(R.drawable.shopping_cart_widget_selector_1);
            mBuyOrderTv.setText(R.string.small_market_buy);
            mBuyOrderTv.setTextColor(getResources().getColor(
                    R.color.white_fefefe));
//            mBuyOrderTv.setEnabled(true);
        } else {
            mBuyOrderTv
                    .setBackgroundResource(R.drawable.shopping_cart_widget_selector_2);
            String txt = getResources().getString(
                    R.string.small_market_buy_not_enough);
            txt = txt.replaceAll(
                    "A",
                    String.valueOf((int) (mOrderController.getReqWeight()
                            - mOrderController.getItemsWeight())));
            mBuyOrderTv.setText(txt);
            mBuyOrderTv.setTextColor(getResources().getColor(android.R.color.black));
//            mBuyOrderTv.setEnabled(false);
        }
    }


    /**
     * 转换总价格
     */
    private void toggleTotalPrice() {
        float totalPrice = mOrderController.getTotalPrice();
        if (totalPrice > 0) {
//            String txt = getResources().getString(
//                    R.string.small_market_shop_cart_price);
//            String p = txt.replaceAll("A", String.valueOf(totalPrice));
            mShopCartPriceTv.setText("共" + mOrderController.getItemsCount() + "份");
        } else {
            mShopCartPriceTv.setText(R.string.small_market_shop_cart_null);
        }
    }

    /**
     * 点击＋时刷新UI
     */
    private void upCountUpdateUI(View v) {
        if (v.getTag() instanceof Integer) {
            int position = (int) v.getTag();
            View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount()
                    - mLv.getFirstVisiblePosition());
            if (pView == null)
                return;
            TextView tvCount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
            if (tvCount.getVisibility() == View.GONE) {
                tvCount.setVisibility(View.VISIBLE);
            }
            ImageView ivDown = (ImageView) pView.findViewById(R.id.goods_sub);
            if (ivDown.getVisibility() == View.GONE) {
                ivDown.setVisibility(View.VISIBLE);
            }
            String count = tvCount.getText().toString();
//        数量加一
            int intCount = Integer.parseInt(count) + 1;
//        获取标记
//            String str[] = ((String) v.getTag()).split("/");
//            int groupPos = Integer.valueOf(str[0]);
//            int childPos = Integer.valueOf(str[1]);
            DishesItemEntry goodItem = adapter.getItem(position);
            if (!mOrderController.getItems().contains(goodItem)) {
                mOrderController.addItem(goodItem);
            }
            goodItem.setCount(intCount);
//        // logger.error("groupPos:"+groupPos+" childPos:"+childPos);
////        添加一项到控制器
//        if (intCount > 0) {
//            String id = str[2];// 作为商品的唯一标记，防止后台改变数据
//            根据id从控制器中获取item
//            DishesItemEntry item = mOrderController.getItemById(id);
////            复制一份item到控制器
////            if (item == null) {
////                // logger.error("price:"+goodItem.getPrice());
////                item = new Item();
////                item.setImg(goodItem.getImg());
////                item.setTitle(goodItem.getTitle());
////                item.setId(id);
////                item.setPrice(goodItem.getPrice());
////                mOrderController.addItem(item);
////            }
////            更新数量

//            mOrderController.addItem(goodItem);
//        }
//        刷新数量
            tvCount.setText(String.valueOf(intCount));
//        红点数量加一
            setBadgeView(true);
//        更新下单按钮状态
            updateBuyOrderStatus();
//        更新总价格
//            toggleTotalPrice();
        }
    }

    private boolean checkReqWeight(int lastWeight) {
        /*超过最大要求份数*/
        int i = mOrderController.outOfReqWeight(lastWeight);
        if (i > 0) {
            /*异常不会出现的情况*/
            if (i > lastWeight) {
                return false;
                /*举例：选了10，要求10，再选*/
            } else if (i == lastWeight) {
                alertOutOfReqWeight();
                return true;
/*举例：选了8，要求10，再选>2的*/
            } else {
                alertOutOfReqWeight1();
                return true;
            }
        } else if (i == 0) {
            return false;
        }
        return false;
    }

    private boolean checkMaxpacks(int id) {
        /*超过该品最大份数*/
        if (mOrderController.outOfMaxpacks(id)) {
            alertOutOfMaxpacks(mOrderController.getMaxpacksById(id));
            return true;
        } else {
            /*超过剩余g数*/
            if (mOrderController.outOfRemainWeight(id)) {
                alertOutOfRemains();
                return true;
            }
        }
        return false;
    }

    private void alertOutOfReqWeight() {
        String content = getResources().getString(R.string.out_of_required_weight);
        content = content.replace("A", mOrderController.getReqWeight() + "g");
        alertDialog(content);
    }

    private void alertOutOfReqWeight1() {
        String content = getResources().getString(R.string.out_of_required_weight1);
        content = content.replace("A", mOrderController.getReqWeight() + "g");
        alertDialog(content);
    }

    private void alertOutOfMaxpacks(int maxpacks) {
        String content = getResources().getString(R.string.out_of_maxpacks);
        content = content.replace("A", maxpacks + "");
        alertDialog(content);
    }

    private void alertOutOfRemains() {
        String content = getResources().getString(R.string.out_of_remains);
        alertDialog(content);
    }

    private void alertDialog(String content) {
        final AlertDialog alert = new AlertDialog.Builder(getFragmentActivity()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.alert_dialog);
        TextView tv = (TextView) alert.getWindow().findViewById(R.id.content_tv);
        tv.setText(content);
        alert.getWindow().findViewById(R.id.ok_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alert.dismiss();
                    }
                });
    }


    /**
     * 减号被点击
     */
    private AvoidDoubleClickListener mDownOnClickListener = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {

            if (v.getTag() instanceof Integer) {
                int position = (int) v.getTag();

                if (position >= adapter.getCount() || adapter.getItem(position) == null)
                    return;
                View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount()
                        - mLv.getFirstVisiblePosition());
                if (pView == null)
                    return;
                TextView tvCount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
                if (tvCount.getVisibility() == View.GONE) {
                    tvCount.setVisibility(View.VISIBLE);
                }
                ImageView ivDown = (ImageView) pView.findViewById(R.id.goods_sub);
                if (ivDown.getVisibility() == View.GONE) {
                    ivDown.setVisibility(View.VISIBLE);
                }
                String count = tvCount.getText().toString();
                int intCount = Integer.parseInt(count) - 1;
                DishesItemEntry goodItem = adapter.getItem(position);
                goodItem.setCount(intCount);
                mOrderController.removeItemById(goodItem.id);
//                mOrderController.addItem(goodItem);

                if (intCount == 0) {
//                数量减为0，则隐藏减号和选择的数量
                    ivDown.setVisibility(View.GONE);
                    tvCount.setVisibility(View.GONE);
//                数量减为0，则将item从控制器中移除
                }

                tvCount.setText(String.valueOf(intCount));

            }

            setBadgeView(false);
//        更新下单按钮状态
            updateBuyOrderStatus();
//        更新总价格
            toggleTotalPrice();
        }
    };

    private AvoidDoubleClickListener mUpOnClickListenerInShopCart = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            int id = (int) (v.getTag());
            DishesItemEntry item = mOrderController.getItemById(id);
            if (checkReqWeight(item.amount)) return;
            if (checkMaxpacks(item.id)) return;
            ViewGroup parentView = (ViewGroup) v.getParent();
            TextView tvCount = (TextView) parentView
                    .findViewById(R.id.good_count_tv);
            if (tvCount.getVisibility() == View.GONE) {
                tvCount.setVisibility(View.VISIBLE);
            }
            ImageView ivDown = (ImageView) parentView
                    .findViewById(R.id.good_count_down_iv);
            if (ivDown.getVisibility() == View.GONE) {
                ivDown.setVisibility(View.VISIBLE);
            }
//            String count = tvCount.getText().toString();
//            int intCount = Integer.parseInt(count);
            if (item == null) {
//                    logger.error("异常：item为空");
            } else {
                item.setCount(item.getCount() + 1);
            }
//            TextView priceTv = (TextView) parentView
//                    .findViewById(R.id.good_price_tv);
//            priceTv.setText(String.valueOf(item.get));

            tvCount.setText(String.valueOf(item.getCount()));
            setBadgeView(true);
            updateShopCartDataToView();
            updateBuyOrderStatus();
        }
    };

    private View.OnClickListener mDownOnClickListenerInShopCart = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ViewGroup parentView0 = (ViewGroup) v.getParent();
            ViewGroup parentView = (ViewGroup) parentView0.getParent();
            TextView tvCount = (TextView) parentView
                    .findViewById(R.id.good_count_tv);
            if (tvCount.getVisibility() == View.GONE) {
                tvCount.setVisibility(View.VISIBLE);
            }
            ImageView ivDown = (ImageView) parentView
                    .findViewById(R.id.good_count_down_iv);
            if (ivDown.getVisibility() == View.GONE) {
                ivDown.setVisibility(View.VISIBLE);
            }
            String count = tvCount.getText().toString();
            int intCount = Integer.parseInt(count);
            intCount--;
            int id = (int) (v.getTag());
            DishesItemEntry item = mOrderController.getItemById(id);
            if (item != null) {
                if (intCount == 0) {
                    ivDown.setVisibility(View.GONE);
                    tvCount.setVisibility(View.GONE);
                    mOrderController.removeItemById(id);
                    containerLl.removeView(parentView);
                } else if (intCount < 0) {
                    intCount = 0;
                }
                item.setCount(intCount);
                TextView priceTv = (TextView) parentView
                        .findViewById(R.id.good_price_tv);
//                priceTv.setText(String.valueOf(item.getTotalPrice()));
            }
            // 减单
            tvCount.setText(String.valueOf(intCount));
            setBadgeView(false);
            updateShopCartDataToView();
            updateBuyOrderStatus();
        }
    };

    /**
     * 购物车变大动画
     */
    private void shopChartIconScaleAnimation(View v) {
        Animation scale = new ScaleAnimation(0.6f, 1.2f, 0.6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(200);
        mShopCartIv.startAnimation(scale);
        upCountUpdateUI(v);
    }

    /**
     * 设定数量红点
     */
    private void setBadgeView(boolean increment) {
        if (increment) {
            mBadgeViewShopCart.increment(1);
            mBadgeViewShopCart.show();
        } else {
            mBadgeViewShopCart.decrement(1);
        }
    }

    private TextView mBuyOrderTv;
    private TextView mShopCartPriceTv;
    private LinearLayout footShopCartLl;
    private LinearLayout containerLl;
    private FrameLayout rootView;
    ListView mLv;
    ChooseDishesAdapter adapter;
    View back;
    TextView rightTv;
    TextView mShopCartClearTv;
    View emptyView;
    /**
     * 已选择菜品数据中心
     */
    DisheDataCenter mOrderController;
    BadgeView mBadgeViewShopCart;
    ImageView mShopCartIv;
}
