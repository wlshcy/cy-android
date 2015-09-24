package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.widget.PullToRefreshBase;
import com.common.widget.PullToRefreshScrollView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shequcun.farm.R;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.ComboListEntry;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.ComboAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;

import java.util.List;

import butterknife.Bind;

/**
 * 套餐页面
 * Created by apple on 15/8/3.
 */
public class ComboFragment extends BaseFragment {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.combo_ly, container, false);
    }

    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.choose_combo);
    }

    @Override
    protected void setWidgetLsn() {
        mListView.setOnItemClickListener(onItemClick);
        back.setOnClickListener(onClick);
        pView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
//        pView.setMode(PullToRefreshBase.Mode.DISABLED);
        pView.setOnRefreshListener(onRefrshLsn);
        buildAdapter();
        requestComboList();
    }


    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                popBackStack();
            }
        }
    };

    void buildAdapter() {
        if (adapter == null)
            adapter = new ComboAdapter(getActivity());
        mListView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (adapter == null)
                return;
            ComboEntry entry = adapter.getItem(position);
            if (entry == null)
                return;
            if (buildIsMyComboClick(position)) {
                gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
            } else
                gotoFragmentByAdd(buildBundle(entry), R.id.mainpage_ly, new ComboSecondFragment(), ComboSecondFragment.class.getName());
        }
    };

    Bundle buildBundle(ComboEntry entry) {
        Bundle bundle = new Bundle();
        entry.setPosition(entry.index);
        bundle.putSerializable("ComboEntry", entry);
        return bundle;
    }

    /**
     * 请求套餐列表
     */
    void requestComboList() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        HttpRequestUtil.getHttpClient(getActivity()).get(LocalParams.getBaseUrl() + "cai/combo", new AsyncHttpResponseHandler() {

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
                if (pView != null)
                    pView.onRefreshComplete();
            }

            @Override
            public void onSuccess(int sCode, Header[] headers, byte[] data) {
                if (data != null && data.length > 0) {
                    ComboListEntry entry = JsonUtilsParser.fromJson(new String(data), ComboListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            if (adapter != null)
                                adapter.clear();
                            doAddMyComboDataToAdapter(entry.myCombos);
                            doAddDataToAdapter(entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }
                }
            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "错误码 " + sCode);
            }
        });
    }


    void doAddMyComboDataToAdapter(List<ComboEntry> aList) {
        if (aList != null && aList.size() > 0) {
            for (ComboEntry comboEntry : aList) {
                comboEntry.setMine(true);
            }
            adapter.setIsMyCombo(true);
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
            Utils.setListViewHeightBasedOnChildren(mListView);
            int size = aList.size();
            UserLoginEntry entry = new CacheManager(getActivity()).getUserLoginEntry();
            if (entry != null) {
                entry.mycomboids = new int[size];
                for (int i = 0; i < size; ++i) {
                    entry.mycomboids[i] = aList.get(i).id;
                }
                new CacheManager(getActivity()).saveUserLoginToDisk(JsonUtilsParser.toJson(entry).getBytes());
            }
//            IntentUtil.sendUpdateMyInfoMsg(getActivity(), aList.get(0));
        }
    }

    void doAddDataToAdapter(List<ComboEntry> aList) {
        if (aList != null && aList.size() > 0) {
            adapter.setIsMyCombo(false);
            adapter.addAll(aList);
            adapter.notifyDataSetChanged();
            Utils.setListViewHeightBasedOnChildren(mListView);
        }
    }

    /**
     * 是不是单击的我的套餐项
     *
     * @param position
     * @return boolean true 是
     */
    boolean buildIsMyComboClick(final int position) {
        if (position >= adapter.getCount() || adapter.getItem(position) == null)
            return false;
        View pView = mListView.getChildAt(position + mListView.getHeaderViewsCount()
                - mListView.getFirstVisiblePosition());
        if (pView == null)
            return false;
        return pView.findViewById(R.id.my_combo).getVisibility() == View.VISIBLE;
    }

    PullToRefreshScrollView.OnRefreshListener2 onRefrshLsn = new PullToRefreshBase.OnRefreshListener2() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            requestComboList();
        }
    };

    ComboAdapter adapter;
    @Bind(R.id.mListView)
    ListView mListView;
    @Bind(R.id.back)
    View back;
    @Bind(R.id.pView)
    PullToRefreshScrollView pView;
}
