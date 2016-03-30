package com.lynp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.common.widget.ExpandableHeightListView;
import com.lynp.ui.adapter.OrderEnsureAdapter;
import com.lynp.ui.data.OrderEnsureEntry;

import com.lynp.R;
import com.lynp.ui.util.ResUtil;
import com.lynp.ui.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 订单确认页面
 * Created by nmg on 16/2/1.
 */
public class OrderEnsureFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_ensure, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        entry = (OrderEnsureEntry) bundle.getSerializable("entry");
//        Log.i("****", entry.items.toString());
//        adapter.addAll(entry.items);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {

        Bundle bundle = getArguments();
        entry = (OrderEnsureEntry) bundle.getSerializable("entry");

        buildAdapter();

        adapter.addAll(entry.items);
        adapter.notifyDataSetChanged();
        pScrollView.fullScroll(View.FOCUS_UP);

        if (entry.freight == 0){
            freight.setText("免运费");
        }else {
            freight.setText(Utils.unitPeneyToYuan(entry.freight));
        }
        Spannable spannable = Utils.getSpanableSpan("共计:",
                Utils.unitPeneyToYuan(entry.price),
                ResUtil.dip2px(getBaseAct(), 18),
                ResUtil.dip2px(getBaseAct(), 18),
                getResources().getColor(R.color.gray_a9a9a9),
                getResources().getColor(R.color.red_f36043));
        total_price.setText(spannable);

        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.ensure);
    }

    @Override
    protected void setWidgetLsn() {

    }

    @OnClick(R.id.back)
    void back() {
        popBackStack();
    }

    void buildAdapter() {
        if (adapter == null)
            adapter = new OrderEnsureAdapter(getBaseAct());
        adapter.clear();
        eLv.setAdapter(adapter);
        eLv.setExpanded(true);

    }

    @OnClick(R.id.choose_address)
    void doModifyAddress() {
        Bundle bundle = new Bundle();
//        bundle.putInt(AddressListFragment.Action.KEY, AddressListFragment.Action.SELECT);
//        gotoFragmentByAdd(bundle, R.id.mainpage_ly, new AddressListFragment(), AddressListFragment.class.getName());
    }
    OrderEnsureAdapter adapter;

    OrderEnsureEntry entry;

    @Bind(R.id.eLv)
    ExpandableHeightListView eLv;

    @Bind(R.id.pScrollView)
    ScrollView pScrollView;

    @Bind(R.id.freight)
    TextView freight;

    @Bind(R.id.total_price)
    TextView total_price;

    @Bind(R.id.choose_address)
    View choose_address;

}
