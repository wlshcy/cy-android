package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.R;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.util.AvoidDoubleClickListener;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.shequcun.farm.util.Utils;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by apple on 15/8/22.
 */
public class FeedbackFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        back = v.findViewById(R.id.back);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.feedback);
        feedback_et = (EditText) v.findViewById(R.id.feedback_et);
        feedback_btn = v.findViewById(R.id.feedback_btn);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        feedback_btn.setOnClickListener(onClick);
    }

    AvoidDoubleClickListener onClick = new AvoidDoubleClickListener() {
        @Override
        public void onViewClick(View v) {
            if (v == back)
                popBackStack();
            else if (v == feedback_btn)
                uploadFeedbackToServer();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideVirtualKeyboard(getActivity(), feedback_btn);
    }

    void uploadFeedbackToServer() {
        String feedback = feedback_et.getText().toString();
        if (TextUtils.isEmpty(feedback)) {
            ToastHelper.showShort(getActivity(), R.string.feedback_error_tip);
            return;
        }

        final ProgressDlg pDlg = new ProgressDlg(getActivity(), "加载中...");
        RequestParams params = new RequestParams();
        params.add("type", "5");
        params.add("content", feedback);
        params.add("_xsrf", PersistanceManager.getCookieValue(getActivity()));
        HttpRequestUtil.httpPost(LocalParams.getBaseUrl() + "app/feedback", params, new AsyncHttpResponseHandler() {
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
            public void onSuccess(int sCode, Header[] headers, byte[] data) {
                try {
                    if (data != null && data.length > 0) {
                        String result = new String(data);
                        JSONObject jObj = new JSONObject(result);
                        if (jObj != null) {
                            String errmsg = jObj.optString("errmsg");
                            if (TextUtils.isEmpty(errmsg)) {
                                ToastHelper.showShort(getActivity(),
                                        R.string.tks_feedback_tip);
                                popBackStack();
                                return;
                            }

                            ToastHelper.showShort(getActivity(), errmsg);
                        }
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
                ToastHelper.showShort(getActivity(), "反馈失败,错误码" + sCode);
            }
        });
    }

    View back;
    EditText feedback_et;
    View feedback_btn;
}
