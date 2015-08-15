package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.UserLoginEntry;
import com.shequcun.farm.datacenter.CacheManager;
import com.shequcun.farm.dlg.ConsultationDlg;
import com.shequcun.farm.ui.adapter.OrderDetailsAdapter;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.JsonUtilsParser;

/**
 * 订单详情页
 * Created by apple on 15/8/10.
 */
public class OrderDetailsFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_details_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        mLv = (ListView) v.findViewById(R.id.mLv);
        back = v.findViewById(R.id.back);
        addressee_info = (TextView) v.findViewById(R.id.addressee_info);
        address = (TextView) v.findViewById(R.id.address);
        rightTv = (TextView) v.findViewById(R.id.title_right_text);
        rightTv.setText(R.string.consultation);
        re_choose_dishes = v.findViewById(R.id.re_choose_dishes);
        buildUserLoginEntry();
        v.findViewById(R.id.addressee_ly).setVisibility(uEntry == null ? View.GONE : View.VISIBLE);
    }

    void buildUserLoginEntry() {
        byte[] data = new CacheManager(getActivity()).getUserLoginFromDisk();
        if (data != null && data.length > 0) {
            uEntry = JsonUtilsParser.fromJson(new String(data), UserLoginEntry.class);
        }
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        rightTv.setOnClickListener(onClick);
        re_choose_dishes.setOnClickListener(onClick);
        buildAdapter();
        setWidgetContent();
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == rightTv)
                ConsultationDlg.showCallTelDlg(getActivity());
            else if (re_choose_dishes == v) {//重新选择菜品

            }
        }
    };

    void setWidgetContent() {
        if (uEntry == null)
            return;
        String addInfo = uEntry.name + "   " + uEntry.mobile;
        addressee_info.setText(addInfo);
        address.setText("地址: " + uEntry.address);
    }

    void addFooter() {
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.order_details_footer_ly, null);
        ((TextView) footerView.findViewById(R.id.distribution_date)).setText("配送日期:");
        ((TextView) footerView.findViewById(R.id.number_copies)).setText("共X份");
        mLv.addFooterView(footerView, null, false);
    }

    void buildAdapter() {
        addFooter();
        if (adapter == null)
            adapter = new OrderDetailsAdapter(getActivity());
        mLv.setAdapter(adapter);
    }

    UserLoginEntry uEntry;
    OrderDetailsAdapter adapter;
    ListView mLv;
    View back;
    TextView addressee_info;
    TextView address;
    TextView rightTv;
    View re_choose_dishes;
}
