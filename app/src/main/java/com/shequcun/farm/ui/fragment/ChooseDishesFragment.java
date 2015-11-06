package com.shequcun.farm.ui.fragment;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.BadgeView;
import com.common.widget.ExpandableHeightListView;
import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.anim.ArcTranslateAnimation;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.data.FixedComboEntry;
import com.shequcun.farm.data.FixedListComboEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.data.goods.DishesListItemEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.AlertDialog;
import com.shequcun.farm.model.PhotoModel;
import com.shequcun.farm.ui.adapter.ChooseDishesAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.PlaySoundUtils;
import com.shequcun.farm.util.ResUtil;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 选择菜品页
 * Created by apple on 15/8/10.
 */
public class ChooseDishesFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_dishes_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return doPopUpStack();
    }

    @Override
    protected void initWidget(View v) {
        entry = buildEntry();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.choose_dishes);
        ((TextView) v.findViewById(R.id.title_right_text)).setText(R.string.combo_introduce);
        v.findViewById(R.id.title_right_text).setVisibility(isShowComboIntroduce() ? View.VISIBLE : View.GONE);
        mOrderController = DisheDataCenter.getInstance();
        mBadgeViewShopCart = Utils.buildBadgeView(getBaseAct(), mShopCartIv);
        option_dishes_tip.setText(Utils.getSpanableSpan(getResources().getString(R.string.option_dishes_tip), getResources().getString(R.string.option_dishes_tip_1), ResUtil.dipToPixel(getBaseAct(), 14), ResUtil.dipToPixel(getBaseAct(), 14), 0xFF7b7b7b, 0xFFf36043));
        enabled = setChooseDishesContent(v);
        buildAdapter(enabled);
        if (!enabled)
            setWidgetEnableStatus();
        if (isMyCombo()) {
            requsetFixedDishesList(entry.con);
        } else {
            requestFixedCombo(entry.id);
        }
    }

    boolean isMyCombo() {
        if (entry != null)
            return entry.isMine();
        return false;
    }

    boolean isShowComboIntroduce() {
        if (entry != null)
            return entry.tiles != null && entry.tiles.length > 0;
        return false;
    }

    ComboEntry buildEntry() {
        Bundle bundle = getArguments();
        return bundle != null && bundle.containsKey("ComboEntry") ? (ComboEntry) bundle.getSerializable("ComboEntry") : null;
    }

    @Override
    protected void setWidgetLsn() {
        requsetDishesList();
    }

    void doAddRefreshLsn(boolean isEnableRefrsh) {
        if (pView == null)
            return;
        pView.setMode(isEnableRefrsh ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_END);
        pView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (isMyCombo()) {
                    requsetFixedDishesList(entry.con);
                } else {
                    requestFixedCombo(entry.id);
                }
                requsetDishesList();
            }
        });
    }

    @OnClick(R.id.empty_view)
    void doHandleEmptyEvent() {
        doPopUpStack();
    }

    @OnClick(R.id.shop_cart_iv)
    void doHandleShopCartEvent() {
        if (mShopCartClearTv.getVisibility() == View.GONE) {
            hideOptionWidget();
            popupShoppingCart();
        } else
            hideShopCart();
    }

    @OnClick(R.id.option_dishes_tv)
    void doHandleOptionDishesEvent() {
        if (option_dishes_tip.getVisibility() == View.GONE)
            popUpOptionsWidget();
        else
            hideOptionWidget();
    }


    @OnClick(R.id.back)
    void back() {
        if (!doPopUpStack())
            popBackStack();
    }

    @OnClick(R.id.title_right_text)
    void gotoWebViewFragment() {
        gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new WebViewFragment(), WebViewFragment.class.getName());
    }

    boolean doPopUpStack() {
        if ((option_dishes_tip != null && option_dishes_tip.getVisibility() == View.VISIBLE) || (mShopCartClearTv != null && mShopCartClearTv.getVisibility() == View.VISIBLE)) {
            hideShopCart();
            hideOptionWidget();
            return true;
        }
        return false;
    }

    int buildRequestID() {
        if (entry == null)
            return 1;
        if (enabled) {
            mOrderController.setReqWeight(entry.weights[entry.getPosition()]);
            mBuyOrderTv.setText("选择" + Utils.unitConversion(mOrderController.getReqWeight()));
        }
        return entry.id;
    }

    void requsetDishesList() {
        int id = buildRequestID();
        RequestParams params = new RequestParams();
        params.add("combo_id", id + "");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/itemlist", params, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                if (pView != null)
                    pView.onRefreshComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    DishesListItemEntry entry = JsonUtilsParser.fromJson(new String(data), DishesListItemEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            doAddRefreshLsn(true);
                            doAddDataToAdapter(entry.aList);
                            return;
                        }
                        doAddRefreshLsn(false);
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] responseBody, Throwable error) {
                doAddRefreshLsn(false);
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "错误码 " + sCode);
            }
        });
    }

    private void doAddDataToAdapter(List<DishesItemEntry> aList) {
        pView.onRefreshComplete();
        if (aList != null && aList.size() > 0) {
            adapter.clear();
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
        }

        if (PersistanceManager.getIsShowLookUpComboDetails(getBaseAct(), buildKey())) {
            gotoFragmentByAnimation(getArguments(), R.id.mainpage_ly, new ComboMongoliaLayerFragment(), ComboMongoliaLayerFragment.class.getName(), R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
        }
    }

    private String buildKey() {
        return entry != null ? (entry.id + "" + entry.weights[entry.getPosition()]) : null;
    }


    void buildAdapter(boolean enabled) {
        if (adapter == null)
            adapter = new ChooseDishesAdapter(getBaseAct());
        adapter.buildOnClickLsn(enabled, onGoodsImgLsn, mUpOnClickListener, mDownOnClickListener);
        mLv.setAdapter(adapter);
        mLv.setExpanded(true);
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
            gotoFragmentByAnimation(budle, R.id.mainpage_ly, new BrowseImageFragment(), BrowseImageFragment.class.getName(), R.anim.puff_in, R.anim.puff_out);
        }
    };

    /**
     * 弹出备选菜品对话框
     */
    private void popUpOptionsWidget() {
        option_dishes_tip.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.VISIBLE);
        LinearLayout option_container_ll = (LinearLayout) rootView.findViewById(R.id.option_container_ll);
        ScrollView scrollView = (ScrollView) LayoutInflater.from(getBaseAct()).inflate(R.layout.shop_cart_popup, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        option_container_ll.addView(scrollView, lp1);
        containerLl = (LinearLayout) scrollView.findViewById(R.id.container_ll);
        List<DishesItemEntry> aList = new ArrayList<DishesItemEntry>();
        for (int i = 0; i < adapter.getCount(); ++i) {
            aList.add(adapter.getItem(i));
        }
        adapter.addAll();
        for (DishesItemEntry it : mOrderController.getNoChooseDishesItems(aList)) {
            View v = LayoutInflater.from(getBaseAct()).inflate(R.layout.option_item_ly, null);
            ImageView goods_img = (ImageView) v.findViewById(R.id.goods_img);
            ImageLoader.getInstance().displayImage(it.imgs[0] + "?imageview2/2/w/180", goods_img);
            ((TextView) v.findViewById(R.id.goods_name)).setText(it.title);
            ((TextView) v.findViewById(R.id.goods_price)).setText(Utils.unitConversion(it.packw) + "/份");
            final View pRview = v.findViewById(R.id.pRview);
            final CheckBox option_cb = (CheckBox) v.findViewById(R.id.option_cb);
            pRview.setTag(it);
            pRview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    option_cb.toggle();
                    List<DishesItemEntry> dLst = mOrderController.getOptionItems();
                    if (option_cb.isChecked()) {
                        if (dLst != null && dLst.size() < 3) {
                            mOrderController.addOptionItem((DishesItemEntry) pRview.getTag());
                        } else {
                            option_cb.toggle();
                            mOrderController.removeOptionItem((DishesItemEntry) pRview.getTag());
                            new AlertDialog().alertDialog(getBaseAct(), "亲,最多只能选择3个备选菜品哦！");
                        }
                    } else {
                        mOrderController.removeOptionItem((DishesItemEntry) pRview.getTag());
                    }
                }
            });
            List<DishesItemEntry> aaList = mOrderController.getOptionItems();
            if (aaList != null && aaList.size() > 0) {
                for (DishesItemEntry iit : aaList) {
                    if (it.id == iit.id) {
                        option_cb.setChecked(true);
                        break;
                    }
                }
            }
            containerLl.addView(v);
            containerLl.addView(LayoutInflater.from(getBaseAct()).inflate(R.layout.common_line, null));
        }
    }

    private void hideOptionWidget() {
        option_dishes_tip.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        LinearLayout option_container_ll = (LinearLayout) rootView
                .findViewById(R.id.option_container_ll);
        option_container_ll.removeAllViews();
    }

    /**
     * 弹出购物车
     */
    private void popupShoppingCart() {
        emptyView.setVisibility(View.VISIBLE);
        mShopCartClearTv.setVisibility(View.VISIBLE);
        LinearLayout popupShopCartLl = (LinearLayout) rootView
                .findViewById(R.id.shop_cart_container_ll);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) popupShopCartLl
                .getLayoutParams();
        lp.bottomMargin = footShopCartLl.getHeight();
        popupShopCartLl.setLayoutParams(lp);
//        添加滑动view到购物车容器
        ScrollView scrollView = (ScrollView) LayoutInflater.from(getBaseAct())
                .inflate(R.layout.shop_cart_popup, null);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        popupShopCartLl.addView(scrollView, lp1);
        containerLl = (LinearLayout) scrollView.findViewById(R.id.container_ll);
        for (DishesItemEntry it : mOrderController.buildItems()) {
            View v = LayoutInflater.from(getBaseAct()).inflate(R.layout.good_item_popup, null);
            v.findViewById(R.id.good_price_tv).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.good_count_tv)).setText(String.valueOf(it.getCount()));
            ImageView downIv = (ImageView) v.findViewById(R.id.good_count_down_iv);
            ImageView upIv = (ImageView) v.findViewById(R.id.good_count_up_iv);
            ((TextView) v.findViewById(R.id.good_name_tv)).setText(it.title);
            upIv.setTag(it.id);
            upIv.setOnClickListener(mUpOnClickListenerInShopCart);
            downIv.setTag(it.id);
            downIv.setOnClickListener(mDownOnClickListenerInShopCart);
            containerLl.addView(v);
            containerLl.addView(LayoutInflater.from(getBaseAct()).inflate(R.layout.common_line, null));
        }
    }

    private boolean goNext = true;
    private View.OnClickListener mUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!goNext) {
                return;
            }
            if (v.getTag() instanceof Integer) {
                goNext = false;
                int position = (int) v.getTag();
                DishesItemEntry goodItem = adapter.getItem(position);
                if (checkReqWeight(goodItem.packw)) {
                    goNext = true;
                    return;
                }
                if (checkMaxpacks(goodItem.id)) {
                    goNext = true;
                    return;
                }
                goNext = true;
                PlaySoundUtils.doPlay(getBaseAct(), R.raw.pop);
                shopChartIconScaleAnimation(v);
            }
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
//        新创建一个红色小球
        final TextView flyTv = new TextView(getBaseAct());
        int flyWidth = ResUtil.dip2px(getBaseAct(), 20);
        int flyHeight = ResUtil.dip2px(getBaseAct(), 20);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(flyWidth,
                flyHeight);
        lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//        红色小球左边距＝设备宽度－开始点
        int rmargin = DeviceInfo.getDeviceWidth(getBaseAct()) - sXY[0];
        int bmargin = DeviceInfo.getDeviceHeight(getBaseAct()) - sXY[1];
        flyTv.setBackgroundColor(Color.RED);
        flyTv.setTextColor(Color.WHITE);
        flyTv.setGravity(Gravity.CENTER);
        flyTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        flyTv.setBackgroundResource(R.drawable.red_oval);
        lp.rightMargin = rmargin;
        lp.bottomMargin = bmargin;
        flyTv.setText("1");
//        添加红色小球到根视图
        rootView.addView(flyTv, lp);
        ArcTranslateAnimation arcAnim = new ArcTranslateAnimation(0, fXY[0]
                - sXY[0] + mShopCartIv.getWidth() / 2 + flyWidth / 2, 0,
                rootView.getHeight() - sXY[1] + mShopCartIv.getHeight() / 2
                        + flyHeight / 2);
//        贝赛尔曲线
        arcAnim.setControl(new PointF(0, ResUtil.dip2px(getBaseAct(), -200)));
        arcAnim.setDuration(700);
        arcAnim.setFillAfter(true);
        arcAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (rootView != null)
                    rootView.removeView(flyTv);
            }
        });
        flyTv.startAnimation(arcAnim);
    }


    private void hideShopCart() {
        mShopCartClearTv.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        LinearLayout popupShopCartLl = (LinearLayout) rootView.findViewById(R.id.shop_cart_container_ll);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) popupShopCartLl.getLayoutParams();
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
    }

    /**
     * 更新下单按钮状态和数量红点的可见性
     */
    private void updateBuyOrderStatus() {
        if (mOrderController.getItemsCount() <= 0) {
            mBadgeViewShopCart.hide();
        }
        toggleBuyOrder(mOrderController.reachReqWeight());
    }

    /**
     * 转换下单按钮的可见性
     */
    private void toggleBuyOrder(boolean buy) {
        if (buy) {
            mBuyOrderTv.setBackgroundResource(R.drawable.shopping_cart_widget_selector_3);
            mBuyOrderTv.setText(R.string.small_market_buy);
            mBuyOrderTv.setTextColor(getResources().getColor(R.color.white_fefefe));
            mShopCartPriceTv.setText(R.string.choose_dishes_successful);
            option_dishes_tv.setVisibility(View.VISIBLE);
        } else {
            option_dishes_tv.setVisibility(View.GONE);
            mBuyOrderTv.setBackgroundResource(R.drawable.shopping_cart_widget_selector_2);
            String txt = getResources().getString(
                    R.string.small_market_buy_not_enough);
            int surplus = mOrderController.getReqWeight() - mOrderController.getItemsWeight();
            if (surplus == mOrderController.getReqWeight()) {
                txt = "选择" + Utils.unitConversion(surplus);
            } else {
                txt = txt.replaceAll("A", Utils.unitConversion(mOrderController.getReqWeight()
                        - mOrderController.getItemsWeight()));
            }
            mBuyOrderTv.setText(txt);
            mBuyOrderTv.setTextColor(getResources().getColor(android.R.color.black));
            String shopCartTip = mOrderController.getItemsWeight() == 0 ? getString(R.string.small_market_shop_cart_null) : "您已选了" + Utils.unitConversion(mOrderController.getItemsWeight());
            mShopCartPriceTv.setText(shopCartTip);
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
            if (pView == null) {
                return;
            }
            DishesItemEntry item = adapter.getItem(position);
            TextView tvCount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
            String count = tvCount.getText().toString();
            int intCount = Integer.parseInt(count) + 1;
            mOrderController.addItem(item);
            mOrderController.removeOptionItem(item);
            tvCount.setVisibility(View.VISIBLE);
            pView.findViewById(R.id.goods_sub).setVisibility(View.VISIBLE);
            item.setCount(intCount);
            tvCount.setText(String.valueOf(intCount));
            setBadgeView(true);
            updateBuyOrderStatus();
        }
    }

    private boolean checkReqWeight(int lastWeight) {
        /*超过最大要求份数*/
        int i = mOrderController.outOfReqWeight(lastWeight);
        if (i > 0) {
            /*异常不会出现的情况*/
            if (i >= lastWeight) {
                new AlertDialog().alertOutOfReqWeight(getBaseAct(), mOrderController.getReqWeight());
                return true;
                /*举例：选了10，要求10，再选*/
            } else {
                new AlertDialog().alertOutOfReqWeight1(getBaseAct(), mOrderController.getReqWeight());
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
            new AlertDialog().alertOutOfMaxpacks(getBaseAct(), mOrderController.getMaxpacksById(id));
            return true;
        } else if (mOrderController.outOfRemainWeight(id)) {
            new AlertDialog().alertOutOfRemains(getBaseAct());
            return true;
        }
        return false;
    }

    private View.OnClickListener mDownOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() instanceof Integer) {
                int position = (int) v.getTag();
                updateListItem(position);
            }
            PlaySoundUtils.doPlay(getBaseAct(), R.raw.psst2);
            setBadgeView(false);
            updateBuyOrderStatus();
        }
    };

    void updateListItem(int position) {
        if (position >= adapter.getCount() || adapter.getItem(position) == null)
            return;
        View pView = mLv.getChildAt(position + mLv.getHeaderViewsCount()
                - mLv.getFirstVisiblePosition());
        if (pView == null)
            return;
        TextView tvCount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
        String count = tvCount.getText().toString();
        int intCount = Integer.parseInt(count) - 1;
        DishesItemEntry goodItem = adapter.getItem(position);
        goodItem.setCount(intCount);
        mOrderController.removeItemById(goodItem.id);
        int visibility = intCount == 0 ? View.GONE : View.VISIBLE;
        pView.findViewById(R.id.goods_sub).setVisibility(visibility);
        tvCount.setVisibility(visibility);
        tvCount.setText(String.valueOf(intCount));
    }

    private AvoidDoubleClickListener mUpOnClickListenerInShopCart = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            int id = (int) v.getTag();
            DishesItemEntry item = mOrderController.getItemById(id);
            if (checkReqWeight(item.packw) || checkMaxpacks(item.id)) {
                return;
            }
            ViewGroup vG = (ViewGroup) v.getParent();
            TextView tvCount = (TextView) vG.findViewById(R.id.good_count_tv);
            tvCount.setVisibility(View.VISIBLE);
            vG.findViewById(R.id.good_count_down_iv).setVisibility(View.VISIBLE);
            item.setCount(item.getCount() + 1);
            mOrderController.addItem(item);
            mOrderController.removeOptionItem(item);
            tvCount.setText(String.valueOf(item.getCount()));
            for (int i = 0; i < adapter.getCount(); ++i) {
                DishesItemEntry tmpItem = adapter.getItem(i);
                if (tmpItem != null && item != null && tmpItem.id == item.id) {
                    View pView = mLv.getChildAt(i + mLv.getHeaderViewsCount() - mLv.getFirstVisiblePosition());
                    if (pView == null)
                        break;
                    TextView tvcount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
                    tvcount.setVisibility(View.VISIBLE);
                    pView.findViewById(R.id.goods_sub).setVisibility(View.VISIBLE);
                    tvcount.setText(item.getCount() + "");
                    break;
                }
            }
            PlaySoundUtils.doPlay(getBaseAct(), R.raw.pop);
            setBadgeView(true);
            updateShopCartWidgetStatus();
            updateBuyOrderStatus();
        }
    };

    private View.OnClickListener mDownOnClickListenerInShopCart = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ViewGroup parentView0 = (ViewGroup) v.getParent();
            ViewGroup parentView = (ViewGroup) parentView0.getParent();
            TextView tvCount = (TextView) parentView.findViewById(R.id.good_count_tv);
            String count = tvCount.getText().toString();
            int intCount = Integer.parseInt(count);
            intCount--;
            int id = (int) v.getTag();
            DishesItemEntry item = mOrderController.getItemById(id);
            if (item != null) {
                item.setCount(intCount);
                mOrderController.removeItemById(id);
                for (int i = 0; i < adapter.getCount(); ++i) {
                    DishesItemEntry tmpItem = adapter.getItem(i);
                    if (tmpItem != null && tmpItem.id == item.id) {
                        View pView = mLv.getChildAt(i + mLv.getHeaderViewsCount() - mLv.getFirstVisiblePosition());
                        if (pView == null)
                            break;
                        TextView tvcount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
                        String s = tvcount.getText().toString();
                        int intcount = Integer.parseInt(s) - 1;
                        DishesItemEntry goodItem = adapter.getItem(i);
                        goodItem.setCount(intcount);
                        int visibility = intcount == 0 ? View.GONE : View.VISIBLE;
                        tvcount.setVisibility(visibility);
                        pView.findViewById(R.id.goods_sub).setVisibility(visibility);
                        tvcount.setText(String.valueOf(intcount));
                        break;
                    }
                }
            }
            PlaySoundUtils.doPlay(getBaseAct(), R.raw.psst2);
            int visibility = intCount <= 0 ? View.GONE : View.VISIBLE;
            parentView.findViewById(R.id.good_count_down_iv).setVisibility(visibility);
            tvCount.setVisibility(visibility);
            if (intCount == 0) {
                containerLl.removeView(parentView);
            } else if (intCount < 0) {
                intCount = 0;
            }
            // 减单
            tvCount.setText(String.valueOf(intCount));
            setBadgeView(false);
            updateShopCartWidgetStatus();
            updateBuyOrderStatus();
        }
    };

    /**
     * 购物车变大动画
     */
    private void shopChartIconScaleAnimation(View v) {
        playShopCartAnimation();
        upCountUpdateUI(v);
        animationFly(v);
    }

    private void playShopCartAnimation() {
        Animation scale = new ScaleAnimation(0.6f, 1.2f, 0.6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(200);
        mShopCartIv.startAnimation(scale);
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

    /**
     * 不能进行新一次选菜(choose=false)提示信息汇总：
     * 1. reason = 1, status = 1，您已选过第{times}次菜品，如需更改请点击。
     * 2. reason = 1, status = 2，您的第{times}次菜品正在配送中，请耐心等待。
     * 3. reason = 2，当前已过选菜日，请您周{shipday[0]+1}至周{shipday[0]-2}进行选菜。
     * 4. reason = 3，您已延期第{times+1}次选菜，请您下次选菜日进行选菜。
     */
    boolean setChooseDishesContent(View v) {
        final TextView choose_dishes_tip = (TextView) v.findViewById(R.id.choose_dishes_tip);
        if (!isChooseNextDishes()) {
            int status = buildStatus();
            if (entry != null && status == 1 && entry.reason == 1) {
                choose_dishes_tip.setVisibility(View.VISIBLE);
                choose_dishes_tip.setText("您已选过第" + entry.times + "次菜品,如需更改请点击.");
                choose_dishes_tip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoFragmentByAdd(buildBundle(buildOrderParams(entry)), R.id.mainpage_ly, new ModifyOrderFragment(), ModifyOrderFragment.class.getName());
                    }
                });
                return false;
            } else if (entry != null && status == 2 && entry.reason == 1) {
                choose_dishes_tip.setVisibility(View.VISIBLE);
                choose_dishes_tip.setText("您的第" + entry.times + "次菜品正在配送中,请耐心等待.");
                Drawable left = getBaseAct().getResources().getDrawable(R.drawable.icon_sigh);
                choose_dishes_tip.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
                return false;
            } else if (entry != null && entry.reason == 2) {
                String tip1 = "";
                if (entry.shipday != null && entry.shipday.length > 0) {
                    int s = entry.shipday[0] - 2;
//                    if (s < 1) {
//                        s = s + 7;
//                    }
                    int e = entry.shipday[0] + 1;
//                    if (e + 1 > 7) {
//                        e = e - 7;
//                    }
//                    当前已过选菜日，请您周{shipday[0]+1}至周{shipday[0]-2}进行选菜。
                    tip1 = "已过选菜日,请本周" + (e == 7 ? "日" : e) + "至下周" + (s == 7 ? "日" : s) + "开始下期选菜。";
                }
                choose_dishes_tip.setVisibility(View.VISIBLE);
                choose_dishes_tip.setText(tip1);
                Drawable left = getBaseAct().getResources().getDrawable(R.drawable.icon_sigh);
                choose_dishes_tip.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);

                return false;
            } else if (entry != null && entry.reason == 3) {
                choose_dishes_tip.setVisibility(View.VISIBLE);
                choose_dishes_tip.setText("您已延期第" + (entry.times + 1) + "次选菜，请您下次选菜日进行选菜。");
                return false;
            }
        }
        return true;
    }

    boolean isChooseNextDishes() {
        return entry != null ? entry.choose : false;
    }


    int buildStatus() {
        return entry != null ? entry.status : -1;
    }

    ModifyOrderParams buildOrderParams(ComboEntry entry) {
        ModifyOrderParams params = new ModifyOrderParams();
        int price = entry.prices[entry.getPosition()];
        String time = "下单日期:" + Utils.getTime(entry.chgtime.get(entry.status + ""));
        params.chooseday = true;
        params.setParams(entry.id, entry.orderno, 1, entry.id, price, entry.combo_idx, entry.status, null, null, null, null, 1, time, null, entry.shipday, entry.times, entry.con, entry.duration);

        return params;
    }

    Bundle buildBundle(ModifyOrderParams entry) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("HistoryOrderEntry", entry);
        return bundle;
    }

    private void setWidgetEnableStatus() {
        mShopCartIv.setEnabled(false);
        mBuyOrderTv.setText(R.string.has_chosen_dishes);
        mBuyOrderTv.setTextColor(getBaseAct().getResources().getColorStateList(R.color.gray_d8d8d8));
        mBuyOrderTv.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mOrderController != null) {
            mOrderController.release();
            mOrderController = null;
        }
    }

    @OnClick(R.id.buy_order_tv)
    void doClick() {
        if (getString(R.string.small_market_buy).equals(mBuyOrderTv.getText().toString())) {
            if (TextUtils.isEmpty(mOrderController.getOrderOptionItemString())) {
                new AlertDialog().alertDialog(getBaseAct(), getString(R.string.no_choose_option_tip));
                return;
            }
            if (mOrderController.getItemsWeight() > entry.weights[entry.getPosition()]) {
                new AlertDialog().alertDialog(getBaseAct(), getString(R.string.dishes_error_much));
                return;
            }
            gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new OrderDetailsFragment(), OrderDetailsFragment.class.getName());
        } else {
            new AlertDialog().alertDialog(getBaseAct(), getString(R.string.dishes_error));
        }
    }

    @OnClick(R.id.shop_cart_clear_tv)
    void clearShopCart() {
        updateHasChooseItem();
        hideShopCart();
        hideOptionWidget();
        clearBadeView(mOrderController.getItemsCount());
        mOrderController.clear();
        updateShopCartWidgetStatus();
    }

    void updateHasChooseItem() {
        List<DishesItemEntry> aList = mOrderController.buildItems();
        for (int i = 0; i < aList.size(); ++i) {
            DishesItemEntry item = aList.get(i);
            for (int j = 0; j < adapter.getCount(); ++j) {
                DishesItemEntry tmpItem = adapter.getItem(j);
                if (item != null && tmpItem != null && item.id == tmpItem.id) {
                    View pView = mLv.getChildAt(j + mLv.getHeaderViewsCount() - mLv.getFirstVisiblePosition());
                    if (pView == null)
                        continue;
                    TextView tvcount = (TextView) pView.findViewById(R.id.goods_count);// 显示数量
                    pView.findViewById(R.id.goods_sub).setVisibility(View.GONE);
                    tvcount.setVisibility(View.GONE);
                    tvcount.setText("0");
                    break;
                }
            }
        }
    }

    /**
     * 如果不是来自我的套餐，则表明是第一次买套餐
     *
     * @param id
     */
    private void requestFixedCombo(int id) {
        RequestParams params = new RequestParams();
        params.add("id", "" + id);
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/combodtl", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    FixedListComboEntry entry = JsonUtilsParser.fromJson(new String(data), FixedListComboEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            addHeader(entry.aList);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {
            }
        });
    }

    private boolean isLastChoose() {
        if (entry.choose && entry.shipday != null && entry.shipday.length > 0) {
            if ((++entry.times) == entry.duration * entry.shipday.length) {
                return true;
            }
        } else {
            if (entry.shipday != null && entry.shipday.length > 0 && entry.times == entry.duration * entry.shipday.length) {
                return true;
            }
        }
        return false;
    }

    private void addHeader(List<FixedComboEntry> aList) {
        if (aList == null || aList.size() <= 0)
            return;

        for (FixedComboEntry entry : aList) {
            View headView = LayoutInflater.from(getBaseAct()).inflate(R.layout.combo_match_ly, null);
            ImageView goods_img = (ImageView) headView.findViewById(R.id.goods_img);
            if (entry != null && entry.imgs != null && entry.imgs.length > 0) {
                ImageLoader.getInstance().displayImage(entry.imgs[0] + "?imageview2/2/w/180", goods_img);
            }
            ((TextView) headView.findViewById(R.id.goods_name)).setText(entry.title);
            TextView goodsPrice = (TextView) headView.findViewById(R.id.goods_price);
            goodsPrice.setText(entry.quantity + entry.unit + "/份");
            final ImageView goods_add = (ImageView) headView.findViewById(R.id.goods_add);
            final TextView goods_count = (TextView) headView.findViewById(R.id.goods_count);
            if (!enabled) {
                goods_add.setEnabled(false);
            } else if (isLastChoose()) {
                goods_add.setEnabled(false);
                goods_count.setText(entry.remains + "");
                goods_count.setVisibility(View.VISIBLE);
                entry.count = entry.remains;
                mOrderController.addComboMatchItem(entry);
            } else {
                goods_add.setEnabled(true);
                goods_add.setImageResource(R.drawable.icon_add);
            }
            final View goods_sub = headView.findViewById(R.id.goods_sub);
            goods_sub.setTag(entry);
            goods_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FixedComboEntry tmpEntry = (FixedComboEntry) v.getTag();
                    tmpEntry.count--;
                    if (tmpEntry.count <= 0) {
                        tmpEntry.count = 0;
                        goods_count.setVisibility(View.GONE);
                        goods_count.setText("0");
                        goods_sub.setVisibility(View.GONE);
                        mOrderController.removeComboMatchItem(tmpEntry);
                    } else {
                        goods_count.setVisibility(View.VISIBLE);
                        goods_count.setText(tmpEntry.count + "");
                        mOrderController.removeComboMatchItem(tmpEntry);
                        mOrderController.addComboMatchItem(tmpEntry);
                    }
                    PlaySoundUtils.doPlay(getBaseAct(), R.raw.psst2);
                }
            });
            goods_add.setTag(entry);
            goods_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FixedComboEntry tmpEntry = (FixedComboEntry) v.getTag();
                    if (tmpEntry.count >= tmpEntry.remains) {
                        new AlertDialog().alertOutOfFixedRemains(getBaseAct(), tmpEntry.remains);
                        return;
                    }
                    tmpEntry.count++;
                    goods_count.setVisibility(View.VISIBLE);
                    goods_count.setText(tmpEntry.count + "");
                    goods_sub.setVisibility(View.VISIBLE);
                    mOrderController.removeComboMatchItem(tmpEntry);
                    mOrderController.addComboMatchItem(tmpEntry);
                    PlaySoundUtils.doPlay(getBaseAct(), R.raw.pop);
                }
            });
            mLv.addHeaderView(headView, null, false);
        }

        // mLv.addHeaderView(LayoutInflater.from(getBaseAct()).inflate(R.layout.view_10dp_ly, null), null, false);

    }

    /**
     * 来自我的套餐
     */
    void requsetFixedDishesList(String con) {
        RequestParams params = new RequestParams();
//      套餐固定菜品使用，套餐订单号
        params.add("orderno", con);
        params.add("mode", isChooseNextDishes() ? "1" : "2");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/itemlist", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    FixedListComboEntry entry = JsonUtilsParser.fromJson(new String(data), FixedListComboEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (entry.items == null || entry.items.isEmpty()) {
                                return;
                            }
                            addHeader(entry.items);
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Bind(R.id.option_dishes_tv)
    TextView option_dishes_tv;
    boolean enabled;
    ComboEntry entry;
    @Bind(R.id.buy_order_tv)
    TextView mBuyOrderTv;
    @Bind(R.id.shop_cart_total_price_tv)
    TextView mShopCartPriceTv;
    @Bind(R.id.option_dishes_tip)
    TextView option_dishes_tip;
    @Bind(R.id.foot_shop_cart_ll)
    LinearLayout footShopCartLl;
    LinearLayout containerLl;
    @Bind(R.id.root_view)
    FrameLayout rootView;
    @Bind(R.id.mLv)
    ExpandableHeightListView mLv;
    ChooseDishesAdapter adapter;
    @Bind(R.id.shop_cart_clear_tv)
    TextView mShopCartClearTv;//清空购物车
    @Bind(R.id.empty_view)
    View emptyView;
    DisheDataCenter mOrderController;
    BadgeView mBadgeViewShopCart;
    @Bind(R.id.shop_cart_iv)
    ImageView mShopCartIv;
    @Bind(R.id.pView)
    PullToRefreshScrollView pView;
}