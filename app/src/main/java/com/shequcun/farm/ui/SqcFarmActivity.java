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
import com.shequcun.farm.dlg.ProgressDlg;
import com.shequcun.farm.dlg.UserGuideDialog;
import com.shequcun.farm.ui.adapter.HomeViewPagerAdapter;
import com.shequcun.farm.util.DeviceInfo;
import com.shequcun.farm.util.HttpRequestUtil;
import com.shequcun.farm.util.JsonUtilsParser;
import com.shequcun.farm.util.LocalParams;
import com.shequcun.farm.util.ToastHelper;
import com.umeng.analytics.MobclickAgent;


import org.apache.http.Header;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * farm home
 * Created by apple on 15/8/3.
 */
public class SqcFarmActivity extends BaseFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!PersistanceManager.getOnce(this)) {
            new UserGuideDialog(this).show();
        }
        initWidget();
        doAuthInit();
        checkVersion();
    }

    private void initWidget() {
        ButterKnife.bind(this);
        buildAdapter();
        setWidgetLsn();
    }

    void setWidgetLsn() {
        radiogroup.setOnCheckedChangeListener(checkedChangeListener);
        hVpager.addOnPageChangeListener(pageChangeLsn);
        hVpager.setOffscreenPageLimit(3);
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
    public void buildRadioButtonStatus(int index) {
        RadioButton rb = ((RadioButton) radiogroup.getChildAt(index));
        rb.setChecked(true);
    }

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.home_rb:// 首页
//                    onPageChanged(0);
                    hVpager.setCurrentItem(0);
                    break;
                case R.id.mine_rb:// 我的
//                    onPageChanged(1);
                    hVpager.setCurrentItem(2);
                    break;
                case R.id.shopping_cart_rb://购物车
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
        HttpRequestUtil.getHttpClient(getApplication()).get(LocalParams.getBaseUrl() + "app/version", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int sCode, Header[] h, byte[] data) {
                if (data != null && data.length > 0) {
                    VersionEntry vEntry = JsonUtilsParser.fromJson(new String(data), VersionEntry.class);
                    if (vEntry != null) {
                        if (TextUtils.isEmpty(vEntry.errmsg)) {
                            if (!TextUtils.isEmpty(vEntry.version) && vEntry.version.compareTo(DeviceInfo.getVersion(SqcFarmActivity.this)) > 0) {
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
                if (vEntry.status == 2) {
                    finish();
                }

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
                    PersistanceManager.saveIsCheckVersion(getApplicationContext(), true);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 鉴权
     */
    private void doAuthInit() {
        HttpRequestUtil.getHttpClient(this).get(LocalParams.getBaseUrl() + "auth/init",
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int sCode, Header[] headers, byte[] data) {
                        for (Header h : headers) {
                            if (h.getName().equals("X-Xsrftoken")) {
                                PersistanceManager.saveCookieValue(SqcFarmActivity.this, h.getValue());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int sCode, Header[] h, byte[] data, Throwable error) {
                    }
                });
    }

    @Bind(R.id.hVpager)
    HomeViewPager hVpager;
    @Bind(R.id.radiogroup)
    RadioGroup radiogroup;
    HomeViewPagerAdapter hAdapter;
}
