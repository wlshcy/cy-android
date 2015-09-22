package com.shequcun.farm.ui.fragment;

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

import com.shequcun.farm.R;
import com.shequcun.farm.util.IntentUtil;
import com.shequcun.farm.util.Utils;

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
        back = v.findViewById(R.id.back);
        leave_msg_to_farm = (EditText) v.findViewById(R.id.leave_msg_to_farm);
        ((TextView) v.findViewById(R.id.title_center_text)).setText(R.string.add_remark);
        leave_msg_to_farm.setText(getArguments().getString("RemarkTip"));
        save = (TextView) v.findViewById(R.id.title_right_text);
        ColorStateList green =
                getActivity().getResources().getColorStateList(R.color.green_2bbc6a);
        save.setTextColor(green);
        save.setText(R.string.save);
    }

    @Override
    protected void setWidgetLsn() {
        back.setOnClickListener(onClick);
        save.setOnClickListener(onClick);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == back) {
                checkQuit();
            } else if (v == save) {


                if (lsn != null) {
                    lsn.updateRemarkWidget(leave_msg_to_farm.getText().toString());
                }

//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                String tag = fragmentManager
//                        .getBackStackEntryAt(
//                                fragmentManager
//                                        .getBackStackEntryCount() - 1)
//                        .getName();
//                Log.d("This is your Top Fragment name: ", "" + tag);


//                FragmentManager fragManager = getActivity().getSupportFragmentManager();
//                int count = fragManager.getBackStackEntryCount();
//                Fragment frag = fragManager.getFragments().get(count > 0 ? count - 2 : count);

                popBackStack();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideVirtualKeyboard(getActivity(), back);
    }

    private void checkQuit(){
        if (checkInput())
            alertQuitEdit();
        else
            popBackStack();
    }

    private boolean checkInput(){
        String content = leave_msg_to_farm.getText().toString();
        return !TextUtils.isEmpty(content);
    }

    private void alertQuitEdit() {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
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
                        popBackStack();
                    }
                });
    }

    CallBackLsn lsn;

    public void setCallBackLsn(CallBackLsn lsn) {
        this.lsn = lsn;
    }

    public interface CallBackLsn {
        public void updateRemarkWidget(String remark);
    }

    View back;
    EditText leave_msg_to_farm;
    TextView save;
}
