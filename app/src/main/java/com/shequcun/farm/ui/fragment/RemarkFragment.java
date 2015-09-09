package com.shequcun.farm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
        save = (TextView) v.findViewById(R.id.title_right_text);
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
            if (v == back)
                popBackStack();
            else if (v == save) {
                IntentUtil.sendUpdateFarmShoppingCartMsg(getActivity(), leave_msg_to_farm.getText().toString());
                popBackStack();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.hideVirtualKeyboard(getActivity(), back);
    }

    View back;
    EditText leave_msg_to_farm;
    TextView save;
}
