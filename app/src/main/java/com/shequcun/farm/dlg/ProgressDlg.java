package com.shequcun.farm.dlg;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.shequcun.farm.R;
import com.lynp.R;

public class ProgressDlg extends Dialog {
	private ProgressBar bar;
	private TextView tvMsg;
	OnSearchKeyEvent mOnSearchKeyEvent = null;

	public interface OnSearchKeyEvent {
		void onSearchKeyEvent();
	}

	public void setOnSearchKeyEvent(OnSearchKeyEvent osEvent) {
		mOnSearchKeyEvent = osEvent;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

			if (mOnSearchKeyEvent != null) {
				mOnSearchKeyEvent.onSearchKeyEvent();
			}
		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}

		return super.onKeyDown(keyCode, event);
	}

	public ProgressDlg(Context context, String msg) {
		super(context, R.style.custom_dlg);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.progress_ly);
		bar = (ProgressBar) findViewById(R.id.progressbar);
		tvMsg = (TextView) findViewById(R.id.msg);
		if (msg != null && !msg.equals("")) {
			tvMsg.setText(msg);
		}

	}

	public ProgressDlg(Context context, String msg, String additionalMsg) {
		super(context, R.style.custom_dlg);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.progress_ly);

		bar = (ProgressBar) findViewById(R.id.progressbar);

		tvMsg = (TextView) findViewById(R.id.msg);
		TextView tvAdditionalMsg = (TextView) findViewById(R.id.additional_msg);

		if (msg != null && !msg.equals("")) {
			tvMsg.setText(msg);
		}

		if (additionalMsg != null && !additionalMsg.equals("")) {
			tvAdditionalMsg.setText(additionalMsg);
			tvAdditionalMsg.setVisibility(View.VISIBLE);
		} else {
			tvAdditionalMsg.setVisibility(View.GONE);
		}

	}

	public void setDlgMessage(String msg) {
		if (msg != null && !msg.equals("")) {
			tvMsg.setText(msg);
		}
	}

	public int getProgress() {
		return bar.getProgress();
	}

	public void setProgress(int progress) {
		bar.setProgress(progress);
	}

	public void updateMsg(String msg) {
		tvMsg.setText(msg);
	}

}
