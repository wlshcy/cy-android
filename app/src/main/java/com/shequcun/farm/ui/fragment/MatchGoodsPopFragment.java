package com.shequcun.farm.ui.fragment;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.common.widget.PinnedHeaderExpandableListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.DishesItemEntry;
import com.shequcun.farm.data.FixedComboEntry;
import com.shequcun.farm.data.FixedListComboEntry;
import com.shequcun.farm.data.goods.DishesListItemEntry;
import com.shequcun.farm.datacenter.DisheDataCenter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by cong on 15/10/24.
 */
public class MatchGoodsPopFragment extends BaseFragment {
    @Bind(R.id.list_lv)
    PinnedHeaderExpandableListView listLv;
    @Bind(R.id.empty_view)
    View emptyView;
    //    @Bind(R.id.selected_tv)
//    TextView selectedTv;
    private ArrayList<GoodGroup> groupList = new ArrayList<GoodGroup>();
    private ArrayList<List<DishesItemEntry>> childList = new ArrayList<>();
    private MyexpandableListAdapter adapter;
    private ArrayList<DishesItemEntry> fixedList = new ArrayList<>();
    private ArrayList<DishesItemEntry> backupList = new ArrayList<>();
    private GoodGroup fixedGroup = new GoodGroup("固定菜品", View.GONE);
    private GoodGroup backupGroup = new GoodGroup("备选菜品", View.VISIBLE);
    private DisheDataCenter mOrderController = DisheDataCenter.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_goods_pop, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFixedCombo();
        loadBackupDishes();
    }

    private void loadFixedCombo() {
        ComboEntry comboEntry = getComboEntryParams();
        if (comboEntry == null) return;
        if (comboEntry.isMine())
            requsetFixedDishesList(comboEntry.orderno);
        else
            requestFixedCombo(comboEntry.id);
    }

    private void loadBackupDishes() {
        Bundle bundle = getArguments();
        ArrayList<DishesItemEntry> list = null;
        if (bundle != null)
            list = (ArrayList<DishesItemEntry>) bundle.getSerializable("chooseList");
        if (list == null || list.isEmpty()) return;
//        for (int i = 0; i < list.size(); i++) {
//            if (i > 3) break;
//            backupList.add(list.get(i));
//        }
        groupList.add(backupGroup);
        childList.add(list);
        adapter.notifyDataSetChanged();
        expandAllGroup();
    }

    private String getOrdernoParams() {
        Bundle bundle = getArguments();
        if (bundle != null)
            return bundle.getString("orderno");
        return null;
    }

    private ComboEntry getComboEntryParams() {
        Bundle bundle = getArguments();
        if (bundle != null)
            return (ComboEntry) bundle.getSerializable("ComboEntry");
        return null;
    }

    @Override
    protected void initWidget(View v) {
        if (adapter == null)
            adapter = new MyexpandableListAdapter(getActivity());
        listLv.setAdapter(adapter);
        groupList.add(fixedGroup);
//        groupList.add(new GoodGroup("备选菜品", View.VISIBLE));
//        List list = new ArrayList();
//        for (int i = 0; i < 2; i++) {
//            DishesItemEntry entry = new DishesItemEntry();
//            entry.title = "固定菜品";
//            list.add(entry);
//        }
//        List list1 = new ArrayList();
//        for (int i = 0; i < 10; i++) {
//            DishesItemEntry entry = new DishesItemEntry();
//            entry.title = "备选菜品";
//            list1.add(entry);
//        }
        childList.add(fixedList);
//        childList.add(backupList);
//        adapter.notifyDataSetChanged();
//        expandAllGroup();
    }

    @Override
    protected void setWidgetLsn() {
        listLv.setOnGroupCollapseListener(onGroupCollapseListener);
        listLv.setOnHeaderUpdateListener(onHeaderUpdateListener);
    }

    @OnClick(R.id.empty_view)
    public void dismiss() {
        popBackStack();
    }

    private ExpandableListView.OnGroupCollapseListener onGroupCollapseListener = new ExpandableListView.OnGroupCollapseListener() {
        @Override
        public void onGroupCollapse(int groupPosition) {
            /**防止折叠*/
            listLv.expandGroup(groupPosition);
        }
    };

    private PinnedHeaderExpandableListView.OnHeaderUpdateListener onHeaderUpdateListener = new PinnedHeaderExpandableListView.OnHeaderUpdateListener() {
        private int rightLastFlag = -1;
        /**防止第一行的刷新被点击到*/
        private boolean refreshVisible;

        @Override
        public View getPinnedHeader() {
            ViewGroup headerView = (ViewGroup) View.inflate(getActivity(),
                    R.layout.good_group, null);
            headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
            headerView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//				什么也不做
                }
            });
            TextView refreshTv = (TextView) headerView.findViewById(R.id.refresh_tv);
            refreshTv.setOnClickListener(new AvoidDoubleClickListener() {
                @Override
                public void onViewClick(View v) {
                    /**为了替换时header的刷新能被点击到*/
                    if (refreshVisible)
                        refreshGoods();
                }
            });
            return headerView;
        }

        @Override
        public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
            if (headerView == null) return;
            GoodGroup firstVisibleGroup = (GoodGroup) adapter
                    .getGroup(firstVisibleGroupPos);
            if (firstVisibleGroup != null) {
                TextView textView = (TextView) headerView.findViewById(R.id.group);
                textView.setText(firstVisibleGroup.name);
                TextView refreshTv = (TextView) headerView.findViewById(R.id.refresh_tv);
                if (firstVisibleGroup.refreshVisible == View.GONE) {
                    refreshTv.setVisibility(View.GONE);
                    refreshVisible = false;
                } else {
                    refreshVisible = true;
                    refreshTv.setText("换一批");
                    refreshTv.setVisibility(View.VISIBLE);
                    refreshTv.refreshDrawableState();
                }
                if (rightLastFlag == -1) {
                    rightLastFlag = firstVisibleGroupPos;
                    return;
                }
//            没有更换组
                if (rightLastFlag == firstVisibleGroupPos) {
                    return;
                }
////            更换了组
//                rightLastFlag = firstVisibleGroupPos;
//                leftAdapter.changeItemBgColor(firstVisibleGroupPos);
//                leftListView.setSelection(firstVisibleGroupPos);
            }
        }
    };


    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void expandAllGroup() {
        for (int i = 0, count = adapter.getGroupCount(); i < count; i++) {
            if (!listLv.isGroupExpanded(i)) {
                listLv.expandGroup(i);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
//                            parseToDishesItemEntry(entry.aList);

                        }
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {

            }
        });
    }

//    private List<DishesItemEntry> parseToDishesItemEntry(List<FixedComboEntry> aList) {
//        List<DishesItemEntry> list = new ArrayList<>();
//        for (FixedComboEntry entry : aList) {
//            DishesItemEntry tEntry = new DishesItemEntry();
//            tEntry.id = entry.id;
//            tEntry.imgs = entry.imgs;
//            tEntry.title = entry.title;
//            list.add(tEntry);
//        }
//        return list;
//    }

    /**
     * 来自我的套餐
     *
     * @param orderno
     */
    void requsetFixedDishesList(String orderno) {
        RequestParams params = new RequestParams();
//      套餐固定菜品使用，套餐订单号
        params.add("orderno", orderno);
//        final ProgressDlg pDlg = new ProgressDlg(getBaseAct(), "加载中...");
        HttpRequestUtil.getHttpClient(getBaseAct()).get(LocalParams.getBaseUrl() + "cai/itemlist", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    DishesListItemEntry entry = JsonUtilsParser.fromJson(new String(data), DishesListItemEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (entry.aList == null || entry.aList.isEmpty()) {
                                return;
                            }
                            successFixedDishesList(entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getBaseAct(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getBaseAct(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getBaseAct(), "错误码 " + sCode);
            }

            @Override
            public void onStart() {
                super.onStart();
//                if (pDlg != null)
//                    pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
//                if (pDlg != null)
//                    pDlg.dismiss();
            }
        });
    }

    private void successFixedDishesList(List<DishesItemEntry> list) {
        fixedList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 数据源
     *
     * @author Administrator
     */
    class MyexpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyexpandableListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        // 返回父列表个数
        @Override
        public int getGroupCount() {
            if (groupList == null || groupList.isEmpty()) return 0;
            return groupList.size();
        }

        // 返回子列表个数
        @Override
        public int getChildrenCount(int groupPosition) {
            if (childList == null || childList.isEmpty()) return 0;
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // logger.error("groupPosition:" + groupPosition);
            if (groupPosition > -1) {
                return groupList.get(groupPosition);
            }
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder;
            if (convertView == null || convertView.getTag() instanceof ChildHolder2 || convertView.getTag() instanceof ChildHolder1) {
                convertView = inflater.inflate(R.layout.good_group, null);
                groupHolder = new GroupHolder(convertView);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            GoodGroup gg = ((GoodGroup) getGroup(groupPosition));
            if (gg != null) {
                groupHolder.group.setText(gg.name);
                groupHolder.refreshTv.setVisibility(gg.refreshVisible);
                groupHolder.refreshTv.setOnClickListener(new AvoidDoubleClickListener() {
                    @Override
                    public void onViewClick(View v) {
                        refreshGoods();
                    }
                });
            }
            return convertView;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            return groupPosition;
        }

        @Override
        public int getChildTypeCount() {
            return getGroupCount();
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            int type = getChildType(groupPosition, childPosition);
            if (type == 0) {
                DishesItemEntry itemEntry = (DishesItemEntry) getChild(groupPosition, childPosition);
                ChildHolder1 childHolder1 = null;
                if (convertView == null || convertView.getTag() instanceof GroupHolder || convertView.getTag() instanceof ChildHolder2) {
                    convertView = inflater.inflate(R.layout.goods_item_ly, null);
                    childHolder1 = new ChildHolder1(convertView);
                    convertView.setTag(childHolder1);
                } else {
                    childHolder1 = (ChildHolder1) convertView.getTag();
                }
                childHolder1.goodsName.setText(itemEntry.title);
                if (itemEntry.imgs != null && itemEntry.imgs.length > 0)
                    ImageLoader.getInstance().displayImage(itemEntry.imgs[0], childHolder1.goodsImg);
            } else if (type == 1) {
                DishesItemEntry itemEntry = (DishesItemEntry) getChild(groupPosition, childPosition);
                ChildHolder2 childHolder2 = null;
                if (convertView == null || convertView.getTag() instanceof GroupHolder || convertView.getTag() instanceof ChildHolder1) {
                    convertView = inflater.inflate(R.layout.option_item_ly, null);
                    childHolder2 = new ChildHolder2(convertView);
                    convertView.setTag(childHolder2);
                } else {
                    childHolder2 = (ChildHolder2) convertView.getTag();
                }
                childHolder2.goodsName.setText(itemEntry.title);
                if (itemEntry.imgs != null && itemEntry.imgs.length > 0)
                    ImageLoader.getInstance().displayImage(itemEntry.imgs[0], childHolder2.goodsImg);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    private void refreshGoods() {
        Toast.makeText(getActivity(), "refresh", Toast.LENGTH_LONG).show();
        childList.remove(adapter.getGroupCount() - 1);
        List list1 = new ArrayList();
        for (int i = 0; i < 10; i++) {
            DishesItemEntry entry = new DishesItemEntry();
            entry.title = "备选菜品111";
            list1.add(entry);
        }
        childList.add(list1);
        adapter.notifyDataSetChanged();
    }

    class GroupHolder {
        @Bind(R.id.group)
        TextView group;
        @Bind(R.id.refresh_tv)
        TextView refreshTv;

        public GroupHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * 固定菜品
     */
    public class ChildHolder1 {
        @Bind(R.id.goods_img)
        ImageView goodsImg;
        @Bind(R.id.goods_name)
        TextView goodsName;

        @Bind(R.id.goods_price)
        TextView goodsPrice;
        @Bind(R.id.lookDtlLy)
        LinearLayout lookDtlLy;
        @Bind(R.id.goods_sub)
        ImageView goodsSub;
        @Bind(R.id.goods_count)
        TextView goodsCount;
        @Bind(R.id.goods_add)
        ImageView goodsAdd;

        public ChildHolder1(View v) {
            ButterKnife.bind(this, v);
        }
    }

    /**
     * 父标题
     */
    static class GoodGroup {
        String name;
        int refreshVisible;

        public GoodGroup(String name, int refreshVisible) {
            this.name = name;
            this.refreshVisible = refreshVisible;
        }
    }

    /**
     * 备选菜
     */
    static class ChildHolder2 {

        @Bind(R.id.goods_img)
        ImageView goodsImg;
        @Bind(R.id.goods_name)
        TextView goodsName;
        @Bind(R.id.goods_price)
        TextView goodsPrice;
        @Bind(R.id.option_cb)
        CheckBox optionCb;
        @Bind(R.id.pRview)
        RelativeLayout pRview;

        public ChildHolder2(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
