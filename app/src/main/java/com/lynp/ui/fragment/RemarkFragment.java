package com.lynp.ui.fragment;

/**
 * Created by niuminguo on 16/3/30.
 */
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lynp.R;
import com.lynp.ui.util.IntentUtil;
import com.lynp.ui.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 添加备注页
 * Created by mac on 15/9/9.
 */
public class RemarkFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.remark_ly, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initWidget(View v) {
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.add_remark);
        leave_msg_to_farm.setText(getArguments().getString("RemarkTip"));
        ColorStateList green =
                getBaseAct().getResources().getColorStateList(R.color.green_2bbc6a);
        save.setTextColor(green);
        save.setText(R.string.save);
    }

    @Override
    protected void setWidgetLsn() {
    }

    @OnClick({R.id.back, R.id.title_right_text})
    void back(View v) {
        Utils.hideVirtualKeyboard(getBaseAct(), v);
        if (v == save) {
            if (lsn != null) {
                lsn.updateRemarkWidget(leave_msg_to_farm.getText().toString());
            }
            popBackStack();
            return;
        }
        checkQuit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void checkQuit() {
        if (checkInput())
            alertQuitEdit();
        else
            popBackStack();
    }

    private boolean checkInput() {
        String content = leave_msg_to_farm.getText().toString();
        return !TextUtils.isEmpty(content);
    }

    private void alertQuitEdit() {
        final AlertDialog alert = new AlertDialog.Builder(getBaseAct()).create();
        alert.show();
        alert.setCancelable(false);
        alert.getWindow().setContentView(R.layout.prompt_dialog);
        ((TextView) alert.getWindow().findViewById(R.id.content_tv))
                .setText("确定退出编辑？");
        alert.getWindow().findViewById(R.id.no)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
        alert.getWindow().findViewById(R.id.yes)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        Utils.hideVirtualKeyboard(getBaseAct(), save);
                        popBackStack();
                    }
                });
    }

    CallBackLsn lsn;

    public void setCallBackLsn(CallBackLsn lsn) {
        this.lsn = lsn;
    }

    public interface CallBackLsn {
        void updateRemarkWidget(String remark);
    }

    @Bind(R.id.leave_msg_to_farm)
    EditText leave_msg_to_farm;
    @Bind(R.id.title_right_text)
    TextView save;
}