package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shequcun.farm.R;
import com.shequcun.farm.data.PayParams;
import com.shequcun.farm.util.AlipayUtils;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.Constrants;
import com.shequcun.farm.util.ToastHelper;

/**
 * 支付界面
 * Created by apple on 15/8/10.
 */
public class PayFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pay_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        alipay_ly = v.findViewById(R.id.alipay_ly);
        pay_money = (TextView) v.findViewById(R.id.pay_money);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.pay);
        pay_money.setText("￥" + getOrderMoney());
    }

    double getOrderMoney() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null) {
            return entry.orderMoney;
        }
        return 0;
    }

    String getAlipayInfo() {
        Bundle bundle = getArguments();
        PayParams entry = bundle != null ? ((PayParams) bundle.getSerializable("PayParams")) : null;
        if (entry != null) {
            return entry.alipay;
        }
        return " ";
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        alipay_ly.setOnClickListener(onClick);
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                clearStack();
            else if (v == alipay_ly) {
                AlipayUtils alipayUtils = new AlipayUtils();
                alipayUtils.setHandler(mHandler);
                alipayUtils.initAlipay(getActivity());
                alipayUtils.doAlipay(getAlipayInfo());
//                gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constrants.SDK_PAY_FLAG: {
                    AlipayUtils.PayResult payResult = new AlipayUtils.PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(PayDemoActivity.this, "支付成功",
//                                Toast.LENGTH_SHORT).show();
                        ToastHelper.showShort(getActivity(), "支付成功");
                        gotoFragmentByAdd(getArguments(), R.id.mainpage_ly, new PayResultFragment(), PayResultFragment.class.getName());
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastHelper.showShort(getActivity(), "支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastHelper.showShort(getActivity(), "支付失败");
                        }
                    }
                    break;
                }
                case Constrants.SDK_CHECK_FLAG: {
                    ToastHelper.showShort(getActivity(), "检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    View alipay_ly;
    View back;
    TextView pay_money;
}
