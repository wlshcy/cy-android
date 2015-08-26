package com.shequcun.farm.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shequcun.farm.BaseFragmentActivity;
import com.shequcun.farm.R;
import com.shequcun.farm.data.VersionEntry;
import com.shequcun.farm.datacenter.PersistanceManager;
import com.shequcun.farm.ui.adapter.HomeViewPagerAdapter;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;

import org.apache.http.Header;

/**
 * farm home
 * Created by apple on 15/8/3.
 */
public class SqcFarmActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        checkVersion();
    }

    private void initWidget() {
        hVpager = (HomeViewPager) findViewById(R.id.hVpager);
        radiogroup = (RadioGroup) findViewById(R.id.radiogroup);
        buildAdapter();
        setWidgetLsn();
    }

    void setWidgetLsn() {
        radiogroup.setOnCheckedChangeListener(checkedChangeListener);
        // mHomeViewPager.clearAnimation();
        hVpager.addOnPageChangeListener(pageChangeLsn);
        hVpager.setOffscreenPageLimit(2);
        buildRadioButtonStatus(0);
    }

    private void buildAdapter() {
        if (hAdapter == null)
            hAdapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        hVpager.setAdapter(hAdapter);
    }

    private ViewPager.OnPageChangeListener pageChangeLsn = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            buildRadioButtonStatus(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * @param index
     */
    private void buildRadioButtonStatus(int index) {
        RadioButton rb = ((RadioButton) radiogroup.getChildAt(index));
        rb.setChecked(true);
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.combo_rb:// 套餐
//                    onPageChanged(0);
                    hVpager.setCurrentItem(0);
                    break;
                case R.id.mine_rb:// 我的
//                    onPageChanged(1);
                    hVpager.setCurrentItem(1);
                    break;
                default:
                    break;
            }

        }
    };

    void checkVersion() {
        if (PersistanceManager.getIsCheckVersion(getApplicationContext()))
            return;
        RequestParams params = new RequestParams();
        params.add("apptype", "5");
        params.add("platform", "2");
        HttpRequestUtil.httpGet(LocalParams.getBaseUrl() + "app/version", params, new AsyncHttpResponseHandler() {
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    VersionEntry vEntry = JsonUtilsParser.fromJson(new String(data), VersionEntry.class);
                    if (vEntry != null) {
                        if (TextUtils.isEmpty(vEntry.errmsg)) {
                            if (!TextUtils.isEmpty(vEntry.version) && vEntry.version.compareTo(getVersionName()) > 0) {
                                showUpdateDlg(vEntry);
                            }
                        } else {
                            ToastHelper.showShort(SqcFarmActivity.this, vEntry.errmsg);
                        }
                    }
                }
            }


            @Override
            public void onFailure(int sCode, Header[] h, byte[] data, Throwable e) {

            }
        });
    }


    String getVersionName() {
        PackageInfo info = null;
        PackageManager manager = getPackageManager();
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void showUpdateDlg(final VersionEntry vEntry) {
        if (vEntry == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage(vEntry.change);
        builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(vEntry.url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        builder.setNeutralButton(vEntry.status == 1 ? R.string.ignore : R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (vEntry.status == 2) {
                    dialog.dismiss();
                    finish();
                } else if (vEntry.status == 1) {
                    dialog.dismiss();
                    PersistanceManager.saveIsCheckVersion(getApplicationContext(),true);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

//    public void onPageChanged(int pageIndex) {
//        hVpager.setCurrentItem(pageIndex);
//    }

    HomeViewPager hVpager;
    HomeViewPagerAdapter hAdapter;
    RadioGroup radiogroup;
}
