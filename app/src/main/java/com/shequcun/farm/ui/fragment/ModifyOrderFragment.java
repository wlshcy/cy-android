package com.shequcun.farm.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.data.AlreadyPurchasedEntry;
import com.shequcun.farm.data.AlreadyPurchasedListEntry;
import com.shequcun.farm.data.ComboEntry;
import com.shequcun.farm.data.ModifyOrderParams;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.ui.adapter.AlreadyPurchasedAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

/**
 * 修改订单
 * Created by apple on 15/8/20.
 */
public class ModifyOrderFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lookup_order_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        order_btn = (TextView) v.findViewById(R.id.order_btn);
        mLv = (ListView) v.findViewById(R.id.mLv);
        hEntry = buildModifyOrderObj();
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.order_details);
        order_btn.setVisibility(isShowFooteWgt() ? View.VISIBLE : View.GONE);
        order_btn.setText(getOrderType() == 1 ? R.string.re_choose_dishes : R.string.cancel_order);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        order_btn.setOnClickListener(onClick);
        requestOrderDetails();
    }

    private ModifyOrderParams buildModifyOrderObj() {
        Bundle bundle = getArguments();
        return bundle != null ? (ModifyOrderParams) bundle.getSerializable("HistoryOrderEntry") : null;
    }

    private boolean isShowFooteWgt() {
        if (hEntry != null)
            return hEntry.isShowFooterBtn;
        return false;
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == order_btn) {
                if (getOrderType() == 1) {//重新选择菜品
                    if (hEntry == null)
                        return;
                    showConfirmDlg();
                } else {//取消订单
                    cancelOrder();
                }
            }
        }
    };

    String getOrderNumber() {
        if (hEntry != null)
            return hEntry.orderno;
        return " ";
    }


    void cancelOrder() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("id", hEntry.id + "");
        params.add("_xsrf", PersistanceManager.INSTANCE.getCookieValue());
        HttpRequestUtil.httpPost(LocalParams.INSTANCE.getBaseUrl() + "cai/delorder", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }

            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (TextUtils.isEmpty(jObj.optString("errmsg"))) {
                            ToastHelper.showShort(getActivity(), R.string.cancel_order_success);
                            popBackStack();
                            return;
                        }
                        ToastHelper.showShort(getActivity(), jObj.optString("errmsg"));

                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }

                ToastHelper.showShort(getActivity(), "错误码" + sCode);
            }
        });
    }

    int getOrderType() {
        if (hEntry != null)
            return hEntry.type;
        return 0;
    }

    void requestOrderDetails() {
        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("orderno", getOrderNumber());
        HttpRequestUtil.httpGet(LocalParams.INSTANCE.getBaseUrl() + "cai/orderdtl", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    AlreadyPurchasedListEntry entry = JsonUtilsParser.fromJson(new String(data), AlreadyPurchasedListEntry.class);
                    if (entry != null) {
                        if (TextUtils.isEmpty(entry.errmsg)) {
                            buildAdapter(entry.aList);
                            return;
                        }
                        ToastHelper.showShort(getActivity(), entry.errmsg);
                    }

                }
            }

            @Override
            public void onFailure(int sCode, Header[] headers, byte[] data, Throwable error) {
                if (sCode == 0) {
                    ToastHelper.showShort(getActivity(), R.string.network_error_tip);
                    return;
                }
                ToastHelper.showShort(getActivity(), "错误码" + sCode);
            }

            @Override
            public void onStart() {
                super.onStart();
                pDlg.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pDlg.dismiss();
            }
        });
    }

    void addFooter(int part) {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        ((TextView) footerView.findViewById(R.id.distribution_date)).setText(hEntry.date);
        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共" + part + "份");
        mLv.addFooterView(footerView, null, false);
    }

    void buildAdapter(List<AlreadyPurchasedEntry> aList) {
        if (aList == null || aList.size() <= 0 || hEntry == null)
            return;
        int part = 0;
        int allWeight = 0;
        for (AlreadyPurchasedEntry entry : aList) {
            part += entry.packs;
            allWeight += entry.packs * entry.packw;
        }
        hEntry.allWeight = allWeight;
        addFooter(part);
        if (adapter == null) {
            adapter = new AlreadyPurchasedAdapter(getActivity());
        }
        mLv.setAdapter(adapter);
        adapter.addAll(aList);
        adapter.notifyDataSetChanged();
    }

    private void showConfirmDlg() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
//        builder.setMessage(R.string.choose_dishes_tip);
        builder.setMessage(R.string.re_choose_dishes_tip);
        builder.setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                PhoneUtil.gotoCall(getActivity(), Constrants.Customer_Service_Phone);
                ComboEntry entry = new ComboEntry();
                entry.id = hEntry.combo_id;
                entry.setPosition(0);
                entry.weights = new int[1];
                entry.weights[0] = hEntry.allWeight;
                entry.prices = new int[1];
                entry.prices[0] = hEntry.price;
                entry.combo_idx = hEntry.combo_idx;
                entry.orderno = hEntry.orderno;
                Bundle bundle = new Bundle();
                bundle.putSerializable("ComboEntry", entry);
                popBackStack();
                gotoFragment(bundle, R.id.mainpage_ly, new ChooseDishesFragment(), ChooseDishesFragment.class.getName());
            }
        });
        builder.setNeutralButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    ModifyOrderParams hEntry;
    AlreadyPurchasedAdapter adapter;
    View back;
    TextView order_btn;
    ListView mLv;
}
